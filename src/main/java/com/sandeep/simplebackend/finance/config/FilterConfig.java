package com.sandeep.simplebackend.finance.config;

import com.sandeep.simplebackend.finance.security.RateLimitFilter;
import com.sandeep.simplebackend.finance.security.RateLimitService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(RateLimitService rateLimitService) {

        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();


        RateLimitFilter rateLimitFilter = new RateLimitFilter(rateLimitService);

        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/api/dashboard/*");
        registration.setOrder(1);

        return registration;
    }
}