package com.metoo.sqlite.model.es;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.metoo.sqlite.dto.SessionInfoDto;
import com.metoo.sqlite.entity.es.IpStatisticsResult;
import com.metoo.sqlite.mapper.IpStatisticsResultMapper;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.elasticsearch.EsClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * es查询
 *
 * @author zhaozhiyuan
 * @version 1.0
 * @date 2024/10/11 19:22
 */
@Service
@Slf4j
public class EsQueryService {
    @Autowired
    private EsClientConfig esClientConfig;

    @Resource
    private IpStatisticsResultMapper ipStatisticsResultMapper;

    /**
     * 保存统计数据
     *
     * @param indexName 索引名称
     * @param type      统计类型（ipv4 或 ipv6)
     * @param topN      top N 数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveStatisticsResult(String indexName, String type, int topN) {
        List<IpStatisticsResult> data = this.filterQuery(indexName, type, topN);
        if (CollUtil.isNotEmpty(data)) {
            // 先删除之前的数据
            ipStatisticsResultMapper.delete(indexName,type);
            for (IpStatisticsResult item : data) {
                // 保存此次查询的数据
                ipStatisticsResultMapper.save(item);
            }
        }
    }

    /**
     * 查询session数据
     *
     * @return
     */
    public SessionInfoDto querySessionInfo() {
        SessionInfoDto result = new SessionInfoDto();
        Map<String,String> params=new HashMap<>();
        String indexName = getIndexName();
        log.info("当前测绘索引名称：{}",indexName);
        params.put("name", indexName);
        // 先查询top10数据
        saveStatisticsResult(indexName,"ipv4",10);
        saveStatisticsResult(indexName,"ipv6",10);
        List<IpStatisticsResult> ipStatisticsResultList = ipStatisticsResultMapper.selectObjByMap(params);
        // 获取top10
        if (CollUtil.isNotEmpty(ipStatisticsResultList)) {
            result.setIpv4Top10(ipStatisticsResultList.stream().map(IpStatisticsResult::getIp).collect(Collectors.toList()));
        }
        // 获取ipv4session总数
        result.setIpv4Session(this.count(indexName,"ipv4"));
        //  获取ipv6session总数
        result.setIpv6Session(this.count(indexName,"ipv6"));
        return result;
    }

    /**
     * 获取索引名称
     * @return
     */
    private String getIndexName(){
        // 获取所有索引
        List<String> allIndex=getAllIndexNames();
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 创建日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        // 将当前日期格式化为指定格式
        String indexDate = currentDate.format(formatter);
        if(CollUtil.isNotEmpty(allIndex)){
            for (String o : allIndex) {
                if (o.contains(indexDate)) {
                    return o;
                }
            }
        }
        return indexDate;
    }

    private List<IpStatisticsResult> query(String indexName, String type, int topN) {
        this.validate(indexName, type);
        List<IpStatisticsResult> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        RestHighLevelClient client = this.esClientConfig.elasticsearchClient();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 按源IP分组查询
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("destIpGroup").field("destIp.keyword");

        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
        filterQuery.mustNot(QueryBuilders.termQuery("destPort", 53));

        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(QueryBuilders.boolQuery().filter(filterQuery));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            //map:key=分组的状态,value=每组的个数
            Map<String, Integer> stateCountMap = new LinkedHashMap<>();
            // 获取前 5 个请求最多的源IP
            Terms terms = response.getAggregations().get("destIpGroup");
            int size = terms.getBuckets().size() < topN ? terms.getBuckets().size() : topN;
            Date time = new Date();
            for (int i = 0; i < size; i++) {
                Terms.Bucket bucket = terms.getBuckets().get(i);
                String ip = bucket.getKeyAsString();
                stateCountMap.put(ip, Long.valueOf(bucket.getDocCount()).intValue());
                IpStatisticsResult item = new IpStatisticsResult();
                item.setName(indexName);
                item.setIp(ip);
                item.setAccessCount(stateCountMap.get(ip));
                item.setStatisticsTime(DateTime.now().toString());
                item.setType(type);
                item.setRank(i + 1);
                results.add(item);
            }
            stateCountMap.forEach((k, v) -> {
                log.info("请求target IP = " + k + "，请求次数 = " + v);
            });
        } catch (IOException e) {
            log.error("查询es服务的topN数据出现错误：{}", e);
        }
        return results;
    }

    private void validate(String indexName, String type) {
        if (StrUtil.isEmpty(indexName)) {
            throw new IllegalArgumentException("索引名称为空！");
        }
        if (StrUtil.isEmpty(type)) {
            throw new IllegalArgumentException("类型为空！");
        }
        if (!ObjectUtils.nullSafeEquals(type, "ipv4") && !ObjectUtils.nullSafeEquals(type, "ipv6")) {
            throw new IllegalArgumentException("类型只能输入ipv4或ipv6！");
        }
    }

    /**
     * 过滤 ipv4 或 ipv6 查询
     *
     * @param indName
     * @param type
     * @param topN
     * @return
     */
    private List<IpStatisticsResult> filterQuery(String indName, String type, int topN) {
        List<IpStatisticsResult> results = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indName);
        RestHighLevelClient client = this.esClientConfig.elasticsearchClient();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String field = "destIp.keyword";
        // 按源IP分组查询
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("destIpGroup").field(field);

        BoolQueryBuilder filterQuery = this.buildFilterQuery(type);

        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(QueryBuilders.boolQuery().filter(filterQuery));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            //map:key=分组的状态,value=每组的个数
            Map<String, Integer> stateCountMap = new LinkedHashMap<>();
            // 获取前 5 个请求最多的源IP
            Terms terms = response.getAggregations().get("destIpGroup");
            int size = terms.getBuckets().size() < topN ? terms.getBuckets().size() : topN;
            for (int i = 0; i < size; i++) {
                Terms.Bucket bucket = terms.getBuckets().get(i);
                String ip = bucket.getKeyAsString();
                stateCountMap.put(ip, Long.valueOf(bucket.getDocCount()).intValue());
                IpStatisticsResult item = new IpStatisticsResult();
                item.setName(indName);
                item.setIp(ip);
                item.setAccessCount(stateCountMap.get(ip));
                item.setStatisticsTime(DateTime.now().toString());
                item.setType(type);
                item.setRank(i + 1);
                results.add(item);
            }
            log.info(type + " TOP " + topN);
            stateCountMap.forEach((k, v) -> {
                log.info("请求target IP = " + k + "，请求次数 = " + v);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 统计会话数
     * @param indName
     * @param type
     */
    public long count(String indName, String type) {
        SearchRequest searchRequest = new SearchRequest(indName);
        RestHighLevelClient client = this.esClientConfig.elasticsearchClient();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String field = "destIp.keyword";
        // 按源IP分组查询
        AggregationBuilder aggregationBuilder = AggregationBuilders.count("sum").field(field);

        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(QueryBuilders.boolQuery().filter(this.buildFilterQuery(type)));
        searchSourceBuilder.trackTotalHits(Boolean.TRUE);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            ValueCount count = response.getAggregations().get("sum");
            log.info(type + " 总数 = " + count.getValue());
            return count.getValue();
        } catch (IOException e) {
            log.error("统计会话数失败：{}",e);
        }
        return 0;
    }

    private BoolQueryBuilder buildFilterQuery(String type) {
        BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
        // 过滤掉目标端口为 53 的
        filterQuery.mustNot(QueryBuilders.termQuery("destPort", 53));
        Map<String, Object> bucketsPathsMap = new HashMap<>();
        bucketsPathsMap.put("destIp", 18);
        String inlineScript = "";
        // 写脚本判断是否 ipv6
        if ("ipv6".equals(type)) {
            inlineScript = "doc['destIp.keyword'].size() > 0 && doc['destIp.keyword'].value.indexOf(':') >= 0";
        } else {
            inlineScript = "doc['destIp.keyword'].size() > 0 && doc['destIp.keyword'].value.indexOf(':') < 0";
        }
        //Pattern.matches()
        Script script = new Script(ScriptType.INLINE, "painless", inlineScript, bucketsPathsMap);
        filterQuery.filter(QueryBuilders.scriptQuery(script));
        return filterQuery;
    }

    /**
     * 获取所有的索引名称
     * @return
     */
    public List<String> getAllIndexNames() {
        IndicesClient indicesClient = this.esClientConfig.elasticsearchClient().indices();
        GetIndexRequest request = new GetIndexRequest("*");
        //request.indicesOptions(IndicesOptions.lenientExpandOpen());
        String [] indices = new String[0];
        try {
            indices = indicesClient.get(request, RequestOptions.DEFAULT).getIndices();
            List<String> indexNames = Arrays.stream(indices).filter(item -> !item.startsWith(".")).collect(Collectors.toList());
            for (String indexName : indexNames) {
                log.info(indexName);
            }
            return indexNames;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 删除索引
     * @param indName
     */
    public void deleteIndex(String indName) {
        RestHighLevelClient client = this.esClientConfig.elasticsearchClient();
        try {
            DeleteIndexRequest deleteReq = new DeleteIndexRequest(indName);
            AcknowledgedResponse response = client.indices().delete(deleteReq, RequestOptions.DEFAULT);
            log.info("删除索引[" + indName + "]结果 = " + response.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
