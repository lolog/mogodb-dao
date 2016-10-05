package net.oicp.wego.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONObject;

import net.oicp.wego.tools.JsonConvert;
import net.oicp.wego.tools.Tool;

/**
 * @className    BaseController
 * @description  base controller
 * @author       lolog
 * @date         2016.06.21 11:04:40 
 */
@SuppressWarnings({"unchecked","rawtypes"}) 
public class BaseController extends Base
{
	public BaseController () {
	}
	
	/**
	 * @title         csrf
	 * @author        lolog
	 * @description   csrf security
	 * @param csrf    store key in session
	 * @return        return true if success, other throw exception
	 * @throws        encrypt error by md5
	 * @date          2016.08.18 10:39:47
	 */
	protected Boolean csrf (String csrf) {
		// invalidate
		if (csrf == null || csrf.length() < 1) {
			return false;
		}
		
		// <input> label name
		Integer csrfIntKey = Tool.random(0, 9999);
		// <input> label value
		Integer csrfIntValue = Tool.random(0, 9999);
		
		String csrfKey   = Tool.md5(csrfIntKey);
		String csrfValue = Tool.md5(csrfIntValue);
		
		// convert to upper case
		csrf = csrf.toUpperCase();
		
		session().setAttribute(csrf+"_CSRF_KEY_", csrfKey);
		session().setAttribute(csrf+"_CSRF_VAL_", csrfValue);
		
		return true;
	}
	
	/**
	 * @title         checkCsrf
	 * @author        lolog
	 * @description   csrf security check
	 * @param csrf    store key in session
	 * @param key     input key
	 * @param val     input value
	 * @return        return true if success, other false
	 * @date          2016.08.18 10:40:47
	 */
	protected Boolean checkCsrf (String csrf, String key, String val) {
		// invalidate
		if (csrf == null 
				|| csrf.length() < 1 
				|| key == null
				|| key.length() < 1
				|| val == null
				|| val.length() < 1) {
			return false;
		}
		
		// convert to upper case
		csrf = csrf.toUpperCase();
		
		String csrfKey   = (String) session().getAttribute(csrf+"_CSRF_KEY_");
		String csrfValue = (String) session().getAttribute(csrf+"_CSRF_VAL_");
		
		if (csrfKey == null 
				|| csrfKey.equals(key) == false
				|| csrfValue == null
				|| csrfValue.equals(val) == false) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @title           setJson
	 * @author          00013052
	 * @description     setting json of return
	 * @param json      json object
	 * @param success   success code
	 * @param message   message
	 * @param errorCode error code
	 * @return          get json data of being deal with
	 * @date            2016.08.18 05:41:03
	 */
	protected Map<String, Object> setJson (Boolean success, String message, Integer errorCode, Map<String, Object>... json) {
		if (json == null || json.length < 1) {
			jsons().put("success", success);
			jsons().put("message",message);
			jsons().put("errorCode", errorCode);
			
			return jsons();
		}
		
		json[0].put("success", success);
		json[0].put("message",message);
		json[0].put("errorCode", errorCode);
		return json[0];
	}
	
	/**
	 * @title         getIDS
	 * @author        lolog
	 * @description   get serial number
	 * @param key     basic serial key
	 * @return        String
	 * @date          2016.08.18 18:36:57
	 */
	protected String getIDS (String key) {
		Date date = new Date();
		Long dateTime = date.getTime();
		Integer randomLast = Tool.random(0, 9999);
		return Tool.md5(key + dateTime.toString() + randomLast.toString());
	}
	
	/**
	 * @title            dealJsonData
	 * @author           lolog
	 * @description      convert parameter of request to Object
	 * @param field      the field of request parameter
	 * @param isArray    whether is JSONArray
	 * @param json       request data
	 * @return           return object
	 * @date          	 2016.08.19 11:06:01
	 */
	protected Object dealJsonData (String json, Class clazz, Boolean isArray, String... field) {
		try {
			if (isArray) {
				String columsToString = null;
				
				if (field == null || field.length == 0 || field[0] == null) {
					columsToString = json;
				}
				else {
					JSONObject jsonObject = new JSONObject(json);
					
					// deal with columns
					JSONArray columns = jsonObject.getJSONArray(field[0]);
					
					// convertToString
					columsToString = columns.toString();
				}
				
				List<Object> objects = JsonConvert.jsonToObjects(clazz, columsToString);
				
				// create object
				List<Object> target = new ArrayList<Object>();
				
				for(Object object : objects) {
					target.add(object);
				}
				return target;
			}
			else {
				String columnsToString = null;
				if (field == null || field.length == 0 || field[0] == null) {
					columnsToString = json;
				}
				else {
					JSONObject jsonObject = new JSONObject(json);
					// deal with search
					JSONObject columns = jsonObject.getJSONObject(field[0]);
					
					// convertToString
					columnsToString = columns.toString();
				}
				
				Object target = JsonConvert.jsonToObject(clazz, columnsToString);
				
				return target;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @title               cookie
	 * @author              00013052
	 * @description         save cookie to value
	 * @param key           cookie key
	 * @param value         cookie object
	 * @date                2016.07.02 00:59:55
	 */
	protected void cookie (String key, String value) {
		if (key == null || key == "" ) {
			return;
		}
		// create cookie object
		Cookie cookie = new Cookie(key, value);
		// XSS
		cookie.setHttpOnly(true);
		// setting max saving time, time id second, don't save key if expiry is negation number,
		// delete key if expiry is zero
		cookie.setMaxAge(propertyContext.getExpiry());
		
		// set cookie
		response().addCookie(cookie);
	}
	
	/**
	 * @title               cookie
	 * @author              00013052
	 * @description         setting cookie
	 * @param key           key
	 * @param value         value
	 * @param expiry        setting cookie time
	 * @date                2016.07.02 00:01:12
	 */
	protected void cookie(String key, String value,Integer expire) {
		if (key == null || key == "" || expire == null) {
			return;
		}
		
		// create cookie
		Cookie cookie = new Cookie(key, value);
		
		// setting max saving time, time id second, don't save key if expiry is negation number,
		// delete key if expiry is zero
		cookie.setMaxAge(expire);
		
		response().addCookie(cookie);
	}
	
	/**
	 * @title               cookie
	 * @author              00013052
	 * @description         setting cookie value
	 * @param key           key
	 * @param value         value
	 * @param httpOnly      HttpOnly status
	 * @param expiry        setting cookie time
	 * @date                2016.07.02 00:01:12
	 */
	protected void cookie(String key, String value,Boolean httpOnly,Integer expire) {
		if (key == null || key == "" || httpOnly == null || expire == null) {
			return;
		}
		
		// create cookie
		Cookie cookie = new Cookie(key, value);
		
		// XSS
		cookie.setHttpOnly(httpOnly);
		
		// setting max saving time, time id second, don't save key if expiry is negation number,
		// delete key if expiry is zero
		cookie.setMaxAge(expire);
		
		response().addCookie(cookie);
	}
	
	/**
	 * @title         deleteCookie
	 * @author        lolog
	 * @description   delete cookie
	 * @param key     key
	 * @return        return true if successful, return false if failed
	 * @date          2016.07.02 00:42:55
	 */
	public Boolean deleteCookie (String key) {
		if (key == null || key == "") {
			return false;
		}
		
		// create cookie
		Cookie cookie = new Cookie(key, "");
		cookie.setMaxAge(0);
		// delete Cookie
		response().addCookie(cookie);
		
		return true;
	}
}
