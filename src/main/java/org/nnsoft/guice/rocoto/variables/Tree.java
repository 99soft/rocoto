/*
 *    Copyright 2009-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.nnsoft.guice.rocoto.variables;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation of a tree.
 * 
 * @param <T>
 */
class Tree<T>
{
	/** Current tree node data */
	private T data;

	/** Parent node */
	private Tree<T> parent = null;

	/** Children */
	private List<Tree<T>> children = new ArrayList<Tree<T>>();

	/**
	 * Default constructor
	 * 
	 * @param data
	 */
	public Tree( T data )
	{
		this.data = data;
	}

	/**
	 * @return True if parent is not null
	 */
	public boolean isRoot()
	{
		return this.parent == null;
	}

	/**
	 * @return True if no children
	 */
	public boolean isLeaf()
	{
		return this.children.isEmpty();
	}

	/**
	 * Add a new leaf to this tree
	 * 
	 * @param child
	 * @return Tree node of the newly added leaf
	 */
	public Tree<T> addLeaf( T child )
	{
		Tree<T> leaf = new Tree<T>(child);
		leaf.parent = this;
		children.add(leaf);
		return leaf;
	}

	/**
	 * @return Parent node, or null if this node is root
	 */
	public Tree<T> getParent()
	{
		return this.parent;
	}

	/**
	 * Remove this tree from its parent, if any.
	 */
	public void removeFromParent()
	{
		if ( !isRoot() )
		{
			getParent().removeSubtree(this);
		}
	}

	/**
	 * Remove given subtree
	 * 
	 * @param subtree
	 */
	public void removeSubtree( Tree<T> subtree )
	{
		if ( this.children.remove(subtree) )
		{
			subtree.parent = null;
		}
	}

	/**
	 * @return Node data
	 */
	public T getData()
	{
		return this.data;
	}

	/**
	 * 
	 * @return Node depth
	 */
	public int getDepth()
	{
		int depth = 0;
		Tree<T> curr = this.parent;
		while (curr != null)
		{
			curr = curr.parent;
			depth++;
		}
		return depth;
	}

	/**
	 * @return Subtrees
	 */
	public List<Tree<T>> getChildren()
	{
		return this.children;
	}

	/**
	 * Add a subtree, provided tree is not modified.
	 * 
	 * @param subtree
	 * @return subtree copy added
	 */
	public Tree<T> addSubtree( Tree<T> subtree )
	{
		Tree<T> copy = addLeaf(subtree.data);
		copy.children = new ArrayList<Tree<T>>(subtree.children);
		return copy;
	}

	/**
	 * @param element
	 * @return true if element is contained in this node subtrees.
	 */
	public boolean inSubtrees( T element )
	{
		for ( Tree<T> child : getChildren() )
		{
			if ( child.isElement(element) || child.inSubtrees(element) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param element
	 * @return True if element is equal to this node data.
	 */
	public boolean isElement( T element )
	{
		return (this.data.equals(element));
	}

	/**
	 * 
	 * @param element
	 * @return true if element is is in the ancestors of this node
	 */
	public boolean inAncestors( T element )
	{
		if ( !isRoot() )
		{
			return getParent().isElement(element) || getParent().inAncestors(element);
		}
		return false;
	}

	/**
	 * @return Root node, this if current node is root
	 */
	public Tree<T> getRoot()
	{
		if ( isRoot() )
		{
			return this;
		}
		return getParent().getRoot();
	}

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		toString(buffer, 0);
		return buffer.toString();
	}

	private void toString( StringBuilder buffer, int level )
	{
		// Create proper indent
		// Search for next cousins in each level
		StringBuilder indent = new StringBuilder();
		Tree<T> prev;
		Tree<T> curr = this;
		for ( int i = level - 1; i >= 0; i-- )
		{
			prev = curr;
			curr = prev.parent;
			if ( i == level - 1 )
			{
				indent.append(" _|");
			} else
			{
				indent.append("  ");

				if ( i < level && curr.children.indexOf(prev) < curr.children.size() - 1 )
				{
					indent.append("|");
				} else
				{
					indent.append(" ");
				}
			}
		}
		buffer.append(indent.reverse());

		// Print data
		buffer.append(getData()).append("\n");

		// Print subtrees
		for ( Tree<T> child : getChildren() )
		{
			child.toString(buffer, level+1);
		}
	}
}