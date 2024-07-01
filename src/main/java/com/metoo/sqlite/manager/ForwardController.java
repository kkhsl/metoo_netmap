package com.metoo.sqlite.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-27 15:23
 */

/**
 * 使其将所有未匹配的请求都重定向到前端的 index.html。
 * 这样，Vue Router 可以处理客户端的路由，而不是让服务器返回 404 错误
 *
 * {path}：路径变量，可以是任意名称。
 * ^(?!admin)：负向先行断言，确保路径不以 "admin" 开头。
 * [^\\.]：匹配任意字符，除了点 (.)。
 * *：匹配零个或多个前面的字符（在这里是零个或多个非点字符）。
 * $：匹配字符串的结束。
 */
@Controller
public class ForwardController {

//    @RequestMapping(value = "/{path:^(?!admin).*[^\\.]*$}")
//    @RequestMapping(value = "/{path:^(?!admin)(?!.*\\..*$).*$}")
//
    @RequestMapping(value = "/{path:[^\\\\.]*}")
//    @RequestMapping(value = "/{path:^(?!admin)[^\\.]*$}")
    public String redirect() {
        // Forward to home page so that route is preserved.
        return "forward:/index.html";
    }
}

