package com.metoo.sqlite.manager.api.remote;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * 接口调用基类
 * @author zzy
 * @version 1.0
 * @date 2024/09/20 20:21
 */
@Slf4j
public abstract class BaseCall {


    /** restful 服务调用 **/
    protected   RestTemplate restTemplate;

    /** 请求协议 **/
    private final static String HTTP="http://";

    /** 请求方式 post **/
    private final static String HTTP_METHOD_POST = "POST";

    /**
     * 构造函数
     * @param restTemplate
     */
    public BaseCall(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    /**
     * 执行接口调用
     * @param paramDto
     * @return
     */
    public String call(BusiParamDto paramDto){

        String result = "";
        //获取请求头
        HttpHeaders headers = getHeaders();
        Map<String, Object> requestBody= null;
        //请求地址
        String url = getUrl();
        //请求参数
        Map<String, Object> maps = addDefaultParam(paramDto.getParamMap());
        //判断请求方式
        HttpMethod httpMethod = getHttpMethod(paramDto.getHttpMethod());
        //处理body信息
        requestBody =  buildBody(httpMethod,requestBody,maps);
        HttpEntity<Map> formEntity = new HttpEntity<>(requestBody, headers);
        try {
            //get方式需要对参数进行处理
            url = (httpMethod == HttpMethod.GET)?handleUrlParam(url,HttpMethod.GET,maps):url;
            //请求实现
            result=  executeCall(url,httpMethod,formEntity,maps);

        } catch (Exception e) {
           log.error("调用接口异常，请求参数信息：路径【"+url+"】参数【"+JSONUtil.parse(maps)+"】head【"+JSONUtil.parse(formEntity)+"】",e);
        }

        return result;
    }


    /**
     * 针对GET类型对url参数进行处理
     * @param url
     * @param method
     * @param maps
     * @return
     */
    private String handleUrlParam(String url, HttpMethod method,Map<String, Object> maps){

        if(method == HttpMethod.GET && CollectionUtil.isNotEmpty(maps)){
            StringBuilder sb = new StringBuilder(url+"?");
            maps.forEach((k,v)->{
                sb.append(k).append("=").append(v).append("&");
            });
            url = sb.toString();
            url = url.substring(0,url.length()-1);
        }

        return url;
    }



    /**
     * 添加默认参数
     * @param maps
     * @return
     */
    public abstract Map<String, Object> addDefaultParam(Map<String, Object> maps);

    /**
     * 执行调用
     * @param url
     * @param method
     * @param requestEntity
     * @param maps
     * @return
     */
    public abstract String executeCall(String url, HttpMethod method,HttpEntity requestEntity,  Map<String, Object> maps) throws URISyntaxException;

    /**
     * 获取请求头
     * @return
     */
    public abstract HttpHeaders getHeaders();


    /**
     * 获取请求地址
     * @return
     */
    public abstract String getUrl();


    /**
     * 构造请求BODY
     * @param httpMethod
     * @param requestBody
     * @param maps
     * @return
     */
    public abstract Map<String, Object> buildBody(HttpMethod httpMethod,Map<String, Object> requestBody,Map<String, Object> maps );

    /**
     * 获取请求方式
     * @param httpMethod
     * @return
     */
    private HttpMethod getHttpMethod(String httpMethod){

        if(StrUtil.isBlank(httpMethod)){
            return HttpMethod.GET;
        }else if(HTTP_METHOD_POST.equalsIgnoreCase(httpMethod)){
            return HttpMethod.POST;
        }else {
            return HttpMethod.GET;
        }
    }



}
