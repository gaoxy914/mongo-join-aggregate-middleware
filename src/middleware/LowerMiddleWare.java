package middleware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import conf.Constants;
import entry.Result;
import entry.Task;
import index.bplustree.BPlusTree;
import index.joinaggregate.IDTuple;
import index.joinaggregate.JoinAggregateIndex;
import index.joinaggregate.JoinAggregateLimitedMemory;
import index.joinaggregate.JoinIndexItem;
import jdbm.btree.BTree;
import mongoimpl.Aggregate;
import mongoimpl.Count;
import mongoimpl.Delete;
import mongoimpl.Find;
import mongoimpl.Insert;
import mongoimpl.Update;

public class LowerMiddleWare extends MiddleWare implements Runnable {
	private MongoClient mongoClient;
	private JoinAggregateIndex joinAggregateIndex = new JoinAggregateIndex();
	private JoinAggregateLimitedMemory joinAggregateLimitedMemory = new JoinAggregateLimitedMemory();
	
	public LowerMiddleWare(String configPath, String mongouser, String password) throws IOException {
		super(configPath);
		if (config.getLocal()) {
			System.out.println("local start");
			Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
	        mongoLogger.setLevel(Level.SEVERE);
			mongoClient = new MongoClient("localhost", 30000);
		} else {
			System.out.println("cluster start");
			Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
	        mongoLogger.setLevel(Level.SEVERE);
			List<ServerAddress> addresses = new ArrayList<ServerAddress>();
			ServerAddress serverAddress = null;
			for (String mongosIP : config.getMongosIP()) {
				serverAddress = new ServerAddress(mongosIP, config.getMongosPort());
				addresses.add(serverAddress);
			}
			MongoCredential credential = MongoCredential.createCredential(mongouser, "admin",
					password.toCharArray());
			mongoClient = new MongoClient(addresses, Arrays.asList(credential));
			new Thread(this).start();
		}
	}
	
	public void startCMD() {
		MongoDatabase mongoDatabase = mongoClient.getDatabase("project");
		Scanner scanner = new Scanner(System.in);
		String line = "";
		System.out.print(">");
		while (!(line = scanner.nextLine()).equals("quit")) {
			if (line.equals("test1")) {
				String count = "select count(*) from employee1 join department1 on dno where dname = \"department1\"";
				String sum = "select sum(salary) from employee1 join department1 on dno where dname = \"department2\"";
				String avg = "select avg(salary) from employee1 join department1 on dno where dname = \"department3\"";
				String max = "select max(salary) from employee1 join department1 on dno where dname = \"department4\"";
				String min = "select min(salary) from employee1 join department1 on dno where dname = \"department5\"";
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(count, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(sum, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(avg, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(max, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(min, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
			} else if (line.equals("test2")) {
				String count = "select count(*) from employee2 join department2 on dno where dname = \"department1\"";
				String sum = "select sum(salary) from employee2 join department2 on dno where dname = \"department2\"";
				String avg = "select avg(salary) from employee2 join department2 on dno where dname = \"department3\"";
				String max = "select max(salary) from employee2 join department2 on dno where dname = \"department4\"";
				String min = "select min(salary) from employee2 join department2 on dno where dname = \"department5\"";
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(count, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(sum, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(avg, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(max, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(min, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
			} else if (line.equals("test3")) {
				String count = "select count(*) from employee3 join department3 on dno where dname = \"department1\"";
				String sum = "select sum(salary) from employee3 join department3 on dno where dname = \"department2\"";
				String avg = "select avg(salary) from employee3 join department3 on dno where dname = \"department3\"";
				String max = "select max(salary) from employee3 join department3 on dno where dname = \"department4\"";
				String min = "select min(salary) from employee3 join department3 on dno where dname = \"department5\"";
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(count, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(sum, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(avg, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(max, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(min, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
			} else if (line.equals("test4")) {
				String count = "select count(*) from employee4 join department4 on dno where dname = \"department1\"";
				String sum = "select sum(salary) from employee4 join department4 on dno where dname = \"department2\"";
				String avg = "select avg(salary) from employee4 join department4 on dno where dname = \"department3\"";
				String max = "select max(salary) from employee4 join department4 on dno where dname = \"department4\"";
				String min = "select min(salary) from employee4 join department4 on dno where dname = \"department5\"";
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(count, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(sum, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(avg, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(max, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(min, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
			} else if (line.equals("test5")) {
				String count = "select count(*) from employee5 join department5 on dno where dname = \"department1\"";
				String sum = "select sum(salary) from employee5 join department5 on dno where dname = \"department2\"";
				String avg = "select avg(salary) from employee5 join department5 on dno where dname = \"department3\"";
				String max = "select max(salary) from employee5 join department5 on dno where dname = \"department4\"";
				String min = "select min(salary) from employee5 join department5 on dno where dname = \"department5\"";
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(count, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(sum, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(avg, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(max, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
				for (int i = 0; i < 5; i ++) {
					long start = System.currentTimeMillis();
					parser(min, mongoDatabase);
					long end = System.currentTimeMillis();
					System.out.println("time : " + (end - start) + " ms.");
				}
			} else if (line.equals("get optimal index")) {
				createIndex("project", "works_on", "pno", "pname");
				createIndex("employee", "department", "dno", "dname");
				createIndex("employee", "department", "dno", "age");
				add_Q("project", "works_on", "pno", "pname", "project5");
				add_Q("project", "works_on", "pno", "pname", "project6");
				add_Q("project", "works_on", "pno", "pname", "project7");
				add_Q("project", "works_on", "pno", "pname", "project8");
				add_Q("project", "works_on", "pno", "pname", "project9");
				add_Q("project", "works_on", "pno", "pname", "project10");
				add_Q("project", "works_on", "pno", "pname", "project11");
				add_Q("project", "works_on", "pno", "pname", "project12");
				add_Q("project", "works_on", "pno", "pname", "project13");
				add_Q("project", "works_on", "pno", "pname", "project14");
				add_Q("project", "works_on", "pno", "pname", "project15");
				add_Q("project", "works_on", "pno", "pname", "project16");
				add_Q("employee", "department", "dno", "dname", "department1");
				add_Q("employee", "department", "dno", "dname", "department2");
				add_Q("employee", "department", "dno", "dname", "department3");
				add_Q("employee", "department", "dno", "dname", "department4");
				add_Q("employee", "department", "dno", "dname", "department5");
				add_Q("employee", "department", "dno", "dname", "department6");
				add_Q("employee", "department", "dno", "dname", "department7");
				add_Q("employee", "department", "dno", "dname", "department8");
				add_Q("employee", "department", "dno", "dname", "department9");
				add_Q("employee", "department", "dno", "dname", "department10");
				add_Q("employee", "department", "dno", "dname", "department11");
				add_Q("employee", "department", "dno", "dname", "department12");
				add_Q("employee", "department", "dno", "dname", "department13");
				add_Q("employee", "department", "dno", "dname", "department14");
				add_Q("employee", "department", "dno", "dname", "department15");
				add_Q("employee", "department", "dno", "dname", "department16");
				add_Q("employee", "department", "dno", "dname", "department17");
				add_Q("employee", "department", "dno", "age", "27");
				add_Q("employee", "department", "dno", "age", "28");
				add_Q("employee", "department", "dno", "age", "29");
				add_Q("employee", "department", "dno", "age", "30");
				add_Q("employee", "department", "dno", "age", "31");
				add_Q("employee", "department", "dno", "age", "32");
				add_Q("employee", "department", "dno", "age", "33");
				add_Q("employee", "department", "dno", "age", "34");
				add_Q("employee", "department", "dno", "age", "35");
				add_Q("employee", "department", "dno", "age", "36");
				add_Q("employee", "department", "dno", "age", "37");
				add_Q("employee", "department", "dno", "age", "38");
				extend();
				System.out.println("all index size : " + joinAggregateLimitedMemory.getAllIndexSize());
				System.out.println("save IO : " + joinAggregateLimitedMemory.calculateIOsave());
			} else if (line.equals("get random index")) {
				createIndex("project", "works_on", "pno", "pname");
				createIndex("employee", "department", "dno", "dname");
				createIndex("employee", "department", "dno", "age");
				add_Q("project", "works_on", "pno", "pname", "project5");
				add_Q("project", "works_on", "pno", "pname", "project6");
				add_Q("project", "works_on", "pno", "pname", "project7");
				add_Q("employee", "department", "dno", "dname", "department1");
				add_Q("employee", "department", "dno", "dname", "department2");
				add_Q("employee", "department", "dno", "dname", "department3");
				add_Q("employee", "department", "dno", "age", "27");
				add_Q("employee", "department", "dno", "age", "28");
				add_Q("employee", "department", "dno", "age", "29");
				String treeName = "project.works_on.pno.pname";
				joinAggregateLimitedMemory.getRandomIndex(treeName, 39);
				treeName = "employee.department.dno.dname";
				joinAggregateLimitedMemory.getRandomIndex(treeName, 50);
				treeName = "employee.department.dno.age";
				joinAggregateLimitedMemory.getRandomIndex(treeName, 39);
				System.out.println("all index size : " + joinAggregateLimitedMemory.getAllIndexSize());
				System.out.println("save IO : " + joinAggregateLimitedMemory.calculateIOsave());
			} else {
				long start = System.currentTimeMillis();
				parser(line, mongoDatabase);
				long end = System.currentTimeMillis();
				System.out.println("time : " + (end - start) + " ms.");
			}
			System.out.print(">");
		}
		scanner.close();
		mongoClient.close();
	}
	
	public void createIndex(String S, String R, String joinAttr, String condition) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase("project");
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(S);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(R);
		joinAggregateLimitedMemory.getJoinIndex(S, R, joinAttr, condition, sCollection, rCollection);
		joinAggregateLimitedMemory.getBtreeIndex(S, R, joinAttr, condition, sCollection, rCollection);
	}
	
	public void add_Q(String S, String R, String joinAttr, String condition, String key) {
		String query = S + "." + R + "." + joinAttr + "." + condition;
		joinAggregateLimitedMemory.add_Q(query, key);
	}
	
	public void extend() {
		joinAggregateLimitedMemory.getOptimalIndex();
	}
	
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
		server.close();
	}
	
	public void run() {
		while (true) {
			if (server.getJobQueueSize() > 0) {
				String serializedTask = server.getJobQueue().remove(0);
				System.out.println("handle task : " + serializedTask);
				Task task = new Task(serializedTask);
				System.out.println("Execute task : " + task.getTaskID());
				executeTask(task);
			}
		}
	}
	
	/**
	 * TODO excute task from UpperMiddleWare.
	 * 
	 * @param task
	 */
	@SuppressWarnings("unchecked")
	private void executeTask(Task task) {
		Result result = null;
		String stringResult = "";
		if (task.getDb().indexOf(Constants.DATABASE_SPLITER) == -1 &&
				task.getCollection().indexOf(Constants.COLLECTION_SPLITER) == -1) { // 单表操作
			MongoDatabase mongoDatabase = mongoClient.getDatabase(task.getDb());
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(task.getCollection());
			Document condition = Document.parse(task.getCondition());
			switch (task.getFunc()) {
			case "aggregate":
				List<Document> aggregateList = Aggregate.aggregate(mongoCollection, (List<Document>) condition.get("aggregateCondition"));
				for (Document document : aggregateList) {
					stringResult += document.toJson();
				}
				break;
			case "count":
				long countNumber = Count.count(mongoCollection, (Document) condition.get("countCondition"));
				stringResult += countNumber;
				break;
			case "deleteOne":
				long deleteNumber = Delete.deleteOne(mongoCollection, (Document) condition.get("deleteCondition"));
				stringResult += deleteNumber;
				break;
			case "deleteMany":
				long deleteNumbers = Delete.deleteMany(mongoCollection, (Document) condition.get("deleteCondition"));
				stringResult += deleteNumbers;
				break;
			case "find":
				List<Document> findList = Find.find(mongoCollection, (Document) condition.get("findCondition"),
						(Document) condition.get("findOrder"), (Document) condition.get("findProjection"),
						condition.getInteger("findLimit"), condition.getInteger("findSkip"));
				for (Document document : findList) {
					stringResult += document.toJson();
				}
				break;
			case "insertOne":
				Insert.insertOne(mongoCollection, (Document) condition.get("insertObject"));
				stringResult += true;
				break;
			case "insertMany":
				Insert.insertMany(mongoCollection, (List<Document>) condition.get("insertObejct"));
				stringResult += true;
				break;
			case "updateOne":
				long updateNumber = Update.updateOne(mongoCollection, (Document) condition.get("updateCondition"),
						(Document) condition.get("updateObject"));
				stringResult += updateNumber;
				break;
			case "updateMany":
				long updateNumbers = Update.updateMany(mongoCollection, (Document) condition.get("updateCondition"),
						(Document) condition.get("updateObject"));
				stringResult += updateNumbers;
				break;
			default:
				stringResult += "uncorrect funtion.";
				break;
			}
		} else {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("project");
			Document condition = Document.parse(task.getCondition());
			String collectionS = task.getCollection().split(Constants.COLLECTION_SPLITER)[0];
			String collectionR = task.getCollection().split(Constants.COLLECTION_SPLITER)[1];
			String joinAttr = condition.getString("joinAttr");
			String conditions = condition.getString("conditions");
			List<Document> list = find(null, collectionS, collectionR, joinAttr, conditions, mongoDatabase);
			for (Document document : list) {
				stringResult += document.toJson();
			}
		}
		result = new Result(task.getTaskID(), stringResult);
		server.sendData(config.getUpperMWIP(), port, result.serialize());
	}
	
	/**
	 * TODO parse instructions.
	 * 
	 * 单表查询 select attributes,aggregates from collection where conditions group by attribute having conditions
	 * 多表查询 select attributes,aggregates from collection join collection on joinAttr ... where conditions group by attribute having conditions
	 * 插入 insert into collection (attr1, attr2, ...) values (value1, value2, ...)
	 * 删除 delete from collection where conditions
	 * 更新 update collection set atrribute = value where condion
	 * @param instruction
	 */
	private void parser(String instruction, MongoDatabase mongoDatabase) {
		if (instruction.contains("insert")) {
			List<String> strings = new ArrayList<String>();
			Pattern pattern = Pattern.compile("(?<=\\()[^\\)]+");
			Matcher matcher = pattern.matcher(instruction);
			while (matcher.find()) {
				strings.add(matcher.group());
			}
			String[] attrbutes = strings.get(0).replaceAll("\\s*,\\s* ",",").split(",");
			String[] values = strings.get(1).replaceAll("\\s*,\\s* ",",").split(",");
			if (attrbutes.length == values.length) {
				Document insertObject = new Document();
				for (int i = 0; i < attrbutes.length; i++) {
					if (values[i].startsWith("\"") && values[i].endsWith("\"")) {
						insertObject.append(attrbutes[i], Integer.valueOf(values[i].substring(1, values[i].length()-1)));
					} else {
						insertObject.append(attrbutes[i], values[i]);
					}
				}
				String collection = instruction.split(" ")[2];
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
				Insert.insertOne(mongoCollection, insertObject);
			}
		}
		if (instruction.contains("delete")) {
			String collection = instruction.split(" ")[2];
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
			String condition = instruction.split("where")[1].trim();
			Document deleteCondition = conditionsParsor(condition);
			Delete.deleteOne(mongoCollection, deleteCondition);
		}
		if (instruction.contains("update")) {
			String collection = instruction.split(" ")[1];
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
			String condition = instruction.split("where")[1].trim();
			Document updateCondition = conditionsParsor(condition);
			String set = instruction.split("set")[1].split("where")[0].trim();
			String attr = set.split("=")[0].trim();
			String value = set.split("=")[1].trim();
			if (value.startsWith("\"") && value.endsWith("\"")) {
				Update.updateOne(mongoCollection, updateCondition, new Document("$set", new Document(attr, value.substring(1, value.length()-1))));
			} else {
				Update.updateOne(mongoCollection, updateCondition, new Document("$set", new Document(attr, Integer.valueOf(value))));
			}
			
		}
		if (instruction.contains("s_LM")) {
			String[] attributes = instruction.split(" ")[1].split(",");
			String S = instruction.split(" ")[3];
			String R = instruction.split(" ")[5];
			String joinAttr = instruction.split(" ")[7];
			String conditions = null;
			String groupBy = null;
			String havingConditions = null;
			if (instruction.contains("where")) {
				String afterwhere = instruction.split("where")[1].trim();
				if (afterwhere.contains("group by")) {
					conditions = afterwhere.split(" group by ")[0];
					String aftergroup = afterwhere.split(" group by ")[1];
					if (aftergroup.contains("having")) {
						groupBy = aftergroup.split(" having ")[0];
						havingConditions = aftergroup.split(" having ")[1];
					} else {
						groupBy = aftergroup;
					}
				} else {
					conditions = afterwhere;
				}
			} else {
				if (instruction.contains("group by")) {
					String aftergroup = instruction.split("group by")[1].trim();
					if (aftergroup.contains("having")) {
						groupBy = aftergroup.split(" having ")[0];
						havingConditions = aftergroup.split(" having ")[1];
					} else {
						groupBy = aftergroup;
					}
				}
			}
			List<Document> result = new ArrayList<>();
			for (String attr : attributes) {
				if (attr.contains("count")) {
					String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
					result = count_LM(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
				} else if (attr.contains("avg")) {
					String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
					result = avg_LM(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
				} else if (attr.contains("sum")) {
					String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
					result = sum_LM(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
				} else if (attr.contains("min")) {
					String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
					result = min_LM(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
				} else if (attr.contains("max")) {
					String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
					result = max_LM(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
				}
//				System.out.println(attr);
				for (Document document : result) {
					System.out.println(document.toJson());
				}
			}
		}
		if (instruction.contains("select")) {
			if (instruction.contains("join")) {
				String[] attributes = instruction.split(" ")[1].split(",");
				String S = instruction.split(" ")[3];
				String R = instruction.split(" ")[5];
				String joinAttr = instruction.split(" ")[7];
				String conditions = null;
				String groupBy = null;
				String havingConditions = null;
				if (instruction.contains("where")) {
					String afterwhere = instruction.split("where")[1].trim();
					if (afterwhere.contains("group by")) {
						conditions = afterwhere.split(" group by ")[0];
						String aftergroup = afterwhere.split(" group by ")[1];
						if (aftergroup.contains("having")) {
							groupBy = aftergroup.split(" having ")[0];
							havingConditions = aftergroup.split(" having ")[1];
						} else {
							groupBy = aftergroup;
						}
					} else {
						conditions = afterwhere;
					}
				} else {
					if (instruction.contains("group by")) {
						String aftergroup = instruction.split("group by")[1].trim();
						if (aftergroup.contains("having")) {
							groupBy = aftergroup.split(" having ")[0];
							havingConditions = aftergroup.split(" having ")[1];
						} else {
							groupBy = aftergroup;
						}
					}
				}
				List<Document> result = new ArrayList<>();
				Document columns = new Document();
				for (String attr : attributes) {
					if (attr.contains("count")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = count(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
					} else if (attr.contains("avg")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = avg(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
					} else if (attr.contains("sum")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = sum(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
					} else if (attr.contains("min")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = min(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
					} else if (attr.contains("max")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = max(column, S, R, joinAttr, conditions, groupBy, havingConditions, mongoDatabase);
					} else {
						columns.append(attr, 1);
					}
//					System.out.println(attr);
					for (Document document : result) {
						System.out.println(document.toJson());
					}
				}
				if (columns.size() > 0) {
					if (columns.containsKey("*")) {
						result = find(null, S, R, joinAttr, conditions, mongoDatabase);
					} else {
						result = find(columns, S, R, joinAttr, conditions, mongoDatabase);
					}
					for (Document document : result) {
						System.out.println(document.toJson());
					}
				}
			} else {
				String[] attributes = instruction.split(" ")[1].split(",");
				String collection = instruction.split(" ")[3];
				String conditions = null;
				String groupBy = null;
				String havingConditions = null;
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
				if (instruction.contains("where")) {
					String afterwhere = instruction.split("where")[1].trim();
					if (afterwhere.contains("group by")) {
						conditions = afterwhere.split(" group by ")[0];
						String aftergroup = afterwhere.split(" group by ")[1];
						if (aftergroup.contains("having")) {
							groupBy = aftergroup.split(" having ")[0];
							havingConditions = aftergroup.split(" having ")[1];
						} else {
							groupBy = aftergroup;
						}
					} else {
						conditions = afterwhere;
					}
				} else {
					if (instruction.contains("group by")) {
						String aftergroup = instruction.split("group by")[1].trim();
						if (aftergroup.contains("having")) {
							groupBy = aftergroup.split(" having ")[0];
							havingConditions = aftergroup.split(" having ")[1];
						} else {
							groupBy = aftergroup;
						}
					}
				}
				List<Document> result = new ArrayList<>();
				Document columns = new Document();
				for (String attr : attributes) {
					if (attr.contains("count")) {
						result = count(mongoCollection, conditions, groupBy, havingConditions);
					} else if (attr.contains("avg")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = avg(column, mongoCollection, conditions, groupBy, havingConditions);
					} else if (attr.contains("sum")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = sum(column, mongoCollection, conditions, groupBy, havingConditions);
					} else if (attr.contains("min")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = min(column, mongoCollection, conditions, groupBy, havingConditions);
					} else if (attr.contains("max")) {
						String column = attr.substring(attr.indexOf("(")+1, attr.indexOf(")"));
						result = max(column, mongoCollection, conditions, groupBy, havingConditions);
					} else {
						columns.append(attr, 1);
					}
//					System.out.println(attr);
					for (Document document : result) {
						System.out.println(document.toJson());
					}
				}
				if (columns.size() > 0) {
					if (columns.containsKey("*")) {
						result = Find.find(mongoCollection, conditionsParsor(conditions), null, null, null, null);
					} else {
						result = Find.find(mongoCollection, conditionsParsor(conditions), null, columns, null, null);
					}
					for (Document document : result) {
						System.out.println(document.toJson());
					}
				}
			}
		}
	}
	
	/**
	 * TODO find on join collection.
	 * 
	 * @param projection
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> find(Document projection, String collectionS, String collectionR, String joinAttr, String conditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
//		System.out.println(joinTable.get(0).getR_Id());
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
//		System.out.println(conditionsParsor(conditions).toJson());
		result = Find.find(mongoCollection, conditionsParsor(conditions), null, projection, null, null);
		mongoCollection.drop();
		return result;
	}
	
	private String getEqCondition(String conditions) {
		if (conditions.contains(Constants.AND)) {
			String[] and = conditions.split(Constants.AND);
			for (String string : and) {
				if (string.contains("=")) {
					return string;
				}
			}
			return null;
		}
		if (conditions.contains("=")) {
			return conditions;
		}
		return null;
	}
	
	/**
	 * TODO count() on join collection.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> count(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BTree tree = joinAggregateIndex.getBTreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				try {
					JoinIndexItem joinIndexItem = (JoinIndexItem) tree.find(value);
//					System.out.println(joinIndexItem.toString());
					MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
					Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
					FindIterable<Document> sIterable = sCollection.find(s_Id);
					for (Document sDocument : sIterable) {
						Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
						FindIterable<Document> rIterable = rCollection.find(r_Id);
						for (Document rDocument : rIterable) {
							for (String string : rDocument.keySet()) {
								sDocument.append(string, rDocument.get(string));
							}
							sDocument.remove(Constants._ID);
							mongoCollection.insertOne(sDocument);
						}
					}
//					System.out.println(conditions);
					result = count(mongoCollection, conditions, groupBy, havingConditions);
					mongoCollection.drop();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = count(mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO sum() on join collection.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> sum(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BTree tree = joinAggregateIndex.getBTreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				try {
					JoinIndexItem joinIndexItem = (JoinIndexItem) tree.find(value);
					MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
					Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
					FindIterable<Document> sIterable = sCollection.find(s_Id);
					for (Document sDocument : sIterable) {
						Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
						FindIterable<Document> rIterable = rCollection.find(r_Id);
						for (Document rDocument : rIterable) {
							for (String string : rDocument.keySet()) {
								sDocument.append(string, rDocument.get(string));
							}
							sDocument.remove(Constants._ID);
							mongoCollection.insertOne(sDocument);
						}
					}
					result = sum(attr, mongoCollection, conditions, groupBy, havingConditions);
					mongoCollection.drop();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = sum(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;		
	}
	
	/**
	 * TODO avg() on join collection.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> avg(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BTree tree = joinAggregateIndex.getBTreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				try {
					JoinIndexItem joinIndexItem = (JoinIndexItem) tree.find(value);
					MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
					Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
					FindIterable<Document> sIterable = sCollection.find(s_Id);
					for (Document sDocument : sIterable) {
						Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
						FindIterable<Document> rIterable = rCollection.find( r_Id);
						for (Document rDocument : rIterable) {
							for (String string : rDocument.keySet()) {
								sDocument.append(string, rDocument.get(string));
							}
							sDocument.remove(Constants._ID);
							mongoCollection.insertOne(sDocument);
						}
					}
					result = avg(attr, mongoCollection, conditions, groupBy, havingConditions);
					mongoCollection.drop();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return result;
			}
		}	
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = avg(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO min() on join collection.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> min(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BTree tree = joinAggregateIndex.getBTreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				try {
					JoinIndexItem joinIndexItem = (JoinIndexItem) tree.find(value);
					MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
					Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
					FindIterable<Document> sIterable = sCollection.find(s_Id);
					for (Document sDocument : sIterable) {
						Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
						FindIterable<Document> rIterable = rCollection.find(r_Id);
						for (Document rDocument : rIterable) {
							for (String string : rDocument.keySet()) {
								sDocument.append(string, rDocument.get(string));
							}
							sDocument.remove(Constants._ID);
							mongoCollection.insertOne(sDocument);
						}
					}
					result = min(attr, mongoCollection, conditions, groupBy, havingConditions);
					mongoCollection.drop();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = min(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO max() on join collection.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> max(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BTree tree = joinAggregateIndex.getBTreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				try {
					JoinIndexItem joinIndexItem = (JoinIndexItem) tree.find(value);
					MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
					Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
					FindIterable<Document> sIterable = sCollection.find(s_Id);
					for (Document sDocument : sIterable) {
						Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
						FindIterable<Document> rIterable = rCollection.find(r_Id);
						for (Document rDocument : rIterable) {
							for (String string : rDocument.keySet()) {
								sDocument.append(string, rDocument.get(string));
							}
							sDocument.remove(Constants._ID);
							mongoCollection.insertOne(sDocument);
						}
					}
					result = max(attr, mongoCollection, conditions, groupBy, havingConditions);
					mongoCollection.drop();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = max(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO count() on join collection using limited memory.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> count_LM(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BPlusTree<String, JoinIndexItem> bPlusTree = joinAggregateLimitedMemory.getBtreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				System.out.println(value);
				JoinIndexItem joinIndexItem = bPlusTree.searchInNodesInMemory(value);
				System.out.println(joinIndexItem);
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
				Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
				FindIterable<Document> sIterable = sCollection.find(s_Id);
				for (Document sDocument : sIterable) {
					Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
					FindIterable<Document> rIterable = rCollection.find(r_Id);
					for (Document rDocument : rIterable) {
						for (String string : rDocument.keySet()) {
							sDocument.append(string, rDocument.get(string));
						}
						sDocument.remove(Constants._ID);
						mongoCollection.insertOne(sDocument);
					}
				}
//				System.out.println(conditions);
				result = count(mongoCollection, conditions, groupBy, havingConditions);
				mongoCollection.drop();
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = count(mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO sum() on join collection using limited memory.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> sum_LM(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BPlusTree<String, JoinIndexItem> bPlusTree = joinAggregateLimitedMemory.getBtreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				JoinIndexItem joinIndexItem = bPlusTree.searchInNodesInMemory(value);
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
				Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
				FindIterable<Document> sIterable = sCollection.find(s_Id);
				for (Document sDocument : sIterable) {
					Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
					FindIterable<Document> rIterable = rCollection.find(r_Id);
					for (Document rDocument : rIterable) {
						for (String string : rDocument.keySet()) {
							sDocument.append(string, rDocument.get(string));
						}
						sDocument.remove(Constants._ID);
						mongoCollection.insertOne(sDocument);
					}
				}
				result = sum(attr, mongoCollection, conditions, groupBy, havingConditions);
				mongoCollection.drop();
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = sum(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;		
	}
	
	/**
	 * TODO avg() on join collection using limited memory.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> avg_LM(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BPlusTree<String, JoinIndexItem> bPlusTree = joinAggregateLimitedMemory.getBtreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				JoinIndexItem joinIndexItem = bPlusTree.searchInNodesInMemory(value);
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
				Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
				FindIterable<Document> sIterable = sCollection.find(s_Id);
				for (Document sDocument : sIterable) {
					Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
					FindIterable<Document> rIterable = rCollection.find( r_Id);
					for (Document rDocument : rIterable) {
						for (String string : rDocument.keySet()) {
							sDocument.append(string, rDocument.get(string));
						}
						sDocument.remove(Constants._ID);
						mongoCollection.insertOne(sDocument);
					}
				}
				result = avg(attr, mongoCollection, conditions, groupBy, havingConditions);
				mongoCollection.drop();
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = avg(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO min() on join collection using limited memory.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> min_LM(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BPlusTree<String, JoinIndexItem> bPlusTree = joinAggregateLimitedMemory.getBtreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				JoinIndexItem joinIndexItem = bPlusTree.searchInNodesInMemory(value);
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
				Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
				FindIterable<Document> sIterable = sCollection.find(s_Id);
				for (Document sDocument : sIterable) {
					Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
					FindIterable<Document> rIterable = rCollection.find(r_Id);
					for (Document rDocument : rIterable) {
						for (String string : rDocument.keySet()) {
							sDocument.append(string, rDocument.get(string));
						}
						sDocument.remove(Constants._ID);
						mongoCollection.insertOne(sDocument);
					}
				}
				result = min(attr, mongoCollection, conditions, groupBy, havingConditions);
				mongoCollection.drop();
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = min(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO max() on join collection using limited memory.
	 * 
	 * @param attr
	 * @param collectionS
	 * @param collectionR
	 * @param joinAttr
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @param mongoDatabase
	 * @return
	 */
	private List<Document> max_LM(String attr, String collectionS, String collectionR, String joinAttr, String conditions, String groupBy, String havingConditions, MongoDatabase mongoDatabase) {
		List<Document> result = new ArrayList<Document>();
		MongoCollection<Document> sCollection = mongoDatabase.getCollection(collectionS);
		MongoCollection<Document> rCollection = mongoDatabase.getCollection(collectionR);
		if (conditions != null) {
			String condition = getEqCondition(conditions);
			if (condition != null) {
				String key = condition.split("=")[0].trim();
				String value = condition.split("=")[1].trim();
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				BPlusTree<String, JoinIndexItem> bPlusTree = joinAggregateLimitedMemory.getBtreeIndex(collectionS, collectionR, joinAttr, key, sCollection, rCollection);
				JoinIndexItem joinIndexItem = bPlusTree.searchInNodesInMemory(value);
				MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
				Document s_Id = new Document(Constants.$OR, joinIndexItem.getS_Id_Document());
				FindIterable<Document> sIterable = sCollection.find(s_Id);
				for (Document sDocument : sIterable) {
					Document r_Id = joinIndexItem.findR_Id(sDocument.getObjectId(Constants._ID));
					FindIterable<Document> rIterable = rCollection.find(r_Id);
					for (Document rDocument : rIterable) {
						for (String string : rDocument.keySet()) {
							sDocument.append(string, rDocument.get(string));
						}
						sDocument.remove(Constants._ID);
						mongoCollection.insertOne(sDocument);
					}
				}
				result = max(attr, mongoCollection, conditions, groupBy, havingConditions);
				mongoCollection.drop();
				return result;
			}
		}
		ArrayList<IDTuple> joinTable = joinAggregateIndex.getJoinTable(collectionS, collectionR, joinAttr, sCollection, rCollection);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(Constants.TEMP_COLLECTION);
		for (IDTuple tuple : joinTable) {
			FindIterable<Document> sIterable = sCollection.find(new Document(Constants._ID, tuple.getS_Id()));
			FindIterable<Document> rIterable = rCollection.find(new Document(Constants._ID, tuple.getR_Id()));
			for (Document sDocument : sIterable) {
				for (Document rDocument : rIterable) {
					for (String string : rDocument.keySet()) {
						sDocument.append(string, rDocument.get(string));
					}
					sDocument.remove(Constants._ID);
					mongoCollection.insertOne(sDocument);
				}
			}
		}
		result = max(attr, mongoCollection, conditions, groupBy, havingConditions);
		mongoCollection.drop();
		return result;
	}
	
	/**
	 * TODO count() on single collection.
	 * 
	 * @param mongoCollection
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @return
	 */
	private List<Document> count(MongoCollection<Document> mongoCollection, String conditions, String groupBy, String havingConditions) {
		List<Document> list = new ArrayList<Document>();
		if (conditions != null) {
			Document match = new Document(Constants.$MATCH, conditionsParsor(conditions));
			list.add(match);
//			System.out.println(match.toJson());
		}
		Document sum = new Document(Constants.$SUM, 1);
//		System.out.println(groupBy);
		Document group = new Document(Constants.$GROUP, new Document(Constants._ID, groupBy == null ? Constants.COUNT : add$Prefix(groupBy)).append(Constants.NUM_TUTORIAL, sum));
		list.add(group);
//		System.out.println(group.toJson());
		if (havingConditions != null) {
			String newCondition = Constants.NUM_TUTORIAL + Constants.SPACE + havingConditions.split(Constants.SPACE)[1] + Constants.SPACE + havingConditions.split(Constants.SPACE)[2];
			Document having = new Document(Constants.$MATCH, conditionsParsor(newCondition));
			list.add(having);
//			System.out.println(having.toJson());
		}
		AggregateIterable<Document> iterable = mongoCollection.aggregate(list);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
	
	/**
	 * TODO sum() on single collection.
	 * 
	 * @param attr
	 * @param mongoCollection
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @return
	 */
	private List<Document> sum(String attr, MongoCollection<Document> mongoCollection, String conditions, String groupBy, String havingConditions) {
		List<Document> list = new ArrayList<Document>();
		if (conditions != null) {
			Document match = new Document(Constants.$MATCH, conditionsParsor(conditions));
			list.add(match);
//			System.out.println(match.toJson());
		}
		Document sum = new Document(Constants.$SUM, add$Prefix(attr));
		Document group = new Document(Constants.$GROUP, new Document(Constants._ID, groupBy == null ? Constants.SUM : add$Prefix(groupBy)).append(Constants.NUM_TUTORIAL, sum));
		list.add(group);
//		System.out.println(group.toJson());
		if (havingConditions != null) {
			String newCondition = Constants.NUM_TUTORIAL + Constants.SPACE + havingConditions.split(Constants.SPACE)[1] + Constants.SPACE + havingConditions.split(Constants.SPACE)[2];
			Document having = new Document(Constants.$MATCH, conditionsParsor(newCondition));
			list.add(having);
//			System.out.println(having.toJson());
		}
		AggregateIterable<Document> iterable = mongoCollection.aggregate(list);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
	
	/**
	 * TODO avg() on single collection.
	 * 
	 * @param attr
	 * @param mongoCollection
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @return
	 */
	private List<Document> avg(String attr, MongoCollection<Document> mongoCollection, String conditions, String groupBy, String havingConditions) {
		List<Document> list = new ArrayList<Document>();
		if (conditions != null) {
			Document match = new Document(Constants.$MATCH, conditionsParsor(conditions));
			list.add(match);
		}
		Document avg = new Document(Constants.$AVG, add$Prefix(attr));
		Document group = new Document(Constants.$GROUP, new Document(Constants._ID, groupBy == null ? Constants.AVG : add$Prefix(groupBy)).append(Constants.NUM_TUTORIAL, avg));
		list.add(group);
		if (havingConditions != null) {
			String newCondition = Constants.NUM_TUTORIAL + Constants.SPACE + havingConditions.split(Constants.SPACE)[1] + Constants.SPACE + havingConditions.split(Constants.SPACE)[2];
			Document having = new Document(Constants.$MATCH, conditionsParsor(newCondition));
			list.add(having);
//			System.out.println(having.toJson());
		}
		AggregateIterable<Document> iterable = mongoCollection.aggregate(list);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
	
	/**
	 * TODO min() on single collection.
	 * 
	 * @param attr
	 * @param mongoCollection
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @return
	 */
	private List<Document> min(String attr, MongoCollection<Document> mongoCollection, String conditions, String groupBy, String havingConditions) {
		List<Document> list = new ArrayList<Document>();
		if (conditions != null) {
			Document match = new Document(Constants.$MATCH, conditionsParsor(conditions));
			list.add(match);
		}
		Document min = new Document(Constants.$MIN, add$Prefix(attr));
		Document group = new Document(Constants.$GROUP, new Document(Constants._ID, groupBy == null ? Constants.MIN : add$Prefix(groupBy)).append(Constants.NUM_TUTORIAL, min));
		list.add(group);
		if (havingConditions != null) {
			String newCondition = Constants.NUM_TUTORIAL + Constants.SPACE + havingConditions.split(Constants.SPACE)[1] + Constants.SPACE + havingConditions.split(Constants.SPACE)[2];
			Document having = new Document(Constants.$MATCH, conditionsParsor(newCondition));
			list.add(having);
//			System.out.println(having.toJson());
		}
		AggregateIterable<Document> iterable = mongoCollection.aggregate(list);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
	
	/**
	 * TODO max() on single collection.
	 * 
	 * @param attr
	 * @param mongoCollection
	 * @param conditions
	 * @param groupBy
	 * @param havingConditions
	 * @return
	 */
	private List<Document> max(String attr, MongoCollection<Document> mongoCollection, String conditions, String groupBy, String havingConditions) {
		List<Document> list = new ArrayList<Document>();
		if (conditions != null) {
			Document match = new Document(Constants.$MATCH, conditionsParsor(conditions));
			list.add(match);
		}
		Document max = new Document(Constants.$MAX, add$Prefix(attr));
		Document group = new Document(Constants.$GROUP, new Document(Constants._ID, groupBy == null ? Constants.MAX : add$Prefix(groupBy)).append(Constants.NUM_TUTORIAL, max));
		list.add(group);
		if (havingConditions != null) {
			String newCondition = Constants.NUM_TUTORIAL + Constants.SPACE + havingConditions.split(Constants.SPACE)[1] + Constants.SPACE + havingConditions.split(Constants.SPACE)[2];
			Document having = new Document(Constants.$MATCH, conditionsParsor(newCondition));
			list.add(having);
//			System.out.println(having.toJson());
		}
		AggregateIterable<Document> iterable = mongoCollection.aggregate(list);
		List<Document> result = new ArrayList<Document>();
		for (Document document : iterable) {
			result.add(document);
		}
		return result;
	}
	
	private static String add$Prefix(String word) {
		return "$" + word;
	}
	
	/**
	 * TODO parse condition.
	 * condition1 and condition2 and conition3 or condition4
	 * @param conditions
	 * @return
	 */
	private Document conditionsParsor(String conditions) {
		if (conditions == null) {
			return new Document();
		}
		String[] orConditions = conditions.split(Constants.OR);
		if (orConditions.length > 1) {
			ArrayList<Document> orList = new ArrayList<Document>();
			for (String or : orConditions) {
				String[] andConditions = or.split(Constants.AND);
				Document document = new Document();
				for (String and : andConditions) {
					and = and.trim();
					String[] args = and.split(Constants.SPACE);
					String attr = args[0];
					String token = args[1];
					String value = args[2];
					if (value.startsWith("\"") && value.endsWith("\"")) {
						value = value.substring(1, value.length()-1);
//						System.out.println(value);
						if (token.equals(Constants.EQ)) {
							document.append(attr, value);
						} else if (token.equals(Constants.LT)) {
							document.append(attr, new Document(Constants.$LT, value));
						} else if (token.equals(Constants.GT)) {
							document.append(attr, new Document(Constants.$GT, value));
						} else if (token.equals(Constants.NE)) {
							document.append(attr, new Document(Constants.$NE, value));
						} else if (token.equals(Constants.LTE)) {
							document.append(attr, new Document(Constants.$LTE, value));
						} else if (token.equals(Constants.GTE)) {
							document.append(attr, new Document(Constants.$GTE, value));
						}
					} else {
//						System.out.println(value);
						if (token.equals(Constants.EQ)) {
							document.append(attr, Integer.valueOf(value));
						} else if (token.equals(Constants.LT)) {
							document.append(attr, new Document(Constants.$LT, Integer.valueOf(value)));
						} else if (token.equals(Constants.GT)) {
							document.append(attr, new Document(Constants.$GT, Integer.valueOf(value)));
						} else if (token.equals(Constants.NE)) {
							document.append(attr, new Document(Constants.$NE, Integer.valueOf(value)));
						} else if (token.equals(Constants.LTE)) {
							document.append(attr, new Document(Constants.$LTE, Integer.valueOf(value)));
						} else if (token.equals(Constants.GTE)) {
							document.append(attr, new Document(Constants.$GTE, Integer.valueOf(value)));
						}
					}
				}
				orList.add(document);
			}
			return new Document(Constants.$OR, orList);
		}
		String[] andConditions = orConditions[0].split(Constants.AND);
		Document document = new Document();
		for (String and : andConditions) {
			and = and.trim();
			String[] args = and.split(Constants.SPACE);
			String attr = args[0];
			String token = args[1];
			String value = args[2];
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length()-1);
//				System.out.println(value);
				if (token.equals(Constants.EQ)) {
					document.append(attr, value);
				} else if (token.equals(Constants.LT)) {
					document.append(attr, new Document(Constants.$LT, value));
				} else if (token.equals(Constants.GT)) {
					document.append(attr, new Document(Constants.$GT, value));
				} else if (token.equals(Constants.NE)) {
					document.append(attr, new Document(Constants.$NE, value));
				} else if (token.equals(Constants.LTE)) {
					document.append(attr, new Document(Constants.$LTE, value));
				} else if (token.equals(Constants.GTE)) {
					document.append(attr, new Document(Constants.$GTE, value));
				}
			} else {
//				System.out.println(value);
				if (token.equals(Constants.EQ)) {
					document.append(attr, Integer.valueOf(value));
				} else if (token.equals(Constants.LT)) {
					document.append(attr, new Document(Constants.$LT, Integer.valueOf(value)));
				} else if (token.equals(Constants.GT)) {
					document.append(attr, new Document(Constants.$GT, Integer.valueOf(value)));
				} else if (token.equals(Constants.NE)) {
					document.append(attr, new Document(Constants.$NE, Integer.valueOf(value)));
				} else if (token.equals(Constants.LTE)) {
					document.append(attr, new Document(Constants.$LTE, Integer.valueOf(value)));
				} else if (token.equals(Constants.GTE)) {
					document.append(attr, new Document(Constants.$GTE, Integer.valueOf(value)));
				}
			}
		}
		return document;
	}
	
	public static void main(String[] args) {
		System.out.println("LowerMiddleWare Start.");
		try {
			LowerMiddleWare lowerMiddleWare = new LowerMiddleWare("Config.txt", "root", "root");
			lowerMiddleWare.startCMD();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
