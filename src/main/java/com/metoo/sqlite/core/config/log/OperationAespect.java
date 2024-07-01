package com.metoo.sqlite.core.config.log;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.OperationLog;
import com.metoo.sqlite.model.LoginBody;
import com.metoo.sqlite.service.IOperationLogService;
import com.metoo.sqlite.utils.date.DateTools;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-01-27 14:24
 */
@Aspect
@Component
public class OperationAespect {

    public static void main(String[] args) {
        OperationType color = OperationType.CREATE;

        System.out.println("枚举常量的中文名称：" + color.getChineseName());
        System.out.println("枚举常量的中文名称：" + OperationType.getChineseName(color));

    }

    @Autowired
    private IOperationLogService operationLogService;

    @Pointcut("@annotation(com.metoo.sqlite.core.config.log.OperationLogAnno)")
    private void cutMethod() {
    }

    @After("cutMethod() && @annotation(operationLogAnno) && args(reques,..)" +
            " && execution(* com.metoo.sqlite.manager.Login2ManagerController.*(..))")
//    @Before("cutMethod() && @annotation(operationLogAnno) && args(reques,..)")
    public void device(JoinPoint joinPoint,
                       OperationLogAnno operationLogAnno, LoginBody reques) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            OperationLog instance = new OperationLog();
            instance.setCreateTime(DateTools.getCreateTime());
            instance.setIp(Ipv4Utils.getRealIP(request));
            instance.setAction(OperationType.getChineseName(operationLogAnno.operationType()));
            instance.setDesc(operationLogAnno.name());
            instance.setType(operationLogAnno.operateType());

            if(reques != null){
                LoginBody loginBody = JSONObject.parseObject(JSONObject.toJSONString(reques), LoginBody.class);
                instance.setAction(loginBody.getUsername());
                instance.setName(loginBody.getUsername());
            }
            System.out.println(instance);
            this.operationLogService.insert(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Object[] args2 = joinPoint.getArgs();
//        // 假设第一个参数是请求体对象
//        if (args2.length > 0) {
//            Object requestBody = args2[0];
//            System.out.println("Request Body: " + requestBody.toString());
//        }
//
//        System.out.println(reques);
//
//        // 获取方法名
//        String methodName = joinPoint.getSignature().getName();
//        // 反射获取目标类
//        Class<?> targetClass = joinPoint.getTarget().getClass();
//
//        Signature signature = joinPoint.getSignature();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        Method method = methodSignature.getMethod();
//
//        System.out.println(operationLogAnno.name());
//        //4. 获取方法的参数 一一对应
//        Object[] args = joinPoint.getArgs();
//        if(args.length > 0){
//            if(!operationLogAnno.name().equals("")){
//                System.out.println(args[0]);
//            }
//
//        }
    }

}
