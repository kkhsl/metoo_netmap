package com.metoo.sqlite.utils.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WindowsUniqueID {

    // 获取硬件序列号，优先顺序：硬盘 -> CPU -> 主板
    public static String getUniqueHardwareSerialNumber() {
        // 1. 尝试获取硬盘序列号
        String serialNumber = getDiskSerialNumber();
        if (serialNumber != null) {
            return serialNumber;
        }

        // 2. 如果硬盘信息不可用，尝试获取 CPU 序列号
        serialNumber = getCPUSerialNumber();
        if (serialNumber != null) {
            return serialNumber;
        }

        // 3. 如果 CPU 信息不可用，尝试获取主板序列号
        return getMotherboardSerialNumber();
    }

    // 获取硬盘序列号
    private static String getDiskSerialNumber() {
        List<String> serialNumbers = executeCommand("wmic diskdrive get serialnumber", "SerialNumber");
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            // 如果 `wmic` 获取失败，尝试 PowerShell
            serialNumbers = executeCommand("powershell -command \"Get-PhysicalDisk | Select-Object -First 1 SerialNumber\"", null);
        }
        return cleanSerialNumber(serialNumbers);
    }

    // 获取 CPU 序列号
    private static String getCPUSerialNumber() {
        List<String> serialNumbers = executeCommand("wmic cpu get processorid", "ProcessorId");
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            // 如果 `wmic` 获取失败，尝试 PowerShell
            serialNumbers = executeCommand("powershell -command \"Get-WmiObject Win32_Processor | Select-Object -ExpandProperty ProcessorId\"", null);
        }
        return cleanSerialNumber(serialNumbers);
    }

    // 获取主板序列号
    private static String getMotherboardSerialNumber() {
        List<String> serialNumbers = executeCommand("wmic baseboard get serialnumber", "SerialNumber");
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            // 如果 `wmic` 获取失败，尝试 PowerShell
            serialNumbers = executeCommand("powershell -command \"Get-WmiObject win32_baseboard | Select-Object -ExpandProperty SerialNumber\"", null);
        }
        return cleanSerialNumber(serialNumbers);
    }

    // 清理数据，去除空行和多余字符，只保留有用的序列号
    private static String cleanSerialNumber(List<String> serialNumbers) {
        for (String serialNumber : serialNumbers) {
            serialNumber = serialNumber.trim();
            if (!serialNumber.isEmpty() && serialNumber.length() > 5 && !serialNumber.contains("_")) {
                return serialNumber; // 返回符合条件的硬件序列号
            }
        }
        return null;
    }

    // 通用命令执行方法，支持 wmic 和 PowerShell
    private static List<String> executeCommand(String command, String skipLineStart) {
        List<String> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && (skipLineStart == null || !line.startsWith(skipLineStart))) {
                    result.add(line); // 收集所有结果
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        // 调用方法获取系统唯一硬件序列号
        String uniqueSerialNumber = getUniqueHardwareSerialNumber();
        System.out.println("Unique Hardware Serial Number: " + uniqueSerialNumber);
    }

}
