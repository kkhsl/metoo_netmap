package com.metoo.sqlite.core.config.global;

import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-22 10:39
 */
@Slf4j
//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleException(Exception ex) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("error"); // 设置错误页面名称，例如 error.html
//        modelAndView.addObject("errorMessage", ex.getMessage()); // 可以添加自定义错误消息
//        return modelAndView;
//    }

    public void print(Exception ex){
        // 获取抛出异常的方法的全路径
        String methodFullName = ex.getStackTrace()[0].toString();

        // 这里可以做其他的异常处理逻辑
        // 例如，返回一个包含异常信息和方法全路径的 JSON 响应
        String errorMessage = "Exception occurred in method: " + methodFullName;

        log.error(errorMessage);
    }

    // 暂时注释
//    @ExceptionHandler(Exception.class)
//    public Result handleException(Exception ex) {
//        log.error(ex.getMessage(), ex);
//
//        this.print(ex);
//
//        return ResponseUtil.error();
//    }


    @ExceptionHandler(NumberFormatException.class)
    public Result handleNumberFormatException(NumberFormatException ex) {
        // 处理 NumberFormatException 异常
//        ResponseEntity<String>
//        return ResponseEntity.badRequest().body("Invalid data format: " + ex.getMessage());

        this.print(ex);

        return ResponseUtil.error();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleNumberFormatException(MissingServletRequestParameterException ex) {
//        ResponseEntity<String>
//        return ResponseEntity.badRequest().body("Invalid data format: " + ex.getMessage());

        this.print(ex);

        return ResponseUtil.badArgument();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleNumberFormatException(HttpRequestMethodNotSupportedException ex) {
//        ResponseEntity<String>
//        return ResponseEntity.badRequest().body("Invalid data format: " + ex.getMessage());

        this.print(ex);

        return ResponseUtil.badArgument("不支持的HTTP方法");
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result handleNumberFormatException(HttpMediaTypeNotSupportedException ex) {
//        ResponseEntity<String>
//        return ResponseEntity.badRequest().body("Invalid data format: " + ex.getMessage());

        this.print(ex);

        return ResponseUtil.badArgument("不支持的媒体类型");
    }


}