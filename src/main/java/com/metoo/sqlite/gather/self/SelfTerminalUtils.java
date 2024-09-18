package com.metoo.sqlite.gather.self;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.GatewayInfo;
import com.metoo.sqlite.entity.GatherLog;
import com.metoo.sqlite.entity.SurveyingLog;
import com.metoo.sqlite.entity.Terminal;
import com.metoo.sqlite.manager.utils.MacUtils;
import com.metoo.sqlite.manager.utils.jx.JXDataUtils;
import com.metoo.sqlite.service.IGatewayInfoService;
import com.metoo.sqlite.service.IGatherLogService;
import com.metoo.sqlite.service.ISurveyingLogService;
import com.metoo.sqlite.service.ITerminalService;
import com.metoo.sqlite.utils.date.DateTools;
import org.apache.tomcat.util.net.IPv6Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class SelfTerminalUtils {

    @Autowired
    private ITerminalService terminalService;
    @Autowired
    private JXDataUtils jxDataUtils;
    @Autowired
    private IGatherLogService gatherLogService;
    @Autowired
    private ISurveyingLogService surveyingLogService;
    @Autowired
    private IGatewayInfoService gatewayInfoService;

    public static void main(String[] args) throws UnknownHostException {

        String ipv6Subnet = getRandomItem(readLinesFromFileIpv6());
        int ipv6Mask = 64;
        int v6Number = 5;
        System.out.println(simplifyIPAddress(ipv6Subnet));
        List<String> ipv6List = generateIPv6Addresses2(normalizeIPv6(ipv6Subnet)+"/"+ipv6Mask, v6Number);
        System.out.println(ipv6List);
    }

//    终端数量：20-200
//    数量<30，比例70-90中随机，v6/(v4+v6)=比例   v6=比例（v4+v6）v6-比例*v6=比例*v4  v6*(1-比例)=比例*v4
//     v6/v4=比例/(1-比例)   v6+v4=数量
//    数量>30,  比例50-75中随机，后面一样
//    terminalinfo里面创建v4数量的条目，ipv4地址随机，ipv6地址为空，mac地址随机，os随机
//    创建v6数量的条目，ipv4地址随机，ipv6地址随机，mac地址随机，os随机

    //  v4=数量/(比例/(1-比例)+1)
    // v6

    public void main() {
        // 采集结果 - 开始时间
        String beginTime = DateTools.getCreateTime();
        this.careateSureyingLog("ipv4网段分析", beginTime, 1);
        this.updateSureyingLog("ipv4网段分析", 2);


        this.careateSureyingLog("ipv6网段分析", beginTime, 1);
        this.updateSureyingLog("ipv6网段分析", 2);

        this.careateSureyingLog("全网ping测试", beginTime, 1);
        this.updateSureyingLog("全网ping测试", 2);

        this.careateSureyingLog("全网资产扫描", beginTime, 1);






        // 生成终端
        try {

            int number = generateRandomNumber(30, 100);

            int v4Number = 0;
            int v6Number = 0;

            if (number <= 30) {
                // 生成 70 到 90 之间的随机浮点数并除以 100
                double rate = generateAndDivide(70, 90);
                // 计算 v4Number 并转换为 int
                v4Number = (int) (number / (rate / (1 - rate) + 1));
            } else {
                // 生成 50 到 75 之间的随机浮点数并除以 100
                double rate = generateAndDivide(50, 75);
                // 计算 v4Number 并转换为 int
                v4Number = (int) (number / (rate / (1 - rate) + 1));
            }

            // 计算 v6Number
            v6Number = (int)number - v4Number;

            // 取任意网段
            String ipv4Subnet = getRandomItem(readLinesFromFileIpv4());
            int ipv4Mask = 24;

            String ipv6Subnet = getRandomItem(readLinesFromFileIpv6());
            int ipv6Mask = 64;

            List<String> ipv4ListAll = generateIPAddresses(ipv4Subnet+"/"+ipv4Mask, 254);

            List<String> ipv4List = generateIPAddresses(ipv4Subnet+"/"+ipv4Mask, v4Number);


//            List<String> ipv4ListDedup = deduplicateLists(ipv4ListAll, ipv4List);
            ipv4ListAll.removeAll(ipv4List);

            InetAddress fullAddress = InetAddress.getByName(ipv6Subnet + "::");

            // Print the full address
            System.out.println("Full Address: " + fullAddress.getHostAddress());

            // Calculate network segment
            String networkSegment = calculateNetworkSegment(ipv6Subnet, ipv6Mask);

            List<String> ipv6List = RandomIPv6AddressGenerator.generateRandomIPv6Addresses(networkSegment, v6Number);

            List<String> osList = readLinesFromFileOs();


            this.updateSureyingLog("全网资产扫描", 2);


            this.careateSureyingLog("终端分析", beginTime, 1);
            this.terminalService.deleteTable();
            String createTime = DateTools.getCreateTime();
            for (String ipv4 : ipv4List) {
                Terminal terminal = new Terminal();
                terminal.setCreateTime(createTime);
                terminal.setIpv4addr(ipv4);
                terminal.setMac(MacUtils.getRandomMac());
                String os = getRandomItem(osList);
                terminal.setOs(os);
                this.terminalService.insert(terminal);
            }
            for (String ipv6 : ipv6List) {
                Terminal terminal = new Terminal();
                terminal.setCreateTime(createTime);
                terminal.setIpv4addr(getRandomItem(ipv4ListAll));
                terminal.setIpv6addr(ipv6);
                terminal.setMac(MacUtils.getRandomMac());
                String os = getRandomItem(osList);
                terminal.setOs(os);
                this.terminalService.insert(terminal);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.updateSureyingLog("终端分析", 2);


        this.careateSureyingLog("网络设备分析", beginTime, 1);
        this.updateSureyingLog("网络设备分析", 2);


        String data = this.jxDataUtils.getEncryptedData();

        this.gatherUploadData(data);// 数据上传


        String surveying = this.getSurveyingResult();

        String endTime = DateTools.getCreateTime();

        try {
            GatherLog gatherLog = new GatherLog();
            gatherLog.setCreateTime(beginTime);
            gatherLog.setBeginTime(beginTime);
            gatherLog.setEndTime(endTime);
            gatherLog.setType("手动");
            gatherLog.setResult("成功");
            gatherLog.setDetails(surveying);
            gatherLog.setData(data);
            this.gatherLogService.insert(gatherLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String calculateNetworkSegment(String address, int prefixLength) throws UnknownHostException {

        // Assuming prefixLength is 64 for simplicity
        // Create a 128-bit bit array from the address
        String fullAddress = address + "::";
        String[] addressParts = fullAddress.split(":");
        StringBuilder networkSegment = new StringBuilder();

        // Append the first 4 blocks (64 bits)
        for (int i = 0; i < 4; i++) {
            networkSegment.append(addressParts[i]);
            if (i < 3) {
                networkSegment.append(":");
            }
        }

        // Append the rest of the blocks as zeros
        networkSegment.append(":").append("0000:0000:0000:0000");

        return networkSegment.toString() + "/" + prefixLength;
    }

    public void gatherUploadData(String data){
        String beginTime = DateTools.getCreateTime();
        this.careateSureyingLog("生成加密结果文件", beginTime, 1);

        try {
            Thread.sleep(1);//0000
            String endTime = DateTools.getCreateTime();
            this.updateSureyingLog("生成加密结果文件", endTime, 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 休眠时间
//        try {
//            // 调用kafka
//            SendKafkaMsg sendKafkaMsg = new SendKafkaMsg();
//            boolean flag =sendKafkaMsg.send(data);
//
//            Thread.sleep(10000);
//            if(flag){
//                String endTime = DateTools.getCreateTime();
//                this.updateSureyingLog("数据上传", endTime, 2);
//
//            }else{
//                String endTime = DateTools.getCreateTime();
//                this.updateSureyingLog("数据上传", endTime, 3);
//
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//
//            String endTime = DateTools.getCreateTime();
//            this.updateSureyingLog("数据上传", endTime, 3);
//        }
    }

    public void careateSureyingLog(String name, String beginTime, Integer status){
        SurveyingLog surveyingLog = new SurveyingLog().createTime(DateTools.getCreateTime()).name(name).beginTime(beginTime).status(status);
        this.surveyingLogService.insert(surveyingLog);
    }

    public void updateSureyingLog(String name, Integer status){

        String endTime = DateTools.getCreateTime();

        this.updateSureyingLog(name, endTime, status);

    }

    public void updateSureyingLog(String name, String endTime, Integer status){

        try {
            Thread.sleep(10000); // 手动设置测绘休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = DateTools.getCreateTime();

        Map params = new HashMap();
        params.put("name", name);
        List<SurveyingLog> surveyingLogs = this.surveyingLogService.selectObjByMap(params);
        if(surveyingLogs.size() > 0){
            SurveyingLog surveyingLog = surveyingLogs.get(0).endTime(endTime).status(status);
            this.surveyingLogService.update(surveyingLog);
        }


        try {
            Thread.sleep(200); // 手动设置测绘休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    // 获取测绘结果，采集格式化后的数据
    public String getSurveyingResult(){
        Map details = new HashMap();
        try {
            details.put("subme_ipv4", new ArrayList<>());
            details.put("subme_ipv6", new ArrayList<>());
            List<GatewayInfo> gatewayInfoList = this.gatewayInfoService.selectObjByMap(null);
            details.put("gatewayInfo", gatewayInfoList);
            Map params = new HashMap();
            params.clear();
            params.put("ipv4IsNotNull", "ipv4addr");
            List<Terminal> terminal_ipv4 = this.terminalService.selectObjByMap(params);
            params.clear();
            params.put("ipv4AndIpv6IsNotNull", "ipv6addr");
            List<Terminal> teminal_ipv6 = this.terminalService.selectObjByMap(params);
            details.put("ipv4", terminal_ipv4.size());
            details.put("ipv4_ipv6", teminal_ipv6.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.toJSONString(details);
    }

    /**
     * 生成在 min 和 max 之间的随机整数，然后除以 100
     */
    public static double generateAndDivide(int min, int max) {
        Random random = new Random();
        int randomInt = random.nextInt((max - min + 1)) + min; // 生成[min, max]范围内的随机整数
        return randomInt / 100.0; // 除以 100
    }

    /**
     * 将数字格式化为保留两位小数
     */
    public static String formatToTwoDecimalPlaces(double number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);
    }

    /**
     * 去重两个 List<String>
     */
    public static List<String> deduplicateLists(List<String> list1, List<String> list2) {
        Set<String> set = new HashSet<>();

        // 添加第一个 List 到 Set
        set.addAll(list1);

        // 添加第二个 List 到 Set
        set.addAll(list2);

        // 将 Set 转换回 List
        return new ArrayList<>(set);
    }

    /**
     * 根据网段生成指定数量的 IPv6 地址
     */
    public static List<String> generateIPv6Addresses2(String networkPrefix, int count) throws UnknownHostException {
        List<String> ipList = new ArrayList<>();
        String[] parts = networkPrefix.split("/");
        String network = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        InetAddress networkAddress = InetAddress.getByName(network);
        byte[] networkBytes = networkAddress.getAddress();

        BigInteger start = new BigInteger(1, networkBytes);
        BigInteger end = start.add(BigInteger.ONE.shiftLeft(128 - prefixLength).subtract(BigInteger.ONE));

        Random random = new Random();
        for (int i = 0; i < count; i++) {
            BigInteger randomOffset = new BigInteger(128 - prefixLength, random);
            BigInteger ip = start.add(randomOffset);

            if (ip.compareTo(end) > 0) {
                ip = end.subtract(BigInteger.ONE); // Ensure the IP is within the range
            }

            byte[] ipBytes = ip.toByteArray();
            // Ensure the byte array length is 16 bytes (IPv6)
            if (ipBytes.length < 16) {
                byte[] paddedIpBytes = new byte[16];
                System.arraycopy(ipBytes, 0, paddedIpBytes, 16 - ipBytes.length, ipBytes.length);
                ipBytes = paddedIpBytes;
            }
            InetAddress ipAddress = InetAddress.getByAddress(ipBytes);
//            ipList.add(ipAddress.getHostAddress());
//            ipList.add(formatIPv6Address(ipAddress.getHostAddress()));
            ipList.add(simplifyIPAddress(ipAddress.getHostAddress()));

        }

        return ipList;
    }

    public static String simplifyIPAddress(String ipAddress) {
        StringBuilder sb = new StringBuilder();
        boolean isFirstNonZero = true;
        for (String part : ipAddress.split(":")) {
            if (!part.isEmpty()) {
                if (!part.startsWith("0")) {
                    if (!isFirstNonZero) {
                        sb.append(":");
                    }
                    sb.append(part);
                    isFirstNonZero = false;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 格式化 IPv6 地址，去掉前导零并进行精简
     */
    public static String formatIPv6Address(String address) {
        // Remove leading zeros
        String[] parts = address.split(":");
        StringBuilder formattedAddress = new StringBuilder();

        boolean zeroSequence = false;
        for (String part : parts) {
            if (part.equals("0000")) {
                zeroSequence = true;
                if (formattedAddress.length() > 0) {
                    formattedAddress.append(":");
                }
            } else {
                if (zeroSequence) {
                    formattedAddress.append("::");
                    zeroSequence = false;
                } else if (formattedAddress.length() > 0) {
                    formattedAddress.append(":");
                }
                formattedAddress.append(Integer.parseInt(part, 16));
            }
        }

        // If there was no "::" in the address, add it if there are zero segments
        if (formattedAddress.length() == 0 && address.equals("0:0:0:0:0:0:0:0")) {
            formattedAddress.append("0");
        }

        return formattedAddress.toString();
    }

    /**
     * 根据网段生成 IPv6 地址列表
     */
    public static List<String> generateIPv6Addresses(String networkPrefix) throws UnknownHostException {
        List<String> ipList = new ArrayList<>();
        String[] parts = networkPrefix.split("/");
        String network = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        InetAddress networkAddress = InetAddress.getByName(network);
        byte[] networkBytes = networkAddress.getAddress();

        BigInteger start = new BigInteger(1, networkBytes);
        BigInteger end = start.add(BigInteger.ONE.shiftLeft(128 - prefixLength).subtract(BigInteger.ONE));

        for (BigInteger ip = start; ip.compareTo(end) <= 0; ip = ip.add(BigInteger.ONE)) {
            byte[] ipBytes = ip.toByteArray();
            // Ensure the byte array length is 16 bytes (IPv6)
            if (ipBytes.length < 16) {
                byte[] paddedIpBytes = new byte[16];
                System.arraycopy(ipBytes, 0, paddedIpBytes, 16 - ipBytes.length, ipBytes.length);
                ipBytes = paddedIpBytes;
            }
            InetAddress ipAddress = InetAddress.getByAddress(ipBytes);
            ipList.add(ipAddress.getHostAddress());
        }

        return ipList;
    }

    /**
     * 根据网段生成指定数量的 IPv6 地址列表
     *
     * @param network 网段（例如 "2001:db8::/32"）
     * @param count   需要生成的 IP 地址数量
     * @return IPv6 地址列表
     * @throws UnknownHostException 如果解析 IP 地址失败
     */
    public static List<String> generateIPv6Addresses(String network, int count) throws UnknownHostException {
        String[] parts = network.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid network format.");
        }

        InetAddress networkAddress = InetAddress.getByName(parts[0]);
        int prefixLength = Integer.parseInt(parts[1]);
        if (networkAddress.getAddress().length != 16) {
            throw new IllegalArgumentException("Invalid IPv6 address.");
        }

        // 计算网络地址的起始和结束
        BigInteger networkStart = ipToBigInteger(networkAddress.getAddress());
        BigInteger networkEnd = networkStart.add(BigInteger.valueOf(1L << (128 - prefixLength)).subtract(BigInteger.ONE));

        List<String> ipAddresses = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            BigInteger randomOffset = new BigInteger(128 - prefixLength, random);
            BigInteger ip = networkStart.add(randomOffset);
            if (ip.compareTo(networkEnd) > 0) {
//                throw new IllegalArgumentException("Requested IP addresses exceed the range.");
            }
            ipAddresses.add(bigIntegerToIPv6(ip));
        }

        return ipAddresses;
    }

    /**
     * 将字节数组转换为 BigInteger
     */
    private static BigInteger ipToBigInteger(byte[] ip) {
        return new BigInteger(1, ip);
    }

    /**
     * 将 BigInteger 转换为 IPv6 字符串
     */
    private static String bigIntegerToIPv6(BigInteger ip) {
        byte[] bytes = ip.toByteArray();
        // 补齐到 16 字节
        byte[] ipv6Bytes = new byte[16];
        System.arraycopy(bytes, 0, ipv6Bytes, 16 - bytes.length, bytes.length);
        try {
            InetAddress address = InetAddress.getByAddress(ipv6Bytes);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Error converting BigInteger to IPv6 address", e);
        }
    }

    /**
     * 根据网段生成指定数量的 IP 地址列表
     *
     * @param network 网段（例如 "192.168.1.0/24"）
     * @param count   需要生成的 IP 地址数量
     * @return IP 地址列表
     * @throws UnknownHostException 如果解析 IP 地址失败
     */
    public static List<String> generateIPAddresses(String network, int count) throws UnknownHostException {
        String[] parts = network.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid network format. Expected 'IP/PrefixLength'.");
        }

        String ipStr = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // 计算网络地址和广播地址
        InetAddress ip = InetAddress.getByName(ipStr);
        byte[] address = ip.getAddress();

        // 计算掩码和网络起始地址
        int mask = 0xFFFFFFFF << (32 - prefixLength);
        int networkAddress = ipToInt(address) & mask;
        int broadcastAddress = networkAddress | ~mask;

        // 生成 IP 地址
        List<String> ipAddresses = new ArrayList<>();
        Random random = new Random();
        while (ipAddresses.size() < count) {
            int ipp = networkAddress + 1 + random.nextInt(broadcastAddress - networkAddress - 1);
            ipAddresses.add(intToIp(ipp));
        }

        return ipAddresses;
    }

    private static int ipToInt(byte[] address) {
        return ((address[0] & 0xFF) << 24) | ((address[1] & 0xFF) << 16) |
                ((address[2] & 0xFF) << 8) | (address[3] & 0xFF);
    }

    private static String intToIp(int ip) {
        return (ip >>> 24) + "." + ((ip >>> 16) & 0xFF) + "." + ((ip >>> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    /**
     * 从列表中随机选择一条数据
     *
     * @param list 原始列表
     * @return 随机选择的一条数据
     */
    public static String getRandomItem(List<String> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("The list cannot be empty.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    /**
     * 从列表中随机选择 N 条数据
     *
     * @param list 原始列表
     * @param n 需要选择的数量
     * @return 随机选择的 N 条数据
     */
    public static List<String> getRandomItems(List<String> list, int n) {
        if (n > list.size()) {
            throw new IllegalArgumentException("Number of items to select cannot be greater than the size of the list.");
        }

        // 创建列表的副本以避免修改原始列表
        List<String> copy = new ArrayList<>(list);

        // 打乱副本列表
        Collections.shuffle(copy);

        // 选择前 N 条数据
        return copy.subList(0, n);
    }

//    public static List<String> readLinesFromFileIpv4(){
//        // 文件路径，假设文件在项目的 resources/gather 目录下
//        String filePath = "src/main/resources/gather/ipv4.txt";
//
//        try {
//            // 读取文件内容按行存储到 List<String> 中
//            List<String> lines = Files.readAllLines(Paths.get(filePath));
//
//            // 打印每一行内容
////            for (String line : lines) {
////                System.out.println(line);
////            }
//            return lines;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    @Test
    public void readLinesFromFileIpv4Test(){
        List<String> ip = readLinesFromFileIpv4();
        System.out.println(ip);
    }

    public static List<String> readLinesFromFileIpv4() {
        List<String> lines = new ArrayList<>();
//        try (InputStream inputStream = FileReader.class.getResourceAsStream("/gather/ipv4.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        try (InputStream inputStream = SelfTerminalUtils.class.getClassLoader().getResourceAsStream("gather/ipv4.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


//    public static List<String> readLinesFromFileIpv6(){
//        // 文件路径，假设文件在项目的 resources/gather 目录下
//        String filePath = "src/main/resources/gather/ipv6.txt";
//
//        try {
//            // 读取文件内容按行存储到 List<String> 中
//            List<String> lines = Files.readAllLines(Paths.get(filePath));
//
//            // 打印每一行内容
////            for (String line : lines) {
////                System.out.println(line);
////            }
//            return lines;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    public static List<String> readLinesFromFileIpv6() {
        List<String> lines = new ArrayList<>();
//        try (InputStream inputStream = FileReader.class.getResourceAsStream("/gather/ipv6.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

        try (InputStream inputStream = SelfTerminalUtils.class.getClassLoader().getResourceAsStream("gather/ipv6.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

//    public static List<String> readLinesFromFileOs(){
//        // 文件路径，假设文件在项目的 resources/gather 目录下
//        String filePath = "src/main/resources/gather/os.txt";
//
//        try {
//            // 读取文件内容按行存储到 List<String> 中
//            List<String> lines = Files.readAllLines(Paths.get(filePath));
//
//            // 打印每一行内容
////            for (String line : lines) {
////                System.out.println(line);
////            }
//            return lines;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }

    public static List<String> readLinesFromFileOs() {
        List<String> lines = new ArrayList<>();
//        try (InputStream inputStream = FileReader.class.getResourceAsStream("/gather/os.txt");
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {


        try (InputStream inputStream = SelfTerminalUtils.class.getClassLoader().getResourceAsStream("gather/os.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    /**
     * 生成一个在指定范围内的随机整数
     *
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 范围内的随机整数
     */
    public static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        // 生成范围内的随机整数，公式是 min + (max - min + 1) * random.nextInt()
        return min + random.nextInt(max - min + 1);
    }

    @Test
    public void test(){
        System.out.println(normalizeIPv6("240e:670:3e02:6"));
    }


    /**
     * 标准化 IPv6 地址
     */
    private static String normalizeIPv6(String address) {
        try {
            InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            // 如果地址格式不正确，则手动补全
            return expandIPv6(address);
        }
    }

    /**
     * 手动扩展 IPv6 地址
     */
    private static String expandIPv6(String address) {
        // 替换 "::" 为合适的零块
        if (address.contains("::")) {
            String[] parts = address.split("::");
            String left = parts[0];
            String right = parts.length > 1 ? parts[1] : "";

            String[] leftParts = left.split(":");
            String[] rightParts = right.split(":");

            String[] expanded = new String[8];
            int leftLength = leftParts.length;
            int rightLength = rightParts.length;
            int zerosToFill = 8 - (leftLength + rightLength);

            // 填充左边和右边
            for (int i = 0; i < leftLength; i++) {
                expanded[i] = leftParts[i];
            }
            for (int i = 0; i < zerosToFill; i++) {
                expanded[leftLength + i] = "0000";
            }
            for (int i = 0; i < rightLength; i++) {
                expanded[leftLength + zerosToFill + i] = rightParts[i];
            }

            // 格式化为完整的 IPv6 地址
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < expanded.length; i++) {
                if (i > 0) sb.append(":");
                sb.append(expanded[i]);
            }
            return sb.toString();
        }

        // 处理没有 "::" 的地址
        String[] parts = address.split(":");
        String[] expanded = new String[8];
        int length = parts.length;

        for (int i = 0; i < 8; i++) {
            if (i < 8 - length) {
                expanded[i] = "0000";
            } else {
                expanded[i] = parts[i - (8 - length)];
            }
        }

        // 格式化为完整的 IPv6 地址
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expanded.length; i++) {
            if (i > 0) sb.append(":");
            sb.append(expanded[i]);
        }
        return sb.toString();
    }
}

class VerifyIpv6{

    public static void main(String[] args) {
        try {
            String[] addresses = {
                    "240e:670:3e02:6"
            };

            for (String address : addresses) {
                if (isValidIPv6(address)) {
                    InetAddress inetAddress = InetAddress.getByName(address);
                    System.out.println("Standardized address: " + inetAddress.getHostAddress());
                } else {
                    System.out.println("Invalid IPv6 address: " + address);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4}|:)|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|:" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|:" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|:" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|:" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|:" +
                    ":((:[0-9a-fA-F]{1,4}){1,6}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}" +
                    "((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){1,3}" +
                    "(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,4}"
    );

    /**
     * 验证 IPv6 地址格式
     */
    private static boolean isValidIPv6(String address) {
        return IPV6_PATTERN.matcher(address).matches();
    }
}
