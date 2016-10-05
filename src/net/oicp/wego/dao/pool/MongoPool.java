package net.oicp.wego.dao.pool;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * @author         lolog
 * @version        V1.0  
 * @Date           2016年6月20日
 * @Company        CIMCSSC
 * @Description    MongoDB数据库连接池
 */
public interface MongoPool {
	/**
	 * @Title               getMongoClient
	 * @author              lolog
	 * @Description         获取MongoClient
	 * @return              返回链接的MongoClient
	 * @Date                2016年6月21日 上午10:22:45
	 */
	public MongoClient getMongoClient ();
	
	/**
	 * @Title        getDB
	 * @author       lolog
	 * @Description  获取链接的数据库
	 * @return       返回链接的数据库
	 * @Date         2016-6-20 18:04:07
	 */
	public MongoDatabase getDB();

	/**
	 * @Title              getDB
	 * @author             lolog
	 * @Description        获取链接的数据库
	 * @param databaseName 数据库名
	 * @param userName     用户名
	 * @param password     密码
	 * @return             返回链接的数据库
	 * @Date               2016-6-20 18:04:07
	 */
	public MongoDatabase getDB (String database, String userName, String password);
}
