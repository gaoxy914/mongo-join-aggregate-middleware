package index.bplustree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * IndexNode an indexNode will hava m nodes and m+1 children
 * 
 * @author 高翔宇
 *
 */
public class IndexNode<K extends Comparable<K>, T> extends Node<K, T> {
	protected ArrayList<Node<K, T>> children;
	
	public IndexNode(K key, Node<K, T> child0, Node<K, T> child1) {
		isLeafNode = false;
		keys = new ArrayList<K>();
		keys.add(key);
		children = new ArrayList<Node<K, T>>();
		children.add(child0);
		children.add(child1);
	}
	
	public IndexNode(List<K> newKeys, List<Node<K, T>> newChildren) {
		isLeafNode = false;
		keys = new ArrayList<K>(newKeys);
		children = new ArrayList<Node<K, T>>(newChildren);
	}
	
	public void insertSorted(Entry<K, Node<K, T>> e, int index) {
		K key = e.getKey();
		Node<K, T> child = e.getValue();
		if (index >= keys.size()) {
			keys.add(key);
			children.add(child);
		} else {
			keys.add(index, key);
			children.add(index+1, child);
		}
	}

	public ArrayList<Node<K, T>> getChildren() {
		return children;
	}
}
