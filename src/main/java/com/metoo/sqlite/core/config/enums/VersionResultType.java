package com.metoo.sqlite.core.config.enums;

/**
 * 客户端版本升级执行结果类型枚举类
 * @author zzy
 */
public enum VersionResultType {
    SUCCESS(0,"升级成功"),
    FAIL(1, "升级失败"),
    DOING(2,"版本正在更新"),
    STATUS(3,"当前状态不允许更新"),
    ERROR(4,"数据错误"),
    NO(5,"数据错误");

    private Integer code;

    private String value;

    private VersionResultType(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code){
        for (VersionResultType type : VersionResultType.values()) {
            if (type.getCode().equals(code)) {
                return type.getValue();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
