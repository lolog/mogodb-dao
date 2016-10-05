## 1. Description:
mongodb version requirements 3.x.x, mainly used in the Spring environment.
To achieve object-oriented development, database objects to support the base class abstract inheritance. Java cascade approach to the operation of the database, including the field of screening and screening conditions, the operation of data addition, deletion, modification and query.

## 2. 环境
MongoDB 3.0+
JDK 6.0+
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
## 5.