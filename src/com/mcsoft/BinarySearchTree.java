package com.mcsoft;

import java.util.Comparator;

/**
 * 二叉查找树的实现
 * Created by Mc on 2017/4/4.
 */
public class BinarySearchTree<T> {

    //树节点
    private static class BinaryNode<T> {
        public BinaryNode(T theElement) {
            this(theElement, null, null);
        }

        public BinaryNode(T theElement, BinaryNode<T> left, BinaryNode<T> right) {
            this.theElement = theElement;
            this.left = left;
            this.right = right;
        }

        T theElement;
        BinaryNode<T> left;
        BinaryNode<T> right;
    }

    private BinaryNode<T> root;
    private Comparator cmp;

    public BinarySearchTree() {
        this.root = null;
    }

    public BinarySearchTree(Comparator cmp) {
        this.cmp = cmp;
    }

    //清空树
    public void makeEmpty() {
        this.root = null;
    }

    //判断树为空
    public boolean isEmpty() {
        return root == null;
    }

    //查找
    public boolean contains(T x) {
        return contains(x, root);
    }

    //查找最小
    public T findMin() {
        if (isEmpty()) return null;
        return findMin(root).theElement;
    }

    //查找最大
    public T findMax() {
        if (isEmpty()) return null;
        return findMax(root).theElement;
    }

    //插入
    public void insert(T x) {
        root = insert(x, root);
    }

    //移除
    public void remove(T x) {
        root = remove(x, root);
    }

    //打印树
    public void printTree() {
        if(isEmpty()) System.out.println("Empty tree.");
        else printTree(root);
    }

    private int myCompare(T lhs, T rhs) {
        if (null != cmp) return cmp.compare(lhs, rhs);
        else return ((Comparable) lhs).compareTo(rhs);

    }

    private boolean contains(T x, BinaryNode<T> t) {
        if (t == null) {
            return false;
        }
        int compareResult = myCompare(x, t.theElement);
        if (0 > compareResult) {
            return contains(x, t.left);
        } else if (0 < compareResult) {
            return contains(x, t.right);
        } else {
            return true;
        }
    }

    private BinaryNode<T> findMin(BinaryNode<T> t) {
        if (t == null) return null;
        else if (t.left == null) return t;
        else return findMin(t.left);
    }

    private BinaryNode<T> findMax(BinaryNode<T> t) {
        if (t != null) {
            while (t.right != null) t = t.right;
        }
        return t;
    }

    private BinaryNode<T> insert(T x, BinaryNode<T> t) {
        if (null == t) return new BinaryNode<T>(x);
        int compareResult = myCompare(x, t.theElement);
        if (0 > compareResult) t.left = insert(x, t.left);
        else if (0 < compareResult) t.right = insert(x, t.right);
        return t;
    }

    private BinaryNode<T> remove(T x, BinaryNode<T> t) {
        //没有找到节点，则什么也不做
        if (t == null) return t;

        int compareResult = myCompare(x, t.theElement);

        //前两个判断根据大小决定继续向左子树或右子树进行遍历推进
        if (0 > compareResult) {
            t.left = remove(x, t.left);
        } else if (0 < compareResult) {
            t.right = remove(x, t.right);
        } else if (t.left != null && t.right != null) {
            //当找到节点，且节点左右子节点都不为空时
            //这种情况需要处理子树重新连接的问题
            //这里的解决办法是：将节点值设置为右子树中最小节点的值，并移除该最小节点
            //因为最小节点不会有左子节点，因此移除该节点将非常简单
            t.theElement = findMin(t.right).theElement;
            //将节点右子树设为移除该最小节点后的右子树
            t.right = remove(t.theElement, t.right);
            //附：这样的树在大量随机操作后，会出现树重心严重左偏移
            //    因为每次移除操作都是用右子树最小节点来替代被移除的节点
            //    因此可以用每次移除时随机选取左子树最大值或右子树最小值代替被移除节点的办法保证树平衡
        } else {
            //当找到节点，且节点左右子节点至少有一个为空时，则用子节点代替该节点
            t = t.left != null ? t.left : t.right;
        }
        return t;
    }

    private void printTree(BinaryNode<T> t) {
        if(null != t){
            //按左、中、右顺序输出，这样最后输出的树就是由小到大排序的了
            printTree(t.left);
            System.out.println(t.theElement);
            printTree(t.right);
        }
    }

}

