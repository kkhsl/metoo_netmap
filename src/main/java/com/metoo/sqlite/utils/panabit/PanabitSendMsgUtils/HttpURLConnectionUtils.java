package com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.print.DocFlavor;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpURLConnectionUtils {


    public static void main(String[] args) {

        // 设置全局的 SSL 配置，跳过证书验证
        SSLUtils.disableCertificateValidation();

        // 现在你可以进行 HTTPS 请求而无需证书验证
        // 创建 URL 对象
        // 发送请求并处理响应

        try {
            String apiUrl = "https://111.78.216.23:10443/api/panabit.cgi";
            String username = "guest";
            String password = "JXDX@panabit0624";

            // 创建 URL 对象
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; GB2312");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // 创建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("api_action", "api_login");
            requestBody.put("username", username);
            requestBody.put("password", password);

            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("GB2312");
                os.write(input, 0, input.length);
            }

            // 读取响应
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GB2312"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // 解析响应
                    JSONObject responseObject = JSONObject.parseObject(response.toString());
                    String token = responseObject.getString("TOKEN"); // 根据实际字段解析
                    System.out.println("Token: " + token);
                }
            } else {
                System.out.println("Request failed with code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // form-data

    private static final String BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
    private static final String CRLF = "\r\n";
    private static final Charset CHARSET = Charset.forName("GB2312");

    String apiUrl = "https://111.78.216.23:10443/api/panabit.cgi";
    String username = "guest";
    String password = "JXDX@panabit0624";


    @Test
    public void testFormat_data(){
        String url= "https://111.78.216.223:10443/api/panabit.cgi";
        Map params = new HashMap();

        params.put("url", url);
        params.put("username", "guest");
        params.put("password", "JXDX@panabit0624");
        params.put("api_action", "api_login");

        String data = this.form_data(url, params);
        System.out.println(data);
    }


    public static String form_data(String requestUrl, Map<String, String> formFields){
        try {
            // 设置全局的 SSL 配置，跳过证书验证
            SSLUtils.disableCertificateValidation();

            // 创建 URL 对象
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setDoOutput(true);


            // 构建请求体
            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true)) {


                // 写入表单字段
                for (Map.Entry<String, String> entry : formFields.entrySet()) {
                    writer.append("--").append(BOUNDARY).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
                    writer.append(CRLF);
                    writer.append(encode(entry.getValue())).append(CRLF);
                }


                // 写入表单字段（例如用户名和密码）
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"username\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("guest")).append(CRLF);
//
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"password\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("JXDX@panabit0624")).append(CRLF);
//
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"api_action\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("api_login")).append(CRLF);

                // 结束请求体
                writer.append("--").append(BOUNDARY).append("--").append(CRLF).flush();
            }catch (Exception e){
                return "";
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

//            JSONObject jsonObject = JSONObject.parseObject(response.toString()); // 实现 extractToken 方法以提取 token
//            if(jsonObject.getString("data") != null){
//                return jsonObject.getString("data");
//            }


            log.info("Api reslut ============== " + response.toString());
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }

    // 编码方法
    private static String encode(String value) throws UnsupportedEncodingException {
        return new String(value.getBytes(CHARSET), CHARSET);
    }

    public static String form_data2(String requestUrl, Map<String, String> formFields){
        try {
            // 设置全局的 SSL 配置，跳过证书验证
            SSLUtils.disableCertificateValidation();

//            URI uri = new URI(scheme, null, host, port, path, null, null);
//            URL url = new URL(uri.toString());
            // 创建 URL 对象
            // 创建 URL 对象
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setDoOutput(true);


            // 构建请求体
            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true)) {


                // 写入表单字段
                for (Map.Entry<String, String> entry : formFields.entrySet()) {
                    writer.append("--").append(BOUNDARY).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
                    writer.append(CRLF);
                    writer.append(encode(entry.getValue())).append(CRLF);
                }


                // 写入表单字段（例如用户名和密码）
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"username\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("guest")).append(CRLF);
//
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"password\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("JXDX@panabit0624")).append(CRLF);
//
//                writer.append("--").append(BOUNDARY).append(CRLF);
//                writer.append("Content-Disposition: form-data; name=\"api_action\"").append(CRLF);
//                writer.append("Content-Type: text/plain; charset=").append(CHARSET.name()).append(CRLF);
//                writer.append(CRLF);
//                writer.append(encode("api_login")).append(CRLF);

                // 结束请求体
                writer.append("--").append(BOUNDARY).append("--").append(CRLF).flush();
            }catch (Exception e){
                return "";
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

//            JSONObject jsonObject = JSONObject.parseObject(response.toString()); // 实现 extractToken 方法以提取 token
//            if(jsonObject.getString("data") != null){
//                return jsonObject.getString("data");
//            }


            log.info("Api reslut ============== " + response.toString());
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }


}
