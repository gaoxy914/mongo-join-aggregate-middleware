package index.bplustree;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Utils {
	public static <K extends Comparable<K>,T> void printTree(BPlusTree<K, T> tree) {
		System.out.println(outputTree(tree));
	}
	
	public static <K extends Comparable<K>, T> String outputTree(BPlusTree<K, T> tree) {
		LinkedBlockingQueue<Node<K, T>> queue;
		queue = new LinkedBlockingQueue<Node<K, T>>();
		String result = "";
		int nodesInCurrentLevel = 1;
		int nodesInNextLevel = 0;
		ArrayList<Integer> childrenPerIndex = new ArrayList<Integer>();
		queue.add(tree.root);
		while (!queue.isEmpty()) {
			Node<K, T> target = queue.poll();
			nodesInCurrentLevel--;
			if (target.isLeafNode) {
				LeafNode<K, T> leafNode = (LeafNode<K, T>) target;
				result += "[";
				for (int i = 0; i < leafNode.keys.size(); i++) {
					result += "(" + leafNode.keys.get(i) + "," + leafNode.values.get(i) + ");";
				}
				if (childrenPerIndex.isEmpty()) {
					result += "]$";
				} else {
					childrenPerIndex.set(0, childrenPerIndex.get(0)-1);
					if (childrenPerIndex.get(0) == 0) {
						result += "]$";
						childrenPerIndex.remove(0);
					} else {
						result += "]#";
					}
				}
			} else {
				IndexNode<K, T> indexNode = (IndexNode<K, T>) target;
				result += "@";
				for (int i = 0; i < indexNode.keys.size(); i++) {
					result += "" + indexNode.keys.get(i) + "/";
				}
				result += "@";
				queue.addAll(indexNode.children);
				if (indexNode.children.get(0).isLeafNode) {
					childrenPerIndex.add(indexNode.children.size());
				}
				nodesInNextLevel += indexNode.children.size();
			}
			if (nodesInCurrentLevel == 0) {
				result += "%%";
				nodesInCurrentLevel += nodesInNextLevel;
				nodesInNextLevel = 0;
			}
		}
		return result;
	}
}
