package com.metoo.sqlite.utils.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.OsScan;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.mapper.OsScanMapper;
import com.metoo.sqlite.service.IOsScanService;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.service.impl.OsScanServiceImpl;
import com.metoo.sqlite.utils.StringUtils;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FileToDatabase {

//    private static String filePath = "C:\\Users\\hkk\\Desktop\\metoo\\192.168.5.101\\docker\\result_overlap.txt";
//    private static String filePath = "C:\\Users\\hkk\\Desktop\\metoo\\192.168.5.101\\result.txt";
//    private static String filePath = "/opt/netmap/result.txt";
    private static String filePath = "/opt/netmap/result_overlap.txt";
//    private static String filePath = "/opt/sqlite/result_overlap.txt";

    public static void main(String[] args) {
        try {
            // 方法1：使用 BufferedReader 读取文件内容
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    contentBuilder.append(currentLine).append("\n");
                }
            }
            String jsonString = contentBuilder.toString();

            // 拆分字符串为单独的 JSON 对象
            String[] jsonObjects = jsonString.split("\\}\\{");

            // 修正每个 JSON 对象的格式
            jsonObjects[0] += "}";
            for (int i = 1; i < jsonObjects.length - 1; i++) {
                jsonObjects[i] = "{" + jsonObjects[i] + "}";
            }
            jsonObjects[jsonObjects.length - 1] = "{" + jsonObjects[jsonObjects.length - 1];

            // 创建 JSON 数组
            List<OsScan> list = new ArrayList<>();
            for (String jsonObjectStr : jsonObjects) {
                OsScan osScan = null;
                try {
                    osScan = JSONObject.parseObject(jsonObjectStr, OsScan.class);
                    list.add(osScan);
                } catch (Exception e) {
                    e.printStackTrace();
                    list.clear();
                    break;
                }

            }

            // 方法2：使用 Files 读取文件内容
            // String jsonString = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

            // 将 JSON 字符串转换为 Java 对象
            ObjectMapper objectMapper = new ObjectMapper();
            OsScan osScan = objectMapper.readValue(jsonString, OsScan.class);


            // 打印 Java 对象
            System.out.println(osScan);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String addBrackets(String input) {
        return "[" + input + "]";
    }

    @Autowired
    private IOsScanService osScanService;
    @Autowired
    private IProbeService probeService;


    public void write(){

        try {
            // 方法1：使用 BufferedReader 读取文件内容
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    contentBuilder.append(currentLine.trim());
                }
            }
            String jsonString = contentBuilder.toString();

            if("".equals(jsonString)){
                return;
            }

            List<OsScan> list = new ArrayList<>();

            // 拆分字符串为单独的 JSON 对象
            String[] jsonObjects = {};
            if(jsonString.contains("}{")){
                jsonObjects = jsonString.split("\\}\\{");
                // 修正每个 JSON 对象的格式
                jsonObjects[0] += "}";
                for (int i = 1; i < jsonObjects.length - 1; i++) {
                    jsonObjects[i] = "{" + jsonObjects[i] + "}";
                }
                jsonObjects[jsonObjects.length - 1] = "{" + jsonObjects[jsonObjects.length - 1];

                for (String jsonObjectStr : jsonObjects) {
                    OsScan osScan = null;
                    try {
                        osScan = JSONObject.parseObject(jsonObjectStr, OsScan.class);
                        list.add(osScan);
                    } catch (Exception e) {
                        e.printStackTrace();
                        list.clear();
                        break;
                    }
                }
            }else{
                OsScan osScan = JSONObject.parseObject(jsonString, OsScan.class);
                list.add(osScan);
            }

            // 遍历OsScan-写入到Probe
            if(list.size() > 0){
                Map params = new HashMap();
                for (OsScan osScan : list) {
                    // 更新probe
                    params.clear();
                    params.put("ip_addr", osScan.getIP());
                    params.put("port_num", osScan.getOpenPort());
                    List<Probe> probes = this.probeService.selectObjByMap(params);
                    if(probes.size() > 0){
                        for (Probe probe : probes) {
                            probe.setTtl(Integer.parseInt(osScan.getTtl()));
                            probe.setFingerId(osScan.getFingerID());
                            probe.setReliability(Float.parseFloat(osScan.getReliability()));
                            probe.setVendor(osScan.getOsVendor());
                            probe.setOs_gen(osScan.getOsGen());
                            probe.setOs_family(osScan.getOsFamily());
                            this.probeService.update(probe);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String type){
        try {
            // 方法1：使用 BufferedReader 读取文件内容
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    contentBuilder.append(currentLine.trim());
                }
            }
            String jsonString = contentBuilder.toString();

            if("".equals(jsonString)){
                return;
            }

            List<OsScan> list = new ArrayList<>();

            // 拆分字符串为单独的 JSON 对象
            String[] jsonObjects = {};
            if(jsonString.contains("}{")){
                jsonObjects = jsonString.split("\\}\\{");
                // 修正每个 JSON 对象的格式
                jsonObjects[0] += "}";
                for (int i = 1; i < jsonObjects.length - 1; i++) {
                    jsonObjects[i] = "{" + jsonObjects[i] + "}";
                }
                jsonObjects[jsonObjects.length - 1] = "{" + jsonObjects[jsonObjects.length - 1];

                for (String jsonObjectStr : jsonObjects) {
                    OsScan osScan = null;
                    try {
                        osScan = JSONObject.parseObject(jsonObjectStr, OsScan.class);
                        list.add(osScan);
                    } catch (Exception e) {
                        e.printStackTrace();
                        list.clear();
                        break;
                    }
                }
            }else{
                OsScan osScan = JSONObject.parseObject(jsonString, OsScan.class);
                list.add(osScan);
            }

            // 遍历OsScan-写入到Probe
            if(list.size() > 0){
                for (OsScan osScan : list) {
                    Probe probe = new Probe();
                    probe.setCreateTime(DateTools.getCreateTime());
                    if(StringUtils.isNotEmpty(osScan.getTtl())){
                        probe.setTtl(Integer.parseInt(osScan.getTtl()));
                    }
                    probe.setFingerId(osScan.getFingerID());
                    if(StringUtils.isNotEmpty(osScan.getReliability())){
                        probe.setReliability(Float.parseFloat(osScan.getReliability()));
                    }
                    probe.setVendor(osScan.getOsVendor());
                    probe.setOs_gen(osScan.getOsGen());
                    probe.setOs_family(osScan.getOsFamily());
                    probe.setIp_addr(osScan.getIP());
                    probe.setPort_num(osScan.getOpenPort());
                    probe.setMac_addr("os-scan");
                    if(type.equals("insert")){
                        this.probeService.insert(probe);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void write(){
//
//        try {
//            // 方法1：使用 BufferedReader 读取文件内容
//            StringBuilder contentBuilder = new StringBuilder();
//            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//                String currentLine;
//                while ((currentLine = br.readLine()) != null) {
//                    contentBuilder.append(currentLine.trim());
//                }
//            }
//            String jsonString = contentBuilder.toString();
//
//            // 拆分字符串为单独的 JSON 对象
//            String[] jsonObjects = jsonString.split("\\}\\{");
//
//            // 修正每个 JSON 对象的格式
//            jsonObjects[0] += "}";
//            for (int i = 1; i < jsonObjects.length - 1; i++) {
//                jsonObjects[i] = "{" + jsonObjects[i] + "}";
//            }
//            jsonObjects[jsonObjects.length - 1] = "{" + jsonObjects[jsonObjects.length - 1];
//
//            // 创建 JSON 数组
//            List<OsScan> list = new ArrayList<>();
//            for (String jsonObjectStr : jsonObjects) {
//                OsScan osScan = null;
//                try {
//                    osScan = JSONObject.parseObject(jsonObjectStr, OsScan.class);
//                    list.add(osScan);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    list.clear();
//                    break;
//                }
//
//            }
//            // 遍历OsScan-写入到Probe
//            if(list.size() > 0){
//                Map params = new HashMap();
//                for (OsScan osScan : list) {
//                    // 更新probe
//                    params.clear();
//                    params.put("ip_addr", osScan.getIP());
//                    params.put("port_num", osScan.getOpenPort());
//                    List<Probe> probes = this.probeService.selectObjByMap(params);
//                    if(probes.size() > 0){
//                        for (Probe probe : probes) {
//                            probe.setTtl(osScan.getTtl());
//                            probe.setFingerId(osScan.getFingerID());
//                            probe.setVendor(osScan.getOsVendor());
//                            probe.setOs_gen(osScan.getOsGen());
//                            probe.setOs_family(osScan.getOsFamily());
//                            this.probeService.update(probe);
//                        }
//                    }
//                }
//            }
//
//            // 方法2：使用 Files 读取文件内容
//            // String jsonString = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
//
//            // 将 JSON 字符串转换为 Java 对象
////            ObjectMapper objectMapper = new ObjectMapper();
////            OsScan osScan = objectMapper.readValue(jsonString, OsScan.class);
////
////
////             打印 Java 对象
////            System.out.println(osScan);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//        try {
//            File file = new File(filePath);
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // 读取 JSON 文件并转换为 List<Person> 对象集合
//            List<OsScan> people = objectMapper.readValue(file, new TypeReference<List<OsScan>>() {});
//
//            // 打印每个 Person 对象
//            for (OsScan osScan : people) {
//                int i = this.osScanService.insert(osScan);
//                System.out.println(i);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
