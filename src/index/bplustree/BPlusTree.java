package index.bplustree;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * BPlusTree
 * 
 * @author 高翔宇
 *
 */
public class BPlusTree<K extends Comparable<K>, T> {
	public static final int D = 1;
	
	public Node<K, T> root;
	public int N = 0;
	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		// emoty tree or key
		if (key == null || root == null) {
			return null;
		}
		// search leaf contains the key
		LeafNode<K, T> leafNode = (LeafNode<K, T>) treeSearch(root, key);
		// search in leaf
		for (int i = 0; i < leafNode.keys.size(); i++) {
			if (key.compareTo(leafNode.keys.get(i)) == 0) {
				return leafNode.values.get(i);
			}
		}
		return null;
	}
	
	public LeafNode<K, T> searchNode(K key) {
		// emoty tree or key
		if (key == null || root == null) {
			return null;
		}
		// search leaf contains the key
		LeafNode<K, T> leafNode = (LeafNode<K, T>) treeSearch(root, key);
		return leafNode;
	}
	
	private Node<K, T> treeSearch(Node<K, T> node, K key) {
		if (node.isLeafNode) {
			return node;
		} else {
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			if (key.compareTo(indexNode.keys.get(0)) < 0) {
				return treeSearch(indexNode.children.get(0), key);
			} else if (key.compareTo(indexNode.keys.get(indexNode.keys.size()-1)) >= 0) {
				return treeSearch(indexNode.children.get(indexNode.children.size()-1), key);
			} else {
				for (int i = 0; i < indexNode.keys.size()-1; i++) {
					if (key.compareTo(indexNode.keys.get(i)) >= 0 && key.compareTo(indexNode.keys.get(i + 1)) < 0) {
						return treeSearch(indexNode.children.get(i+1), key);
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * TODO Insert a key-value into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		LeafNode<K, T> newLeaf = new LeafNode<K, T>(key, value);
		Entry<K, Node<K, T>> entry = new AbstractMap.SimpleEntry<K, Node<K, T>>(key, newLeaf);
		if (root == null || root.keys.size() == 0) {
			root = entry.getValue();
			N++;
		}
		Entry<K, Node<K, T>> newChildEntry = getChildEntry(root, entry, null);
		if (newChildEntry == null) {
			return;
		} else {
			N++;
			IndexNode<K, T> newRoot = new IndexNode<K, T>(newChildEntry.getKey(), root, newChildEntry.getValue());
			root = newRoot;
			return;
		}
	}
	
	/**
	 * TODO Insert an entry into the subtree node, return newChildEntry
	 * newChildEntry is null unless the child is splited, newChildEntry is the newNode should be insert into the parentNode
	 * 
	 * @param node
	 * @param entry
	 * @param newChildEntry
	 * @return
	 */
	private Entry<K, Node<K, T>> getChildEntry(Node<K, T> node, Entry<K, Node<K, T>> entry, Entry<K, Node<K, T>> newChildEntry) {
		if (!node.isLeafNode) {
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			int i = 0;
			while (i < indexNode.keys.size()) {
				if (entry.getKey().compareTo(indexNode.keys.get(i)) < 0) {
					break;
				}
				i++;
			}
			// insret recursively
			newChildEntry = getChildEntry(indexNode.children.get(i), entry, newChildEntry);
			if (newChildEntry == null) {
				return null;
			} else { // the child is splited, insert newChildEntry into the parentNode
				int j = 0;
				while (j < indexNode.keys.size()) {
					if (newChildEntry.getKey().compareTo(indexNode.keys.get(j)) < 0) {
						break;
					}
					j++;
				}
				indexNode.insertSorted(newChildEntry, j);
				if (!indexNode.isOverflowed()) {
					return null;
				} else {
					N++;
					newChildEntry = splitIndexNode(indexNode);
					if (indexNode == root) {
						IndexNode<K, T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), root, newChildEntry.getValue());
						root = newRoot;
						N++;
						return null;
					}
					return newChildEntry;
				}
			}
		} else {
			LeafNode<K, T> leafNode = (LeafNode<K, T>) node;
			LeafNode<K, T> newLeafNode = (LeafNode<K, T>) entry.getValue();
			leafNode.insertSorted(entry.getKey(), newLeafNode.values.get(0));
			if (!leafNode.isOverflowed()) {
				return null;
			} else {
				N++;
				newChildEntry = splitLeafNode(leafNode);
				if (leafNode == root) {
					IndexNode<K, T> newRoot = new IndexNode<K, T>(newChildEntry.getKey(), leafNode, newChildEntry.getValue());
					root = newRoot;
					N++;
					return null;
				}
				return newChildEntry;
			}
		}
	}
	
	/**
	 * TODO Split a leafNode and return the new rightNode(as a pointer)
	 * get two new leafNode: 0-D-1th keys and values; D-2Dth keys and values
	 * 
	 * @param leafNode
	 * @return 0th key and rightNode as an Entry
	 */
	public Entry<K, Node<K, T>> splitLeafNode(LeafNode<K, T> leafNode) {
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<T> newValues = new ArrayList<T>();
		for (int i = D; i <= 2*D; i++) {
			newKeys.add(leafNode.keys.get(i));
			newValues.add(leafNode.values.get(i));
		}
		for (int i = D; i <= 2*D; i++) {
			leafNode.keys.remove(leafNode.keys.size()-1);
			leafNode.values.remove(leafNode.values.size()-1);
		}
		K splitKey = newKeys.get(0);
		LeafNode<K, T> rightNode = new LeafNode<K, T>(newKeys, newValues);
		LeafNode<K, T> tmp = leafNode.nextLeaf;
		leafNode.nextLeaf = rightNode;
		if (tmp != null) {
			tmp.previousLeaf = rightNode;
		}
//		leafNode.nextLeaf.previousLeaf = rightNode;
		rightNode.previousLeaf = leafNode;
		rightNode.nextLeaf = tmp;
		Entry<K, Node<K, T>> newChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(splitKey, rightNode);
		return newChildEntry;
	}
	
	/**
	 * TODO Split an indexNode and return the new rightNode(as a pointer)
	 * get two new indexNode: 0-D-1th keys and 0-Dth values; D+1-2Dth keys and D+1-2D+1th values
	 * 
	 * @param indexNode
	 * @return Dth key and rightNode as an Entry
	 */
	public Entry<K, Node<K, T>> splitIndexNode(IndexNode<K, T> indexNode) {
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<Node<K, T>> newChildren = new ArrayList<Node<K, T>>();
		K splitKey = indexNode.keys.get(D);
		indexNode.keys.remove(D);
		newChildren.add(indexNode.children.get(D+1));
		indexNode.children.remove(D+1);
		while (indexNode.keys.size() > D) {
			newKeys.add(indexNode.keys.get(D));
			indexNode.keys.remove(D);
			newChildren.add(indexNode.children.get(D+1));
			indexNode.children.remove(D+1);
		}
		IndexNode<K, T> rightNode = new IndexNode<K, T>(newKeys, newChildren);
		Entry<K, Node<K, T>> newChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(splitKey, rightNode);
		return newChildEntry;
	}
	
	
	/**
	 * TODO Delete a key-value from the BPlusTree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		if (key == null || root == null) {
			return;
		}
		LeafNode<K, T> leafNode = (LeafNode<K, T>) treeSearch(root, key);
		if (leafNode == null) {
			return;
		}
		Entry<K, Node<K, T>> entry = new AbstractMap.SimpleEntry<K, Node<K, T>>(key, leafNode);
		Entry<K, Node<K, T>> oldChildEntry = deleteChildEntry(root, root, entry, null);
		if (oldChildEntry == null) {
			if (root.keys.size() == 0) {
				if (!root.isLeafNode) {
					root = ((IndexNode<K, T>) root).children.get(0);
				}
			}
			return;
		} else {
			int i = 0;
			K oldKey = oldChildEntry.getKey();
			while (i < root.keys.size()) {
				if (oldKey.compareTo(root.keys.get(i)) == 0) {
					break;
				}
				i++;
			}
			if (i == root.keys.size()) {
				return;
			}
			root.keys.remove(i);
			((IndexNode<K, T>) root).children.remove(i+1);
			return;
		}
	}
	
	/**
	 * TODO Delete an entry from the subtree node, return oldChildEntry
	 * oldChildEntry is null unless the child is deleted, oldChildEntry is the node which is deleted.
	 * 
	 * @param parentNode
	 * @param node
	 * @param entry
	 * @param oldChildEntry
	 * @return
	 */
	private Entry<K, Node<K, T>> deleteChildEntry(Node<K, T> parentNode, Node<K, T> node, Entry<K, Node<K, T>> entry, Entry<K, Node<K, T>> oldChildEntry) {
		if (!node.isLeafNode) {
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			int i = 0;
			K entryKey = entry.getKey();
			while (i < indexNode.keys.size()) {
				if (entryKey.compareTo(indexNode.keys.get(i)) < 0) {
					break;
				}
				i++;
			}
			// delete recursively
			oldChildEntry = deleteChildEntry(indexNode, indexNode.children.get(i), entry, oldChildEntry);
			if (oldChildEntry == null) {
				return null;
			} else {
				int j = 0;
				K oldKey = oldChildEntry.getKey();
				while (j < indexNode.keys.size()) {
					if (oldKey.compareTo(indexNode.keys.get(j)) == 0) {
						break;
					}
					j++;
				}
				indexNode.keys.remove(j);
				indexNode.children.remove(j+1);
				if (!indexNode.isUnderflowed() || indexNode.keys.size() == 0) {
					return null;
				} else {
					if (indexNode == root) {
						return oldChildEntry;
					}
					int s = 0;
					K firstKey = indexNode.keys.get(0);
					while (s < parentNode.keys.size()) {
						if (firstKey.compareTo(parentNode.keys.get(s)) < 0) {
							break;
						}
						s++;
					}
					int splitKeyPos;
					IndexNode<K, T> parent = (IndexNode<K, T>) parentNode;
					if (s > 0 && parent.children.get(s-1) != null) {
						splitKeyPos = handleIndexNodeUnderflow((IndexNode<K, T>) parent.children.get(s-1), indexNode, parent);
					} else {
						splitKeyPos = handleIndexNodeUnderflow(indexNode, (IndexNode<K, T>) parent.children.get(s+1), parent);
					}
					if (splitKeyPos == -1) {
						return null;
					} else {
						K parentKey = parentNode.keys.get(splitKeyPos);
						oldChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(parentKey, parentNode);
						return oldChildEntry;
					}
				}
			}
		} else {
			LeafNode<K, T> leafNode = (LeafNode<K, T>) node;
			for (int i = 0; i < leafNode.keys.size(); i++) {
				if (leafNode.keys.get(i) == entry.getKey()) {
					leafNode.keys.remove(i);
					leafNode.values.remove(i);
					break;
				}
			}
			if (!leafNode.isUnderflowed()) {
				return null;
			} else {
				if (leafNode == root || leafNode.keys.size() == 0) {
					return oldChildEntry;
				}
				int splitKeyPos;
				K firstKey = leafNode.keys.get(0);
				K parentKey = parentNode.keys.get(0);
				if (leafNode.previousLeaf != null && firstKey.compareTo(parentKey) >= 0) {// firstKey < parentKey : leafNode is the children[0] of parentNode
					splitKeyPos = handleLeafNodeUnderflow(leafNode.previousLeaf, leafNode, (IndexNode<K, T>) parentNode);
				} else {
					splitKeyPos = handleLeafNodeUnderflow(leafNode, leafNode.nextLeaf, (IndexNode<K, T>) parentNode);
				}
				if (splitKeyPos == -1) {
					return null;
				} else {
					parentKey = parentNode.keys.get(splitKeyPos);
					oldChildEntry = new AbstractMap.SimpleEntry<K, Node<K, T>>(parentKey, parentNode);
					return oldChildEntry;
				}
			}
		}
	}
	
	/**
	 * TODO Handle leafNode underflow (merge or redistribution)
	 * 
	 * @param left
	 * @param right
	 * @param parent
	 * @return merge the splitkey position in parent; redistribution -1.
	 */
	public int handleLeafNodeUnderflow(LeafNode<K, T> left, LeafNode<K, T> right, IndexNode<K, T> parent) {
		int i = 0;
		K rKey = right.keys.get(0);
		while (i < parent.keys.size()) {
			if (rKey.compareTo(parent.keys.get(i)) < 0) {
				break;
			}
			i++;
		}
		if (left.keys.size() + right.keys.size() >= 2*D) { // redistribution
			if (left.keys.size() > right.keys.size()) {
				while (left.keys.size() > D) {
					right.keys.add(0, left.keys.get(left.keys.size()-1));
					right.values.add(0, left.values.get(left.values.size()-1));
					left.keys.remove(left.keys.size()-1);
					left.values.remove(left.values.size()-1);
				}
			} else {
				while (left.keys.size() < D) {
					left.keys.add(right.keys.get(0));
					left.values.add(right.values.get(0));
					right.keys.remove(0);
					right.values.remove(0);
				}
			}
			parent.keys.set(i-1, right.keys.get(0));
			return -1;
		} else { // merge
			while (right.keys.size() > 0) {
				left.keys.add(right.keys.get(0));
				left.values.add(right.values.get(0));
				right.keys.remove(0);
				right.values.remove(0);
			}
			if (right.nextLeaf != null) {
				right.nextLeaf.previousLeaf = left;
			}
			left.nextLeaf = right.nextLeaf;
			return i-1;
		}
	}
	
	/**
	 * TODO Handle indexNode underflow (merge or redistribution)
	 * 
	 * @param leftIndex
	 * @param rightIndex
	 * @param parent
	 * @return merge the splitkey position in parent; redistribution -1.
	 */
	public int handleIndexNodeUnderflow(IndexNode<K, T> leftIndex, IndexNode<K, T> rightIndex, IndexNode<K, T> parent) {
		int i = 0;
		K rKey = rightIndex.keys.get(0);
		while (i < parent.keys.size()) {
			if (rKey.compareTo(parent.keys.get(i)) < 0) {
				break;
			}
			i++;
		}
		if (leftIndex.keys.size() + rightIndex.keys.size() >= 2*D) { // redistribution
			if (leftIndex.keys.size() > rightIndex.keys.size()) {
				while (leftIndex.keys.size() > D) {
					rightIndex.keys.add(0, parent.keys.get(i-1));
					rightIndex.children.add(leftIndex.children.get(leftIndex.children.size()-1));
					parent.keys.set(i-1, leftIndex.keys.get(leftIndex.keys.size()-1));
					leftIndex.keys.remove(leftIndex.keys.size()-1);
					leftIndex.children.remove(leftIndex.children.size()-1);
				}
			} else {
				while (leftIndex.keys.size() < D) {
					leftIndex.keys.add(parent.keys.get(i-1));
					leftIndex.children.add(rightIndex.children.get(0));
					parent.keys.set(i-1, rightIndex.keys.get(0));
					rightIndex.keys.remove(0);
					rightIndex.children.remove(0);
				}
			}
			return -1;
		} else { // merge
			leftIndex.keys.add(parent.keys.get(i-1));
			while (rightIndex.keys.size() > 0) {
				leftIndex.keys.add(rightIndex.keys.get(0));
				leftIndex.children.add(rightIndex.children.get(0));
				rightIndex.keys.remove(0);
				rightIndex.children.remove(0);
			}
			leftIndex.children.add(rightIndex.children.get(0));
			rightIndex.children.remove(0);
			return i-1;
		}
	}
	
	/**
	 * TODO To string
	 */
	public String toString() {
		LinkedBlockingQueue<Node<K, T>> queue;
		queue = new LinkedBlockingQueue<Node<K, T>>();
		String result = "";
		int nodesInCurrentLevel = 1;
		int nodesInNextLevel = 0;
		ArrayList<Integer> childrenPerIndex = new ArrayList<Integer>();
		queue.add(root);
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
				result += "\n";
				nodesInCurrentLevel += nodesInNextLevel;
				nodesInNextLevel = 0;
			}
		}
		return result;
	}
	
	/**
	 * Limited Memory
	 */
	public ArrayList<Node<K, T>> nodesInMemory = new ArrayList<Node<K, T>>();
	
	/**
	 * TODO Get parent node of node n
	 * 
	 * @param n
	 * @return
	 */
	public Node<K, T> getParent(Node<K, T> n) {
		List<Node<K, T>> list = new ArrayList<Node<K, T>>();
		ancient(root, n, list);
		return list.get(list.size()-1);
	}
	
	/**
	 * TODO Get ancient nodes of node x
	 * 
	 * @param x
	 * @return
	 */
	public List<Node<K, T>> getAncient(Node<K, T> x) {
		List<Node<K, T>> list = new ArrayList<Node<K, T>>();
		if (x == null || root == null) {
			return list;
		}
		ancient(root, x, list);
		return list;
	}
	
	private void ancient(Node<K, T> node, Node<K, T> x, List<Node<K, T>> list) {
		if (node.isLeafNode) {
			return;
		} else {
			if (node.equals(x)) {
				return;
			}
			list.add(node);
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			K key = x.keys.get(0);
			if (key.compareTo(indexNode.keys.get(0)) < 0) {
				ancient(indexNode.children.get(0), x, list);
			} else if (key.compareTo(indexNode.keys.get(indexNode.keys.size()-1)) >= 0) {
				ancient(indexNode.children.get(indexNode.children.size()-1), x, list);
			} else {
				for (int i = 0; i < indexNode.keys.size()-1; i++) {
					if (key.compareTo(indexNode.keys.get(i)) >= 0 && key.compareTo(indexNode.keys.get(i+1)) < 0) {
						ancient(indexNode.children.get(i+1), x, list);
					}
				}
			}
		}
	}
	
	/**
	 * TODO Get the ancients A(n) of node n and ancients A(m) of node m
	 * then return nodes A(n)/A(m) as a list 
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public List<Node<K, T>> p(Node<K, T> n, Node<K, T> m) {
		List<Node<K, T>> nList = getAncient(n);
		List<Node<K, T>> mList = getAncient(m);
		for (Node<K, T> node : mList) {
			nList.remove(node);
		}
		return nList;
	}
	
	/**
	 * TODO Caculate how many child nodes are in intersectNodes
	 * 
	 * @param node
	 * @param intersectNodes
	 * @return
	 */
	public int f(Node<K, T> node, List<LeafNode<K, T>> intersectNodes) {
		if (node.isLeafNode) {
			return 0;
		} else {
			int count = 0;
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			for (int i = 0; i < indexNode.children.size(); i++) {
				if (indexNode.children.get(i).isLeafNode) {
					LeafNode<K, T> leafNode = (LeafNode<K, T>) indexNode.children.get(i);
//					System.out.println(leafNode.values.get(0));
					for (int j = 0; j < intersectNodes.size(); j++) {
//						System.out.println(intersectNodes.get(j).values.get(0));
						if (intersectNodes.get(j).values.get(0).equals(leafNode.values.get(0))) {
							count++;
						}
					}
				} else {
					if (f(indexNode.children.get(i), intersectNodes) > 0) {
						count++;
					}
				}
			}
			return count;
		}
	}
	
	/**
	 * TODO Caculate cache n can save I/O times for processing q
	 * 
	 * @param n
	 * @param q
	 * @return
	 */
	public double B(Node<K, T> n, List<LeafNode<K, T>> q) {
		double sum = 0;
		if (q.isEmpty()) {
			return sum;
		} else {
			if (n.isLeafNode) {
				boolean intersect = false;
				for (LeafNode<K, T> leafNode : q) {
					if (leafNode.keys.get(0) == n.keys.get(0)) {
						intersect = true;
					}
				}
				if (!intersect) {
					return sum;
				}
			} else {
				if (f(n, q) == 0) {
					return sum;
				}
			}
		}
		List<Node<K, T>> An = getAncient(n);
//		System.out.println(An.size());
		for (int i = 0; i < An.size(); i++) {
			List<Node<K, T>> Pnm = p(n, An.get(i));
			double f = 1;
			for (int j = 0; j < Pnm.size(); j++) {
//				System.out.println(f(Pnm.get(j), q));
				f = f * (1.0 / f(Pnm.get(j), q));
			}
			sum += f;
		}
		return sum;
	}
	
	/**
	 * TODO Caculate the extend benifit of node n
	 * 
	 * @param n
	 * @param q
	 * @param leftMemory
	 * @return
	 */
	public double ExtendBenifit(Node<K, T> n, List<LeafNode<K, T>> q, int leftMemory) {
		double benifit = 0;
		if (n.isLeafNode) {
			return benifit;
		} else {
			IndexNode<K, T> indexNode = (IndexNode<K, T>) n;
			if (indexNode.children.size()-1 > leftMemory) {
				return benifit;
			} else {
				for (Node<K, T> child : indexNode.children) {
					benifit += B(child, q);
//					System.out.println(B(child, q));
				}
				benifit -= B(n, q);
				benifit = benifit / (indexNode.children.size() - 1);
				return benifit;
			}
		}
	}
	
	public int extend(Node<K, T> n) {
		int space = 0;
		if (n.isLeafNode) {
//			System.out.println("1");
			return space;
		}
		nodesInMemory.remove(n);
		space--;
		IndexNode<K, T> indexNode = (IndexNode<K, T>) n;
		for (Node<K, T> child : indexNode.children) {
			if (nodesInMemory.size() == 0) {
				nodesInMemory.add(child);
				space++;
			} else {
				K key = child.keys.get(0);
//				System.out.println(key);
				if (key.compareTo(nodesInMemory.get(0).keys.get(0)) < 0) {
//					System.out.println("1");
					nodesInMemory.add(0, child);
				} else if (key.compareTo(nodesInMemory.get(nodesInMemory.size()-1).keys.get(0)) > 0) {
//					System.out.println("2");
					nodesInMemory.add(child);
				} else {
//					System.out.println("3");
//					System.out.println(nodesInMemory.size());
					for (int i = 0; i < nodesInMemory.size(); i++) {
//						System.out.println(i);
						if (key.compareTo(nodesInMemory.get(i).keys.get(0)) < 0) {
							nodesInMemory.add(i, child);
							break;
						}
					}
				}
			}
			System.out.println("add : " + child.keys.get(0));
			space++;
		}
		return space;
	}
	
	public void randExtend(int memorySize) {
		int leftMemory = memorySize;
		List<Node<K, T>> extendNodes = new ArrayList<Node<K, T>>();
		extendNodes.add(root);
		while (leftMemory > 0 && extendNodes.size() > 0) {
			int size = extendNodes.size();
//			System.out.println(size);
			Random random = new Random();
			int position = random.nextInt(size);
//			System.out.println(extendNodes.get(position).isLeafNode);
//			System.out.println(position);
			Node<K, T> node = extendNodes.get(position);
			int space = extend(node);
//			System.err.println(space);
			leftMemory -= space;
			extendNodes.clear();
			for (Node<K, T> n : nodesInMemory) {
				if (!n.isLeafNode) {
					IndexNode<K, T> indexNode = (IndexNode<K, T>) n;
					if (indexNode.children.size()-1 < leftMemory) {
						extendNodes.add(n);
					}
				}
			}
		}
	}
	
	public T searchInNodesInMemory(K key) {
		for (int i = 0; i < nodesInMemory.size(); i++) {
			if (nodesInMemory.get(i).isLeafNode) {
				LeafNode<K, T> leafNode = (LeafNode<K, T>) nodesInMemory.get(i);
				for (int j = 0; j < leafNode.keys.size(); j++) {
					if (leafNode.keys.get(j).compareTo(key) == 0) {
						return leafNode.values.get(j);
					}
				}
			} else {
				LeafNode<K, T> leafNode = disk_find(nodesInMemory.get(i), key);
				if (leafNode != null) {
					for (int j = 0; j < leafNode.keys.size(); j++) {
						if (leafNode.keys.get(j).compareTo(key) == 0) {
							return leafNode.values.get(j);
						}
					}
				}
			}
		}
		return null;
	}
	
	private LeafNode<K, T> disk_find(Node<K, T> node, K key) {
		if (node.isLeafNode) {
			LeafNode<K, T> leafNode = (LeafNode<K, T>) node;
			if (leafNode.keys.contains(key)) {
				return leafNode;
			} else {
				return null;
			}
		} else {
			IndexNode<K, T> indexNode = (IndexNode<K, T>) node;
			if (key.compareTo(indexNode.keys.get(0)) < 0) {
				return disk_find(indexNode.children.get(0), key);
			} else if (key.compareTo(indexNode.keys.get(indexNode.keys.size()-1)) >= 0) {
				return disk_find(indexNode.children.get(indexNode.children.size()-1), key);
			} else {
				for (int i = 0; i < indexNode.keys.size()-1; i++) {
					if (key.compareTo(indexNode.keys.get(i)) >= 0 && key.compareTo(indexNode.keys.get(i + 1)) < 0) {
						return disk_find(indexNode.children.get(i+1), key);
					}
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Character alphabet[] = new Character[] { 'a', 'b', 'f', 'g', 'h', 'e', 'c', 'd', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		for (int i = 0; i < alphabet.length; i++) {
			tree.insert(alphabet[i], alphabetStrings[i]);
//			System.out.println(tree.toString());
		}
		System.out.println("build B + tree :");
		System.out.print(tree.toString());
		System.out.println(tree.N);
		
		ArrayList<LeafNode<Character, String>> intersectNodes = new ArrayList<LeafNode<Character, String>>();
//		LeafNode<Character, String> f = new LeafNode<Character, String>('a', "a");
//		f.insertSorted('b', "b");
//		LeafNode<Character, String> g = new LeafNode<Character, String>('c', "c");
//		g.insertSorted('d', "d");
//		g.insertSorted('e', "e");
//		LeafNode<Character, String> h = new LeafNode<Character, String>('f', "f");
//		h.insertSorted('g', "g");
//		LeafNode<Character, String> i = new LeafNode<Character, String>('h', "h");
//		i.insertSorted('i', "i");
//		LeafNode<Character, String> j = new LeafNode<Character, String>('j', "j");
//		j.insertSorted('k', "k");
//		LeafNode<Character, String> k = new LeafNode<Character, String>('l', "l");
//		k.insertSorted('m', "m");
//		LeafNode<Character, String> l = new LeafNode<Character, String>('n', "n");
//		l.insertSorted('o', "o");
//		LeafNode<Character, String> m = new LeafNode<Character, String>('p', "p");
//		m.insertSorted('q', "q");
//		LeafNode<Character, String> n = new LeafNode<Character, String>('r', "r");
//		n.insertSorted('s', "s");
//		LeafNode<Character, String> o = new LeafNode<Character, String>('t', "t");
//		o.insertSorted('n', "n");
//		LeafNode<Character, String> p = new LeafNode<Character, String>('v', "v");
//		p.insertSorted('w', "w");
//		LeafNode<Character, String> q = new LeafNode<Character, String>('x', "x");
//		q.insertSorted('y', "y");
//		q.insertSorted('z', "z");
//		intersectNodes.add(f);
//		intersectNodes.add(g);
//		intersectNodes.add(h);
//		intersectNodes.add(i);
//		IndexNode<Character, String> a = (IndexNode<Character, String>) tree.root;
//		IndexNode<Character, String> b = (IndexNode<Character, String>) a.children.get(0);
//		IndexNode<Character, String> c = (IndexNode<Character, String>) a.children.get(1);
//		IndexNode<Character, String> d = (IndexNode<Character, String>) a.children.get(2);
//		IndexNode<Character, String> e = (IndexNode<Character, String>) a.children.get(3);
//		System.out.println("f(a, q) = " + tree.f(a, intersectNodes));
//		System.out.println("f(b, q) = " + tree.f(b, intersectNodes));
//		System.out.println("f(c, q) = " + tree.f(c, intersectNodes));
//		System.out.println("f(d, q) = " + tree.f(d, intersectNodes));
//		System.out.println("f(e, q) = " + tree.f(e, intersectNodes));
//		System.out.println("f(f, q) = " + tree.f(f, intersectNodes));
//		System.out.println("B(a, q) = " + tree.B(a, intersectNodes));
//		System.out.println("B(b, q) = " + tree.B(b, intersectNodes));
//		System.out.println("B(c, q) = " + tree.B(c, intersectNodes));
//		System.out.println("B(d, q) = " + tree.B(d, intersectNodes));
//		System.out.println("B(e, q) = " + tree.B(e, intersectNodes));
//		System.out.println("B(f, q) = " + tree.B(f, intersectNodes));
//		System.out.println("B(g, q) = " + tree.B(g, intersectNodes));
//		System.out.println("B(h, q) = " + tree.B(h, intersectNodes));
//		System.out.println("B(i, q) = " + tree.B(i, intersectNodes));
//		System.out.println("B(j, q) = " + tree.B(j, intersectNodes));
		LeafNode<Character, String> leafNode = tree.searchNode('a');
		intersectNodes.add(leafNode);
		leafNode = tree.searchNode('c');
		intersectNodes.add(leafNode);
		leafNode = tree.searchNode('d');
		intersectNodes.add(leafNode);
		leafNode = tree.searchNode('f');
		intersectNodes.add(leafNode);
		leafNode = tree.searchNode('g');
		intersectNodes.add(leafNode);
		int memorySize = 6;
		tree.nodesInMemory.add(tree.root);
		memorySize--;
		while (memorySize > 0) {
			double max = 0;
			Node<Character, String> node = null;
			for (Node<Character, String> node2 : tree.nodesInMemory) {
				double eb = tree.ExtendBenifit(node2, intersectNodes, memorySize);
				System.out.println("extend : " + node2.keys.get(0) + " benifit : " + eb);
				if (eb > max) {
					max = eb;
					node = node2;
				}
			}
			int space = tree.extend(node);
			memorySize -= space;
			System.out.println("left memory : " + memorySize);
		}
		System.out.println("memory size : " + memorySize);
		for (Node<Character, String> node : tree.nodesInMemory) {
			System.out.print(node.toString() + " ");
		}
		double saveIO = 0;
		for (Node<Character, String> node : tree.nodesInMemory) {
			saveIO += tree.B(node, intersectNodes);
		}
		System.out.println("save I/O : " + saveIO);
		for (int i = 0; i < 9; i ++) {
			tree.nodesInMemory.clear();
			memorySize = 6;
			tree.nodesInMemory.add(tree.root);
			tree.randExtend(memorySize);
			for (Node<Character, String> node : tree.nodesInMemory) {
				System.out.print(node.toString() + " ");
			}
			saveIO = 0;
			for (Node<Character, String> node : tree.nodesInMemory) {
				saveIO += tree.B(node, intersectNodes);
			}
			System.out.println("save I/O : " + saveIO);
		}
	}
}
