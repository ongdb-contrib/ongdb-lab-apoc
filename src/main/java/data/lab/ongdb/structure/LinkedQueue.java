package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

//  链式队列

//链式队列类，最终类，实现队列接口，T表示数据元素的数据类型
public final class LinkedQueue<T> implements Queue<T> {
    private Node<T> front, rear;                           //front和rear分别指向队头和队尾结点

    public LinkedQueue()                                   //构造空队列
    {
        this.front = this.rear = null;
    }

    @Override
    public boolean isEmpty()                               //判断队列是否空，若空返回true
    {
        return this.front == null && this.rear == null;
    }

    @Override
    public boolean add(T x)                                //元素x入队，空对象不能入队
    {
        if (x == null) {
            return false;
        }
//            throw new NullPointerException("x==null");      //抛出空对象异常
        Node<T> q = new Node<T>(x, null);
        if (this.front == null) {
            this.front = q;                                  //空队插入
        } else {
            this.rear.next = q;                              //队列尾插入
        }
        this.rear = q;
        return true;
    }

    @Override
    public T peek()                                       //返回队头元素，没有删除。若队列空，则返回null
    {
        return this.isEmpty() ? null : this.front.data;
    }

    @Override
    public T poll()                                        //出队，返回队头元素，若队列空返回null
    {
        if (isEmpty()) {
            return null;
        }
        T x = this.front.data;                             //取得队头元素
        this.front = this.front.next;                      //删除队头结点
        if (this.front == null) {
            this.rear = null;
        }
        return x;
    }

    @Override
    public String toString()                               //返回队列所有元素的描述字符串，形式为“(,)”
    {                                                      //算法同不带头结点的单链表
        StringBuffer strbuf = new StringBuffer(this.getClass().getName() + "(");
        for (Node<T> p = this.front; p != null; p = p.next) {
            strbuf.append(p.data.toString() + ",");          //按照队列元素次序复制数组元素
        }
        strbuf.setCharAt(strbuf.length() - 1, ')');          //将串最后多余的一个字符','改为')'
        return new String(strbuf);                         //由StringBuffer对象构造String对象
    }
}

