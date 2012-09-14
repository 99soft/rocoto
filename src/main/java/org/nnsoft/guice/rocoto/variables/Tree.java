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
public class Tree<T>
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
		if ( isRoot() )
		{
			return 0;
		}
		return 1 + getParent().getDepth();
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
	 * @return true if element is the tree node data or is contained in its
	 *         subtrees.
	 */
	public boolean inSubtrees( T element )
	{
		if ( this.data.equals(element) )
		{
			return true;
		}
		for ( Tree<T> child : getChildren() )
		{
			if ( child.inSubtrees(element) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param element
	 * @return true if element is the tree node data or is in the ancestors
	 */
	public boolean inAncestors( T element )
	{
		if ( this.data.equals(element) )
		{
			return true;
		}
		if ( !isRoot() )
		{
			return getParent().inAncestors(element);
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
		for ( int i = 0; i < level; i++ )
		{
			buffer.append("  ").append(" ");
		}

		if ( !isRoot() )
		{
			buffer.append("|_ ");
		}
		// Print data
		buffer.append(getData()).append("\n");

		// Print subtrees
		level++;
		for ( Tree<T> child : getChildren() )
		{
			child.toString(buffer, level);
		}
	}
}