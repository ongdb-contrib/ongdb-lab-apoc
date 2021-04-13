package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

// 线性表的链式存储和实现
//  排序单链表

//排序单链表类（升序），T或T的某个祖先类实现Comparable<T>接口；继承单链表类
public class SortedSinglyList<T extends Comparable<? super T>> extends SinglyList<T> {
    public SortedSinglyList()                              //构造空排序单链表
    {
        super();                                           //默认调用父类构造方法SinglyList()
    }

    public SortedSinglyList(T[] values)                    //构造，按值插入values数组元素。直接插入排序算法
    {
        super();                                           //构造空单链表，默认调用SinglyList()
        //直接插入排序，每趟插入1个元素
        for (T value : values) {
            this.insert(value);                        //排序单链表按值插入
        }
    }

    public SortedSinglyList(SortedSinglyList<T> list)      //深拷贝构造方法
    {
        super(list);                                       //调用父类同参数的构造方法，不可省略
    }

    //由单链表list构造（深拷贝），重载构造方法。直接插入排序
    public SortedSinglyList(SinglyList<T> list) {
        super();                                           //构造空单链表
        for (Node<T> p = list.head.next; p != null; p = p.next)//直接插入排序，每趟插入1个元素
        {
            this.insert(p.data);                           //排序单链表按值插入
        }
    }

    //不支持父类的以下两个方法，将其覆盖并抛出异常。方法体同排序顺序表
    @Override
    public void set(int i, T x) {
        throw new UnsupportedOperationException("set(int i, T x)");
    }

    @Override
    public Node<T> insert(int i, T x) {
        throw new UnsupportedOperationException("insert(int i, T x)");
    }

    //插入x，x!=null，根据x对象大小顺序查找确定插入位置，插入在等值结点之前。
    //返回插入结点。O(n)。覆盖父类的insert(x)方法
    @Override
    public Node<T> insert(T x) {
        Node<T> front = this.head, p = front.next;             //front指向p的前驱结点
        while (p != null && x.compareTo(p.data) > 0)           //寻找插入位置
        {
            front = p;
            p = p.next;
        }
        front.next = new Node<T>(x, p);                    //在front之后、p之前插入值为x结点
        return front.next;                                 //返回插入结点
    }

    //顺序查找首个与key相等元素，返回结点，若查找不成功返回null，O(n)。覆盖。    //用于5.2.2节稀疏矩阵行的单链表
    @Override
    public Node<T> search(T key) {
        for (Node<T> p = this.head.next; p != null && key.compareTo(p.data) >= 0; p = p.next) {
//            System.out.print(p.data+"？");
            if (key.compareTo(p.data) == 0)                  //由compareTo()提供比较对象大小和相等的依据
            {
                return p;
            }
        }
//        System.out.println();
        return null;
    }

    //插入不重复元素x，返回x结点；覆盖。//5.2.2节矩阵行的单链表可用，第4版教材没有写
    //声明子类实现，互异排序单链表类
    @Override
    public Node<T> insertDifferent(T x) {
        Node<T> front = this.head, p = front.next;             //front指向p的前驱结点
        while (p != null && x.compareTo(p.data) > 0)           //寻找插入位置
        {
            front = p;
            p = p.next;
        }
        if (p != null && x.compareTo(p.data) == 0) {
            return p;                                      //查找成功，元素重复，不插入，返回该结点
        }
        return front.next = new Node<T>(x, p);             //在front之后、p之前插入值为x结点，返回插入结点
    }

    //删除首个与key相等元素结点，返回被删除元素，查找不成功返回null。O(n)。覆盖
    @Override
    public T remove(T key) {
        Node<T> front = this.head, p = front.next;             //front是p的前驱结点
        while (p != null && key.compareTo(p.data) > 0)         //查找与key相等元素结点（p指向）
        {
            front = p;
            p = p.next;
        }
        if (p != null && key.compareTo(p.data) == 0)           //若查找成功，删除p结点，返回被删除元素
        {
            front.next = p.next;                           //删除p结点
            return p.data;
        }
        return null;
    }

    //单链表对子表的操作。
    //单链表作为方法参数与返回值问题讨论。 未写
    @Override
    public void concat(SinglyList<T> list)                 //覆盖，不支持直接合并连接，抛出异常
    {
        throw new UnsupportedOperationException("concat(SinglyList<T> list)");
    }

    //复制list所有元素按值插入到this排序单链表，不改变list，集合并。
    //覆盖，功能与父类相同，算法实现不同，每次查找全部元素后再插入，O(n*n)
    @Override
    public void addAll(SinglyList<T> list) {
        for (Node<T> p = list.head.next; p != null; p = p.next)//直接插入排序，每趟插入1个元素
            this.insert(p.data);                           //排序单链表按值插入
    }

    //返回复制this和list所有结点的排序单链表，并集，不改变this和list。
    //覆盖，返回子类实例。覆盖方法的返回值类型必须与父类方法赋值相容
    @Override
    public SortedSinglyList<T> union(SinglyList<T> list) {
        SortedSinglyList<T> result = new SortedSinglyList<T>(this);   //排序单链表深拷贝
        result.addAll(list);
        return result;
    }
//  public void union(SinglyList<T> list)                //编译错，覆盖方法的返回类型必须与父类方法相同

    //单链表的排序算法
    // 载深拷贝构造方法，由单链表list构造排序单链表。
    //直接选择排序算法，每趟交换结点元素值，不删除和插入结点
/*    public SortedSinglyList(SinglyList<T> list)
    {
        super(list);                                       //this深拷贝list单链表
        for (Node<T> first=this.head.next;  first.next!=null;  first=first.next)
        {                                                  //first指向待排序单链表首个结点  
            Node<T> min=first;                             //min指向最小值结点
            for (Node<T> p=min.next;  p!=null;  p=p.next)  //遍历单链表，寻找最小值结点
                if (p.data.compareTo(min.data)<0)          //比较大小
                    min = p;                               //min记住最小值结点
            if (min!=first)                                //交换min元素到前面first结点
            {
                T temp = min.data;
                min.data = first.data;
                first.data = temp;
            }
//            System.out.println(this.toString());
        }
    }*/

    //9.5.2算法描述、程序
    public void merge(SortedSinglyList<T> list)            //将list所有结点归并到this中，归并后设置list为空
    {
        Node<T> front = this.head, p = front.next;             //p遍历this单链表，front是p的前驱
        Node<T> q = list.head.next;                          //q遍历list单链表
        while (p != null && q != null)                         //遍历两条排序单链表
        {
            if ((p.data).compareTo(q.data) < 0)              //若p结点值小，则p继续前进
            {
                front = p;
                p = p.next;
            } else {                                              //否则，将q结点插入到front结点之后
                front.next = q;
                q = q.next;
                front = front.next;
                front.next = p;
            }
        }
        if (q != null)                                       //将list中剩余结点并入this尾
            front.next = q;
        list.head.next = null;                               //设置list为空单链表
    }

    //返回this和list归并后的排序单链表，不改变this。设置list为空。二次遍历，效率低
/*    public SortedSinglyList<T> mergeWith(SortedSinglyList<T> list)  
    {
        SortedSinglyList<T> slist = new SortedSinglyList<T>(this);   //单链表深拷贝this，O(n)
        slist.merge(new SortedSinglyList<T>(list));                  //slist归并list，设置list为空。O(n)
        return slist;
    }*/

    //返回this和list归并后的排序单链表，不改变this和list。效率高，简化
    public SortedSinglyList<T> mergeWith(SortedSinglyList<T> list) {
        Node<T> p = this.head.next, q = list.head.next;        //p、q分别遍历this、list单链表
        SortedSinglyList<T> result = new SortedSinglyList<T>();
        Node<T> rear = result.head;                        //rear指向result单链表尾，准备插入
        while (p != null || q != null)                         //p、q分别遍历this、list单链表
        {
            if (p != null && (q != null && (p.data).compareTo(q.data) <= 0 || q == null)) {                                              //复制this结点到result，若p结点值小，或q已结束
                rear.next = new Node<T>(p.data, null);
                rear = rear.next;
                p = p.next;
            } else if (q != null && (p != null && (p.data).compareTo(q.data) > 0 || p == null)) {                                              //否则，复制list结点到result，若q结点值小，或p已结束
                rear.next = new Node<T>(q.data, null);
                rear = rear.next;
                q = q.next;
            }
        }
        return result;
    }
}

//返回this和list归并（升序）后的排序单链表，不改变this和list，一次归并算法。正确，第3版程序
/*    public SortedSinglyList<T> mergeWith(SortedSinglyList<T> list)  
    {
        SortedSinglyList<T> result = new SortedSinglyList<T>();
        Node<T> p=this.head.next;                //p遍历this单链表
        Node<T> q=list.head.next;                //q遍历list单链表
        Node<T> rear=result.head;                //rear指向result单链表尾，准备插入        
        while (p!=null && q!=null)               //遍历两条排序单链表
            if ((p.data).compareTo(q.data)<0)    //若p结点值小，则复制p结点，p继续前进
            {
                rear.next = new Node<T>(p.data,null);
                rear = rear.next;
                p = p.next;
            }
            else
            {                                    //否则，复制q结点，q继续前进
                rear.next = new Node<T>(q.data,null);
                rear = rear.next;
                q = q.next;
            }
        while (p!=null)                          //将this中剩余结点并入result尾
        {
            rear.next = new Node<T>(p.data,null);
            rear = rear.next;
            p = p.next;
        }
        while (q!=null)                          //将list中剩余结点并入result尾
        {
            rear.next = new Node<T>(q.data,null);
            rear = rear.next;
            q = q.next;
        }
        return result;
    }*/

//深拷贝构造方法，由单链表list构造排序单链表，直接选择排序
/*    //第3版程序，每趟寻找最小值结点，交换到前面，通过删除、插入结点方式实现交换
    public SortedSinglyLinkedList(SinglyLinkedList<T> list)
    {
        super(list);                                       //深拷贝list单链表
        Node<T> srear=head;                                //指向排序单链表尾
        while (srear.next!=null)                           //原单链表不空
        {
            Node<T> mfront=srear, min=mfront.next;         //min指向最小值结点，mfront指向min的前驱
            Node<T> pfront=min, p=min.next;                //p遍历单链表，pfront指向p的前驱结点
            while (p!=null)
            {
                if (p.data.compareTo(min.data)<0)          //比较，min记住最小值结点
                {   mfront = pfront;                       //mfront是min的前驱结点
                    min = p;
                }
                pfront = p;                                //pfront是p的前驱结点
                p = p.next;
            }  
            if (mfront!=srear)
            {
                mfront.next = min.next;                    //从链表原位置删除min结点
                min.next=srear.next;                       //将min结点插入srear之后
                srear.next = min;
            }
            srear = min;                                   //srear指向排序单链表尾 
        }
    }

*/
