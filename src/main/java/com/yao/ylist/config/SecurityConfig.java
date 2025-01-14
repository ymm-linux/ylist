package com.yao.ylist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Override
   protected void configure(HttpSecurity http) throws Exception {
//       http.authorizeRequests()
//               .antMatchers("/login", "/public/**").permitAll() // 允许所有用户访问登录页面和公共资源
//               .anyRequest().authenticated() // 其他所有请求都需要认证
//               .and()
//           .formLogin()
//               .loginPage("/login") // 指定登录页面
//               .permitAll()
//               .and()
//           .logout()
//               .permitAll();
       http.authorizeRequests().anyRequest().permitAll() // 允许所有请求
               .and()
               .csrf().disable(); // 禁用 CSRF 保护
   }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("username") // 用户名
//                .password(passwordEncoder().encode("password")) // 密码，使用BCryptPasswordEncoder加密
//                .roles("USER"); // 角色
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    @Bean
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
}
