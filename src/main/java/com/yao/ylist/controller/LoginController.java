//package com.yao.ylist.controller;
//
///**
// * <p>Copyright: Copyright (c) 2023-2028</p>
// * <p>Company: dcits</p>
// *
// * @description: TODO
// * @author: yaowei
// * @date: 2025/1/10 11:34
// * @version: V0.1
// */
//
//import com.yao.ylist.model.User;
//import com.yao.ylist.service.UserListManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Map;
//
//@Controller
//public class LoginController {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserListManager userListManager;
//
//    @Autowired
//    public LoginController(AuthenticationManager authenticationManager, UserListManager userListManager) {
//        this.authenticationManager = authenticationManager;
//        this.userListManager = userListManager;
//    }
//
//    @PostMapping("/login")
//    public ModelAndView login(User user, HttpServletRequest request) {
//        ModelAndView mv = new ModelAndView();
//
//        // 使用 UserListManager 校验用户名和密码
//        String username = user.getUsername();
//        String password = user.getPassword();
//        Map<String, String> userCache = userListManager.getUserCache();
//
//        if (userCache.containsKey(username) && userCache.get(username).equals(password)) {
//            // 创建一个简单的 Authentication 对象
//            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            request.getSession().setAttribute("user", username);
//
//            mv.setViewName("redirect:/");
//        } else {
//            mv.addObject("error", "Invalid username or password");
//            mv.setViewName("login");
//        }
//
//        return mv;
//    }
//
//    @GetMapping("/login")
//    public ModelAndView login() {
//        return new ModelAndView("login");
//    }
//
//    @GetMapping("/index")
//    public ModelAndView index() {
//        return new ModelAndView("index");
//    }
//}
