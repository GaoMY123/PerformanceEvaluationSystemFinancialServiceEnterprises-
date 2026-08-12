package com.performance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.performance.common.Result;
import com.performance.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    /** 密码编码器 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 认证管理器 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** 安全过滤器链配置 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF（使用JWT无需CSRF防护）
            .csrf().disable()
            // 不使用Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 允许匿名访问的接口
            .antMatchers("/api/auth/login", "/api/auth/register", "/api/auth/findPassword").permitAll()
            // Knife4j/Swagger文档
            .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs/**").permitAll()
            // 静态资源和文件下载
            .antMatchers("/uploads/**").permitAll()
            // 注册页需要获取部门列表（公开信息）
            .antMatchers("/api/department/list").permitAll()
            // OPTIONS预检请求
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            // 其他请求需要认证
            .anyRequest().authenticated()
            .and()
            // 未认证处理
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) -> {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(401);
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或Token已过期")));
            })
            // 权限不足处理
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(403);
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "权限不足")));
            });

        // 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
