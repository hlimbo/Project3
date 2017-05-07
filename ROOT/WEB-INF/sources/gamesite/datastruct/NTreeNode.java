package gamesite.datastruct;

import java.util.function.*;
import java.util.*;

public class NTreeNode<T> {

    public NTreeNode () {
        children = new ArrayList<NTreeNode<T>>();
    }

    public NTreeNode (T newData) {
        children = new ArrayList<NTreeNode<T>>();
        data = newData;
    }
    
    public void addChild (NTreeNode<T> node) {
        children.add(node);
    }

    public boolean removeChild (NTreeNode<T> node) {
        return children.remove(node);
    }

    public void consumePreOrder(Consumer<T> func) {
        func.accept(data);
        for (NTreeNode<T> child : children) {
            child.consumePreOrder(func);
        }
    }

    public <V> NTreeNode<V> applyPreOrder (Function<T,V> func) {
        NTreeNode<V> root = new NTreeNode<V>(func.apply(data));
        for (NTreeNode<T> child : children) {
            root.addChild(child.applyPreOrder(func));
        }
        return root;
    }

    public T data;
    public ArrayList<NTreeNode<T>> children;
}
