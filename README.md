## 1. Description:
mongodb version requirements 3.x.x, mainly used in the Spring environment.
To achieve object-oriented development, database objects to support the base class abstract inheritance. Java cascade approach to the operation of the database, including the field of screening and screening conditions, the operation of data addition, deletion, modification and query.

## 2. 环境
* MongoDB 3.0+
* JDK 6.0+
## 3. 配置
###3.1 MongoDB链接配置
```xml
## database name
mongo_host=127.0.0.1
mongo_port=27017
mongo_user=kolap
mongo_pwd=kolap
mongo_db=wego
mongo_depr=_
mongo_prefix=go_
```
### 3.2 MongoDB连接池配置
```xml
<bean id="mongoPool" class="net.oicp.wego.dao.pool.impl.MongoPoolImpl">
	<property name="host" value="#{conf.mongo_host}" />
	<property name="port" value="#{conf.mongo_port}" />
	<property name="userName" value="#{conf.mongo_user}" />
	<property name="password" value="#{conf.mongo_pwd}" />
	<property name="dbName"   value="#{conf.mongo_db}" />
	<property name="connectionsPerHost" value="10" />
	<property name="threadsAllowedToBlockForConnectionMultiplier" value="15" />
</bean>
```
### 3.3 MongoDB监听器配置
```xml
<!-- 拦截器的配置 -->
<mvc:interceptors>
	<!-- mongodb链接对象的拦截器 -->
	<bean id="mongoListener" class="net.oicp.wego.listener.MongoListener"/>
</mvc:interceptors>
```
## 4. 注解
### 4.1 Collection(文档注解):
name：注解名

### 4.2 EKey(更新对象注解):
* value:更新对象键
* index:更新排序,1 --> 递增,-1 --> 递减
* priority:索引的级别

### 4.3 Index(索引注解):
* key:是否是索引
* unique:时候是唯一字段
* order:排序方式,1 --> 递增,-1 --> 递减:索引
* name:索引名
* id:是否是ObjectID
* omission:omission
* background:后台方式创建索引
* sparse:文档中不存在的字段数据不启用索引。设置为true,索引字段中不会查询出不包含对应字段的文档

## 5. MongoDao接口
```Java
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
	 * @author        lolog
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
	 * @author         lolog
	 * @description    set mongodb database
	 * @param database connected database
	 * @return         MongoDao
	 * @throws         exception
	 * @date           2016.7.13 16:59:03
	 */
	public MongoDao database (MongoDatabase database);
	
	/**
	 * @Title               data
	 * @author              lolog
	 * @description         set data of operation
	 * @param t             single data
	 * @return              MongoDao
	 * @throws              exception
	 * @date                2016.7.13 16:59:52
	 */
	public MongoDao data (T t);
	
	/**
	 * @Title               data
	 * @author              lolog
	 * @description         set data of operation
	 * @param t             single data
	 * @return              MongoDao
	 * @throws              exception
	 * @date                2016.9.1 10:59:52
	 */
	public MongoDao data (Map<String, Object> mapData);
	
	/**
	 * @Title                data
	 * @author               lolog
	 * @description          set data of operation
	 * @param ts             mutil data
	 * @return               MongoDao
	 * @throws               exception
	 * @date                 2016.7.13 17:00:40
	 */
	public MongoDao data (List<T> ts);
	
	/**
	 * @Title                collection
	 * @author               lolog
	 * @description          set collection name
	 * @param collectionName collection name
	 * @return               MongoDao
	 * @throws               exception
	 * @date                 2016.7.13 17:02:05
	 */
	public MongoDao collection (String collectionName);
	
	/**
	 * @Title         exclude
	 * @author        lolog
	 * @description   exclude field
	 * @param exclude exclude field,for example:"name,title"
	 * @return        MongoDao
	 * @date          2016.7.13 17:08:09
	 */
	public MongoDao exclude (String exclude);
	
	/**
	 * @Title              exclude
	 * @author             lolog
	 * @description        exclude field
	 * @param excludeArray exclude field,for example:{"name","title"}
	 * @return             MongoDao
	 * @date               2016.7.13 17:08:09
	 */
	public MongoDao exclude (String[] excludeArray);
	
	/**
	 * @Title         exclude
	 * @author        lolog
	 * @description   include field
	 * @param include include field,for example:"name,title"
	 * @return        MongoDao
	 * @throws        exception
	 * @date          2016.7.13 17:08:09
	 */
	public MongoDao include (String include);
	
	/**
	 * @Title              exclude
	 * @author             lolog
	 * @description        include field
	 * @param includeArray include field,for example:{"name","title"}
	 * @return             MongoDao
	 * @date               2016.7.13 17:08:09
	 */
	public MongoDao include (String[] includeArray);
	
	/**
	 * @Title              distinct
	 * @author             lolog
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
	 * @author        lolog
	 * @description   save datas
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:05:04
	 */
	public Boolean insert ();
	
	/**
	 * @Title         insert
	 * @author        lolog
	 * @description   save datas
	 * @param t       single data of saving
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:05:54
	 */
	public Boolean insert (T t);
	
	/**
	 * @Title         insert
	 * @author        lolog
	 * @description   save datas
	 * @param ts      more datas of saving
	 * @return        get true if successfully save
	 * @throws        exception
	 * @date          2016.7.13 17:06:33
	 */
	public Boolean insert (List<T> ts);
	
	/**
	 * @Title         update
	 * @author        lolog
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
	 * @author        lolog
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
	 * @author        lolog
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
	 * @author        lolog
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
	 * @author               lolog
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
	 * @author               lolog
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
	 * @author               lolog
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
	 * @author        lolog
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
	 * @author        lolog
	 * @description   get database collection name
	 * @return        return list collection name
	 * @date          2016.08.15 15:01:11
	 */
	public List<String> getCollectionNames ();
```

## 6.Demo
### 6.1 Model of MongoDB Document
```Java
public class Keys {
	private String key;
	private Integer order;
	private String name;
	private String ns;
	private Boolean background;
	private Boolean sparse;
	
	public Keys() {
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	// All set and get methods ......
	
	@Override
	public String toString() {
		return "Keys [key=" + key + ", order=" + order + ", name=" + name + ", ns=" + ns + ", background=" + background
				+ ", sparse=" + sparse + "]";
	}
}
```
```Java
public class Base {
	@Index(id=true)
	private String id;
	// manager id of add 
	private String addy_id;
	// manager name of add
	private String addy_name;
	// add time
	private Date add_time;
	// edit manager
	@EKey("$push")
	List<Editor> editors;
	// delete label
	@Index(key=false)
	private Boolean is_del;
	// forbidden label
	@Index(key=false)
	private Boolean is_forbidden;
	
	public Base() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	// All set and get methods ......

	@Override
	public String toString() {
		return "Base [id=" + id + ", addy_id=" + addy_id + ", addy_name=" + addy_name + ", add_time=" 
		+ add_time + ", editors=" + editors + ", is_del=" + is_del + ", is_forbidden=" + is_forbidden 
		+ "]";
	}
}
```
```Java
@EKey("$set")
public class Currency extends Base {
	@Index(unique = true)
	private String currency_id;
	@Index(unique = true)
	private String name;
	private Double rate;

	public Currency() {
	}

	public String getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}
	
	// All set and get methods ......

	@Override
	public String toString() {
		return "Currency [currency_id=" + currency_id + ", name=" + name + ", rate=" + rate + ", Base [id=" 
		+ getId()+ ", addy_id=" + getAddy_id() + ", add_time=" + getAdd_time() + ", editors=" + getEditors() 
		+ ", is_del=" + getIs_del() + ", is_forbidden=" + getIs_forbidden() + "]]";
	}
}
```
```Java
public interface CurrencyDao extends MongoDao<Currency> {

}

@Service("currencyDao")
public class CurrencyDaoImpl extends MongoDaoImpl<Currency> implements CurrencyDao {

}
```
```Java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:config/app.xml")
public class CurrencyDaoTest {
	@Resource
	private CurrencyDao currencyDao;
	
	@Test
	public void insertTest () {
		Currency currency = new Currency();
		
		currency.setName("港币");
		currency.setRate(.857);
		currency.setCurrency_id("current_id0");
		
		Editor editor = new Editor();
		editor.setEdit_id("xxx");
		editor.setEdit_name("lolog");
		editor.setEdit_time(new Date());
		
		List<Editor> editors = new ArrayList<Editor>();
		editors.add(editor);
		
		currency.setEditors(editors);
		
		currencyDao.insert(currency);
	}
	
	@Test
	public void queryTest () {
		Map<String, Object> filter = new HashMap<String, Object>();
		// filter.put("name", "人民币");
		filter.put("editors.$.edit_name", new HashMap<>().put("$eq", "lolog"));
		System.out.println(currencyDao.filter(filter).query());;
	}
	
	@Test
	public void queryIndexTest () {
		System.out.println(currencyDao.queryIndex());;
	}
}
```