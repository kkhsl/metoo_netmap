package com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.utils.net.Ipv6Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PanabitService {

    public String login(String url, String username, String password) {

//        String username = "guest";
//        String password = "JXDX@panabit0624";
//        String url = "https://111.78.216.23:10443/api/panabit.cgi";

        // 准备表单字段
        Map<String, String> formFields = new HashMap<>();
        formFields.put("username", username);
        formFields.put("password", password);
        formFields.put("api_action", "api_login");

        log.info("login start ...");
        // 调用发送请求的方法
        String response = HttpURLConnectionUtils.form_data(url, formFields);
        log.info("login end ...");


        if(StringUtil.isNotEmpty(response)){
            // 从响应中提取 token
            if(extractCode(response).equals("0")){
                return extractData(response);
            }
        }
        return ""; // 根据实际响应格式提取 token
    }


    // type v4/v6/all
    public String load_ipobj_list(String type, String ip, String port, String username, String password) {

//        String url = "https://111.78.216.23:10443/api/panabit.cgi";
        if(Ipv6Utils.isValidIPv6(ip)){
            ip = "[" + ip.concat("]");
        }
        String url = "https://"+ip+":"+port+"/api/panabit.cgi";

        String token = this.login(url, username, password);
        if(StringUtil.isNotEmpty(token)){
            Map<String, String> formFields = new HashMap<>();
            formFields.put("api_token", token);
            formFields.put("api_action", "load_ipobj_list");
            formFields.put("api_route", "dashboard@ipobj");
            formFields.put("page", "1");
            formFields.put("limit", "5000");
            formFields.put("type", type);

            // 调用发送请求的方法
            String response = HttpURLConnectionUtils.form_data(url, formFields);

            if(StringUtil.isNotEmpty(response)){
                // 从响应中提取 token
                if(extractCode(response).equals("0")){
                    return extractData(response);
                }
            }
        }
        return "";
    }

    private static String extractCode(String responseBody) {
        // 假设 token 在 JSON 响应的字段 "token" 中
        JSONObject jsonObject = JSONObject.parseObject(responseBody.toString()); // 实现 extractToken 方法以提取 token
        if(jsonObject.getString("code") != null){
            return jsonObject.getString("code");
        }
        return "";
    }

    // 提取 token 方法（根据实际响应格式修改）
    private static String extractToken(String responseBody) {
        // 假设 token 在 JSON 响应的字段 "token" 中
        JSONObject jsonObject = JSONObject.parseObject(responseBody.toString()); // 实现 extractToken 方法以提取 token
        if(jsonObject.getString("data") != null){
            return jsonObject.getString("data");
        }
        return "";
    }

    // 提取 token 方法（根据实际响应格式修改）
    private static String extractData(String responseBody) {
        // 假设 token 在 JSON 响应的字段 "token" 中
        JSONObject jsonObject = JSONObject.parseObject(responseBody.toString()); // 实现 extractToken 方法以提取 token
        if(jsonObject.getString("data") != null){
            log.info("Pana data:", jsonObject.getString("data"));
            return jsonObject.getString("data");
        }
        return "";
    }
}
