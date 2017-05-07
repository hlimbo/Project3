package tests;

//---------------- JUnit 4 testing imports -------------------------//
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;
//-------------------------------------------------------

import java.sql.*;
import java.util.*;

import gamesite.datastruct.NTreeNode;

public class NTreeNodeTest 
{
	@Test
	public void testConstructor()
	{
        NTreeNode<Integer> node = new NTreeNode<Integer>(5);
        assertNotNull("Node should not be null upon construction", node);
        assertEquals("Data not initialized properly in constructor",new Integer(5),node.data);
    }

	@Test
	public void testAddChild()
	{
        NTreeNode<Integer> node = new NTreeNode<Integer>(9);
        node.addChild(new NTreeNode<Integer>(11));
        node.addChild(new NTreeNode<Integer>(12));
        node.addChild(new NTreeNode<Integer>(13));
        assertEquals("addChild not adding properly. Incorrect number of children.",new Integer(3),new Integer(node.children.size()));
        assertEquals("Data not initialized properly in addChild on 11",new Integer(11),node.children.get(0).data);
        assertEquals("Data not initialized properly in addChild on 12",new Integer(12),node.children.get(1).data);
        assertEquals("Data not initialized properly in addChild on 13",new Integer(13),node.children.get(2).data);
    }
}
