package mongoimpl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class Find {
	public static List<Document> find(MongoCollection<Document> collection, Document filter, Document order,
			Document projection, Integer limit, Integer skip) {
		FindIterable<Document> iterable;
		iterable = collection.find(filter == null ? new Document() : filter)
				.projection(projection).sort(order)
				.limit(limit == null ? 0 : limit)
				.skip(skip == null ? 0 : skip);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
}
