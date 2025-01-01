package soncat.db.memtable

import soncat.io.config.{ConfigHandler, Config, MemtableConfig}
import scala.reflect.ClassTag


class BTreeNode[K: ClassTag : Ordering, V](val isLeaf: Boolean, val maxKeys: Int) {
	// Maximize the number of keys to 2 * maxKeys
	var keys: Array[K] = Array.ofDim[K](maxKeys)

	// Maximize the number of values to 2 * maxKeys
	var values: Array[Option[V]] = Array.fill(maxKeys)(None)

	// Maximize the number of children to 2 * maxKeys + 1
	var children: Array[BTreeNode[K, V]] = Array.ofDim[BTreeNode[K, V]](maxKeys + 1)

	// Initialize the key count to 0
	var keyCount: Int = 0
}

class TrieMemtable[K: ClassTag : Ordering, V](
	config: MemtableConfig
) {
	var MAX_KEY_SIZE = config.trie_max_key_size
	var root: BTreeNode[K, V] = new BTreeNode[K, V](isLeaf = true, MAX_KEY_SIZE)

	def put(key: K, value: V): Unit = {
		// If the root node is full, split it
		if (root.keyCount == MAX_KEY_SIZE) {
			// We create a new root node with the current root node as its child
			val newRootNode = new BTreeNode[K, V](isLeaf = false, MAX_KEY_SIZE)
			newRootNode.children(0) = root

			// Split the child node, since it is full
			splitChildNode(newRootNode, 0)

			// Update the root node to the new root node
			root = newRootNode
		}

		// Insert the key and value into the root node
		insertNonFull(root, key, value)
	}

	def getAllData(): List[(K, Option[V])] = {
		inOrderTraverseTree(root).asInstanceOf[List[(K, Option[V])]]
	}

	def size(): Int = {
		root.keyCount
	}

	def wipe(): Unit = {
		root = new BTreeNode[K, V](isLeaf = true, MAX_KEY_SIZE)
	}

	private def splitChildNode(rootNote: BTreeNode[K, V], index: Int): Unit = {
		val nodeToSplit = rootNote.children(index)
		val newNode = new BTreeNode[K, V](isLeaf = nodeToSplit.isLeaf, MAX_KEY_SIZE)

		// Copy the last half of the keys and values to the new node
		for (i <- 0 until MAX_KEY_SIZE / 2) {
			newNode.keys(i) = nodeToSplit.keys(i + MAX_KEY_SIZE / 2)
			newNode.values(i) = nodeToSplit.values(i + MAX_KEY_SIZE / 2)
		}

		// Copy the last half of the children to the new node
		if (!nodeToSplit.isLeaf) {
			for (i <- 0 to MAX_KEY_SIZE / 2) {
				newNode.children(i) = nodeToSplit.children(i + MAX_KEY_SIZE / 2)
			}
		}

		// Update the key count of the root node
		rootNote.keyCount += 1

		// Shift the children of the root node to the right
		for (i <- rootNote.keyCount until index by -1) {
			rootNote.children(i + 1) = rootNote.children(i)
		}

		// Add the new node as the child of the root node
		rootNote.children(index + 1) = newNode

		// Shift the keys of the root node to the right
		for (i <- rootNote.keyCount - 1 until index by -1) {
			rootNote.keys(i + 1) = rootNote.keys(i)
		}

		// Move the middle key of the node to the root node
		rootNote.keys(index) = nodeToSplit.keys(MAX_KEY_SIZE / 2)

		// Update the key count of the node to split
		nodeToSplit.keyCount = MAX_KEY_SIZE / 2
	}

	private def insertNonFull(node: BTreeNode[K, V], key: K, value: V): Unit = {
		// Find the index to insert the key
		var i = node.keyCount - 1

		// If the node is a leaf node
		if (node.isLeaf) {
			// Find the correct position to insert the key
			while (i >= 0 && implicitly[Ordering[K]].gt(node.keys(i), key)) {
				node.keys(i + 1) = node.keys(i)
				node.values(i + 1) = node.values(i)
				i -= 1
			}

			// Insert the key and value
			node.keys(i + 1) = key
			node.values(i + 1) = Some(value)
			node.keyCount += 1
		}

		// If the node is not a leaf node (internal node)
		else {
			// Find the correct child to insert the key
			while (i >= 0 && implicitly[Ordering[K]].gt(node.keys(i), key)) {
				i -= 1
			}

			// Increment the index
			i += 1

			// If the child node is full, split it
			if (node.children(i).keyCount == MAX_KEY_SIZE) {
				splitChildNode(node, i)

				// Update the index if the key is greater than the current key
				if (implicitly[Ordering[K]].gt(key, node.keys(i))) {
					i += 1
				}
			}

			// Insert the key and value into the child node
			insertNonFull(node.children(i), key, value)
		}
	}

	private def inOrderTraverseTree(node: BTreeNode[K, V]): List[Any] = {
		// If the node is a leaf node
		if (node.isLeaf) {
			// Return the keys and values of the leaf node (we just return a node)
			(0 until node.keyCount).map(i => (node.keys(i), node.values(i))).toList
		}

		// If the node is an internal node
		else {
			// Return the keys and values of the internal node
			(0 to node.keyCount).map { i =>
				// If the index is less than the key count (the key is not the last key)
				if (i < node.keyCount) {
					// Traverse the children of the internal node
					inOrderTraverseTree(node.children(i)) :+ (node.keys(i), node.values(i))
				}

				// If the index is equal to the key count (the key is the last key)
				else {
					inOrderTraverseTree(node.children(i))
				}
			}.toList
		}
	}
}

