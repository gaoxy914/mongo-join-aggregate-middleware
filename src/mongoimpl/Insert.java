package mongoimpl;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class Insert {
	public static void insertOne(MongoCollection<Document> collection, Document object) {
		collection.insertOne(object);
	}
	
	public static void insertMany(MongoCollection<Document> collection, List<Document> object) {
		collection.insertMany(object);
	}
}
