package net.oicp.wego.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import net.oicp.wego.dao.pool.MongoPool;

/** 
 * @author         lolog
 * @version        V1.0
 * @Date           2016.07.02
 * @Company        CIMCSSC
 * @Description    MongoDB
*/
public class MongoListener implements HandlerInterceptor,ApplicationContextAware {
	// MongoDB连接池
	private MongoPool mongoPool;
	// ApplicatinContext
	private ApplicationContext applicationContext;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("======================preHandle========================");
		// ServletContext
		ServletContext servletContext = request.getSession().getServletContext();
		
		// MongoDB
		if (servletContext.getAttribute("mongoPool") == null) {
			// get MongoDB database connection pool, and operation from ApplicationContext
			mongoPool = applicationContext.getBean("mongoPool", MongoPool.class);
			
			// save it in ServleContext
			servletContext.setAttribute("mongoPool", mongoPool);
		}
		
		// continue next listener 
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("===========postHandle==============");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("============afterConpletion==============");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
