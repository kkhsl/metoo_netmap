package com.metoo.sqlite.manager.api.remote;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务参数封装
 * @author zzy
 * @version 1.0
 * @date 2024/09/20 21:46
 */
@Data
public class BusiParamDto {

    /**
     * 请求方法
     */
    private String httpMethod;

    /** 查询参数 **/
    private Map<String,Object> paramMap = new HashMap<>(8);

    /**
     * 添加参数
     * @param paramName 参数名称
     * @param value 参数值
     */
    public void addParam(String paramName,Object value){
        paramMap.put(paramName,value);
    }


}
