package index.joinaggregate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import conf.Constants;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.StringComparator;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

public class JoinAggregateIndex {
	private Hashtable<String, ArrayList<IDTuple>> joinTable;
	private Hashtable<String, ArrayList<JoinIndexItem>> joinIndex;
	private Hashtable<String, BTree> btreeIndex;
	
	public JoinAggregateIndex() {
		joinTable = new Hashtable<String, ArrayList<IDTuple>>();
		joinIndex = new Hashtable<String, ArrayList<JoinIndexItem>>();
		btreeIndex = new Hashtable<String, BTree>();
	}
	
	public ArrayList<IDTuple> getJoinTable(String S, String R, String joinAttr, MongoCollection<Document> sCollection, MongoCollection<Document> rCollection) {
		String key = getKey(S, R, joinAttr, null);
//		System.out.println("get jointable : " + key);
		if (joinTable.containsKey(key)) {
			return joinTable.get(key);
		}
		ArrayList<IDTuple> list = createJoinTable(sCollection, rCollection, joinAttr);
		joinTable.put(key, list);
		return list;
	}
	
	public ArrayList<JoinIndexItem> getJoinIndex(String S, String R, String joinAttr, String condition, MongoCollection<Document> sCollection, MongoCollection<Document> rCollection) {
		String key = getKey(S, R, joinAttr, condition);
		if (joinIndex.containsKey(key)) {
			return joinIndex.get(key);
		}
		ArrayList<JoinIndexItem> list = createJoinIndex(sCollection, rCollection, joinAttr, condition);
		joinIndex.put(key, list);
		return list;
	}
	
	public BTree getBTreeIndex(String S, String R, String joinAttr, String condition, MongoCollection<Document> sCollection, MongoCollection<Document> rCollection) {
		String key = getKey(S, R, joinAttr, condition);
//		System.out.println(key);
		if (btreeIndex.containsKey(key)) {
			return btreeIndex.get(key);
		} else {
			String btreeName = getKey(S, R, joinAttr, condition);
			BTree tree = null;
			RecordManager recordManager;
			Long recid;
			Properties properties = new Properties();
			try {
				recordManager = RecordManagerFactory.createRecordManager(Constants.DATABASE, properties);
				recid = recordManager.getNamedObject(btreeName);
				if (recid != 0) {
					tree = BTree.load(recordManager, recid);
					System.out.println("Reloaded existing BTree with " + tree.size() + " " + btreeName);
					btreeIndex.put(key, tree);
				} else {
					tree = createBTreeIndex(S, R, joinAttr, condition, getJoinIndex(S, R, joinAttr, condition, sCollection, rCollection));
				}
			} catch ( Exception except ) {
	            except.printStackTrace();
	        }
			return tree;
		}
	}
	
	private ArrayList<IDTuple> createJoinTable(MongoCollection<Document> S, MongoCollection<Document> R, String joinAttr) {
		ArrayList<IDTuple> joinTable = new ArrayList<IDTuple>();
		FindIterable<Document> sIterable = S.find();
		for (Document sDocument : sIterable) {
			if (sDocument.containsKey(joinAttr)) {
				ObjectId s_Id = sDocument.getObjectId(Constants._ID);
				FindIterable<Document> rIterable = R.find(new Document(joinAttr, sDocument.get(joinAttr)));
				for (Document rDocument : rIterable) {
					ObjectId r_Id = rDocument.getObjectId(Constants._ID);
					joinTable.add(new IDTuple(s_Id, r_Id));
				}
			}
		}
		return joinTable;
	}
	
	private ArrayList<JoinIndexItem> createJoinIndex(MongoCollection<Document> S, MongoCollection<Document> R, String joinAttr, String condition) {
		ArrayList<JoinIndexItem> indexTable = new ArrayList<JoinIndexItem>();
		FindIterable<Document> sIterable = S.find();
		for (Document sDocument : sIterable) {
			if (sDocument.containsKey(joinAttr)) {
				ObjectId S_id = sDocument.getObjectId(Constants._ID);
				FindIterable<Document> rIterable = R.find(new Document(joinAttr, sDocument.get(joinAttr)));
				for (Document rDocument : rIterable) {
					ObjectId R_id = rDocument.getObjectId(Constants._ID);
					Object value;
					if (sDocument.containsKey(condition)) {
						value = sDocument.get(condition);
					} else {
						value = rDocument.get(condition);
					}
					boolean flag = false;
					for (JoinIndexItem joinIndexItem : indexTable) {
						if (joinIndexItem.getValue().equals(value)) {
							joinIndexItem.addTuple(S_id, R_id);
							flag = true;
							break;
						}
					}
					if (!flag) {
						JoinIndexItem indexItem = new JoinIndexItem(condition, value);
						indexItem.addTuple(S_id, R_id);
						indexTable.add(indexItem);
					}
				}
			}
		}
		return indexTable;
	}
	
	private BTree createBTreeIndex(String S, String R, String joinAttr, String condition, ArrayList<JoinIndexItem> joinIndex) {
		String btreeName = getKey(S, R, joinAttr, condition);
		BTree tree = null;
		RecordManager recordManager;
		Long recid;
		Properties properties = new Properties();
		try {
			recordManager = RecordManagerFactory.createRecordManager(Constants.DATABASE, properties);
			recid = recordManager.getNamedObject(btreeName);
			if (recid != 0) {
				tree = BTree.load(recordManager, recid);
				System.out.println( "Reloaded existing BTree with " + tree.size() + " " + btreeName);
			} else {
				tree = BTree.createInstance(recordManager, new StringComparator());
				recordManager.setNamedObject(btreeName, tree.getRecid());
				System.out.println( "Created a new empty BTree" );
				for (int i = 0; i < joinIndex.size(); i++) {
					JoinIndexItem item = joinIndex.get(i);
					tree.insert(item.getValue().toString(), item, false);
				}
				recordManager.commit();
			}
		} catch ( Exception except ) {
            except.printStackTrace();
        }
		return tree;
	}
	
	private String getKey(String S, String R, String joinAttr, String condition) {
		if (condition == null) {
			return S + "." + R + "." + joinAttr;
		}
		return S + "." + R + "." + joinAttr + "." + condition;
	}
	
	public static void main(String[] args) {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
		MongoClient mongoClient = new MongoClient("localhost", 30000);
		MongoDatabase mongoDatabase = mongoClient.getDatabase("project");
		MongoCollection<Document> student = mongoDatabase.getCollection("student");
		MongoCollection<Document> major = mongoDatabase.getCollection("major");
		System.out.println("student collection");
		FindIterable<Document> sIterable = student.find();
		for (Document document : sIterable) {
			System.out.println(document.toJson());
		}
		System.out.println("major collection");
		FindIterable<Document> mIterable = major.find();
		for (Document document : mIterable) {
			System.out.println(document.toJson());
		}
		JoinAggregateIndex joinAggreagateIndex = new JoinAggregateIndex();
		System.out.println("show index on student and major, join attribute is major");	
		ArrayList<JoinIndexItem> items = joinAggreagateIndex.getJoinIndex("student", "major", "major", "studentnumber", student, major);
		for (int i = 0; i < items.size(); i++) {
			System.out.println(items.get(i).toString());
		}
		BTree tree = joinAggreagateIndex.getBTreeIndex("student", "major", "major", "studentnumber", student, major);
		Tuple tuple = new Tuple();
		TupleBrowser browser;
		try {
			browser = tree.browse();
			while (browser.getNext(tuple)) {
				print(tuple);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mongoClient.close();
	}
	
	static void print( Tuple tuple ) {
        String person = (String) tuple.getKey();
        JoinIndexItem occupation = (JoinIndexItem) tuple.getValue();
        System.out.println( pad( person, 25) + occupation.toString() );
    }
	
	static String pad( String str, int width ) {
        StringBuffer buf = new StringBuffer( str );
        int space = width-buf.length();
        while ( space-- > 0 ) {
            buf.append( ' ' );
        }
        return buf.toString();
    }

}
