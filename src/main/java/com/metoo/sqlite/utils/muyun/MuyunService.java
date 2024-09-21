package com.metoo.sqlite.utils.muyun;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.net.Ipv6Utils;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.SSLUtils;
import org.apache.tomcat.util.net.IPv6Utils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

@Component
public class MuyunService {


    public String getArp(String ip, Integer port, String loginName, String loginPassword){
        String path = "/api/v1/arp/";
        return get(ip, port, path, loginName, loginPassword);
    }

    public String sendApi(String ip, Integer port, String loginName, String loginPassword){
        if(Ipv6Utils.isValidIPv6(ip)){
            ip = "[" + ip.concat("]");
        }
        return getIpv6_neighbor(ip, port, loginName, loginPassword);
    }

    public String getIpv6_neighbor(String ip, Integer port, String loginName, String loginPassword){
        String path = "/api/v1/ipv6_neighbor/";
        return get(ip, port, path, loginName, loginPassword);
    }

    public String get(String ip, Integer port, String path, String loginName, String loginPassword){
        try {
            URL url = new URL(Global.muyun_scheme, ip, port, path);
            String result = get(url, loginName, loginPassword);
            MuyunResult muyunResult = JSONObject.parseObject(result, MuyunResult.class);
            if(muyunResult.getCode() == 0){
                return muyunResult.getData();
            }
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String get(URL url, String username, String password){
        try {
            SSLUtils.disableCertificateValidation();

            // 请求的 URL
//            URL url = new URL(urlString);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 设置 Basic Authentication 头
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            connection.setRequestProperty("Authorization", authHeader);

            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 打印响应
            System.out.println("Response: " + response.toString());

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String username = "apiuser";
        String password = "ufAPI@9588";
        String path = "/api/v1/arp/";
        String host = "https://171.34.163.38:9000";
        String ip = "171.34.163.38";
        String port = "9000";
    }
}
