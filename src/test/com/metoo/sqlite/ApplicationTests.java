package com.metoo.sqlite;

import com.metoo.sqlite.entity.Device;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.factory.gather.thread.ExecThread;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.strategy.other.Ipv4CollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.service.impl.Ipv4ServiceImpl;
import com.metoo.sqlite.utils.Global;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.panabit.PanabitSendMsgUtils.SSLUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class ApplicationTests {


    @Test
    public void testGlobal(){
        System.out.println("171.34.163.38");
    }

    @Test
    public void test() {
        String ip = "171.34.163.38";
        String port = "9000";
        getArp(ip, port);
    }


    @Value("${muyun.scheme}")
    private static String scheme;

    public static void getArp(String ip, String port) {
        String path = "/api/v1/arp/";
        String url = scheme + ip + ":" + port + path;
        System.out.println(url);
    }

    public static void get() {
        try {
            SSLUtils.disableCertificateValidation();

            // 请求的 URL
            String urlString = "https://171.34.163.38:9000/api/v1/arp/";
            URL url = new URL(urlString);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 设置 Basic Authentication 头
            String username = "apiuser";
            String password = "ufAPI@9588";
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Test
    public void gatherIpv4(){
        Device device = new Device();
        device.setIp("192.168.6.1");
        device.setDeviceTypeAlias("switch");
        device.setDeviceVendorAlias("h3c");
        device.setLoginName("metoo");
        device.setLoginPassword("metoo89745000");
        device.setLoginType("ssh");
        device.setLoginPort("22");
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(device);

        PyCommandBuilder pyCommand = new PyCommandBuilder();
        pyCommand.setVersion(Global.py_name);
        pyCommand.setPy_prefix("-W ignore");
        pyCommand.setPath(Global.PYPATH);
        pyCommand.setName("main.pyc");
        pyCommand.setParams(new String[]{
                device.getDeviceVendorAlias(),
                device.getDeviceTypeAlias(),
                device.getIp(),
                device.getLoginType(),
                device.getLoginPort(),
                device.getLoginName(),
                device.getLoginPassword(), Global.PY_SUFFIX_IPV4});

        CountDownLatch latch = false ? new CountDownLatch(deviceList.size()) : null;

        for (Device device1 : deviceList) {
            Context context = new Context();
            context.setCreateTime(DateTools.getCreateTime());
            context.setLatch(latch);
            context.setEntity(device1);
            DataCollectionStrategy collectionStrategy = new Ipv4CollectionStrategy(new Ipv4ServiceImpl(),
                    new PyExecUtils());
            ExecThread.exec(collectionStrategy, context);
        }


    }

}
