package net.oicp.wego.util;


import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.IndexModel;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.08
 * @Company        WEGO
 * @Description    Document
*/

public interface DocumentUtil<T> {
	/**
	 * @title         getIndexs
	 * @author        lolog
	 * @description   get The Document Indexes
	 * @param t       Object
	 * @return        Indexes
	 * @throws        exception
	 * @date          2016.07.10 08:48:06
	 */
	public List<IndexModel> getIndexs (T t);
	
	/**
	 * @title         getListDocument
	 * @author        lolog
	 * @description   transform Object to Document
	 * @param object  Object
	 * @param filters filter field
	 * @param ekeys   update keys, for example, $set,$push...
	 * @return        get The Object of transform
	 * @throws        exception
	 * @date          2016.07.10 08:49:43
	 */
	public Document  ekeyObjectToDocument(T t, Boolean updateFlag, String[] ekeys, String... filter);
	
	/**
	 * @title         objectToDocument
	 * @author        lolog
	 * @description   transform Object to Document
	 * @param t       Object
	 * @param filters filter field
	 * @return        get The Object of transform
	 * @throws        exception
	 * @date          2016.07.10 08:49:43
	 */
	public Document objectToDocument(T t, String... filter);
	
	/**
	 * @title         ekeyObjectsToDocuments
	 * @author        lolog
	 * @description   transform List Object to List Document
	 * @param t       Object
	 * @param filter  field filter
	 * @param ekeys   update keys, for example, $set,$push...
	 * @return        Document
	 * @date          2016.07.10 09:26:31
	 */
	public List<Document>  ekeyObjectsToDocuments (List<T> ts, Boolean updateFlag, String[] ekeys, String... filters);
	
	/** 
	 * @title          documentToObject
	 * @author         lolog
	 * @description    transform Document to T Object
	 * @param document the document of target
	 * @return         the result document of target
	 * @date           2016.07.10 08:45:11
	 */
	public T documentToObject (Document document, String... filter);
	
	/**
	 * @title               documentsToObjects
	 * @author              lolog
	 * @description         transform List Document to List T
	 * @param listDocuments List Document
	 * @return              the result T of transform
	 * @date                2016.07.10 08:46:27
	 */
	public List<T> documentsToObjects (List<Document> listDocuments, String... filters);
	
	/**
	 * @title          filterDocument
	 * @author         lolog
	 * @description    document filter
	 * @param document target document
	 * @param filters  filters
	 * @return         return result document
	 * @date           2016.08.20 07:32:59
	 */
	public Document filterDocument (Document document, String... filters);
	
	/**
	 * @title               filterDocuments
	 * @author              lolog
	 * @description         list document filter
	 * @param listDocuments target list document
	 * @param filters       filters
	 * @return              return list document
	 * @date                2016.08.20 07:32:59
	 */
	public List<Document> filterDocuments (List<Document> listDocuments, String... filters);
	
	/**
	 * @title         mapToBson
	 * @author        lolog
	 * @description   convert map to Bson
	 * 				  support map format : Map<String, Map<String,Object>>
	 * @param filter  target object
	 * @return        get result of being converted
	 * @date          2016.0820 07:51:28
	 */
	public Bson mapToBson (Map<String, Object> target);
}
