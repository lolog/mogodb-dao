package net.oicp.wego.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author      lolog
 * @version     v1.0
 * @date        2016.08.18
 * @company     WEIGO
 * @description Tools
 */
@SuppressWarnings("rawtypes")
public class Tool {
	/**
	 * @title         random
	 * @author        lolog
	 * @description   get random,for example [0,10]
	 * @param left    left delimiter
	 * @param right   right
	 * @return        get random between left and right
	 * @date          2016.08.18 10:09:58
	 */
	public static int random (int left, int right) {
		// create random object
		Random random = new Random();
		// get random between left and right
		return (random.nextInt(right+1) % (right - left+1) + left);
	}
	
	/**
	 * @title         md5
	 * @author        lolog
	 * @description   encrypt code by md5
	 * @param code    encrypting string
	 * @return        get the string of being encrypting by md5
	 * @throws        encrypt error
	 * @date          2016.08.18 10:26:47
	 */
	public static String md5 (String code) {
		return (new MD5()).getMD5Code(code);
	}
	public static String md5 (Integer code) {
		return (new MD5()).getMD5Code(code.toString());
	}
	public static String md5 (Long code) {
		return (new MD5()).getMD5Code(code.toString());
	}
	
	/**
	 * @title         stringToDouble
	 * @author        lolog
	 * @description   convert string to double
	 * @param string  double string
	 * @return        return double of converting
	 * @date          2016.08.22 21:51:20
	 */
	public static Double stringToDouble (String string) {
		Double result = null;
		try {
			result = Double.valueOf(string);
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * @title         longStringToDate
	 * @author        lolog
	 * @description   convert string of long format to Date  
	 * @param time    string of long format
	 * @return        Date
	 * @date          2016.08.20 09:40:43
	 */
	public static Date longStringToDate (String time) {
		if (time == null) {
			return null;
		}
		try {
			return new Date(Long.parseLong(time));
		} catch (Exception e) {
			return null;
		}
	}
	public static Date longStringToDate (Long time) {
		if (time == null) {
			return null;
		}
		try {
			return new Date(time);
		} catch (Exception e) {
			return null;
		}
	}
	public static Date dateStringToDate (String dateString, String format) {
		if (dateString == null || format == null) {
			return null;
		}
		try {
			SimpleDateFormat formatter=new SimpleDateFormat(format);
			return formatter.parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}
	public static Date stringToDate (String dateString, String...format) {
		// long string deal
		Date date = longStringToDate(dateString);
		// date string date
		if (date == null 
				&& format != null 
				&& format.length > 0 
		) {
			date = dateStringToDate(dateString, format[0]);
		}
		return date;
	}
	public static String dateToString (Object date, String...format) {
		if (date == null 
				|| format == null 
				|| format.length < 0
				|| format[0] == null) {
			return null;
		}
		
		String dateString = null;
		try {
			SimpleDateFormat formatter=new SimpleDateFormat(format[0]);  
			dateString = formatter.format(date); 
		} 
		catch (Exception e) {
		}
	    
	    return dateString;
	}
	
	/**
	 * {
	 *	 {"$eq","$nin"},
	 *	 {"lolog",{"name","nn"}}
	 * }
	 */
	public static Map<String, Object> arrayToMap (Object[][] data) {
		if (data == null
			|| data instanceof Object[][] == false) {
			return null;
		}
		
		Integer outerLength = data.length;
		Integer innerLength = data[0].length;
		
		// outerLength > innerLength
		if(outerLength < 1
			|| innerLength < 1) {
			return null;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		for(int i=0; i<innerLength; i++) {
			String ekey = (data[0][i] == null ? null : data[0][i].toString());
			Map<Object, Object> lastMap = new HashMap<Object, Object>();
			Object ekeyLast = (data[1][i] == null ? null : data[1][i]);
			
			if (ekeyLast instanceof Object[]) {
				Object[] ekeyLastArray = (Object[]) ekeyLast; 
				Integer ekeyLastLength = (ekeyLast == null) ? 0 : ekeyLastArray.length;
				
				List<Object> ekeyLastList = new ArrayList<Object>();
				
				for(int j=0; j<ekeyLastLength; j++) {
					ekeyLastList.add(ekeyLastArray[j]);
				}
				lastMap.put(ekey, ekeyLastList);
				result.put(ekey, lastMap);
			}
			else if (ekeyLast instanceof Object) {
				result.put(ekey, ekeyLast);
			}
			else {
				continue;
			}
		}
		
		return result;
	}
	/**
	 * {
	 * 	 {"name","rate"},
	 *	 {"$eq","$nin"},
	 *	 {"lolog",{"name","nn"}}
	 * }
	 */
	public static List<Object> arrayXMap (Object[][] data) {
		if (data == null
			|| data instanceof Object[][] == false) {
			return null;
		}
		
		Integer outerLength = data.length;
		Integer innerLength = data[0].length;
		
		// outerLength > innerLength
		if(outerLength < 2
			|| innerLength < 1) {
			return null;
		}
		
		 List<Object> resultList = new ArrayList<Object>();
		
		for(int i=0; i<innerLength; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			String name = (data[0][i] == null ? null : data[0][i].toString());
			String ekey = (data[1][i] == null ? null : data[1][i].toString());
			
			if (name == null
				|| name.trim().length() < 1
				|| ekey == null
				|| ekey.trim().length() < 1) {
				continue;
			}
			
			if (outerLength == 2) {
				map.put(name, ekey);
				resultList.add(map);
				continue;
			}
			
			Object ekeyLast = (data[2][i] == null ? null : data[2][i]);
			
			if (ekeyLast == null
				|| ekeyLast.toString().trim().length() < 1) {
				continue;
			}
			
			Map<Object, Object> lastMap = new HashMap<Object, Object>();
			
			if (ekeyLast instanceof Object[][]) {
				continue;
			}
			else if (ekeyLast instanceof Object[]) {
				Object[] ekeyLastArray  = (Object[]) ekeyLast; 
				Integer  ekeyLastLength = (ekeyLast == null) ? 0 : ekeyLastArray.length;
				
				List<Object> ekeyLastList = new ArrayList<Object>();
				
				for(int j=0; j<ekeyLastLength; j++) {
					ekeyLastList.add(ekeyLastArray[j]);
				}
				lastMap.put(ekey, ekeyLastList);
				map.put(name, lastMap);
			}
			else if (ekeyLast instanceof Object) {
				lastMap.put(ekey, ekeyLast);
				map.put(name, lastMap);
			}
			else {
				continue;
			}
			resultList.add(map);
		}
		
		return resultList;
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
	public static Object dealJsonData (String json, Class clazz, Boolean isArray, String... field) {
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
}
