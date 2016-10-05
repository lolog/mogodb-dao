package net.oicp.wego.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.print.Doc;

import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoNamespace;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.diagnostics.logging.Logger;

import net.oicp.wego.annotations.Collection;
import net.oicp.wego.dao.MongoDao;
import net.oicp.wego.dao.pool.MongoPool;
import net.oicp.wego.model.base.Keys;
import net.oicp.wego.util.PropertyContext;
import net.oicp.wego.util.impl.DocumentUtilImpl;
import sun.launcher.resources.launcher;

/** 
 * @author         lolog
 * @version        V1.0
 * @Date           2016.07.10
 * @Company        WEGO
 * @Description    MongoDB
*/
@SuppressWarnings({"unchecked", "rawtypes", "unused","null"})
public class MongoDaoImpl<T> extends DocumentUtilImpl<T> implements MongoDao<T> {
	// property auto ware
	@Autowired
	private PropertyContext propertyContext;
	
	@Autowired
	public void setMongoPool(MongoPool mongoPool) {
		database(mongoPool.getDB());
	}
	
	// get T Class
	private Class clazz;
	
	// update key,for example, $set, $push
	private ThreadLocal<String[]>            ekeys              = new ThreadLocal<String[]>();
	// CURD's condition
	private ThreadLocal<Map<String, Object>> filters            = new ThreadLocal<Map<String, Object>>();
	// skip offset
	private ThreadLocal<Integer>             skips              = new ThreadLocal<Integer>();
	// limit item
	private ThreadLocal<Integer>             limits             = new ThreadLocal<Integer>();
	// sort
	private ThreadLocal<Map<String, Object>> sorts              = new ThreadLocal<Map<String, Object>>();
	// one object of saving
	private ThreadLocal<T>                   data               = new ThreadLocal<T>();
	// more object of saving
	private ThreadLocal<List<T>>             datas              = new ThreadLocal<List<T>>();
	// more object of update
	private ThreadLocal<Map<String, Object>> mapDatas           = new ThreadLocal<Map<String, Object>>();
	// collection name for operating
	private ThreadLocal<String>              collectionNames    = new ThreadLocal<String>();
	// exclude field
	private ThreadLocal<String>              excludes           = new ThreadLocal<String>();
	// include field
	private ThreadLocal<String>              includes           = new ThreadLocal<String>();
	// distinct fieldName
	private ThreadLocal<String>              distinctFieldNames = new ThreadLocal<String>();
	// page
	private ThreadLocal<Integer>             pages              = new ThreadLocal<Integer>();
	private ThreadLocal<Integer>             rows               = new ThreadLocal<Integer>();
	// distinct to target Class
	private ThreadLocal<Class>               targetClasses      = new ThreadLocal<Class>();
	
	// have bean connected MongoDatabase
	private MongoDatabase database = null;
	
	
	public MongoDaoImpl() {
		// T
		ParameterizedType parameters = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class) parameters.getActualTypeArguments()[0];
	}
	
	public MongoDao ekeys (String[] ekeys) {
		this.ekeys.set(ekeys);
		return this;
	}
	
	@Override
	public Boolean renameCollection(String newCollection,String oldCollection) {
		// old collection name
		String collectionNameForT = null;
		
		if (oldCollection == null) {
			collectionNameForT = getCollectionName(this.collectionNames.get());
		}
		else {
			collectionNameForT = getCollectionName(oldCollection);
		}
		
		// collection
		MongoCollection<Document> collection = database.getCollection(collectionNameForT);
		
		// database name
		String databaseName = database.getName();
		// MongoDB namespace
		MongoNamespace mongoNamespace = new MongoNamespace(databaseName+"."+newCollection);
		
		if (collection == null) {
			collection.renameCollection(mongoNamespace);
		}
		else {
			gc(); // clear
			return false;
		}
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public MongoDao skip(Integer skip) {
		skips.set(skip);
		return this;
	}
	
	@Override
	public MongoDao limit(Integer limit) {
		limits.set(limit);
		return this;
	}
	
	@Override
	public MongoDao page(Integer page, Integer row) {
		pages.set(page);
		rows.set(row);
		return this;
	}
	
	@Override
	public MongoDao sort(Map<String, Object> sort) {
		// null
		if (sort == null || sort.size() == 0) { 
			return this; 
		}
		// sort
		sorts.set(sort);
		return this;
	}
	
	@Override
	public MongoDaoImpl filter (Map<String, Object> filter) {
		// null
		if (filter == null || filter.size() == 0) {
			return this;
		}
		filters.set(filter);
		return this;
		
	}
	@Override
	public MongoDaoImpl database (MongoDatabase database) {
		this.database = database;
		return this;
	}
	@Override
	public MongoDaoImpl data(T t) {
		data.set(t);
		return this;
	}
	@Override
	public MongoDao data(Map<String, Object> mapData) {
		mapDatas.set(mapData);
		return this;
	}
	@Override
	public MongoDaoImpl data (List<T> ts) {
		datas.set(ts);
		return this;
	}
	@Override
	public MongoDaoImpl collection (String collectionName) {
		collectionNames.set(collectionName);
		return this;
	}
	
	@Override
	public MongoDaoImpl exclude (String[] excludeArray) {
		// include target
		String exclude_target = "";
		
		for (int i=0; i<excludeArray.length; i++) {
			if(i == (excludeArray.length - 1)) {
				exclude_target += excludeArray[i];
			}
			else {
				exclude_target += excludeArray[i] + ",";
			}
		}
		
		excludes.set(exclude_target);
		
		return this;
	}
	@Override
	public MongoDaoImpl exclude (String exclude) {
		excludes.set(exclude);
		return this;
	}
	@Override
	public MongoDao include(String[] includeArray) {
		if(includeArray == null) {
			return this;
		}
		
		// include target
		String include_target = "";
		
		for (int i=0; i<includeArray.length; i++) {
			if(i == (includeArray.length - 1)) {
				include_target += includeArray[i];
			}
			else {
				include_target += includeArray[i] + ",";
			}
		}
		
		includes.set(include_target);
		return this;
	}
	@Override
	public MongoDao include(String include) {
		includes.set(include);
		return this;
	}
	@Override
	public MongoDaoImpl distinct (String fieldName, Class targetClass) {
		distinctFieldNames.set(fieldName);
		targetClasses.set(targetClass);
		return this;
	}
	
	// save object
	@Override
	public Boolean insert () {
		// data of saving don't allowed be null
		if ((data.get() == null && datas.get() == null) || database == null) {
			gc(); // clear
			return false;
		}
		
		// get collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			database.createCollection(collectionNameForT);
			this.createIndex();
		}
		
		// save object
		if(data.get() == null) {
			List<Document> documents = ekeyObjectsToDocuments(datas.get(), false, null, excludes.get(), includes.get());
			database.getCollection(collectionNameForT).insertMany(documents);
		}
		else {
			Document document = ekeyObjectToDocument(data.get(), false, null, excludes.get(), includes.get());
			database.getCollection(collectionNameForT).insertOne(document);
		}
		
		gc(); // clear
		
		return true;
	}
	
	// save object
	@Override
	public Boolean insert (T t) {
		// data of saving don't allowed be null
		if ((t.getClass() != clazz)||(t == null && this.data.get() == null) || database == null) {
			gc(); // clear
			return false;
		}
		
		// get collection name
		String collectionNameForT = getCollectionName(this.collectionNames.get());
		
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			database.createCollection(collectionNameForT);
			this.createIndex();
		}
				
		// get object of saving
		Document document = null;
		
		if(t == null) {
			document = ekeyObjectToDocument(this.data.get(), false, null, this.excludes.get(), includes.get());
		}
		else {
			document = ekeyObjectToDocument(t, false, null, this.excludes.get(), includes.get());
		}
		
		if (document == null || document.size() < 1) {
			return false;
		}
		
		// save object
		database.getCollection(collectionNameForT).insertOne(document);
		
		gc(); // clear
		
		return true;
	}
	
	// save object
	@Override
	public Boolean insert (List<T> ts) {
		// data of saving don't allowed be null
		if ((ts == null && this.datas.get() == null) || database == null) {
			gc(); // clear
			return false;
		}
		
		// get collection name
		String collectionNameForT = getCollectionName(this.collectionNames.get());
		
		// create Index flag
		Boolean flag = false;
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			database.createCollection(collectionNameForT);
		}
		
		// get object of saving
		List<Document> documents = null;
		
		if(data.get() == null) {
			documents = ekeyObjectsToDocuments(this.datas.get(), false, null, this.excludes.get(), includes.get());
		}
		else {
			documents = ekeyObjectsToDocuments(ts, false, null, this.excludes.get(), includes.get());
		}
		
		if (documents == null || documents.size() < 1) {
			return false;
		}
		
		Iterator<Document> iterator = documents.iterator();
		while (iterator.hasNext()) {
			Document document = iterator.next();
			if (document == null || document.size() < 1) {
				return false;
			}
		}
		
		// save object
		database.getCollection(collectionNameForT).insertMany(documents);
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public Boolean update (Boolean...notMuti) {
		// database,filter, data of inserting don't allow be null
		if (database == null || filters.get() == null || (data.get() == null && (datas.get() == null || datas.get().size() < 1))) {
			gc(); // clear
			return false;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return false;
		}
				
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// get update result
		UpdateResult result = null;
		
		try {
			if (data.get() == null) {
				// get Document of ts[0]
				Document document = ekeyObjectToDocument(datas.get().get(0), true, ekeys.get(), excludes.get(), includes.get());
				
				// judge
				if (document == null || document.size() < 1) {
					return false;
				}
				
				// update many datas
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				
				result.getMatchedCount();
			}
			else {
				Document document = ekeyObjectToDocument(data.get(), true, ekeys.get(), excludes.get(), includes.get());
				
				// judge
				if (document == null || document.size() < 1) {
					return false;
				}
				
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
			}
		} catch (Exception e) {
			
		}
		
		if (result == null
			|| result.getModifiedCount() == 0
			|| result.getMatchedCount()  == 0) {
			gc(); // clear
			return false;
		}
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public Boolean update (Map<String, Object> data, Boolean...notMuti) {
		// database,filter, data of inserting don't allow be null
		if (database == null || filters.get() == null || (mapDatas.get() == null && data == null)) {
			gc(); // clear
			return false;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return false;
		}
		
		Map<String, Object> updateData = (data == null ? mapDatas.get() : data);
		
		Document document = new Document(updateData);
		
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// get update result
		UpdateResult result = null;
		
		try {
			// update many datas
			if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
				result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
			}
			else {
				result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
			}
		} catch (Exception e) {
			
		}
		
		if (result == null
			|| result.getModifiedCount() == 0
			|| result.getMatchedCount()  == 0
		) {
			gc(); // clear
			return false;
		}
		
		gc(); // clear
		return true;
	}
	
	@Override
	public Boolean update (T t, Boolean...notMuti) {
		// database,filter, data of inserting don't allow be null
		if (database == null || filters.get() == null || (t == null && this.data.get() == null)) {
			gc(); // clear
			return false;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return false;
		}
				
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// get update result
		UpdateResult result = null;
		
		try {
			if (t == null) {
				// get Document of ts[0]
				Document document = ekeyObjectToDocument(this.data.get(), true, ekeys.get(), excludes.get(), includes.get());
				
				if (document == null || document.size() < 1) {
					return false;
				}
				
				// update many datas
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
			}
			else {
				Document document = ekeyObjectToDocument(t, true, ekeys.get(), excludes.get(), includes.get());
				
				if (document == null || document.size() < 1) {
					return false;
				}
				
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
			}
		} catch (Exception e) {
			
		}
		
		if (result == null) {
			gc(); // clear
			return false;
		}
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public Boolean update (List<T> ts, Boolean...notMuti) {
		// database,filter, data of inserting don't allow be null
		if (database == null || filters.get() == null 
				|| (ts == null || ts.size() < 1)
				|| (this.datas.get() == null || this.datas.get().size() < 1)) {
			gc(); // clear
			return false;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return false;
		}
		
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// get update result
		UpdateResult result = null;
		
		try {
			if (ts == null || ts.size() < 1) {
				// get Document of ts[0]
				Document document = ekeyObjectToDocument(this.datas.get().get(0), true, ekeys.get(), excludes.get(), includes.get());
				
				// judge
				if (document == null || document.size() < 1) {
					return false;
				}
				
				// update many datas
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
			}
			else {
				Document document = ekeyObjectToDocument(ts.get(0), true, ekeys.get(), excludes.get(), includes.get());
				
				// judge
				if (document == null || document.size() < 1) {
					return false;
				}
				
				if (notMuti == null || notMuti.length == 0 || notMuti[0] == false) {
					result = database.getCollection(collectionNameForT).updateOne(filterBson, document);
				}
				else {
					result = database.getCollection(collectionNameForT).updateMany(filterBson, document);
				}
			}
		} catch (Exception e) {
			
		}
		
		if (result == null) {
			gc(); // clear
			return false;
		}
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public List<T> query () {
		if (database == null) {
			gc(); // clear
			return new ArrayList<T>();
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return new ArrayList<T>();
		}
		
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// result of find
		FindIterable     iterable = null;
		// distinct result
		DistinctIterable diterable = null;
		
		// collection
		MongoCollection<Document> collection = database.getCollection(collectionNameForT);
		
		if (filterBson == null) {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find();
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), targetClasses.get());
			}
		}
		else {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find(filterBson);
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), filterBson, targetClasses.get());
			}
		}
		
		// data sort
		if (sorts.get() != null) {
			iterable = iterable.sort(new Document(sorts.get()));
		}
		
		// ship operate
		if (skips.get() != null) {
			iterable = (iterable == null) ? iterable : iterable.skip(skips.get());
		}
		
		// item limit
		if (limits.get() != null && limits.get() != 0) {
			iterable = (iterable == null) ? iterable : iterable.limit(limits.get());
		}
		
		// deal with simple query data
		if (iterable != null) {
			List<T> list = new ArrayList<T> ();
			
			Iterator iterator = iterable.iterator();
			
			// cycle
			while (iterator.hasNext()) {
				Document document = (Document)iterator.next();
				T tt = documentToObject(document, excludes.get(), includes.get());
				list.add(tt);
			}
			
			gc(); // clear
			return list;
		}
		
		// deal with distinct query data
		if (diterable != null) {
			List<T> list = new ArrayList<T> ();
			
			Iterator iterator = diterable.iterator();
			
			// cycle
			while (iterator.hasNext()) {
				T tt = documentToObject((Document)iterator.next(), excludes.get(), includes.get());
				list.add(tt);
			}
			
			gc(); // clear
			return list;
		}
		
		gc(); // clear
		
		return new ArrayList<T>();
	}
	
	@Override
	public T queryOne () {
		if (database == null) {
			gc(); // clear
			return null;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return null;
		}
		
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// result of find
		FindIterable     iterable = null;
		// distinct result
		DistinctIterable diterable = null;
		
		// collection
		MongoCollection<Document> collection = database.getCollection(collectionNameForT);
		
		if (filterBson == null) {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find();
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), targetClasses.get());
			}
		}
		else {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find(filterBson);
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), filterBson, targetClasses.get());
			}
		}
		
		// data sort
		if (sorts.get() != null) {
			iterable = iterable.sort(new Document(sorts.get()));
		}
		
		// ship operate
		if (skips.get() != null) {
			iterable = (iterable == null) ? iterable : iterable.skip(skips.get());
		}
		
		// item limit
		if (limits.get() != null && limits.get() != 0) {
			iterable = (iterable == null) ? iterable : iterable.limit(limits.get());
		}
		
		// deal with simple query data
		if (iterable != null) {
			Document document = (Document) iterable.first();
			
			T tt = documentToObject(document, excludes.get(), includes.get());
			
			gc(); // clear
			return tt;
		}
		
		// deal with distinct query data
		if (diterable != null) {
			Document document = (Document) diterable.first();
			
			T tt = documentToObject(document, excludes.get(), includes.get());
			
			gc(); // clear
			return tt;
		}
		
		gc(); // clear
		
		return null;
	}
	
	@Override
	public List<Document> queryDocument () {
		
		if (database == null) {
			gc(); // clear
			return new ArrayList<Document>();
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return new ArrayList<Document>();
		}
				
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		// result of find
		FindIterable     iterable = null;
		// distinct result
		DistinctIterable diterable = null;
		
		// collection
		MongoCollection<Document> collection = database.getCollection(collectionNameForT);
		
		// { "$nin" : { "is_del" : [  null  , true]}}
		if (filterBson == null) {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find();
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), targetClasses.get());
			}
		}
		else {
			// simple query
			if (this.distinctFieldNames.get() == null || this.targetClasses.get() == null) {
				iterable = collection.find(filterBson);
			}
			// distinct query
			else {
				diterable = collection.distinct(distinctFieldNames.get(), filterBson, targetClasses.get());
			}
		}
		
		// data sort
		if (sorts.get() != null) {
			iterable = iterable.sort(new Document(sorts.get()));
		}
		
		// ship operate
		if (skips.get() != null) {
			iterable = (iterable == null) ? iterable : iterable.skip(skips.get());
		}
		
		// item limit
		if (limits.get() != null && limits.get() != 0) {
			iterable = (iterable == null) ? iterable : iterable.limit(limits.get());
		}
		
		// deal with simple query data
		if (iterable != null) {
			List<Document> list = new ArrayList<Document> ();
			Iterator iterator = iterable.iterator();
			
			// cycle
			while (iterator.hasNext()) {
				// get the result
				Document document = (Document) iterator.next();
				// filter
				document = filterDocument(document, excludes.get(), includes.get(), propertyContext.regex_time());
				// add it to list document
				list.add(document);
			}
			
			gc(); // clear
			return list;
		}
		
		// deal with distinct query data
		if (diterable != null) {
			List<Document> list = new ArrayList<Document> ();
			
			Iterator iterator = diterable.iterator();
			
			// cycle
			while (iterator.hasNext()) {
				// get the result
				Document document = (Document) iterator.next();
				// filter
				document = filterDocument(document, excludes.get(), includes.get(), propertyContext.regex_time());
				// add it to list document
				list.add(document);
			}
			
			gc(); // clear
			return list;
		}
		
		gc(); // clear
		
		return new ArrayList<Document>();
	}
	
	@Override
	public Boolean delete (Boolean...notMuti) {
		if (database == null || filters.get() == null) {
			gc(); // clear
			return false;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		if (filterBson == null) {
			// delete collection
			database.getCollection(collectionNameForT).drop();
			// create collection
			database.createCollection(collectionNameForT);
		}
		else if (notMuti == null || notMuti.length < 0 || notMuti[0] == null || notMuti[0] == false) {
			database.getCollection(collectionNameForT).deleteMany(filterBson);
		}
		else {
			database.getCollection(collectionNameForT).deleteOne(filterBson);
		}
		
		gc(); // clear
		
		return true;
	}
	
	@Override
	public Long count () {
		if (database == null) {
			gc(); // clear
			return 0L;
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		// judge collection whether it's exited
		List<String> collections = getCollectionNames();
		// create index if the collection is not exited
		if (collections == null || collections.size() == 0 || collections.contains(collectionNameForT) == false) {
			gc(); // clear
			return 0L;
		}
				
		// condition
		Bson filterBson = mapToBson(filters.get());
		
		Long count = 0L;
		
		if (filterBson == null) {
			count = database.getCollection(collectionNameForT).count();
		}
		else {
			count = database.getCollection(collectionNameForT).count(filterBson);
		}
		
		gc(); // clear
		return count;
	}
	
	@Override
	public List<String> createIndex (T... t) {
		T theT = null;
		
		if (t == null || t.length < 1 || t[0] == null) {
			try {
				// new Instance
				theT = (T) Class.forName(clazz.getName()).newInstance();
			} catch (Exception e) {
				gc(); // clear data
				throw new RuntimeException(e);
			} 
		}
		else {
			theT = t[0];
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		
		List<String> index = new ArrayList<String>();
		
		List<IndexModel> indexs = getIndexs(theT);
		if (indexs.isEmpty() == false) {
			index = database.getCollection(collectionNameForT).createIndexes(indexs);
		}
		
		return index;
	}
	
	@Override
	public List<String> reCreateIndex (T... t) {
		T theT = null;
		
		if (t == null || t.length < 1 || t[0] == null) {
			try {
				// new Instance
				theT = (T) Class.forName(clazz.getName()).newInstance();
			} catch (Exception e) {
				gc(); // clear data
				throw new RuntimeException(e);
			} 
		}
		else {
			theT = t[0];
		}
		
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		
		List<IndexModel> indexs   = getIndexs(theT);
		
		// delete collection indexes
		database.getCollection(collectionNameForT).dropIndexes();
		List<String>     index    = database.getCollection(collectionNameForT).createIndexes(indexs);
		
		// clear garbage
		gc();
		
		return index;
	}
	
	@Override
	public Map<String, Object> queryIndex () {
		// get current collection name
		String collectionNameForT = getCollectionName(collectionNames.get());
		
		// Indexes Iterator
		ListIndexesIterable iterable = database.getCollection(collectionNameForT).listIndexes();
		
		// cycle the indexes
		Iterator iterator = iterable.iterator();
		
		List<String> indexes    = new ArrayList<String>();
		List<Keys>   keys       = new ArrayList<Keys>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		while (iterator.hasNext()) {
			// read value
			Document document = (Document) iterator.next();
			Keys indexKey = new Keys();
			
			// cycle key
			for (String key :document.get("key", Document.class).keySet()) {
				indexes.add(key);
				indexKey.setKey(key);
				// key's order value
				indexKey.setOrder(document.get("key", Document.class).getInteger(key));
			}
			
			indexKey.setName(document.getString("name"));
			indexKey.setNs(document.getString("ns"));
			indexKey.setBackground(document.getBoolean("background"));
			indexKey.setSparse(document.getBoolean("sparse"));
			
			keys.add(indexKey);
		}
		map.put("indexes", indexes);
		map.put("keys", keys);
		
		// gc
		gc();
		
		return map;
	}
	
	@Override
	public String getCollectionName (String... collectionName) {
		// collection name
		String collectionNameTemp = null;
		
		// get collection name
		if (collectionName == null || collectionName.length == 0) {
			// 获取Collection注解值
			if (clazz.isAnnotationPresent(Collection.class)) {
				Collection collection = (Collection) clazz.getAnnotation(Collection.class);
				collectionNameTemp    = collection.name();
			}
		}
		else {
			collectionNameTemp = collectionName[0];
		}
		
		// T class simple name
		if (collectionNameTemp == null || collectionNameTemp.length() == 0) {
			collectionNameTemp = clazz.getSimpleName();
		}
		
		// collection separator is null
		if (propertyContext.db_depr() == null || propertyContext.db_depr().length() == 0 ) {
			return (propertyContext.db_prefix() == null) ? collectionNameTemp : (propertyContext.db_prefix() + collectionNameTemp);
		}
		
		// collection name of lower character
		String table = collectionNameTemp.toLowerCase();
		// split collection name
		StringTokenizer tokenizer = new StringTokenizer(collectionNameTemp, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		
		// collection name
		String theCollectionName = "";
		
		// cycle deal with the collection name
		while (tokenizer.hasMoreTokens()) {
			// get the separator one
			String search = tokenizer.nextToken();
			
			// search in position 
			Integer index = table.indexOf(search);
			
			// first character is lower 
			if (index == 0) {
				theCollectionName = search;
				continue;
			}
			
			// upper character change to lower character
			Character s = (char) (collectionNameTemp.charAt(index - 1) + 32);
			
			// first deal with,don't add database separator separator
			if (theCollectionName == "") {
				theCollectionName = (propertyContext.db_prefix() == null) ? (s+search) : (propertyContext.db_prefix()+s+search);
			}
			// other
			else {
				theCollectionName += propertyContext.db_depr()+s+search; 
			}
		}
		
		return theCollectionName;
	}
	
	@Override
	public List<String> getCollectionNames () {
		List<String> collectionNames = new ArrayList<String>();
		
		if (database == null) {
			return collectionNames;
		}
		
		// Collection Name
		Iterator iterator = database.listCollectionNames().iterator();
		while (iterator.hasNext()) {
			collectionNames.add((String)iterator.next());
		}
		
		return collectionNames;
	}
	
	private Boolean gc() {
		filters.remove();
		skips.remove();
		limits.remove();
		sorts.remove();
		data.remove();
		datas.remove();
		mapDatas.remove();
		collectionNames.remove();
		excludes.remove();
		includes.remove();
		distinctFieldNames.remove();
		ekeys.remove();
		pages.remove();
		rows.remove();
		targetClasses.remove();
		return true;
	}
}
