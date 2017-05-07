package gamesite.datastruct;

import java.util.function;

public class NTreeNode<T> () {
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
    public void applyPreOrder(Consumer<T> func) {
        func.accept(data);
        for (NTreeNode<T> child : children) {
            child.applyPreOrder(func);
        }
    }
    public T data;
    public ArrayList<NTreeNode<T>> children;
}
