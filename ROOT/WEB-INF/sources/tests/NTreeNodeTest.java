package tests;

//---------------- JUnit 4 testing imports -------------------------//
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.core.CombinableMatcher;
import org.junit.Test;
//-------------------------------------------------------

import java.sql.*;
import java.util.*;
import java.util.function.*;

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
	public void testAddAndRemoveChild()
	{
        NTreeNode<Integer> node = new NTreeNode<Integer>(9);
        NTreeNode<Integer> nodeOne = new NTreeNode<Integer>(11);
        NTreeNode<Integer> nodeTwo = new NTreeNode<Integer>(12);
        NTreeNode<Integer> nodeThree = new NTreeNode<Integer>(13);
        node.addChild(nodeOne);
        node.addChild(nodeTwo);
        node.addChild(nodeThree);
        assertEquals("addChild not adding properly. Incorrect number of children.",new Integer(3),new Integer(node.children.size()));
        assertEquals("Data not initialized properly in addChild on 11",new Integer(11),node.children.get(0).data);
        assertEquals("Data not initialized properly in addChild on 12",new Integer(12),node.children.get(1).data);
        assertEquals("Data not initialized properly in addChild on 13",new Integer(13),node.children.get(2).data);
        assertTrue("Failure removing nodeOne",node.removeChild(nodeOne));
        assertTrue("Failure removing nodeTwo",node.removeChild(nodeTwo));
        assertTrue("Failure removing nodeThree",node.removeChild(nodeThree));
    }

    private class StringConsumer<T> implements Consumer<T> {
        public StringConsumer () {
            result = "";
        }
        public void accept(T t) {
            result+=t;
            result+=",";
        }
        String result;
    }

	@Test
	public void testConsumePreOrder()
	{
        NTreeNode<Integer> node = new NTreeNode<Integer>(0);
        for (int i=0;i<10;++i) {
            node.addChild(new NTreeNode<Integer>(i*10+1));
        }
        for (int i=0;i<10;++i) {
            for (int j=1;j<10;++j) {
                node.children.get(i).addChild(new NTreeNode<Integer>(i*10+1+j));
            }
        }
        StringConsumer<Integer> result = new StringConsumer<Integer>();
        String expected = "";
        for (int i=0;i<101;++i) {
            expected+=i;
            expected+=",";
        }
        node.consumePreOrder(result);
        assertEquals("Consumption failure",expected,result.result);
    }

    @Test
	public void testEquals()
	{
        NTreeNode<Integer> node = new NTreeNode<Integer>(0);
        NTreeNode<Integer> other = new NTreeNode<Integer>(0);
        assertTrue("NTreeNode equals method not equal.",node.equals(other));
    }
}
