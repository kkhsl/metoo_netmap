package com.metoo.sqlite.manager.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 14:56
 */
@Component
public class VerifyUtils {


    // 正则表达式，可以根据需要调整
    private static final String PHONE_NUMBER_PATTERN = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);

    // 验证手机号码格式是否正确
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


    // 邮箱验证的正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    // 验证邮箱格式
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
