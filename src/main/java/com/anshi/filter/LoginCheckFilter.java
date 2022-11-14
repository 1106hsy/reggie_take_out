package com.anshi.filter;

import com.alibaba.fastjson.JSON;
import com.anshi.common.GetThreadLocal;
import com.anshi.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String requestURI = request.getRequestURI();

        //定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",  //移动端发送短信
                "/user/login"  //移动端登录
        };

        //判断本次请求是否需要处理
        if(check(urls,requestURI)){
            //匹配到了，放行
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){
            //登陆了，放行
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("user") != null){
            //登陆了，放行
            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录，返回未登录结果,通过客户端页面来响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
