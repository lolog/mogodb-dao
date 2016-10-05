package net.oicp.wego.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import net.oicp.wego.dao.pool.MongoPool;
import net.oicp.wego.util.PropertyContext;

/** 
 * @author         lolog
 * @version        v1.0
 * @date           2016.07.15
 * @company        WEIGO
 * @description    base
*/
public class Base extends WebApplicationObjectSupport {
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	// global variable
	protected static ApplicationContext  applicationContext;
	protected static ServletContext      servletContext;
	protected static String              contextPath;
	
	@Autowired
	protected MongoPool mongoPool;
	
	@Autowired
	protected PropertyContext propertyContext;
	
	static protected ThreadLocal<HttpServletRequest>   requests = new ThreadLocal<HttpServletRequest>();
	static protected ThreadLocal<HttpServletResponse>  respones = new ThreadLocal<HttpServletResponse>();
	static protected ThreadLocal<HttpSession>          sessions = new ThreadLocal<HttpSession>();
	static protected ThreadLocal<Map<String, Object>>  jsons    = new ThreadLocal<Map<String, Object>>();
	static protected ThreadLocal<Map<String, Object>>  models    = new ThreadLocal<Map<String, Object>>();
	
	public Base () {
		
	}
	
	@ModelAttribute
	protected void initialMapping (
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session,
			Map<String, Object> model
	) {
		
		Map<String, Object> json = new HashMap<String, Object>();
		
		requests.set(request);
		respones.set(response);
		sessions.set(session);
		jsons.set(json);
		models.set(model);
		
		applicationContext = super.getApplicationContext();
		servletContext     = super.getServletContext();
		contextPath        = servletContext.getContextPath();
	}
	
	// return JSON
	protected ModelAndView json () {
		// create ModelAndView Object
		ModelAndView json = new ModelAndView();
		// setting return JSON
		json.setView(new MappingJackson2JsonView());
		// add data
		json.getModelMap().addAllAttributes(jsons());
		
		// clear garbage
		gc();
		
		// return JSON ModelAndView
		return json;
	}
	
	// return JSON
	protected ModelAndView json (Map<String, Object> model) {
		// create ModelAndView Object
		ModelAndView json = new ModelAndView();
		// setting return JSON
		json.setView(new MappingJackson2JsonView());
		// add data
		json.getModelMap().addAllAttributes(model);
		
		// clear garbage
		gc();
				
		// return JSON ModelAndView
		return json;
	}
	
	// clear Model value
	protected Boolean clearModel (Map<String, Object> model) {
		if (model == null) {
			return false;
		}
		
		// clear values
		model.clear();
		
		return true;
	}
	
	// get current thread HttpServletRequest Object
	protected HttpServletRequest request() {
		return requests.get();
	}
	
	// get current thread HttpServletResponse Object 
	protected HttpServletResponse response () {
		return respones.get();
	}
	
	// get current thread HttpSession Object 
	protected HttpSession session () {
		return sessions.get();
	}
	
	// get current json data object
	protected Map<String, Object> jsons () {
		return jsons.get();
	}
	
	protected Map<String, Object> models () {
		return models.get();
	}
	// gc 
	protected void gc () {
		requests.remove();
		respones.remove();
		sessions.remove();
		jsons.remove();
		models.remove();
	}
}
 