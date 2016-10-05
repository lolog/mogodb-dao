package net.oicp.wego.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/** 
 * @author         lolog
 * @version        V1.0  
 * @date           2016年7月2日
 * @company        CIMCSSC
 * @description    请求拦截
*/

public class UrlFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("==============UrlFilter============init===================");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("==============UrlFilter============doFilter===================");
		// 请求对象
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String method      = servletRequest.getMethod();
		String url         = servletRequest.getRequestURI();
		String queryString = servletRequest.getQueryString();
		String servletPath = servletRequest.getServletPath();
		
		System.out.println("[method]="+method+";\n[url]="+url+";\n[queryString]="+queryString+";\n[servletPath]="+servletPath);
		
		// 执行下一个拦截器
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		System.out.println("==============UrlFilter============destroy===================");
	}

}
