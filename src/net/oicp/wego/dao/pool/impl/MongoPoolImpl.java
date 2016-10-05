package net.oicp.wego.dao.pool.impl;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import net.oicp.wego.dao.pool.MongoPool;

public class MongoPoolImpl implements MongoPool{
	// Mongo
	private MongoClient mongoClient;

	// host
	private String host;
	// port
	private Integer port;
	// username
	private String userName;
	// password
	private String password;
	// database name
	private String dbName;
	// connected number of one host
	private Integer connectionsPerHost;
	// thread number
	private Integer threadsAllowedToBlockForConnectionMultiplier;
	// max wait time
	private Integer maxWaitTime;
	// connect timeout
	private Integer connectTimeout;
	// socket timeout
	private Integer socketTimeout;
	// socket keep alive
	private Boolean socketKeepAlive;

	public MongoPoolImpl () {
	}

	@Override
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	/**
	 * @Title               getDB
	 * @author              00013052
	 * @Description         connected database
	 * @return              return connected database
	 * @Date                2016.06.20 18:16:04
	 */
	public MongoDatabase getDB() {
		if (mongoClient == null) {
			mongoClient = new MongoClient(
					getServerAddress(), 
					setCredential(userName, dbName, password), 
					getOptions()
				);
		}
		return mongoClient.getDatabase(dbName);
	}

	/**
	 * @Title               getDB
	 * @author              00013052
	 * @Description         connected database
	 * @param databaseName  database name
	 * @param userName      user name
	 * @param password      password
	 * @return              return connected database 
	 * @Date                2016.06.20 18:04:07
	 */
	public MongoDatabase getDB (String database, String userName, String password) {
		if (database == null) {
			database = this.dbName;
		}
		if (userName == null) {
			userName = this.userName;
		}
		if (password == null) {
			password = this.password;
		}
		mongoClient = new MongoClient(
				getServerAddress(), 
				setCredential(
						userName, 
						database, 
						password
					), 
				getOptions()
			);
		return mongoClient.getDatabase(dbName);
	}

	/**
	 * @Title               setCredential
	 * @author              00013052
	 * @Description         user login setting
	 * @param userName      user name
	 * @param database      database
	 * @param password      password
	 * @return              user authentication
	 * @Date                2016.06.21 08:36:42
	 */
	private List<MongoCredential> setCredential(String userName, String database, String password) {
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		MongoCredential credential = MongoCredential.createScramSha1Credential(
				userName, 
				database, 
				password.toCharArray()
			);
		credentials.add(credential);
		return credentials;
	}

	/**
	 * @Title               getServerAddress
	 * @author              00013052
	 * @Description         set client address
	 * @return              client address
	 * @Date                2016.06.21 08:38:31
	 */
	private ServerAddress getServerAddress() {
		if(host == null) {
			this.host = "127.0.0.1";
		}
		if (port == null) {
			this.port = 27017;
		}
		return new ServerAddress(host, port);
	}

	private MongoClientOptions getOptions() {
		Builder builder = new Builder();
		
		if (this.connectionsPerHost != null) {
			builder.connectionsPerHost(connectionsPerHost);
		}
		if (this.threadsAllowedToBlockForConnectionMultiplier != null) {
			builder.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);
		}
		if (this.maxWaitTime != null) {
			builder.maxWaitTime(maxWaitTime);
		}
		if (this.connectTimeout != null) {
			builder.connectTimeout(connectTimeout);
		}
		if (this.socketTimeout != null) {
			builder.socketTimeout(socketTimeout);
		}
		if (this.socketKeepAlive != null) {
			builder.socketKeepAlive(socketKeepAlive);
		}
		// database configure
		return builder.build();
	}

	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setConnectionsPerHost(Integer connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public void setThreadsAllowedToBlockForConnectionMultiplier(
			Integer threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	public void setMaxWaitTime(Integer maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setSocketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setSocketKeepAlive(Boolean socketKeepAlive) {
		this.socketKeepAlive = socketKeepAlive;
	}
}
