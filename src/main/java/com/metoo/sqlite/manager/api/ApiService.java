package com.metoo.sqlite.manager.api;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-29 21:11
 */
@Slf4j
@Service
public class ApiService {

    private final RestTemplate restTemplate;

    public static void main(String[] args) {
        String url = "http://43.153.82.44:8930//api/soft/version";
        RestTemplate restTemplate = new RestTemplate();
        ApiService apiService = new ApiService(restTemplate);
        String result = apiService.callApi(url);
        JSONObject json = JSONObject.parseObject(result, JSONObject.class);
        if(json != null){
            String version = json.getString("version");
            if(version != null && !version.isEmpty()){
                String a = "1.1.0";
                System.out.println(a.compareTo(version));
                if(a.compareTo(version) < 0){
                    System.out.println("更新。。。");
                }
            }
        }

    }

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callApi(String apiUrl) {
        // 发起GET请求并获取响应
        return restTemplate.getForObject(apiUrl, String.class);
    }

    public String callThirdPartyApi(String apiUrl, JsonRequest request) {
        log.info("Chuangfa api ==============================");
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 设置请求体参数
        HttpEntity<JsonRequest> requestBody = new HttpEntity<>(request, headers);

        // 发起POST请求，并获取响应
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestBody,
                String.class
        );

        // 返回响应内容
        return response.getBody();
    }
}
