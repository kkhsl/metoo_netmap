package com.metoo.sqlite.manager.api.remote;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 版本管理接口调用
 * @author zzy
 * @version 1.0
 * @date 2022/4/6 22:23
 */
@Component
@Slf4j
public class VersionManagerRemote extends BaseCall {
    /**
     * 版本更新检测url
     */
    @Value("${version.update.url}")
    private String url;

    /**
     * 构造函数
     *
     * @param restTemplate
     */
    public VersionManagerRemote(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * 组装查询参数
     * @param unitId
     * @param curVersion
     * @return
     */
    public BusiParamDto versionParam(Long unitId,String curVersion){
        BusiParamDto paramDto = new BusiParamDto();
        paramDto.setHttpMethod("POST");
        Map<String, Object> param = MapUtil.newHashMap(8);
        param.put("curVersion",curVersion );
        param.put("unitId", unitId);
        paramDto.setParamMap(param);
        return paramDto;
    }

    @Override
    public String executeCall(String url, HttpMethod method, HttpEntity requestEntity, Map<String, Object> maps) throws URISyntaxException {
        if (null == maps) {
            maps = new HashMap<>(8);
        }
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        URI uri = new URI(url);
        ResponseEntity<String> exchange = restTemplate.exchange(uri, method, requestEntity, reference);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            String result = exchange.getBody();
            if (StrUtil.isNotEmpty(result)) {
                JSONObject json = JSONObject.parseObject(result, JSONObject.class);
                return json.getString("data");
            }
            log.info("调用版本升级接口返回参数：{}", result);
        } else {
            log.error("调用版本升级接口错误");
        }
        return "";
    }


    /**
     * 添加默认参数（分页参数）
     * @param maps
     */
    @Override
    public Map<String, Object> addDefaultParam(Map<String, Object> maps){
        return maps;
    }


    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, Object> buildBody(HttpMethod httpMethod,Map<String, Object> requestBody, Map<String, Object> maps) {
        return requestBody!=null ?requestBody:new HashMap<>(8);
    }
}
