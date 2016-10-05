package net.oicp.wego.util.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;

import net.oicp.wego.annotations.EKey;
import net.oicp.wego.annotations.Index;
import net.oicp.wego.tools.Tool;
import net.oicp.wego.util.DocumentUtil;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.08
 * @Company        CIMCSSC
 * @Description    MongoDB Change Class
*/

@SuppressWarnings({"unchecked", "rawtypes", "null"})
public class DocumentUtilImpl<T> implements DocumentUtil<T> {
	
	// T的Class对象
	private Class clazz;
	// T继承的对象
	private Class superClazz;
	
	public DocumentUtilImpl() {
		// 获取T类型
		ParameterizedType parameters = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class) parameters.getActualTypeArguments()[0];
		superClazz = clazz.getSuperclass();
	}
	
	@Override
	public List<IndexModel> getIndexs (T t) {
		// 获取该类的属性
		Field[] clazz_fields      = clazz.getDeclaredFields();
		// 获取T父对象的属性
		Field[] superFields       = superClazz.getDeclaredFields();
		
		Field[] fields = new Field[clazz_fields.length + superFields.length];
		
		// 合并T与T父对象的属性
		System.arraycopy(clazz_fields, 0, fields, 0, clazz_fields.length);
		// 合并T与T父对象的属性
		System.arraycopy(superFields, 0, fields, clazz_fields.length, superFields.length);
		
		// 创建索引序列
		List<IndexModel> indexModels = new ArrayList<IndexModel>();
		
		// 循环该类的属性
		for (Field field: fields) {
			// 如果属性被Index注解,那么
			if (field.isAnnotationPresent(Index.class)) {
				// 获取注解对象
				Index index = field.getAnnotation(Index.class);
				
				// MongoDB ObjectID主键或者不是主键
				if(index.id() == true || index.key() == false) {
					continue;
				}
				
				// 创建索引键
				BasicDBObject keys = new BasicDBObject();
				keys.append(field.getName(), index.order());
				
				// 附加选项
				IndexOptions indexOptions = new IndexOptions();
				
				if(index.background()) {
					indexOptions.background(index.background());
				}
				
				// 所有名字
				if("".equals(index.name()) == false) {
					indexOptions.name(index.name());
				}
				
				// 唯一字段
				if(index.unique()) {
					indexOptions.unique(index.unique());
				}
				
				if(index.sparse()) {
					indexOptions.sparse(index.sparse());
				}
				
				// 创建索引
				IndexModel indexModel = new IndexModel(keys,indexOptions);
				// 索引
				indexModels.add(indexModel);
			}
		}
		return indexModels;
	}
	
	@Override
	public Document ekeyObjectToDocument(T t, Boolean updateFlag, String[] ekeys, String... filter) {
		// 空对象处理
		if (t == null) {
			return null;
		}
		
		// 获取类型对象
		Class claz          = t.getClass();
		
		// 获取t对象的属性
		Field[] claz_fields = claz.getDeclaredFields();
		// 获取t父对象的属性
		Field[] superFields = superClazz.getDeclaredFields();
		
		Field[] fields = new Field[claz_fields.length + superFields.length];
		
		// 合并t与t父对象的属性
		System.arraycopy(claz_fields, 0, fields, 0, claz_fields.length);
		System.arraycopy(superFields, 0, fields, claz_fields.length, superFields.length);
		
		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// 过滤的参数
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// 过滤的参数
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}
		
		// all key, (name, key)
		Map<String, String> keys = new HashMap<String, String>();
		// 创建返回对象
		Document document = new Document();
		
		// 循环属性
		for (Field field: fields) {
			
			// ObjectId属性不处理
			if (field.isAnnotationPresent(Index.class)) {
				Index index = field.getAnnotation(Index.class);
				if (index.id() == true) {
					continue;
				}
			}
			
			// 字段
			String  updateKey      = null;
			
			// Update annotation
			if (updateFlag != null && updateFlag == true 
				&& field.isAnnotationPresent(EKey.class)) {
				EKey update = field.getAnnotation(EKey.class);
				
				// default value
				updateKey = update.value();
				
				if (update.priority() == true) {
					updateKey = (updateKey != null || updateKey.startsWith("$") == false) ? null : updateKey;
				}
				else {
					if ((ekeys == null
						|| update.index() == -1
						|| ekeys.length <= update.index()
						|| ekeys[update.index()] == null
						|| ekeys[update.index()].startsWith("$") == false) == false) {
						updateKey = ekeys[update.index()];
					}
				}
			}
			
 			// 属性名
			String fieldName = field.getName();
			
			// 不加载
			if (excludeParameter.size() > 0 && excludeParameter.contains(fieldName) == true) {
				continue;
			}
			
			if (includeParameter.size() > 0 && includeParameter.contains(fieldName) == false) {
				continue;
			}
			
			// 获取对象的get方法名
			String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			
			try {
				// 获取get方法
				Method method = claz.getMethod(getMethodName);
				// 获取get方法的值
				Object value  = method.invoke(t);
				
				// continue next operate
				if (value == null) {
					continue;
				}
				
				// List对象处理
				if (field.getType() == List.class) {
					// 获取类型
					ParameterizedType parameter = (ParameterizedType) field.getGenericType();
					// 获取泛型类参数
					Type              listType  = parameter.getActualTypeArguments()[0];
					
					// 基本类型
					if (listType == Integer.class 
							|| listType == Boolean.class
							|| listType == String.class
							|| listType == Double.class
							|| listType == Map.class
							|| listType == List.class 
							|| listType == Set.class) {
						// 直接加入值
						if (value != null) {
							if (updateKey == null) {
								keys.put(fieldName, null);
								document.append(fieldName, value);
							}
							else {
								keys.put(fieldName, updateKey);
								document.append(updateKey, new Document().append(fieldName, value));
							}
						}
						continue;
					}
					
					// 获取类的类型名
					String typeName = listType.toString();
					typeName = typeName.substring(typeName.indexOf("class ")+"class ".length());
					
					// 实例化List泛型类
					Object object = Class.forName(typeName).newInstance();
					
					// 获取List泛型参数类型对应的Class对象
					Class         listClazz = object.getClass();
					// 获取泛型类的属性
					Field[] tFields = listClazz.getDeclaredFields();
					
					// 获取其List大小
					List<Object>    listValue = (List<Object>) value;
					List<Object>    listMap  = new ArrayList<Object>();
					
					for (int i=0; i<listValue.size(); i++) {
						// 创建需要返回的值
						Map<String, Object> map = new HashMap<String, Object>();
						// 循环处理
						for (Field field2: tFields) {
							// ObjectId属性不处理
							if (field2.isAnnotationPresent(Index.class)) {
								Index index = field2.getAnnotation(Index.class);
								if (index.id() == true) {
									continue;
								}
							}
							
				 			// 属性名
							String field2Name = field2.getName();
							// 获取对象的get方法名
							String getMethod2Name = "get" + field2Name.substring(0, 1).toUpperCase() + field2Name.substring(1);
							// 获取get方法
							Method method2 = listClazz.getMethod(getMethod2Name);
							
							List<Object> list = (List<Object>) value;
							// 获取get方法的值
							Object value2  = method2.invoke(list.get(0));
							
							// 不为空值添加
 							if (value2 != null) {
								map.put(field2Name, value2);
							}
						}
						listMap.add(map);
					}
					// update operate
					if (updateFlag  != null 
						&& updateFlag == true 
						&& updateKey   != null) {
						keys.put(fieldName, updateKey);
						
						Document documentValue = (Document) document.get(updateKey);
						if (documentValue == null) { // first
							document.append(updateKey, new Document().append(fieldName, listMap));
						}
						else {
							documentValue.append(fieldName, listMap);
							document.append(updateKey, documentValue);
						}
					}
					// not update
					else {
						keys.put(fieldName, null);
						document.append(fieldName, listMap);
					}
					continue;
				}
				
				if (value != null) {
					if (updateFlag  != null 
						&& updateFlag == true 
						&& updateKey   != null) {
						keys.put(fieldName, updateKey);
						
						Document documentValue = (Document) document.get(updateKey);
						if (documentValue == null) { // first
							document.append(updateKey, new Document().append(fieldName, value));
						}
						else {
							documentValue.append(fieldName, value);
							document.append(updateKey, documentValue);
						}
					}
					else {
						keys.put(fieldName, null);
						document.append(fieldName, value);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		Document result = new Document();
		
		// class Update annotation
		if ( updateFlag != null 
				&& updateFlag == true
				&& claz.isAnnotationPresent(EKey.class) == true 
			) {
			EKey update = (EKey) claz.getAnnotation(EKey.class);
			
			// default value
			String updateKey = update.value();
			
			if (update.priority() == true) {
				updateKey = (updateKey != null || updateKey.startsWith("$") == false) ? null : updateKey;
			}
			else {
				if ((ekeys == null
					|| update.index() == -1
					|| ekeys.length <= update.index()
					|| ekeys[update.index()] == null
					|| ekeys[update.index()].startsWith("$") == false) == false) {
					updateKey = ekeys[update.index()];
				}
			}
			
			if(updateKey != null
				&& updateKey.length() > 1) {
				// keys cycle 
				for (Entry<String, String> entry: keys.entrySet()) {
					String value = entry.getValue();
					if (value != null 
						&& document.get(value) != null
						&& value.startsWith("$") == true ) {
						// update $,for example $set 
						if (value.equals(updateKey) == true) {
							Document temp = (Document) document.get(value);
							for (String set: temp.keySet()) {
								document.append(set, temp.get(set));
							}
							document.remove(value);
						}
						else {
							document.append(value, document.get(value));
							document.remove(value);
						}
					}
				}
				
				if (document.size() > 0) {
					result.append(updateKey, document);
				}
			}
		}
		else {
			result = document;
		}
		
		return result;
	}
	
	@Override
	public Document objectToDocument (T t, String... filter) {
		// 空对象处理
		if (t == null) {
			return null;
		}
		
		// 获取类型对象
		Class claz          = t.getClass();
		
		// 获取t对象的属性
		Field[] claz_fields = claz.getDeclaredFields();
		// 获取t父对象的属性
		Field[] superFields = superClazz.getDeclaredFields();
		
		Field[] fields = new Field[claz_fields.length + superFields.length];
		
		// 合并t与t父对象的属性
		System.arraycopy(claz_fields, 0, fields, 0, claz_fields.length);
		System.arraycopy(superFields, 0, fields, claz_fields.length, superFields.length);
		
		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// 过滤的参数
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// 过滤的参数
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}
		
		// 创建返回对象
		Document document = new Document();
		
		// 循环属性
		for (Field field: fields) {
			// ObjectId属性不处理
			if (field.isAnnotationPresent(Index.class)) {
				Index index = field.getAnnotation(Index.class);
				if (index.id() == true) {
					continue;
				}
			}
			
			// 属性名
			String fieldName = field.getName();
			
			// 不加载
			if (excludeParameter.size() > 0 && excludeParameter.contains(fieldName) == true) {
				continue;
			}
			if (includeParameter.size() > 0 && includeParameter.contains(fieldName) == false) {
				continue;
			}
			
			// 获取对象的get方法名
			String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			try {
				// 获取get方法
				Method method = claz.getMethod(getMethodName);
				// 获取get方法的值
				Object value  = method.invoke(t);
				
				// continue next operate
				if (value == null) {
					continue;
				}
				
				// List对象处理
				if (field.getType() == List.class) {
					// 获取类型
					ParameterizedType parameter = (ParameterizedType) field.getGenericType();
					// 获取泛型类参数
					Type              listType  = parameter.getActualTypeArguments()[0];
					
					// 基本类型
					if (listType == Integer.class 
							|| listType == Boolean.class
							|| listType == String.class
							|| listType == Double.class
							|| listType == Map.class
							|| listType == List.class 
							|| listType == Set.class) {
						// 直接加入值
						if (value != null) {
							document.append(fieldName, value);
						}
						continue;
					}
					
					// 获取类的类型名
					String typeName = listType.toString();
					typeName = typeName.substring(typeName.indexOf("class ")+"class ".length());
					
					// 实例化List泛型类
					Object object = Class.forName(typeName).newInstance();
					
					// 获取List泛型参数类型对应的Class对象
					Class         listClazz = object.getClass();
					// 获取泛型类的属性
					Field[] tFields = listClazz.getDeclaredFields();
					
					// 获取其List大小
					List<Object>    listValue = (List<Object>) value;
					List<Object>    listMap  = new ArrayList<Object>();
					
					for (int i=0; i<listValue.size(); i++) {
						// 创建需要返回的值
						Map<String, Object> map = new HashMap<String, Object>();
						// 循环处理
						for (Field field2: tFields) {
							// ObjectId属性不处理
							if (field2.isAnnotationPresent(Index.class)) {
								Index index = field2.getAnnotation(Index.class);
								if (index.id() == true) {
									continue;
								}
							}
							
							// 属性名
							String field2Name = field2.getName();
							// 获取对象的get方法名
							String getMethod2Name = "get" + field2Name.substring(0, 1).toUpperCase() + field2Name.substring(1);
							// 获取get方法
							Method method2 = listClazz.getMethod(getMethod2Name);
							
							List<Object> list = (List<Object>) value;
							// 获取get方法的值
							Object value2  = method2.invoke(list.get(0));
							
							// 不为空值添加
							if (value2 != null) {
								map.put(field2Name, value2);
							}
						}
						listMap.add(map);
					}
					document.append(fieldName, listMap);
					continue;
				}
				
				if (value != null) {
					document.append(fieldName, value);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return document;
	}

	@Override
	public List<Document> ekeyObjectsToDocuments (List<T> ts, Boolean updateFlag, String[] ekeys, String... filters) {
		// 空对象处理
		if (ts == null) {
			return null;
		}
		
		// 需要返回的对象
		List<Document> list = new ArrayList<Document>();
		
		// 循环对象
		Iterator iterator = ts.iterator();
		
		while (iterator.hasNext()) {
			T t = (T) iterator.next();
			Document document = ekeyObjectToDocument(t, updateFlag, ekeys, filters);
			list.add(document);
		}
		return list;
	}

	@Override
	public T documentToObject (Document document, String... filter) {
		// 空对象处理
		if (document == null) {
			return null;
		}
		
		// 读取键
		Set<String> keys = document.keySet();
		
		// 创建对象
		T t;
		try {
			t = (T) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// 过滤的参数
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// 过滤的参数
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}
		
		// 获取T属性
		Field[] clazz_fields   = clazz.getDeclaredFields();
		// 获取T父对象的属性
		Field[] superFields    = superClazz.getDeclaredFields();
		
		Field[] fields = new Field[clazz_fields.length + superFields.length];
		
		// 合并T与T父对象的属性
		System.arraycopy(clazz_fields, 0, fields, 0, clazz_fields.length);
		System.arraycopy(superFields, 0, fields, clazz_fields.length, superFields.length);
		
		// 键循环
		for (String str : keys) {
			
			if (excludeParameter.size() > 0 && excludeParameter.contains(str) == true) {
				continue;
			}
			if (includeParameter.size() > 0 && includeParameter.contains(str) == false) {
				continue;
			}
			
			// 属性循环
			for (Field field: fields) {
				//属性名
				String fieldName = field.getName();
				
				// set方法名
				String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				
				// 声明获取set方法
				Method setMethod;
				try {
					// 获取set方法
					setMethod = clazz.getMethod(setMethodName, field.getType());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				// ===================获取ObjectId================
				// Index注解
				if (field.isAnnotationPresent(Index.class)) {
					// 获取Index注解
					Index index = field.getAnnotation(Index.class);
					if (index.id() == true && field.getType() == String.class) {
						try {
							// 获取ObjectId
							setMethod.invoke(t, document.get("_id").toString());
						}  catch (Exception e) {
							throw new RuntimeException(e);
						}
						continue;
					}
				}
				
				// 字段与属性相同判断
				if (!fieldName.equalsIgnoreCase(str)) {
					continue;
				}
				
				// 获取其他字段值
				try {
					// 判断返回对象的类型,获取键(字段)对应的值,调用相应的set方法
					if (field.getType() == Integer.class) {
						setMethod.invoke(t, document.getInteger(str));
					}
					else  if(field.getType() == Double.class) {
						setMethod.invoke(t, document.getDouble(str));
					}
					else if (field.getType() == Long.class) {
						setMethod.invoke(t, document.getLong(str));
					}
					else if (field.getType() == Boolean.class) {
						setMethod.invoke(t, document.getBoolean(str));
					}
					else if (field.getType() == Date.class) {
						setMethod.invoke(t, document.getDate(str));
					}
					else if (field.getType() == String.class) {
						setMethod.invoke(t, document.getString(str));
					}
					else if (field.getType() == List.class) {
						// 获取List<T>的T参数
						ParameterizedType types = (ParameterizedType) field.getGenericType();
						Type           listType = types.getActualTypeArguments()[0];
						// 读取值
						Object        listValue = document.get(str, List.class); 
						
						// List<T>泛型为基本类型
						if (listType == Integer.class 
								|| listType == Boolean.class
								|| listType == Double.class 
								|| listType == Map.class
								|| listType == List.class 
								|| listType == Set.class) 
						{
							setMethod.invoke(t, listValue);
							continue;
						}
						
						// 获取泛型类T名
						String listTypeName = listType.toString();
						listTypeName        = listTypeName.substring(listTypeName.indexOf("class ")+"class ".length());
						
						// 实例化List泛型类
						Object object = Class.forName(listTypeName).newInstance();
						
						// 获取List泛型参数类型对应的Class对象
						Class         listTypeClazz = object.getClass();
						// 获取泛型类的属性
						Field[] tFields = listTypeClazz.getDeclaredFields();
						
						// 存储获取转换之后的值
						List<Object> lists = new ArrayList<Object>();
						
						// 值
						List<Object> valueList = (List<Object>) listValue;
						
						// 循环,Document转换为T
						for (int i=0; i<valueList.size(); i++) {
							// 创建泛型类对象
							Object ts = Class.forName(listTypeName).newInstance();
							
							// 1.Document对象
							if (valueList.get(i).getClass() == Document.class) {
								for (Field field2 : tFields) {
									String field2Name = field2.getName();
									Object field2Value = ((Document) valueList.get(i)).get(field2Name);
									
									// set方法
									String tsSetMethodName = "set"+field2Name.substring(0, 1).toUpperCase()+field2Name.substring(1);
									Method tsMethod = listTypeClazz.getMethod(tsSetMethodName, field2.getType());
									tsMethod.invoke(ts, field2Value);
								}
								lists.add(ts);
							}
							// 2. Map对象
							else if (valueList.get(i).getClass() == Map.class) {
								// 取值
								Map<String, Object> mapValue = (Map<String, Object>) valueList.get(i);
								// 循环
								for (Field field2 : tFields) {
									String field2Name = field2.getName();
									Object field2Value = mapValue.get(field2Name);
									
									// set方法
									String tsSetMethodName = "set"+field2Name.substring(0, 1).toUpperCase()+field2Name.substring(1);
									Method tsMethod = listTypeClazz.getMethod(tsSetMethodName, field2.getType());
									tsMethod.invoke(ts, field2Value);
								}
								lists.add(ts);
							}
							else {
								//其他情况直接取值
								lists.add(valueList.get(i));
							}
						}
						setMethod.invoke(t, lists);
					}
					else if (field.getType() == Map.class){
						// 取值
						Object fieldValue = document.get(str);
						
						Map<String, Object> map = new HashMap<String,Object>();
						// 1.处理Document类型
						if (fieldValue.getClass() == Document.class) {
							Document document2 = (Document) fieldValue;
							for (String docKey: document2.keySet()) {
								map.put(docKey, document2.get(docKey));
							}
							setMethod.invoke(t, map);
						}
						// 2.处理Map类
						else {
							setMethod.invoke(t, document.get(str, Map.class));
						}
					}
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
		}
		
		// 属性循环
		for (Field field: fields) {
			//属性名
			String fieldName = field.getName();
			
			// set方法名
			String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			
			// 声明获取set方法
			Method setMethod;
			try {
				// 获取set方法
				setMethod = clazz.getMethod(setMethodName, field.getType());
				// Index Annotation
				if (field.isAnnotationPresent(Index.class)) {
					Index index = field.getAnnotation(Index.class);
						// already setting default value
						if(index.omission() == true) {
							if (field.getType()     == Integer.class && document.getInteger(field.getName()) == null) {
								setMethod.invoke(t, 0);
							}
							else if (field.getType() == Double.class && document.getDouble(field.getName())   == null) {
								setMethod.invoke(t, 0.00);
							}
							else if (field.getType() == Boolean.class && document.getBoolean(field.getName()) == null) {
								setMethod.invoke(t, false);
							}
							else if (field.getType() == Long.class && document.getLong(field.getName())       == null) {
								setMethod.invoke(t, 0L);
							}
							else {
							}
						}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		// 判断是否存在ObjectId
		return t;
	}

	@Override
	public List<T> documentsToObjects (List<Document> listDocuments, String... filters) {
		// 空对象处理
		if (listDocuments == null || listDocuments.size() == 0) {
			return null;
		}
		
		// 创建返回的对象
		List<T> ts = new ArrayList<T>();
		
		// 获取循环对象
		Iterator<Document> iterator = listDocuments.iterator();
		// 循环处理
		while (iterator.hasNext()) {
			// 获取Document对象
			Document document = iterator.next();
			// Document转换为T对象
			T t = documentToObject(document, filters);
			
			ts.add(t);
		}
		return ts;
	}
	
	@Override
	public Document filterDocument (Document document, String... filters) {
		// 空对象处理
		if (document == null) {
			return null;
		}
		// all key-value
		Set<Entry<String, Object>> set = document.entrySet();
		// cycle
		Iterator<Entry<String, Object>> iterator = set.iterator();
		
		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		List<String> timesParameter = new ArrayList<String>();
		
		// exclude parameter 
		if (filters != null && filters.length > 0 && filters[0] != null) {
			excludeParameter = Arrays.asList(filters[0].split(","));
		}
		// include parameter
		if (filters != null && filters.length > 1 && filters[1] != null) {
			includeParameter = Arrays.asList(filters[1].split(","));
		}
		// include parameter
		if (filters != null && filters.length > 2 && filters[2] != null) {
			timesParameter = Arrays.asList(filters[2].split(","));
		}
		
		Document resultDocument = new Document();
		
		while (iterator.hasNext()) {
			// key-value
			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
			
			// key
			String key = entry.getKey();
			
			// excludes
			if (excludeParameter.size() > 0 && excludeParameter.contains(key) == true) {
				continue;
			}
			// includes
			if (includeParameter.size() > 0 && includeParameter.contains(key) == false) {
				continue;
			}
			
			Boolean timeFlag = false;
			// deal with time
			if (timesParameter.size() > 0) {
				Iterator timeIterator = timesParameter.iterator();
				while (timeIterator.hasNext()) {
					String regex_time = (String) timeIterator.next();
					if (key != null 
							&& regex_time != null 
							&& key.matches(".*"+ regex_time+".*") == true) {
						timeFlag = true;
						break;
					}
				}
			}
			
			// deal with time
			if (timeFlag == true) {
				resultDocument.append(key, Tool.dateToString(entry.getValue(), "yyyy-MM-dd HH:mm:ss"));
			}
			else {
				resultDocument.append(key, entry.getValue());
			}
		}
		
		return resultDocument;
	}
	
	@Override
	public List<Document> filterDocuments (List<Document> listDocuments, String... filters) {
		// 空对象处理
		if (listDocuments == null || listDocuments.size() == 0) {
			return null;
		}
		
		// 创建返回的对象
		List<Document> documents = new ArrayList<Document>();
		
		// 获取循环对象
		Iterator<Document> iterator = listDocuments.iterator();
		// 循环处理
		while (iterator.hasNext()) {
			// get document
			Document document = iterator.next();
			document = filterDocument(document, filters);
			
			// add it to document list
			documents.add(document);
		}
		return documents;
	}
	
	@Override
	public Bson mapToBson (Map<String, Object> target) {
		if (target == null) {
			return null;
		}
		
		Iterator iterator = target.entrySet().iterator();
		BasicDBObject bson = new BasicDBObject ();
		
		// cycle 
		while (iterator.hasNext()) {
			Entry entry  = (Entry) iterator.next();
			// key
			String key   = (String) entry.getKey();
			// value
			Object value = entry.getValue();
			
			// if it Map Object
			if (value instanceof Map) {
				BasicDBObject result = new BasicDBObject();
				Map<String, Object> map = (Map<String, Object>) value;
				
				// cycle all keys
				for (String valueKey: map.keySet()) {
					// add it to result
					result.append(valueKey, map.get(valueKey));
				}
				bson.put(key, result);
			}
			else {
				bson.put(key, value);
			}
		}
		
		return bson;
	}
}
