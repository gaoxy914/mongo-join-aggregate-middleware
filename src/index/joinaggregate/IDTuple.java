package index.joinaggregate;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class IDTuple implements Serializable{
	private static final long serialVersionUID = 1L;
	private ObjectId s_Id;
	private ObjectId r_Id;
	
	public IDTuple(ObjectId s_Id, ObjectId r_Id) {
		this.s_Id = s_Id;
		this.r_Id = r_Id;
	}
	
	public ObjectId getS_Id() {
		return s_Id;
	}
	
	public ObjectId getR_Id() {
		return r_Id;
	}
}
