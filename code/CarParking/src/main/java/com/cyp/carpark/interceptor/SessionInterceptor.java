package com.cyp.carpark.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cyp.carpark.entity.User;
import com.cyp.carpark.service.UserService;
import com.cyp.carpark.utils.Constants;


public class SessionInterceptor extends HandlerInterceptorAdapter{
	@Autowired
	private UserService userService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setCharacterEncoding("UTF-8"); 
	    response.setCharacterEncoding("UTF-8"); 
	    response.setContentType("text/html;charset=UTF-8"); 

	    User user=(User) request.getSession().getAttribute("user");
	    if(null==user)
	    {
	          PrintWriter out = response.getWriter(); 
	          StringBuilder builder = new StringBuilder(); 
	          builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">"); 
	          builder.append("alert(\"登录已过期\");");
	          builder.append("window.top.location.href=\""); 
	          builder.append(Constants.basePath); 
	          builder.append("/login/login\";</script>"); 
	          out.print(builder.toString()); 
	          out.close(); 
	          return false;
	    }
	    User user1=userService.findUserById(user.getId());
	    if(user1==null)
	    {
	    	request.getSession().removeAttribute("user");
	    	PrintWriter out = response.getWriter(); 
	          StringBuilder builder = new StringBuilder(); 
	          builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">"); 
	          builder.append("alert(\"登录已过期\");");
	          builder.append("window.top.location.href=\""); 
	          builder.append(Constants.basePath); 
	          builder.append("/login/login\";</script>"); 
	          out.print(builder.toString()); 
	          out.close(); 
	          return false;
	    }
		return super.preHandle(request, response, handler);
	}
}
