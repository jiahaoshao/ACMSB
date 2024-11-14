package net.fangyi.acmsb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AddResponseHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.addHeader("X-Frame-Options", "DENY");
        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        httpServletResponse.addHeader("Cache-Control", "no-cache='set-cookie'");
        httpServletResponse.addHeader("Pragma", "no-cache");
        System.out.println("SessionId:" + httpServletRequest.getSession().getId());
        //支持跨域请求
        httpServletResponse.setHeader("Access-Control-Allow-Origin",httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
        //支持cookie跨域
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, Accept, Authorization, token, Responsetype");//Origin, X-Requested-With, Content-Type, Accept,Access-Token
        //如果使用REST风格的话，还需要加上下面这句，允许这些请求方法跨域
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
