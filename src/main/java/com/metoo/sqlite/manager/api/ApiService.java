package com.metoo.sqlite.manager.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-29 21:11
 */
@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callApi(String apiUrl) {
        // 发起GET请求并获取响应
        return restTemplate.getForObject(apiUrl, String.class);
    }

    public String callThirdPartyApi(String apiUrl, JsonRequest request) {
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
