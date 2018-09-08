package mongoimpl;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class Update {
	public static long updateOne(MongoCollection<Document> collection, Document filter, Document object) {
		UpdateResult result = collection.updateOne(filter, object);
		return result.getModifiedCount();
	}
	
	public static long updateMany(MongoCollection<Document> collection, Document filter, Document object) {
		UpdateResult result = collection.updateMany(filter, object);
		return result.getModifiedCount();
	}
}
