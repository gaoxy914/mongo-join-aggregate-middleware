package mongoimpl;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class Count {
	public static long count(MongoCollection<Document> collection, Document filter) {
		return collection.count(filter);
	}
}
