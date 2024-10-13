package com.metoo.sqlite.gather.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * 版本工具类
 * @author zzy
 * @version 1.0
 * @date 2024/9/19 11:51
 */
@UtilityClass
public class VersionUtils {
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("^V\\d+\\.\\d+\\.\\d+$", Pattern.CASE_INSENSITIVE);

    /**
     * 版本号比较
     * @param version1
     * @param version2
     * @return
     */
    public int compare(String version1, String version2) {
        String[] parts1 = version1.replaceAll("[Vv]","").split("\\.");
        String[] parts2 = version2.replaceAll("[Vv]","").split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (part1 < part2) {
                return -1;
            } else if (part1 > part2) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * 版本号校验
     * @param version
     * @return
     */
    public  boolean isValid(String version) {
        return VERSION_PATTERN.matcher(version).matches();
    }
    public static void main(String[] args) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Void> task = () -> {
                for (int i = 0; i < 100; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Task interrupted!");
                        return null;
                    }
                    try {
                        Thread.sleep(100); // 模拟长时间运行的任务
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 保留中断状态
                        System.out.println("Sleep interrupted!");
                        return null;
                    }
                    System.out.println("Running...");
                }
                return null;
            };

            Future<Void> future = executor.submit(task);

            // 模拟一段时间后中断任务
        try {
            Thread.sleep(3000);
            future.cancel(true); // 尝试中断任务
            // 等待任务完成，这里主要是为了演示，实际使用中可能需要更复杂的逻辑
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

            executor.shutdown();
    }
}
