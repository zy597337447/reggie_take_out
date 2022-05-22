package com.zhang.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zhang.reggie.common.BaseContext;
import com.zhang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"

        };
        //2.判断本次请求是否需要处理
        boolean check = check(uris,requestURI);

        //3.如果不需要处理，则直接放行
        if (check){
            log.info("本地请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态，如果已经登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null){
//            Long id = Thread.currentThread().getId();
//            log.info("线程id：{}",id);
            //公共字段填充，将empId在登录时写入线程
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        };
        //5.如果未登录则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));


    }

    public boolean check(String[] uris,String requerstURI){
        for(String uri : uris){
            boolean match = PATH_MATCHER.match(uri,requerstURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
