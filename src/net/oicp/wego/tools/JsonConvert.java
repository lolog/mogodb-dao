package net.oicp.wego.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.08.16
 * @Company        WEIGO
 * @Description    transform JSON to Object
*/
@SuppressWarnings({"unchecked","rawtypes"})
public class JsonConvert {
	/**
	 * @Title         jsonToObject
	 * @author        lolog
	 * @description   JSON convert to Object
	 * @param target  target Class
	 * @param json    JSON string
	 * @return        return target object if success,other null
	 * @exception     1.JSON format invalidate, 2.target new Instance error, 3.call method error
	 * @date          2016.08.17 16:52:45
	 */
	public static Object jsonToObject (Class target, String json) {
		
		if (json == null) {
			return null;
		}
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (Exception e) {
			return null;
		}
		
		// initial Object
		Object result = null;
		
		try {
			result = target.newInstance();
		} catch (Exception e) {
			// throw new RuntimeException(e);
			return null;
		}
		
		// get json data all key
		Iterator keyIterator = jsonObject.keys();
		List<String> keys = new ArrayList<String>();
		while (keyIterator.hasNext()) {
			keys.add((String)keyIterator.next());
		}
		
		// get target Class all Fields
		Field[] clazz_fields   = target.getDeclaredFields();
		// 获取T父对象的属性
		Field[] superFields    = target.getSuperclass().getDeclaredFields();
		
		Field[] fields         = new Field[clazz_fields.length + superFields.length];
		
		// 合并T与T父对象的属性
		System.arraycopy(clazz_fields, 0, fields, 0, clazz_fields.length);
		// 合并T与T父对象的属性
		System.arraycopy(superFields, 0, fields, clazz_fields.length, superFields.length);
		
		for (Field field: fields) {
			// get the field name
			String fieldName = field.getName();
			// set method name of the field
			String setFieldMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			// key judge
			if (keys.contains(fieldName) == false) {
				continue;
			}
			
			// field type
			Type type = field.getType();
			
			try {
				
				// set method of the field
				Method setFieldMethod = target.getMethod(setFieldMethodName, field.getType());
				
				if (type == String.class) {
					// get the value
					setFieldMethod.invoke(result, jsonObject.getString(fieldName));
				}
				else if(type == Long.class) {
					// get the value
					setFieldMethod.invoke(result, jsonObject.getLong(fieldName));
				}
				else if (type == Double.class) {
					setFieldMethod.invoke(result, jsonObject.getDouble(fieldName));
				}
				else if (type == Integer.class) {
					setFieldMethod.invoke(result, jsonObject.getInt(fieldName));
				}
				else if (type == Boolean.class) {
					setFieldMethod.invoke(result, jsonObject.getBoolean(fieldName));
				}
				else if (type == Map.class) {
					// get Map<?,?> type
					ParameterizedType parameters = (ParameterizedType) field.getGenericType();
					// key must string
					if (parameters.getActualTypeArguments()[0] != String.class) {
						break;
					}
					
					
					Class clazz = (Class) parameters.getActualTypeArguments()[1];
					
					// the second parameter type of Map
					if((clazz == Integer.class 
							|| clazz == Double.class
							|| clazz == Float.class
							|| clazz == Boolean.class
							|| clazz == String.class
							|| clazz == Object.class) == false) {
						break;
					}
					
					// get the value
					JSONObject subValue = jsonObject.getJSONObject(fieldName);
					
					// cycle value
					Iterator iterator = subValue.keys();
					Map map = new HashMap<>();
					while (iterator.hasNext()) {
						String keyValue = (String) iterator.next();
						map.put(keyValue, subValue.get(keyValue));
					}
					setFieldMethod.invoke(result, map);
				}
				
				else if (type == List.class) {
					// get the parameter type of List
					ParameterizedType parameters = (ParameterizedType) field.getGenericType();
					// get the parameter type Class of List
					Class clazz = (Class) parameters.getActualTypeArguments()[0];
					
					// get value
					JSONArray subValue = jsonObject.getJSONArray(fieldName);
					List list = new ArrayList<>();
					
					// the second parameter type of Map
					if((clazz == Integer.class 
							|| clazz == Double.class
							|| clazz == Float.class
							|| clazz == Boolean.class
							|| clazz == String.class
							|| clazz == Object.class) == false) {
						String theValue = subValue.toString();
						list = (List) Tool.dealJsonData(theValue, clazz, true);
					}
					else {
						// cycle value
						for (int i = 0; i<subValue.length(); i++) {
							list.add(subValue.get(i));
						}
					}
					
					// call the method
					setFieldMethod.invoke(result, list);
				}
				else {
					
				}
			} catch (Exception e) {
				// throw new RuntimeException(e);
				return null;
			}
		}
		
		return result;
	}
	
	/**
	 * @Title         jsonToObjects
	 * @author        lolog
	 * @description   convert JSON to List Object
	 * @param target  target Class
	 * @param json    demand convert json data
	 * @return        return converted List<Object>
s	 * @date          2016.08.17 17:44:59
	 */
	public static List<Object> jsonToObjects (Class target, String json) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(json);
		} catch (Exception e) {
			return null;
		}
		
		List list = new ArrayList<>();
		
		try {
			for(int i=0; i<jsonArray.length(); i++) {
				Object rs = jsonToObject(target, jsonArray.get(i).toString());
				list.add(rs);
			}
		} catch (Exception e) {
			return null;
		}
		return list;
	}
	
	/**
	 * @title         toMap
	 * @author        lolog
	 * @description   convert json to Map Object
	 * @param json    target json Object
	 * @return        return converted Map
	 * @date          2016.08.17 20:26:29
	 */
	public Map toMap (String json) {
		Map<String, Object> map =  new HashMap<String, Object>();
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (Exception e) {
			return null;
		}
		
		try {
			Iterator iterator = jsonObject.keys();
			while (iterator.hasNext()) {
				// get the key
				String key = (String) iterator.next();
				// get the value
				map.put(key, jsonObject.get(key));
			}
		} catch (Exception e) {
			return null;
		}
		
		return map;
	}
}
