package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

// 线性表的链式存储和实现
// 带头结点的单链表类
//  提供迭代器的类

//单链表类，实现ADT List<T>声明的方法，T表示数据元素的数据类型
public class SinglyList<T> extends Object implements Iterable<T>  //
//public class SinglyList<T> extends MyAbstractList<T>       //单链表类，继承抽象列表类//
{
    public Node<T> head;                                   //头指针，指向单链表的头结点

    //（1）构造方法
    public SinglyList()                                    //构造方法，构造空单链表
    {
        this.head = new Node<T>();                         //创建头结点，data和next值均为null
    }

    //构造单链表，由values数组提供元素，忽略其中空对象。采用尾插入，单链表元素次序与数组元素次序相同
    public SinglyList(T[] values) {
        this();                                            //创建空单链表，只有头结点
        Node<T> rear = this.head;                            //rear指向单链表最后一个结点
        //若values.length==0，构造空链表
        for (T value : values) {
            if (value != null) {
                rear.next = new Node<T>(value, null);     //尾插入，创建结点链入rear结点之后
                rear = rear.next;                          //rear指向新的链尾结点
            }
        }
    }

    public boolean isEmpty()                               //判断单链表是否空，O(1)
    {
        return this.head.next == null;
    }

    //（2）存取
    public T get(int i)                                    //返回第i个元素，0≤i<表长度。若i越界，返回null。O(n)
    {
        Node<T> p = this.head.next;
        for (int j = 0; p != null && j < i; j++)                 //遍历单链表，寻找第i个结点（p指向）
        {
            p = p.next;
        }
        return (i >= 0 && p != null) ? p.data : null;          //若p指向第i个结点，返回其元素值
    }

    //设置第i个元素为x，0≤i<n。若i越界，抛出序号越界异常；若x==null，抛出空对象异常。O(n)
    public void set(int i, T x) {
        if (x == null) {
            throw new NullPointerException("x==null");     //抛出空对象异常
        }
        Node<T> p = this.head.next;
        for (int j = 0; p != null && j < i; j++)                 //遍历寻找第i个结点（p指向）
        {
            p = p.next;
        }
        if (i >= 0 && p != null) {
            p.data = x;                                    //p指向第i个结点
        } else {
            throw new IndexOutOfBoundsException(i + "");    //抛出序号越界异常
        }
    }

    //返回单链表所有元素的描述字符串，形式为“(,)”。覆盖Object类的toString()方法，O(n)
    @Override
    public String toString() {
//        String str = this.getClass().getName() + "(";          //返回类名
////        String str="(";
//        for (Node<T> p = this.head.next; p != null; p = p.next)//p遍历单链表
//        {
//            str += p.data.toString();
//            if (p.next != null)
//                str += ",";                                //不是最后一个结点时，加分隔符
//        }
//        return str + ")";                                    //空表返回()
        String str = "(";          //返回类名
//        String str="(";
        for (Node<T> p = this.head.next; p != null; p = p.next)//p遍历单链表
        {
            str += p.data.toString();
            if (p.next != null) {
                str += ",";                                //不是最后一个结点时，加分隔符
            }
        }
        return str + ")";                                    //空表返回()
    }

    public int size()                                      //返回单链表长度，O(n)
    {
        int i = 0;
        for (Node<T> p = this.head.next; p != null; p = p.next) //p遍历单链表
        {
            i++;
        }
        return i;
    }

    //（3）插入
    //插入x作为第i个元素，x!=null，返回插入结点。
    //对序号i采取容错措施，若i<0，插入x在最前；若i>n，插入x在最后。O(n)
    public Node<T> insert(int i, T x) {
        if (x == null) {
            throw new NullPointerException("x==null");     //抛出空对象异常
        }
        Node<T> front = this.head;                           //front指向头结点
        for (int j = 0; front.next != null && j < i; j++)      //寻找第i-1个或最后一个结点（front指向）
        {
            front = front.next;
        }
        front.next = new Node<T>(x, front.next);           //在front之后插入值为x结点，包括头插入、中间/尾插入
        return front.next;                                 //返回插入结点
    }

    public Node<T> insert(T x)                             //在单链表最后添加x对象，O(n)
    {
        return insert(Integer.MAX_VALUE, x);
        //调用insert(i,x)，用整数最大值指定插入在最后，遍历一次，i必须容错
//        return insert(this.count(), x);                  //需遍历单链表两次，效率较低
    }

    //（4）删除
    public T remove(int i)    //删除第i个元素，0≤i<n，返回被删除元素；若i越界，返回null。O(n)
    {
        Node<T> front = this.head;                           //front指向头结点
        for (int j = 0; front.next != null && j < i; j++)      //遍历寻找第i-1结点（front指向）
        {
            front = front.next;
        }
        if (i >= 0 && front.next != null)                      //若front的后继结点存在，则删除之
        {
            T old = front.next.data;                       //获得待删除结点引用的对象
            front.next = front.next.next;                  //删除front的后继结点，包括头删除、中间/尾删除
            //由Java虚拟机稍后释放结点占用存储单元
            return old;
        }
        return null;                                       //若i<0或i>表长
//        throw new IndexOutOfBoundsException(i+"");       //抛出序号越界异常
    }

    public void clear()                                    //删除单链表所有元素
    {
        this.head.next = null;                               //Java自动收回所有结点占用的存储空间
    }

    //（5）查找，散列表用
    //功能及参数：返回首个与key相等元素结点，若查找不成功返回null
    //特殊情况：若key为空对象，Java将抛出空对象异常
    //算法及效率：顺序查找，O(n)

    //顺序查找关键字为key元素，返回首次出现的元素，若查找不成功返回null
    //key可以只包含关键字数据项，由T类的equals()方法提供比较对象相等的依据
    public Node<T> search(T key) {
        for (Node<T> p = this.head.next; p != null; p = p.next) {
            if (key.equals(p.data))                        //执行T类的equals(Object)方法，运行时多态
            {
                return p;
            }
        }
        return null;
    }

    public boolean contains(T key)               //判断是否包含关键字为key元素
    {
        return this.search(key) != null;                     //以查找结果获得判断结果
    }

    //尾插入互异元素x，若查找到与x的关键字相同元素，不插入，返回x结点；覆盖。//散列表用
    public Node<T> insertDifferent(T x) {
        Node<T> front = this.head, p = front.next;             //front是p的前驱结点
        while (p != null && !p.data.equals(x))               //顺序查找
        {
            front = p;
            p = p.next;
        }
        if (p != null)                                       //查找成功，元素重复，不插入，返回p结点
        {
            System.out.println("x=" + x + "，元素重复，未插入。");
            return p;
        }
        return front.next = new Node<T>(x, null);           //尾插入值为x结点，返回插入结点
    }

    public T remove(T key)             //删除首个与key相等元素结点，返回被删除元素；查找不成功返回null。O(n)散列表用
    {
        Node<T> front = this.head, p = front.next;
        while (p != null && !key.equals(p.data))             //顺序查找首次出现的与key相等元素
        {
            front = p;                                     //front指向p的前驱结点
            p = p.next;
        }
        if (p != null)                                       //若查找成功，删除front的后继（p结点）
        {
            front.next = p.next;                           //包括头删除、中间/尾删除
            return p.data;
        }
        return null;
    }

//不行    public SinglyList(SinglyList<? extends T> list)   //深拷贝构造方法，复制单链表list的所有结点
    //相当于Node<? extends T>，即Node<?>

    public SinglyList(SinglyList<T> list)                  //深拷贝构造方法，复制单链表list的所有结点
    {
        this();                                            //创建空单链表，只有头结点
        Node<T> rear = this.head;
        for (Node<T> p = list.head.next; p != null; p = p.next)  //p遍历list单链表
        {
            rear.next = new Node<T>(p.data, null);        //复制结点尾插入到this单链表
            rear = rear.next;                             //指向this单链表尾
        }
    }

    @Override
    public boolean equals(Object obj)            //比较两条单链表是否相等，覆盖Object类的equals(obj)方法
    {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SinglyList<?>)) {
            return false;
        }
        Node<T> p = this.head.next;
        Node<T> q = ((SinglyList<T>) obj).head.next;
        while (p != null && q != null && p.data.equals(q.data)) {
            p = p.next;
            q = q.next;
        }
        return p == null && q == null;
    }

    //(1)以下4个方法提供迭代遍历单链表方式
    public Node<T> first()                                 //返回单链表第一个元素结点，O(1)
    {
        return this.head.next;                             //若单链表空返回null
    }

    public Node<T> next(Node<T> p)                         //返回p的后继结点，O(1)
    {
        return (this.head.next == null || p == null) ? null : p.next;
    }

    public Node<T> previous(Node<T> p)                     //返回p的前驱结点，O(n)
    {
        if (this.head.next == null || p == null || p == this.head.next) {
            return null;
        }
        Node<T> front = this.head.next;
        while (front.next != p) {
            front = front.next;
        }
        return front;
    }

    public Node<T> last()                                  //返回单链表最后一个元素结点，O(n)
    {
        Node<T> p = this.head.next;
        while (p != null && p.next != null) {
            p = p.next;
        }
        return p;                                          //若单链表空返回null
    }

    //在this单链表之后连接list单链表，首尾相接合并成一条单链表；设置list为空单链表，O(n)
    public void concat(SinglyList<T> list) {
        Node<T> rear = this.head;
        while (rear.next != null)                            //寻找this单链表的最后一个结点作为插入位置
        {
            rear = rear.next;
        }
        rear.next = list.head.next;                        //将list链接在this单链表之后，合并两条单链表成一条单链表
        list.head.next = null;                               //设置list为空单链表，否则逻辑错。对引用形式参数的任何修改均作用于实际参数
    }

    public void addAll(SinglyList<T> list)                 //复制list所有结点插入到this单链表之后，集合并，不改变list。
    {
        this.concat(new SinglyList<T>(list));              //先执行单链表深拷贝，再传递对象引用
        //功能同以下。
/*        Node<T> rear=this.head;
        while (rear.next!=null)                            //寻找this单链表的最后一个结点作为插入位置
            rear = rear.next;
        for (Node<T> p=list.head.next;  p!=null;  p=p.next)  //p遍历list单链表
        {
            rear.next = new Node<T>(p.data, null);        //复制结点尾插入到this单链表
            rear = rear.next;                             //指向this单链表尾
        }*/
    }

    //返回复制this和list合并连接的单链表，并集，不改变this和list。
    //说明：只能返回SinglyList<T>，不能返回子类实例，子类如果需要必须覆盖，因此，对子类没有作用。
    public SinglyList<T> union(SinglyList<T> list) {
        SinglyList<T> result = new SinglyList<T>(this);   //单链表深拷贝
        result.addAll(list);
        return result;
    }

    @Override
    public java.util.Iterator<T> iterator()      //返回Java迭代器对象
    {
        return new SinglyIterator();
    }

    private class SinglyIterator implements java.util.Iterator<T> //私有内部类，实现迭代器接口
    {
        Node<T> current = SinglyList.this.head;    //当前结点，初值为外部类单链表头结点
        Node<T> front = null;                      //当前结点的前驱结点

        @Override
        public boolean hasNext()                 //若有后继元素，返回true
        {
            return this.current != null && this.current.next != null;
        }

        @Override
        public T next()                          //返回后继元素
        {
            if (this.hasNext()) {
                this.front = this.current;
                this.current = this.current.next;
                return this.current.data;
            } else {
                throw new java.util.NoSuchElementException();  //抛出无此元素异常
            }
        }

        @Override
        public void remove()                     //删除迭代器对象表示的集合当前元素
        {
            if (this.front != null) {
                this.front.next = this.current.next; //删除当前结点
                this.current = this.front;
                this.front = null;                     //设置不能连续删除
            } else {
                throw new IllegalStateException();//抛出无效状态异常
            }
//            throw new UnsupportedOperationException();     //不支持该操作，抛出异常
        }
    }//内部类结束
}

