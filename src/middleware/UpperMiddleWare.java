package middleware;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import org.bson.Document;

import conf.Constants;
import entry.Result;
import entry.Task;

public class UpperMiddleWare extends MiddleWare implements Runnable {
	private long taskID = 0;
	private Hashtable<Long, String> resultTable = new Hashtable<Long, String>();
	private Hashtable<String, String> indexDistribution = new Hashtable<String, String>();
	
	public UpperMiddleWare(String configPath) throws IOException {
		super(configPath);
		new Thread(this).start();
	}
	
	public void close() {
		server.close();
	}
	
	public void run() {
		while (true) {
			if (server.getJobQueueSize() > 0) {
				String line = server.getJobQueue().remove(0);
				if (line.startsWith(Constants.INDEXADDRESS_PREFIX)) {
					String[] strings = line.substring(line.indexOf("(")+1, line.indexOf(")")).split(","); // index-address:(key,ip)
					indexDistribution.put(strings[0], strings[1]);
				} else {
					Result result = new Result(line);
					resultTable.put(result.getTaskID(), result.getResult());
				}
			}
		}
	}
	
	public String aggregate(String dataBase, String collection, String condition) {
		return aggregateImpl(dataBase, collection, condition);
	}
	
	public String count(String dataBase, String collection, String condition) {
		return countImpl(dataBase, collection, condition);
	}
	
	public long deletOne(String dataBase, String collection, String condition) {
		return deleteOneImpl(dataBase, collection, condition);
	}
	
	public long deleteMany(String dataBase, String collection, String condition){
		return deleteManyImpl(dataBase, collection, condition);
	}
	
	public String find(String dataBase, String collection, String condition) {
		return findImpl(dataBase, collection, condition);
	}
	
	public boolean insertOne(String dataBase, String collection, String condition) {
		return insertOneImpl(dataBase, collection, condition);
	}
	
	public boolean insertMany(String dataBase, String collection, String condition) {
		return insertManyImpl(dataBase, collection, condition);
	}
	
	public long updateOne(String dataBase, String collection, String condition) {
		return updateOneImpl(dataBase, collection, condition);
	}
	
	public long updateMany(String dataBase, String collection, String condition) {
		return updateManyImpl(dataBase, collection, condition);
	}
	
	public String aggregateJoin(String dataBaseS, String collectionS, String dataBaseR, String collectionR, String condition) {
		return aggregateJoinImpl(dataBaseS, collectionS, dataBaseR, collectionR, condition);
	}
	
	private String aggregateImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "aggregate", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return getResult(id);
	}
	
	private String countImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "count", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return getResult(id);
	}
	
	private long deleteOneImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "deleteOne", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return Long.valueOf(getResult(id));
	}
	
	private long deleteManyImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "deleteMany", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return Long.valueOf(getResult(id));
	}
	
	private String findImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "find", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return getResult(id);
	}
	
	private boolean insertOneImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "insertOne", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return Boolean.valueOf(getResult(id));
	}
	
	private boolean insertManyImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "insertMany", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return Boolean.valueOf(getResult(id));
	}
	
	private long updateOneImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "updateOne", dataBase, collection, condition);
		server.sendData(getIP(collection), port, task.serialize());
		return Long.valueOf(getResult(id));
	}
	
	private long updateManyImpl(String dataBase, String collection, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "updateMany", dataBase, collection, condition);
		server.sendData(getIndexAddress(collection, collection), port, task.serialize());
		return Long.valueOf(getResult(id));
	}
	
	private String aggregateJoinImpl(String dataBaseS, String collectionS, String dataBaseR, String collectionR, String condition) {
		long id = getTaskID();
		Task task = new Task(id, "aggregateJoin", dataBaseS + Constants.DATABASE_SPLITER + dataBaseR,
				collectionS + Constants.COLLECTION_SPLITER + collectionR, condition);
		server.sendData(getIndexAddress(collectionS, collectionR), port, task.serialize());
		return getResult(id);
	}
	
	private String getIP(String collection) {
		Random random = new Random();
		return config.getLowerMWIP().get(random.nextInt(config.getLowerMWIP().size()));
	}
	
	private String getIndexAddress(String collectionS, String collectionR) {
		if (indexDistribution.size() == 0) {
			Random random = new Random();
			return config.getLowerMWIP().get(random.nextInt(config.getLowerMWIP().size()));
		}
		String key = collectionS + "." + collectionR;
		if (indexDistribution.containsKey(key)) {
			return indexDistribution.get(key);
		}
		Hashtable<String, Integer> count = new Hashtable<String, Integer>();
		for (String ip : indexDistribution.values()) {
			if (count.containsKey(ip)) {
				int time = count.get(ip);
				count.replace(ip, ++time);
			} else {
				count.put(ip, 1);
			}
		}
		int min = Constants.INFINITY;
		String ip = "";
		for (String string : count.keySet()) {
			if (count.get(string) < min) {
				ip = string;
				min = count.get(string);
			}
		}
		return ip;
	}
	
	private long getTaskID() {
		return taskID++;
	}
	
	private String getResult(long taskID) {
		while (true) {
			if (resultTable.containsKey(taskID)) {
				System.out.println("Get result for task :" + taskID);
				return resultTable.remove(taskID);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("UpperMiddleWare Start.");
		try {
			UpperMiddleWare upperMiddleWare = new UpperMiddleWare(args[0]);
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.equals("send task")) {
				Random random = new Random();
				Document params = new Document("findCondition", new Document("Id", random.nextInt(400)));
				params.append("findOrder", null).append("findProjection", null);
				params.append("findLimit", 0);
				params.append("findSkip", 0);
				String result = upperMiddleWare.find("project", "Ship", params.toJson());
				System.out.println(result);
			}
			scanner.close();
			upperMiddleWare.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
