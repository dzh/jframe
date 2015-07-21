/**
 * 
 */
package mongodb.client;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author dzh
 * @data Jul 6, 2015 3:50:35 PM
 * @since 1.0
 */
public class TestMongoClient {

	private MongoClient mongoClient;

	@BeforeClass
	public void start() {
		MongoClientOptions.builder().cursorFinalizerEnabled(false);
//		MongoClientURI uri = new MongoClientURI();
		
		mongoClient = new MongoClient();
		// mongoClient.setOptions();
		
//		MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017,localhost:27018,localhost:27019");
//		MongoClient mongoClient = new MongoClient(connectionString);
//		MongoDatabase database = mongoClient.getDatabase("mydb");
	}

	@Test
	public void testConn() {
		MongoDatabase db = mongoClient.getDatabase("");
		db.getCollection("");

		// Replaces the default UuidCodec with one that uses the new standard
		// UUID representation
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				CodecRegistries.fromCodecs(new UuidCodec(
						UuidRepresentation.STANDARD)), MongoClient
						.getDefaultCodecRegistry());
		// globally
		MongoClientOptions options = MongoClientOptions.builder()
				.codecRegistry(codecRegistry).build();
		MongoClient client = new MongoClient(new ServerAddress(), options);
		// or per database
		MongoDatabase database = client.getDatabase("mydb").withCodecRegistry(
				codecRegistry);
		// or per collection
		MongoCollection<Document> collection = database.getCollection("mycoll")
				.withCodecRegistry(codecRegistry);
	}

	@AfterClass
	public void close() {
		mongoClient.close();
	}

}
