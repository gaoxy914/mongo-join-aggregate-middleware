package index.joinaggregate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import conf.Constants;
import index.bplustree.BPlusTree;
import index.bplustree.LeafNode;
import index.bplustree.Node;

public class JoinAggregateLimitedMemory {
	private int memoryLeft = 64;
	private Hashtable<String, ArrayList<JoinIndexItem>> joinIndex;
	private Hashtable<String, BPlusTree<String, JoinIndexItem>> btreeIndex;
	private Hashtable<String, ArrayList<LeafNode<String, JoinIndexItem>>> Q;
	
	public JoinAggregateLimitedMemory() {
		joinIndex = new Hashtable<String, ArrayList<JoinIndexItem>>();
		btreeIndex = new Hashtable<String, BPlusTree<String, JoinIndexItem>>();
		Q = new Hashtable<String, ArrayList<LeafNode<String, JoinIndexItem>>>();
	}
	
	public double calculateIOsave() {
		double saveIO = 0;
		for (String treeName : btreeIndex.keySet()) {
			BPlusTree<String, JoinIndexItem> bPlusTree = btreeIndex.get(treeName);
			for (Node<String, JoinIndexItem> node : bPlusTree.nodesInMemory) {
				for (String query : Q.keySet()) {
					if (intersect(treeName, query)) {
						List<LeafNode<String, JoinIndexItem>> q = Q.get(query);
						saveIO += bPlusTree.B(node, q);
					}
				}
			}
		}
		return saveIO;
	}
	
	public int getAllIndexSize() {
		int size = 0;
		for (BPlusTree<String, JoinIndexItem> bPlusTree : btreeIndex.values()) {
			size += bPlusTree.N;
		}
		return size;
	}
	
	public void add_Q(String query, String key) {
		LeafNode<String, JoinIndexItem> leafNode = btreeIndex.get(query).searchNode(key);
		System.out.println(leafNode.toString());
		if (Q.containsKey(query)) {
			Q.get(query).add(leafNode);
		} else {
			ArrayList<LeafNode<String, JoinIndexItem>> list = new ArrayList<LeafNode<String, JoinIndexItem>>();
			list.add(leafNode);
			Q.put(query, list);
		}
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
	
	public BPlusTree<String, JoinIndexItem> getBtreeIndex(String S, String R, String joinAttr, String condition, MongoCollection<Document> sCollection, MongoCollection<Document> rCollection) {
		String key = getKey(S, R, joinAttr, condition);
		if (btreeIndex.containsKey(key)) {
			return btreeIndex.get(key);
		}
		BPlusTree<String, JoinIndexItem> bPlusTree = createBtreeIndex(getJoinIndex(S, R, joinAttr, condition, sCollection, rCollection));
		btreeIndex.put(key, bPlusTree);
		return bPlusTree;
	}
	
	public ArrayList<JoinIndexItem> createJoinIndex(MongoCollection<Document> S, MongoCollection<Document> R, String joinAttr, String condition) {
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
	
	public BPlusTree<String, JoinIndexItem> createBtreeIndex(ArrayList<JoinIndexItem> joinIndex) {
		BPlusTree<String, JoinIndexItem> tree = new BPlusTree<String, JoinIndexItem>();
		for (JoinIndexItem item : joinIndex) {
			tree.insert(item.getValue().toString(), item);
		}
//		System.out.println(tree.toString());
		tree.nodesInMemory.add(tree.root);
		return tree;
	}
	
	public String getKey(String S, String R, String joinAttr, String condition) {
		return S + "." + R + "." + joinAttr + "." + condition;
	}
	
	public double EB() {
		double max = 0;
		BPlusTree<String, JoinIndexItem> tree = null;
		Node<String, JoinIndexItem> n = null;
		for (String treeName : btreeIndex.keySet()) {
//			System.out.println(treeName);
			BPlusTree<String, JoinIndexItem> bPlusTree = btreeIndex.get(treeName);
			System.out.println(bPlusTree.N);
			for (Node<String, JoinIndexItem> node : bPlusTree.nodesInMemory) {
				double benifit = 0;
				for (String query : Q.keySet()) {
//					System.out.println(treeName);
//					System.out.println(query);
					if (intersect(treeName, query)) {
						List<LeafNode<String, JoinIndexItem>> q = Q.get(query);
						benifit += bPlusTree.ExtendBenifit(node, q, memoryLeft);
//						System.out.println(benifit);
					}
				}
//				System.out.println(benifit);
				if (benifit > max) {
					max = benifit;
					tree = bPlusTree;
					n = node;
				}
			}
		}
		if (n != null) {
			int space = tree.extend(n);
			memoryLeft -= space;
		}
		return max;
	}
	
	public void getOptimalIndex() {
		while (memoryLeft > 0 && EB() > 0) {
			System.out.println("extending...");
			System.out.println("left memory : " + memoryLeft);
		}
		System.out.println("get optimal index.");
	}
	
	public void getRandomIndex(String treeName, int memorySize) {
		BPlusTree<String, JoinIndexItem> bPlusTree = btreeIndex.get(treeName);
		bPlusTree.randExtend(memorySize);
		memoryLeft -= memorySize;
		System.out.println(bPlusTree.nodesInMemory.size());
		System.out.println("left memory : " + memoryLeft);
	}
	
	private boolean intersect(String treeName, String query) {
		if (treeName.equals(query)) {
			return true;
		}
		return false;
	}
}
