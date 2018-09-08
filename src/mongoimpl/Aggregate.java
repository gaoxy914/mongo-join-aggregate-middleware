package mongoimpl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;

public class Aggregate {
	public static List<Document> aggregate(MongoCollection<Document> collection, List<Document> condition) {
		AggregateIterable<Document> iterable = collection.aggregate(condition);
		List<Document> list = new ArrayList<Document>();
		for (Document document : iterable) {
			list.add(document);
		}
		return list;
	}
}
