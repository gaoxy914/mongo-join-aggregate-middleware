package mongoimpl;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

public class Delete {
	public static long deleteOne(MongoCollection<Document> collection, Document filter) {
		DeleteResult result = collection.deleteOne(filter);
		return result.getDeletedCount();
	}
	
	public static long deleteMany(MongoCollection<Document> collection, Document filter) {
		DeleteResult result = collection.deleteMany(filter);
		return result.getDeletedCount();
	}
}
