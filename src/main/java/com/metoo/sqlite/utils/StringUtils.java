package com.metoo.sqlite.utils;

import org.junit.Test;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-19 11:38
 */
public class StringUtils {

    public static void main(String[] args) {
        String str = "";

        System.out.println(isEmpty(str));

        System.out.println(isNotEmpty(str));
    }

    /** 空字符串 */
    private static final String NULLSTR = "";

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str)
    {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    public static boolean isNonEmptyAndTrimmed(String str) {
        return str != null && !str.trim().isEmpty();
    }


    @Test
    public void toUpperCaseLetters() {
        String input = "ac:67:5d";
        String result = toUpperCaseLetters(input);
        System.out.println(result); // 输出: JAVA AC:67:5D
    }

    public static String toUpperCaseLetters(String str) {
        StringBuilder result = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
