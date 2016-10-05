package net.oicp.wego.dao;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

/** 
 * @author         lolog
 * @version        V1.0
 * @date           2016.7.8
 * @company        CIMCSSC
 * @description    basic class of operating MongoDB 
*/
@SuppressWarnings({"unchecked","rawtypes"})
public interface MongoDao<T> {
	
	/**
	 * @title               renameCollection
	 * @author              lolog
	 * @description         edit collection name
	 * @param newCollection new collection name
	 * @param oldCollection old collection name
	 * @return              return true if successful, return error if have been failed
	 * @date                2016.07.16 19:46:28
	 */
	public Boolean renameCollection (String newCollection, String oldCollection);
	
	/**
	 * @Title         filter
	 * @author        00013052
	 * @description   filer condition
	 * @param filter  operate conditions,for example: {"title": {"$eq", 1}, {"name":"lolog"}}
	 * @return        MongoDao
	 * @throws        exception
	 * @date          2016.7.13 16:58:24
	 */
	public MongoDao filter (Map<String, Object> filter);
	
	/**
	 * @title         skip
	 * @author        lolog
	 * @description   read data of skipping offset item
	 * @param skip    the offset of datas
	 * @return        MongoDao
	 * @date          2016.07.16 19:48:30
	 */
	public MongoDao skip (Integer offset);
	
	/**
	 * @title         limit
	 * @author        lolog
	 * @description   read data of limiting limit item
	 * @param limit   the number of data
	 * @return        MongoDao
	 * @date          2016.07.16 19:49:55
	 */
	public MongoDao limit (Integer limit);
	
	/**
	 * @title         page
	 * @author        lolog
	 * @description   read data of starting (page-1) and row limit item
	 * @param page    page
	 * @param rows    loading item
	 * @return        MongoDao
	 * @date          2016.07.16 19:51:32
	 */
	public MongoDao page (Integer page, Integer rows);
	
	/**
	 * @title         sort
	 * @author        lolog
	 * @description   sort data
	 * @param sort    sort filter
	 * @return        MongoDao
	 * @date          2016.07.17 14:55:16
	 */
	public MongoDao sort (Map<String, Object> sort);
	
	/**
	 * @Title          database
	 * @author         00013052
	 * @description    set mongodb database
	 * @param database connected database
	 * @return         MongoDao
	 * @throws         exception
	 * @date           2016.7.13 16:59:03
	 */
	public MongoDao database (MongoDatabase database);
	
	/**
	 * @Title               data
	 * @author              00013052
	 * @description         set data of operation
	 * @param t             single data
	 * @return              MongoDao
	 * @throws              exception
	 * @date                2016.7.13 16:59:52
	 */
	public MongoDao data (T t);
	
	/**
	 * @Title               data
	 * @author              00013052
	 * @description         set data of operation
	 * @param t             single data
	 * @return              MongoDao
	 * @throws              exception
	 * @date                2016.9.1 10:59:52
	 */
	public MongoDao data (Map<String, Object> mapData);
	
	/**
	 * @Title                data
	 * @author               00013052
	 * @description          set data of operation
	 * @param ts             mutil data
	 * @return               MongoDao
	 * @throws               exception
	 * @date                 2016.7.13 17:00:40
	 */
	public MongoDao data (List<T> ts);
	
	/**
	 * @Title                collection
	 * @author               00013052
	 * @description          set collection name
	 * @param collectionName collection name
	 * @return               MongoDao
	 * @throws               exception
	 * @date                 2016.7.13 17:02:05
	 */
	public MongoDao collection (String collectionName);
	
	/**
	 * @Title         exclude
	 * @author        00013052
	 * @description   exclude field
	 * @param exclude exclude field,for example:"name,title"
	 * @return        MongoDao
	 * @date          2016.7.13 17:08:09
	 */
	public MongoDao exclude (String exclude);
	
	/**
	 * @Title              exclude
	 * @author             00013052
	 * @description        exclude field
	 * @param excludeArray exclude field,for example:{"name","title"}
	 * @return             MongoDao
	 * @date               2016.7.13 17:08:09
	 */
	public MongoDao exclude (String[] excludeArray);
	
	/**
	 * @Title         exclude
	 * @author        00013052
	 * @description   include field
	 * @param include include field,for example:"name,title"
	 * @return        MongoDao
	 * @throws        exception
	 * @date          2016.7.13 17:08:09
	 */
	public MongoDao include (String include);
	
	/**
	 * @Title              exclude
	 * @author             00013052
	 * @description        include field
	 * @param includeArray include field,for example:{"name","title"}
	 * @return             MongoDao
	 * @date               2016.7.13 17:08:09
	 */
	public MongoDao include (String[] includeArray);
	
	/**
	 * @Title              distinct
	 * @author             00013052
	 * @description        distinct query data
	 * @param fieldName    field name of distinct
	 * @param targetClass  class of return
	 * @return             MongoDao
	 * @date               2016.7.13 17:09:00
	 */
	public MongoDao distinct (String fieldName, Class targetClass);
	
	/**
	 * @title         ekeys
	 * @author        lolog
	 * @description   the keys of mongodb update
	 * @param ekeys   keys
	 * @date          2016.08.22 09:44:16
	 */
	public MongoDao ekeys (String[] ekeys);
	
	/**
	 * @Title         insert
	 * @author        00013052
	 * @description   save datas
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:05:04
	 */
	public Boolean insert ();
	
	/**
	 * @Title         insert
	 * @author        00013052
	 * @description   save datas
	 * @param t       single data of saving
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:05:54
	 */
	public Boolean insert (T t);
	
	/**
	 * @Title         insert
	 * @author        00013052
	 * @description   save datas
	 * @param ts      more datas of saving
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:06:33
	 */
	public Boolean insert (List<T> ts);
	
	/**
	 * @Title         update
	 * @author        00013052
	 * @description   update datas (T type) for MongoDB
	 * @param notMuti update single data if notMuti is true, other update many
	 * @return        get true if successfully update, get false if t equals null, update all
	 * 			      datas if filter is null other throw exception
	 * @throws        exception
	 * @date          2016.7.13 17:07:14
	 */
	public Boolean update (Boolean...notMuti);
	
	/**
	 * @Title         update
	 * @author        00013052
	 * @description   update datas (T type) for MongoDB
	 * @param data    map data
	 * @param notMuti update single data if notMuti is true, other update many
	 * @return        get true if successfully update, get false if t equals null, update all
	 * 			      datas if filter is null other throw exception
	 * @throws        exception
	 * @date          2016.7.13 17:07:14
	 */
	public Boolean update (Map<String, Object> data, Boolean...notMuti);
	
	/**
	 * @Title         update
	 * @author        00013052
	 * @description   update datas (T type) for MongoDB
	 * @param t       single data of saving
	 * @param notMuti update single data if notMuti is true, other update many
	 * @return        get true if successfully update, get false if t equals null, update all
	 * 			      datas if filter is null other throw exception
	 * @throws        exception
	 * @date          2016.7.13 17:11:30
	 */
	public Boolean update (T t, Boolean...notMuti);
	
	/**
	 * @Title         update
	 * @author        00013052
	 * @description   update datas (T type) for MongoDB
	 * @param ts      more data of saving
	 * @param notMuti update single data if notMuti is true, other update many
	 * @return        get true if successfully update, get false if t equals null, update all
	 * 			      datas if filter is null other throw exception
	 * @throws        exception
	 * @date          2016.7.13 17:12:26
	 */
	public Boolean update (List<T> ts, Boolean...notMuti);
	
	/**
	 * @Title                select
	 * @author               00013052
	 * @description          select datas
	 * @param database       connected database pool before inserting data
	 * @param filter         select condition, for example: {"title": {"$eq", 1}, {"name":"lolog"}}
	 * @param collectionName collection name
	 * @return               select's result of List<T>
	 * @date                 2016.7.13 11:30:13
	 */
	public List<T> query ();
	
	/**
	 * @title         queryOne
	 * @author        lolog
	 * @description   select one data
	 * @param         @return
	 * @return        select's result of T
	 * @date          2016.08.21 17:24:14
	 */
	public T queryOne ();
	
	/**
	 * @Title                select
	 * @author               00013052
	 * @description          select datas
	 * @param database       connected database pool before inserting data
	 * @param filter         select condition, for example: {"title": {"$eq", 1}, {"name":"lolog"}}
	 * @param collectionName collection name
	 * @return               select's result of List<T>
	 * @date                 2016.7.13 11:30:13
	 */
	public List<Document> queryDocument ();
	
	/**
	 * @Title                delete
	 * @author               00013052
	 * @description          select datas
	 * @param database       connected database pool before inserting data
	 * @param filter         delete condition
	 * @param collectionName collection name, colletion name if T simple name if collectionName is null
	 * @return               get true if successfully deleting, delete all datas if filter id null
	 * @throws               exception
	 * @date                 2016.7.13 11:33:07
	 */
	public Boolean delete (Boolean...notMuti);
	
	/**
	 * @Title         count
	 * @author        00013052
	 * @description   query count of object
	 * @return        return datas count for filter condition
	 * @throws        exception
	 * @date          2016.7.13 17:13:36
	 */
	public Long count();
	
	/**
	 * @title         createIndex
	 * @author        lolog
	 * @description   create index
	 * @param t       object
	 * @return        index
	 * @throws        exception
	 * @date          2016.7.13 20:34:02
	 */
	public List<String> createIndex (T...t);
	
	/**
	 * @title         reCreateIndex
	 * @author        lolog
	 * @description   again create Index
	 * @param t       Object of contain annotation Index.class
	 * @return        return List<String> of Index 
	 * @date          2016.08.17 20:58:41
	 */
	public List<String> reCreateIndex (T... t);
	
	/**
	 * @title         queryIndex
	 * @author        lolog
	 * @description   get Indexes
	 * @return        return indexes
	 * @throws        exception
	 * @date          2016.7.13 21:10:33
	 */
	public Map<String, Object> queryIndex ();
	
	/**
	 * @title                getCollectionName
	 * @author               lolog
	 * @description          get T collection name
	 * @param collectionName collection name
	 * @return               return collection name
	 * @date                 2016.07.017 13:59:17
	 */
	public String getCollectionName (String... collectionName);
	
	/**
	 * @Title         getCollectionName
	 * @author        00013052
	 * @description   get database collection name
	 * @return        return list collection name
	 * @date          2016.08.15 15:01:11
	 */
	public List<String> getCollectionNames ();
}
