package index.bplustree;

import java.util.ArrayList;

/**
 * Node
 * 
 * @author 高翔宇
 * 
 */
public class Node<K extends Comparable<K>, T> {
	protected boolean isLeafNode;
	protected ArrayList<K> keys;
	
	public boolean isOverflowed() {
		return keys.size() > 2*BPlusTree.D;
	}
	
	public boolean isUnderflowed() {
		return keys.size() < BPlusTree.D;
	}

	public boolean isLeafNode() {
		return isLeafNode;
	}	
	
	public String toString() {
		String result = "[";
		for (int i = 0; i < keys.size()-1; i++) {
			result += keys.get(i) + ", ";
		}
		result += keys.get(keys.size()-1) + "]";
		return result;
	}
}
