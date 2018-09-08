package middleware;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class loadData {
	public static void main(String[] args) throws IOException {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
		MongoClient mongoClient = new MongoClient("localhost", 30000);
		MongoDatabase mongoDatabase = mongoClient.getDatabase("project");
//		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("employee");
//		FileReader fileReader = new FileReader("employee.txt");
//		BufferedReader bufferedReader = new BufferedReader(fileReader);
//		String line;
//		while ((line = bufferedReader.readLine()) != null) {
//			String[] data = line.split("\t");
//			Document document = new Document("ename", data[0]).append("essn", data[1])
//					.append("address", data[2]).append("salary", Integer.valueOf(data[3])).append("superssn", data[4]).append("dno", data[5]);
//			mongoCollection.insertOne(document);
//		}
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("department");
		FileReader fileReader = new FileReader("department.txt");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] data = line.split("\t");
			Document document = new Document("dname", data[0])
					.append("dno", data[1])
					.append("mgrssn", data[2])
					.append("mgrstartdate", data[3]);
			mongoCollection.insertOne(document);
		}
//		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("project");
//		FileReader fileReader = new FileReader("project.txt");
//		BufferedReader bufferedReader = new BufferedReader(fileReader);
//		String line;
//		while ((line =  bufferedReader.readLine()) != null) {
//			String[] data = line.split("\t");
//			Document document = new Document("pname", data[0])
//					.append("pno", data[1])
//					.append("plocation", data[2])
//					.append("dno", data[3]);
//			mongoCollection.insertOne(document);
//		}
//		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("works_on");
//		FileReader fileReader = new FileReader("works_on.txt");
//		BufferedReader bufferedReader = new BufferedReader(fileReader);
//		String line;
//		while ((line =  bufferedReader.readLine()) != null) {
//			String[] data = line.split("\t");
//			Document document = new Document("essn", data[0])
//					.append("pno", data[1])
//					.append("hours", data[2]);
//			mongoCollection.insertOne(document);
//		}
		bufferedReader.close();
		fileReader.close();
		mongoClient.close();
	}
}
