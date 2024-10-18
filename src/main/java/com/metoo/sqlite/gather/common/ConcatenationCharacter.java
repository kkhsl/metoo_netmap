package com.metoo.sqlite.gather.common;

import com.github.pagehelper.util.StringUtil;

import java.util.StringJoiner;

public class ConcatenationCharacter {

    public static String concatenateWithSpace(String type, String... parts) {

        // 使用 StringJoiner 以空格为分隔符进行拼接
        StringJoiner joiner = new StringJoiner(type);

        // 遍历可变参数并添加到 StringJoiner 中
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                joiner.add(part);
            }
        }

        // 返回拼接后的结果
        return joiner.toString();
    }

    public static String disassembleWithSpace(String type, String parts) {
        if(StringUtil.isNotEmpty(parts)){
            // 使用逗号分隔字符串
            String[] splits = parts.split(type);

            if(splits.length > 0){
                return splits[0];
            }
        }
        return "";
    }

    public static void main(String[] args) {
        String vendor = "Hewlett Packard";
        String os_gen = "";
        String os_family = "Windows";

        // 调用方法传递多个参数
        String result = concatenateWithSpace(",", vendor, os_gen, os_family);

        // 输出拼接结果
        System.out.println(result);  // 输出: Hewlett Packard Windows Extra Data

        result = disassembleWithSpace(",", result);
        System.out.println(result);
    }

}
