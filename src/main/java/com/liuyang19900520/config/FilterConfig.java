package com.liuyang19900520.config;

import com.liuyang19900520.commons.interceptor.HttpServletRequestReplacedFilter;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liuyang on 2018/3/21
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean filterDemo4Registration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //注入过滤器
        registration.setFilter(httpServletRequestReplacedFilter());
        //拦截规则
        registration.addUrlPatterns("/*");
        //过滤器名称
        registration.setName("jsonfilter");
        //是否自动注册 false 取消Filter的自动注册
        registration.setEnabled(true);
        //过滤器顺序
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public HttpServletRequestReplacedFilter httpServletRequestReplacedFilter() {
        return new HttpServletRequestReplacedFilter();
    }


}
