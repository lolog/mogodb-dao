package net.oicp.wego.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import net.oicp.wego.dao.pool.MongoPool;

/**
 * @author 00013052
 * @version V1.0
 * @Date 2016年6月20日
 * @Company CIMCSSC
 * @Description TODO(文件描述)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/app.xml")
public class MongoAndSpringTest {
	@Resource
	private MongoPool mongoPool;
	@Resource
	private Date date;

	@Test
	public void springTest() {
		System.out.println(date);
	}

	@Test
	public void MongoTest() {
		mongoPool.getDB().createCollection("test");
	}

	@Test
	public void delateCollectionTest() {
		mongoPool.getDB().getCollection("test").drop();
		// MongoCollection<Document> collection =  mongoPool.getDB().getCollection("test");
		// collection.createIndex(keys, indexOptions)
		// collection.listIndexes(resultClass)
	}

	@Test
	public void insertData() {
		Document document = new Document("title", "MongoDB2")
				.append("description", "database2").append("likes", 102)
				.append("by", "Fly2");
		Document documents = new Document("title", "MongoDB3")
				.append("description", "database3").append("likes", 103)
				.append("by", "Fly3");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("nc", "lolog");
		map.put("sc", 1);
		
		documents.append("map", map);
		
		documents.append("append", document);

		List<Document> list = new ArrayList<Document>();
		list.add(documents);

		mongoPool.getDB().getCollection("test").insertMany(list);
		;
	}

	@Test
	public void queryDataTest() {
		BasicDBObject fields = new BasicDBObject();
		fields.put("likes", new BasicDBObject("$gt", 16));
		FindIterable<Document> iterable = mongoPool.getDB()
				.getCollection("test").find(fields);
		MongoCursor<Document> cursor = iterable.iterator();
		System.out.println(cursor);
		while (cursor.hasNext()) {
			Document document = cursor.next();
			
			System.out.println(document);
			
			if (document.get("append") != null) {
				System.out.println(document.get("append") instanceof Document);
			}
		}
	}
}
