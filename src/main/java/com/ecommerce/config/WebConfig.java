//package com.ecommerce.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import com.ecommerce.interceptor.CacheControlInterceptor;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    private final CacheControlInterceptor cacheControlInterceptor;
//
// 
//    public WebConfig(CacheControlInterceptor cacheControlInterceptor) {
//        this.cacheControlInterceptor = cacheControlInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(cacheControlInterceptor);
//    }
//}
