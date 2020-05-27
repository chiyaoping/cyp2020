package com.cyp.carpark.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cyp.carpark.entity.User;


public class LoginInterceptor extends HandlerInterceptorAdapter{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setCharacterEncoding("UTF-8"); 
	    response.setCharacterEncoding("UTF-8"); 
	    response.setContentType("text/html;charset=UTF-8"); 

	    User user=(User) request.getSession().getAttribute("user");
	    if(null!=user)
	    {
	    	  response.sendRedirect("/depot_system_war_exploded/index/toindex");
	    	  return false;
	    }
		return super.preHandle(request, response, handler);
	}
}
