/**
 * 
 */
package mongodb.client.service;

import jframe.mongodb.client.service.MongoClientService;
import jframe.mongodb.client.service.impl.MongoClientServiceImpl;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;

import com.mongodb.MongoClient;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoCursor;

/**
 * @author dzh
 * @date Aug 7, 2015 11:49:15 AM
 * @since 1.0
 */
public class TestMongoClientService {

	MongoClientService mongo;

	@Before
	public void init() {
		String path = Thread.currentThread().getContextClassLoader()
				.getResource("mongodb/client/service/mongoclient.properties")
				.getPath();
		mongo = MongoClientServiceImpl.test(path);
	}

	public void testDatabase() {
		MongoClient mongoClient = mongo.getClient("mongo1");
		ListDatabasesIterable<Document> list = mongoClient.listDatabases();
		MongoCursor<Document> iterD = list.iterator();
		while (iterD.hasNext()) {
			Document doc = iterD.next();
			System.out.println(doc);
			if (!doc.getBoolean("empty", true)) {
				System.out.println(mongoClient.getDatabase(doc
						.getString("name")));
			}
		}

		// MongoIterable<String> mongo = mongoClient.listDatabaseNames();
		// MongoCursor<String> iter = mongo.iterator();
		// while (iter.hasNext()) {
		// System.out.println(iter.next());
		// }
	}

	@After
	public void stop() {
		if (mongo != null)
			((MongoClientServiceImpl) mongo).stop();
	}

}
