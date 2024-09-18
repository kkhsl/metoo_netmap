package com.metoo.sqlite.utils.elasticsearch;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void searchDocuments() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nat-2024.09.16");
        searchRequest.source().query(QueryBuilders.matchAllQuery());

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSONObject.toJSONString(searchRequest));

        System.out.println(JSONObject.toJSONString(searchResponse.getHits()));
        searchResponse.getHits().forEach(hit -> {
            System.out.println(hit.getSourceAsString());
        });
    }

    public void searchAndAggregateDocuments() throws IOException {
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("nat-2024.09.16"); // 替换为你的索引名称

        // 创建聚合请求
        searchRequest.source().aggregation(
                AggregationBuilders.terms("destip_agg").field("destIp.keyword").size(10) // 统计字段出现次数，并获取前三个
        ).query(QueryBuilders.matchAllQuery()); // 替换为你的查询条件

        // 执行搜索请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取聚合结果
        Terms terms = (Terms) searchResponse.getAggregations().get("destip_agg");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();

        // 输出结果
        for (Terms.Bucket bucket : buckets) {
            String destip = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            System.out.println("IP: " + destip + ", Count: " + count);
        }
    }

//    @Autowired
//    private ElasticsearchClient elasticsearchClient;
//
//    public void searchAndAggregateDocuments() throws IOException {
//        // 创建聚合请求
//        SearchResponse<Map<String, Object>> searchResponse = elasticsearchClient.search(s -> s
//                        .index("nat-2024.09.16") // 替换为你的索引名称
//                        .query(q -> q
//                                .matchAll() // 替换为你的查询条件
//                        )
//                        .aggregations("destip_agg", a -> a
//                                .terms(t -> t
//                                        .field("destip") // 替换为你的字段名称
//                                        .size(3) // 返回出现次数最多的前 3 个
//                                )
//                        ),
//                Map.class
//        );
//
//        // 获取聚合结果
//        Map<String, Aggregations> aggregations = searchResponse.aggregations();
//        TermsAggregation termsAggregation = (TermsAggregation) aggregations.get("destip_agg");
//        List<TermsBucket> buckets = termsAggregation.buckets();
//
//        // 输出结果
//        buckets.forEach(bucket -> {
//            System.out.println("IP: " + bucket.key() + ", Count: " + bucket.docCount());
//        });
//    }
}
