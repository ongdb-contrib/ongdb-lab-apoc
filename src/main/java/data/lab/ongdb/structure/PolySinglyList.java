package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

//  线性表的应用：多项式的表示及运算
//  一元多项式的表示及运算

//多项式排序单链表类，继承排序单链表类，提供排序单链表的多项式相加运算；
//T或T的某个祖先类必须实现Comparable<T>接口，约定对象比较大小的规则；T实现Addible<T>可相加接口
public class PolySinglyList<T extends Comparable<? super T> & Addible<T>>
        extends SortedSinglyList<T> {
    public PolySinglyList()                                //构造方法
    {
        super();                                           //创建空单链表
    }

    public PolySinglyList(T terms[])                       //构造方法，由项数组指定多项式各项值
    {
        super(terms);
    }

    public PolySinglyList(PolySinglyList<T> list)          //拷贝构造方法
    {
        super(list);                                       //单链表深拷贝，复制所有结点，没有复制对象
    }

    public void addAll(PolySinglyList<T> list)             //多项式相加，this＋=list功能，不改变list
    {
        Node<T> front = this.head, p = front.next;
        Node<T> q = list.head.next;
        while (p != null && q != null) {
            if (p.data.compareTo(q.data) == 0)               //两项大小相同
            {
                p.data.add(q.data);                        //两项相加，add()方法由Addible接口约定
                if (p.data.removable())                    //相加后元素满足删除条件
                {                                          //removable()方法由Addible接口约定
                    front.next = p.next;                     //相加后元素不需要存储，删除p结点
                    p = front.next;
                } else {
                    front = p;                             //front是p的前驱结点
                    p = p.next;
                }
                q = q.next;
            } else if (p.data.compareTo(q.data) < 0) {
                front = p;
                p = p.next;
            } else {
                front.next = new Node<T>(q.data, p);  //复制q结点并插入到front结点之后
                q = q.next;
            }
        }
        while (q != null)                                    //将list单链表中剩余结点复制并插入到当前链表尾
        {
            front.next = new Node<T>(q.data, null);
            front = front.next;
            q = q.next;
        }
    }
}
/* 
public void insert(Term term)                          //插入项
{
    list.insert(term);                                 //在排序单链表中插入结点，插入位置由term项指数决定
}*/
/*
//（2） 多项式相加算法
public Polynomial (Polynomial pol)                 //加法＋，C=this＋pol
{
    Polynomial cpol = new Polynomial();
    Node<Term> p=this.list.head.next;            
    Node<Term> q=pol.list.head.next;
    Node<Term> rear=cpol.list.head;
    while (p!=null && q!=null)
    {
        if (p.data.compareTo(q.data)==0)               //两项指数相同时
        {
            double sum=p.data.coef + q.data.coef;      //两项系数相加
            if (Math.abs(sum)>0.00001)                 //浮点数是否为0由精度确定
            {
                rear.next=new Node<Term>(new Term(sum, p.data.exp), null);//创建并链接非0系数结点
                rear=rear.next;
            }
            p = p.next;
            q = q.next;
        }
        else if (p.data.compareTo(q.data)<0)
        {
            rear.next = new Node<Term>(p.data, null);  //复制p结点并插入到rear结点之后
            rear=rear.next;
            p = p.next;
        }
        else
        {
            rear.next = new Node<Term>(q.data, null);  //复制q结点并插入到rear结点之后
            rear=rear.next;
            q = q.next;
        }
    }
    if (p==null)
        p=q;
    while (p!=null)                                    //将当前链表或pol链表中剩余结点复制并插入到cpol链表尾
    {
        rear.next = new Node<Term>(p.data, null);
        rear=rear.next;
        p = p.next;
    }
    return cpol;  
}
    public PolySLinkedList<T> plus(PolySLinkedList<T> list)    //加法＋，C=this＋list
    {
    	PolySLinkedList<T> polyc=new PolySLinkedList<T>(this);   //深拷贝
        polyc.add(list);
        return polyc;                                      //返回对象引用
    }*/
