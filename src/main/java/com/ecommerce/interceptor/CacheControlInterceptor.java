//package com.ecommerce.interceptor;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@ControllerAdvice
//public class CacheControlInterceptor implements HandlerInterceptor {
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		System.out.println("Interceptor is called");
//		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//		response.setHeader("Pragma", "no-cache");
//		response.setHeader("Expires", "0");
//		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//	}
//}
