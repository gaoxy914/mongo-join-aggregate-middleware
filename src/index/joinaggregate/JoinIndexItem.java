package index.joinaggregate;

import java.io.Serializable;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import conf.Constants;

public class JoinIndexItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private ArrayList<IDTuple> tuples;
	private String condition;
	private Object value;
	private int count; 
	
	public JoinIndexItem(String condition, Object value) {
		tuples = new ArrayList<IDTuple>();
		this.condition = condition;
		this.value = value;
		count = 0;
	}

	public void addTuple(ObjectId s_Id, ObjectId r_Id) {
		IDTuple iTuple = new IDTuple(s_Id, r_Id);
		tuples.add(iTuple);
		count ++;
	}
	
	public ArrayList<ObjectId> getS_Id() {
		ArrayList<ObjectId> list = new ArrayList<ObjectId>();
		for (IDTuple tuple : tuples) {
			list.add(tuple.getS_Id());
		}
		return list;
	}
	
	public ArrayList<Document> getS_Id_Document() {
		ArrayList<Document> list = new ArrayList<Document>();
		for (IDTuple tuple : tuples) {
			list.add(new Document(Constants._ID, tuple.getS_Id()));
		}
		return list;
	}
	
	public ArrayList<Document> getR_Id_Document() {
		ArrayList<Document> list = new ArrayList<Document>();
		for (IDTuple tuple : tuples) {
			list.add(new Document(Constants._ID, tuple.getR_Id()));
		}
		return list;
	}
	
	public Document findR_Id(ObjectId s_Id) {
		ArrayList<Document> id = new ArrayList<Document>();
		for (IDTuple tuple : tuples) {
			if (tuple.getS_Id().equals(s_Id)) {
				id.add(new Document(Constants._ID, tuple.getR_Id()));
			}
		}
		if (id.size() > 1) {
			return new Document(Constants.$OR, id);
		}
		return id.get(0);
	}
	
	public ArrayList<ObjectId> getR_Id() {
		ArrayList<ObjectId> list = new ArrayList<ObjectId>();
		for (IDTuple tuple : tuples) {
			list.add(tuple.getR_Id());
		}
		return list;
	}
	
	public int getCount() {
		return count;
	}

	public ArrayList<IDTuple> getTuples() {
		return tuples;
	}

	public void setTuples(ArrayList<IDTuple> tuples) {
		this.tuples = tuples;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		String string = "";
		for (int i = 0; i < tuples.size(); i++) {
			string += "(" + tuples.get(i).getS_Id() + "," + tuples.get(i).getR_Id() + ")";
		}
		string += condition + ":" + value;
		return string;
	}
}
