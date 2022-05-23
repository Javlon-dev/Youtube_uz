package com.company.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecuredFilterConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public FilterRegistrationBean filterRegistrationBeanRegion() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(jwtFilter);

        bean.addUrlPatterns("/profile/adm/*");
        bean.addUrlPatterns("/profile/public/*");

        bean.addUrlPatterns("/attach/adm/*");

        bean.addUrlPatterns("/category/adm/*");

        bean.addUrlPatterns("/tag/public/*");
        bean.addUrlPatterns("/tag/adm/*");

        bean.addUrlPatterns("/email/adm/*");

        bean.addUrlPatterns("/channel/adm/*");
        bean.addUrlPatterns("/channel/public/*");

        bean.addUrlPatterns("/playlist/adm/*");
        bean.addUrlPatterns("/playlist/public/*");

        bean.addUrlPatterns("/video/adm/*");
        bean.addUrlPatterns("/video/public/*");

        bean.addUrlPatterns("/video-like/adm/*");
        bean.addUrlPatterns("/video-like/public/*");

        bean.addUrlPatterns("/playlist-video/public/*");

        bean.addUrlPatterns("/video-tag/public/*");

        bean.addUrlPatterns("/comment/adm/*");
        bean.addUrlPatterns("/comment/public/*");

        bean.addUrlPatterns("/comment-like/adm/*");
        bean.addUrlPatterns("/comment-like/public/*");

        bean.addUrlPatterns("/subscription/adm/*");
        bean.addUrlPatterns("/subscription/public/*");

        bean.addUrlPatterns("/report/adm/*");
        bean.addUrlPatterns("/report/public/*");

        return bean;
    }

}
