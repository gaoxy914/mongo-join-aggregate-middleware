package entry;

import conf.Constants;

public class Result {
	private long taskID;
	private String result;
	
	public Result(long taskID, String result) {
		this.taskID = taskID;
		this.result = result;
	}
	
	public Result(String serializedData) {
		String[] args = serializedData.split(Constants.SPLITER);
		taskID = Long.parseLong(args[0]);
		result = args[1];
	}
	
	public String serialize() {
		String string = taskID + Constants.SPLITER + result;
		return string;
	}

	public long getTaskID() {
		return taskID;
	}

	public void setTaskID(long taskID) {
		this.taskID = taskID;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
