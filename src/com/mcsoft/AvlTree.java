package com.mcsoft;

import java.util.Comparator;

/**
 * 二叉平衡树AVL树实现
 * Created by Mc on 2017/4/11.
 */
public class AvlTree<T> {
    private final static int ALLOWED_IMBALENCE = 1;//允许节点不平衡的最大差

    private AvlNode<T> root;
    private Comparator<T> myComparator;

    AvlTree(Comparator<T> comparator) {
        this.myComparator = comparator;
    }

    AvlTree() {
        this.root = null;
    }

    public AvlNode<T> insert(T x) {
        return this.insert(x, this.root);
    }

    public AvlNode<T> remove(T x) {
        return this.remove(x, this.root);
    }

    public AvlNode<T> findMin() {
        return this.findMin(root);
    }

    public AvlNode<T> findMax() {
        return this.findMax(root);
    }

    public void printTree(){
        if(isEmpty()) System.out.println("Empty tree.");
        else printTree(root);
    }

    public boolean isEmpty(){
        return null == root;
    }

    private int myCompare(T lt, T rt) {
        if (null != myComparator) {
            return myComparator.compare(lt, rt);
        } else {
            return ((Comparable) lt).compareTo(rt);
        }
    }

    //返回节点高度
    private int height(AvlNode<T> t) {
        return t == null ? -1 : t.height;
    }

    //查找
    private boolean contains(T x, AvlNode<T> t) {
        if (null == t) return false;//没有找到

        int compareResult = myCompare(x, t.theElement);

        if (0 > compareResult) return contains(x, t.left);
        else if (0 < compareResult) return contains(x, t.right);
        else return true;
    }

    //插入，在返回节点前将节点进行平衡
    private AvlNode<T> insert(T x, AvlNode<T> t) {
        if (t == null) {
            return new AvlNode<T>(x, null, null);
        }

        int compareResult = myCompare(x, t.theElement);

        if (0 > compareResult) {
            t.left = insert(x, t.left);
        } else if (0 < compareResult) {
            t.right = insert(x, t.right);
        }

        return balance(t);
    }

    //删除，平衡节点
    private AvlNode<T> remove(T x, AvlNode<T> t) {
        if (null == t) return t;//未找到元素

        int compareResult = myCompare(x, t.theElement);

        if (0 > compareResult) {
            //大于小于的情形，则继续向左或右子节点进行查找删除
            t = remove(x, t.left);
        } else if (0 < compareResult) {
            t = remove(x, t.right);
        } else if (null != t.left && null != t.right) {
            //当找到数据，且该节点左右子节点都不为空时
            t.theElement = findMin(t.right).theElement;
            t.right = remove(t.theElement, t.right);
        } else {
            //当找到数据，且该节点只有一个子节点时
            t = null == t.left ? t.right : t.left;
        }

        return balance(t);
    }

    private AvlNode<T> findMin(AvlNode<T> t) {
        if (null == t) return null;
        else if (null == t.left) return t;
        else return findMin(t.left);
    }

    private AvlNode<T> findMax(AvlNode<T> t) {
        if (null == t) return null;
        if (null == t.right) return t;
        else return findMax(t.right);
    }

    //平衡节点并更新节点高度，当节点是平衡的，高度会被更新
    //在递归的删除或插入时，被更新的节点会继续作为他父节点判断高度的依据
    private AvlNode<T> balance(AvlNode<T> t) {
        if (null == t) {
            return t;
        }

        if (height(t.left) - height(t.right) > ALLOWED_IMBALENCE) {
            //当左子节点高度减右子节点高度大于允许高度差时，说明左子树不平衡，下面的操作针对左子树进行
            if (height(t.left.left) >= height(t.left.right)) {
                //当左子节点的左子节点高度大于等于它的右子节点高度时，说明不平衡是发生在外侧的
                //(即引起不平衡的节点是插入到左子节点的左子树下)
                //(或删除导致不平衡时，可能出现高度相等的情形，这时两种旋转都可以，因此用单旋转而不是双旋转)
                //使用(左子节点为轴向右)单旋转进行平衡
                t = rotateWithLeftChild(t);
            } else {
                //当左子节点的右子节点高度大于它的左子节点高度时，说明不平衡是发生在内侧的
                //(即引起不平衡的节点是插入到左子节点的右子树下)
                //使用先左后右双旋转进行平衡
                t = doubleWithLeftChild(t);
            }
        } else if (height(t.right) - height(t.left) > ALLOWED_IMBALENCE) {
            //当右子节点高度减左子节点高度大于允许高度差时，说明不平衡发生在右子树，下面的操作针对右子树
            if (height(t.right.right) >= height(t.right.left)) {
                //当右子节点的右子节点高度大于等于它的左子节点高度时，说明不平衡是发生在外侧的
                //(即引起不平衡的节点是插入到右子节点的右子树下)
                //(或删除导致不平衡时，可能出现高度相等的情形，这时两种旋转都可以，因此用单旋转而不是双旋转)
                //使用(右子节点为轴向左)单旋转进行平衡
                t = rotateWithRightChild(t);
            } else {
                //当右子节点的左子节点高度大于它的右子节点高度时，说明不平衡是发生在内侧的
                //(即引起不平衡的节点是插入到右子节点的左子树下)
                //使用先右后左双旋转进行平衡
                t = doubleWithRightChild(t);
            }
        }

        //计算节点高度，用左右子节点的最高高度加一即是该节点的高度
        t.height = Math.max(height(t.left), height(t.right)) + 1;
        return t;
    }

    //左侧不平衡时，节点(以左子节点为轴)向右单旋转
    private AvlNode<T> rotateWithLeftChild(AvlNode<T> k2) {
        //将k2左子节点k1提到根部
        AvlNode<T> k1 = k2.left;
        //左子节点k1的右子节点转移到k2左子节点
        k2.left = k1.right;
        //k2转移到左子节点k1的右子节点
        k1.right = k2;
        k1.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k2.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    //右侧不平衡时，节点(以右子节点为轴)向左单旋转
    private AvlNode<T> rotateWithRightChild(AvlNode<T> k2) {
        AvlNode<T> k1 = k2.right;
        k2.right = k1.left;
        k1.left = k2;
        k1.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k2.height = Math.max(height(k1.right), k2.height) + 1;
        return k1;
    }

    //左侧不平衡时，节点双旋转，一次左子节点(以它的右子节点为轴)向左旋转，一次本身(以左子节点为轴)向右旋转
    private AvlNode<T> doubleWithLeftChild(AvlNode<T> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    //右侧不平衡时，节点双旋转，一次右子节点(以它的左子节点为轴)向右旋转，一次本身(以右子节点为轴)向左旋转
    private AvlNode<T> doubleWithRightChild(AvlNode<T> k3) {
        k3.right = rotateWithLeftChild(k3.right);
        return rotateWithRightChild(k3);
    }

    private void printTree(AvlNode<T> t){
        if(null != t){
            //按左、中、右顺序输出，这样最后输出的树就是由小到大排序的了
            printTree(t.left);
            System.out.println(t.theElement);
            printTree(t.right);
        }
    }

    private static class AvlNode<T> {
        AvlNode(T theElement) {
            this(theElement, null, null);
        }

        AvlNode(T theElement, AvlNode<T> lt, AvlNode<T> rt) {
            this.theElement = theElement;
            this.left = lt;
            this.right = rt;
            this.height = 0;
        }

        T theElement;
        AvlNode<T> left;
        AvlNode<T> right;
        int height;
    }
}
