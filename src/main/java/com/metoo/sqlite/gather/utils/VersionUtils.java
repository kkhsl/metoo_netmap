package com.metoo.sqlite.gather.utils;

import lombok.experimental.UtilityClass;

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
        // 测试版本号
        String[] versions = {"V1.2.3", "v2.0.0", "V10.1.1", "V1.0", "v1.2", "V1.2.3.4"};

        for (String version : versions) {
            System.out.println(version + " is valid: " + isValid(version));
        }
        String version1 = "V1.2.3";
        String version2 = "v1.2.10";

        int result = compare(version1, version2);
        if (result < 0) {
            System.out.println(version1 + " is older than " + version2);
        } else if (result > 0) {
            System.out.println(version1 + " is newer than " + version2);
        } else {
            System.out.println(version1 + " is the same version as " + version2);
        }
    }

}
