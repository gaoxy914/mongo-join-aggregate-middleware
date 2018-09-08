package entry;

import conf.Constants;

public class Task {
	private long taskID;
	private String func;
	private String db;
	private String collection;
	private String condition;
	
	public Task(long taskID, String func, String db, String collection, String condition) {
		this.taskID = taskID;
		this.func = func;
		this.db = db;
		this.collection = collection;
		this.condition = condition;
	}
	
	public Task(String serializedData) {
		String[] args = serializedData.split(Constants.SPLITER);
		taskID = Long.parseLong(args[0]);
		func = args[1];
		db = args[2];
		collection = args[3];
		condition = args[4];
	}
	
	public String serialize() {
		String string = taskID + Constants.SPLITER + func + Constants.SPLITER + db + Constants.SPLITER
				+ collection + Constants.SPLITER + condition;
		return string;
	}
	
	public long getTaskID() {
		return taskID;
	}
	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
