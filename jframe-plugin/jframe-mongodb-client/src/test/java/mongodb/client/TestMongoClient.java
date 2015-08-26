/**
 * 
 */
package mongodb.client;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.After;
import org.junit.Before;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

/**
 * @author dzh
 * @data Jul 6, 2015 3:50:35 PM
 * @since 1.0
 */
public class TestMongoClient {

	static MongoClient mongoClient;

	@Before
	public void start() {
		// MongoClientOptions.builder().cursorFinalizerEnabled(false);
		// MongoClientURI uri = new MongoClientURI();

		// mongoClient = new MongoClient();
		// mongoClient.setOptions();

		// MongoClientURI connectionString = new MongoClientURI(
		// "mongodb://localhost:27017,localhost:27018,localhost:27019/");
		MongoClientURI connectionString = new MongoClientURI(
				"mongodb://localhost:27017/");
		mongoClient = new MongoClient(connectionString);

	}

	public void testDatabase() {
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

	public void testCarStatus() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromCodecs(new UuidCodec(
						UuidRepresentation.STANDARD)), MongoClient
						.getDefaultCodecRegistry());

		MongoDatabase rent = mongoClient.getDatabase("lech_rent")
				.withCodecRegistry(codecRegistry);
		MongoCollection<Document> status = rent.getCollection("car_status");

	}

	public void testDriverStatus() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromCodecs(new UuidCodec(
						UuidRepresentation.STANDARD)), MongoClient
						.getDefaultCodecRegistry());

		mongoClient.getDatabase("lech_rent").drop();
		MongoDatabase rent = mongoClient.getDatabase("lech_rent")
				.withCodecRegistry(codecRegistry);
		// rent.createCollection("driver_status", new CreateCollectionOptions()
		// .capped(true).sizeInBytes(0x100000));
		MongoCollection<Document> status = rent.getCollection("driver_status");
		status.deleteMany(Filters.eq("mobile", "18616020610"));
		if (status.count() == 0) {

		}
		status.createIndex(new Document("mobile", "text"));
		// status.createIndex(new Document("no", "text"));
		for (final Document index : status.listIndexes()) {
			System.out.println(index.toJson());
		}

		Document doc = new Document("loc",
				new Document("type", "Point").append("coordinates",
						Arrays.asList(-73.97, 40.77))).append("no", "dno")
				.append("usrImg", "/usr/driver.png")
				.append("mobile", "18616020610").append("status", 7)
				.append("car", new Document("no", "A00001"));
		status.insertOne(doc);
		// status.createIndex(keys);
		doc = status.find(Filters.eq("mobile", "18616020610")).first();

		System.out.println(doc.get("loc", Document.class).get("coordinates"));
		System.out.println(doc.get("loc", Document.class).get("coordinates",
				ArrayList.class));
		System.out.println(doc.get("car", Document.class));
		// System.out.println(doc.get("loc", Document.class));

		UpdateResult updateResult = status.updateOne(Filters.eq("mobile",
				"18616020610"), new Document("$set", new Document("car",
				new Document("no", "A00002"))));
		doc = status.find(Filters.eq("mobile", "18616020610")).first();
		System.out.println(doc.get("car", Document.class));

		// updateResult = status.updateMany(Filters.lt("i", 100), new Document(
		// "$inc", new Document("i", 100)));
		// System.out.println(updateResult.getModifiedCount());
		// DeleteResult deleteResult = status.deleteOne(Filters.eq("i", 110));
		// System.out.println(deleteResult.getDeletedCount());

		// 2. Ordered bulk operation - order is guarenteed
		// status.bulkWrite(Arrays.asList(new InsertOneModel<>(new
		// Document("_id",
		// 4)), new InsertOneModel<>(new Document("_id", 5)),
		// new InsertOneModel<>(new Document("_id", 6)),
		// new UpdateOneModel<>(new Document("_id", 1), new Document(
		// "$set", new Document("x", 2))), new DeleteOneModel<>(
		// new Document("_id", 2)),
		// new ReplaceOneModel<>(new Document("_id", 3), new Document(
		// "_id", 3).append("x", 4))));

		// 2. Unordered bulk operation - no guarantee of order of operation
		// status.bulkWrite(Arrays.asList(new InsertOneModel<>(new
		// Document("_id",
		// 4)), new InsertOneModel<>(new Document("_id", 5)),
		// new InsertOneModel<>(new Document("_id", 6)),
		// new UpdateOneModel<>(new Document("_id", 1), new Document(
		// "$set", new Document("x", 2))), new DeleteOneModel<>(
		// new Document("_id", 2)),
		// new ReplaceOneModel<>(new Document("_id", 3), new Document(
		// "_id", 3).append("x", 4))), new BulkWriteOptions()
		// .ordered(false));

	}

	public void testUsrStatus() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromCodecs(new UuidCodec(
						UuidRepresentation.STANDARD)), MongoClient
						.getDefaultCodecRegistry());

		MongoDatabase rent = mongoClient.getDatabase("lech_rent")
				.withCodecRegistry(codecRegistry);
		MongoCollection<Document> status = rent.getCollection("usr_status");

	}

	public void testWithCodec() {
		// MongoDatabase db = mongoClient.getDatabase("mydb");
		// db.getCollection("test");

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromCodecs(new UuidCodec(
						UuidRepresentation.STANDARD)), MongoClient
						.getDefaultCodecRegistry());
		// globally
		MongoClientOptions options = MongoClientOptions.builder()
				.codecRegistry(codecRegistry).build();
		MongoClient client = new MongoClient(new ServerAddress("127.0.0.1"),
				options);
		// or per database
		MongoDatabase database = client.getDatabase("mydb").withCodecRegistry(
				codecRegistry);
		// or per collection
		MongoCollection<Document> collection = database.getCollection("mycoll")
				.withCodecRegistry(codecRegistry);
	}

	@After
	public void close() {
		mongoClient.close();
	}

}
