package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

// 稀疏矩阵的压缩存储
// 稀疏矩阵行的单链表
// 图的邻接表表示和实现

// 声明行的单链表矩阵类
public class LinkedMatrix                                  //三元组行的单链表存储的矩阵类
{
    //private
    int rows, columns;                             //矩阵行数、列数
    SeqList<SortedSinglyList<Triple>> rowlist;     //行指针顺序表，元素是排序单链表，默认权限
//    SeqList<PolySinglyList<Triple>> rowlist;     //行指针顺序表，元素是多项式排序单链表，默认权限
    //private //以上不能，因为节 图的邻接表 要遍历边单链表
//    SeqList<SortedSeqList<Triple>> rowlist;     //行指针顺序表，元素是排序顺序表

    public LinkedMatrix(int m, int n)            //构造m×n零矩阵。若m或n≤0，抛出无效参数异常
    {
        if (m>0 && n>0)
        {
            this.rows = m;
            this.columns = n;
            this.rowlist = new SeqList<SortedSinglyList<Triple>>();  //构造顺序表，默认容量，初值为null
//            this.rowlist = new SeqList<PolySinglyList<Triple>>();  //构造顺序表，默认容量，初值为null
            for (int i=0; i<m; i++)                                  //顺序表增加m个空单链表
            {
                this.rowlist.insert(new SortedSinglyList<Triple>()); //顺序表尾插入，自动扩容
            }
//              this.rowlist.insert(new PolySinglyList<Triple>()); //顺序表尾插入，自动扩容
        }
        else {
            throw new IllegalArgumentException("矩阵行列数不能≤0，m="+m+"，n="+n);
        }
    }
    public LinkedMatrix(int m)                             //构造m×m零矩阵
    {
        this(m, m);
    }

    public LinkedMatrix(int m, int n, Triple[] tris)       //构造m×n矩阵，由三元组数组tris提供矩阵初值
    {
        this(m, n);
        for (int i=0; i<tris.length; i++)
            this.set(tris[i]);                             //按行主序插入一个元素的三元组
    }

    public SeqList<SortedSinglyList<Triple>> getRowlist() {
        return rowlist;
    }

    public void setRowlist(SeqList<SortedSinglyList<Triple>> rowlist) {
        this.rowlist = rowlist;
    }

    public int getRows()                                   //返回矩阵行数。方法体省略
    {
        return this.rows;
    }
    public int getColumns()                                //返回矩阵列数。方法体省略
    {
        return this.columns;
    }

    //（2） 返回矩阵元素
    //返回矩阵第i行第j列元素。若i、j越界，则抛出序号越界异常。查找算法比较三元组大小
    public int get(int i, int j)
    {
        if (i>=0 && i<this.rows && j>=0 && j<this.columns)
        {
            //在第i行排序单链表中顺序查找三元组(i,j,0)，比较三元组大小
            Node<Triple> find=this.rowlist.get(i).search(new Triple(i,j,0));
            return (find!=null) ? find.data.value : 0;     //若查找成功，返回元素值，否则返回0
        }
        throw new IndexOutOfBoundsException("i="+i+"，j="+j);
    }

    //（3） 设置矩阵元素
    //设置矩阵第i行第j列元素为x。若i、j越界，则抛出序号越界异常。
    //查找、插入、删除算法均比较三元组大小
    public void set(int i, int j, int x)
    {
        if (i>=0 && i<this.rows && j>=0 && j<this.columns)
        {
            SortedSinglyList<Triple> link = this.rowlist.get(i);//获得第i行排序单链表
            if (x==0) {
                link.remove(new Triple(i,j,0));  //若查找成功，删除(i,j,?)结点
            } else
            {   //以下查找再插入或替换元素操作，遍历link排序单链表二次
                Triple tri = new Triple(i,j,x);
                Node<Triple> find=link.search(tri); //顺序查找tri，若有元素>tri，则查找不成功
                if (find!=null) {
                    find.data.value = x;                   //查找成功，修改修改find引用对象的成员变量值
                } else {
                    link.insert(tri);                     //查找不成功，排序单链表link按(i,j)次序插入三元组tri
                }

/*                //也可，一次遍历
                Node<Triple> find = link.insertDifferent(new Triple(i,j,x));//插入不重复元素，返回查找到或插入结点
                if (find.data.value!=x)          //上述若返回查找到结点，修改该结点元素
                    find.data.value = x;
*/
            }
        }
        else {
            throw new IndexOutOfBoundsException("i="+i+"，j="+j);//抛出序号越界异常
        }
    }

    public void set(Triple tri)                  //以三元组tri设置矩阵元素
    {
        this.set(tri.row, tri.column, tri.value);
    }

    //（4） 输出矩阵
    @Override
    public String toString()                               //返回稀疏矩阵三元组行的单链表描述字符串
    {
        String str="";
        for (int i=0; i<this.rowlist.size(); i++)         //循环次数为行指针顺序表长度
        {
            str += i+" -> "+this.rowlist.get(i).toString()+"\n";//获得第i行排序单链表的描述字符串
        }
        return str;
    }

    public void printMatrix()                               //输出矩阵
    {
        System.out.println("矩阵"+this.getClass().getName()+"（"+rows+"×"+columns+"）：");
//        for (int i=0; i<this.rowlist.size(); i++)
        for (int i=0; i<this.rows; i++)
        {
            Node<Triple> p = this.rowlist.get(i).head.next;//遍历第i行排序单链表
            for (int j=0; j<this.columns; j++) {
                if (p!=null && j==p.data.column)            //有i==p.data.row
                {
                    System.out.print(String.format("%4d", p.data.value));
                    p = p.next;
                }
                else {
                    System.out.print(String.format("%4d", 0));
                }
            }
            System.out.println();
        }
    }

    //可行，效率低，用于测试get(i,j)方法是否正确
    public void printMatrix2()                             //输出矩阵
    {
        System.out.println("矩阵"+this.getClass().getName()+"（"+rows+"×"+columns+"）：");
        for (int i=0; i<this.rows; i++)
        {
            for (int j=0; j<this.columns; j++) {
                System.out.print(String.format("%4d", this.get(i,j)));
            }
            System.out.println();
        }
    }

    //（5） 比较矩阵是否相等
    @Override
    public boolean equals(Object obj)                      //比较两个矩阵是否相等，算法同SeqSparseMatrix类
    {
        if (this==obj) {
            return true;
        }
        if (!(obj instanceof LinkedMatrix)) {
            return false;
        }
        LinkedMatrix mat=(LinkedMatrix)obj;
        return this.rows==mat.rows && this.columns==mat.columns && this.rowlist.equals(mat.rowlist);
        //比较两个三元组顺序表是否相等
    }

    //（6） 设置矩阵行列数
    //设置矩阵为m行n列。若m指定行数较大，则将行指针顺序表扩容，使用原各行单链表。
    //用于7.2.2节图的邻接表存储结构。
    public void setRowsColumns(int m, int n)
    {
        if (m>0 && n>0)
        {
            if (m > this.rows)                             //若m参数指定行数较大
            {
                for (int i=this.rows; i<m; i++)
//                    this.rowlist.insert(new SortedSinglyList<Triple>());//顺序表尾插入，自动扩容
                {
                    this.rowlist.insert(new PolySinglyList<Triple>());//顺序表尾插入，自动扩容
                }
            }
            this.rows = m;
            this.columns = n;
        }
        else {
            throw new IllegalArgumentException("矩阵行列数不能≤0，m="+m+"，n="+n);
        }
    }

    //（7） 矩阵相加
    public void addAll(LinkedMatrix mat)
    {
        if (this.rows==mat.rows && this.columns==mat.columns) {
            for (int i=0; i<this.rows; i++) {
                this.rowlist.get(i).addAll(mat.rowlist.get(i));        //调用多项式单链表相加算法
            }
        } else {
            throw new IllegalArgumentException("两个矩阵阶数不同，不能相加");
        }
    }

    //矩阵深拷贝
    //拷贝构造方法，深度拷贝，复制顺序表，复制顺序表中所有单链表及其中所有结点和元素对象
    public LinkedMatrix(LinkedMatrix mat)
    {
        this(mat.rows, mat.columns);             //构造rows行columns列零矩阵，行指针顺序表有rows个空单链表
        for (int i=0; i<this.rows; i++)
        {
            SortedSinglyList<Triple> link=new SortedSinglyList<Triple>(mat.rowlist.get(i));
//            PolySinglyList<Triple> link = new PolySinglyList<Triple>(mat.rowlist.get(i));
            //调用SortedSinglyList(SortedSinglyList<T> list)，O(n)
            //排序单链表深拷贝，复制所有结点，没有复制元素对象
            for (Node<Triple> p=link.head.next;  p!=null;  p=p.next) {
                p.data = new Triple(p.data);     //复制一条单链表中各结点引用的元素对象
            }
            this.rowlist.set(i, link);           //将复制后的单链表设置为顺序表第i个元素
        }
    }

    public LinkedMatrix union(LinkedMatrix mat)  //返回this+mat相加的矩阵，不改变this和mat
    {
        LinkedMatrix matc=new LinkedMatrix(this);//深度拷贝
        matc.addAll(mat);
        return matc;                             //返回对象引用
    }

    public LinkedMatrix transpose()                        //返回this的转置矩阵
    {
        LinkedMatrix trans=new LinkedMatrix(columns, rows);//构造columns行rows列零矩阵
        //遍历每行排序单链表，将各三元组的对称元素，插入到trans矩阵
        for (int i=0; i<this.rows; i++) {
            for (Node<Triple> p=this.rowlist.get(i).head.next; p!=null; p=p.next) {
                trans.set(p.data.toSymmetry());
            }
        }
        return trans;
    }
}

// 实现迭代器
// 提供迭代器的类
// 使用迭代器遍历顺序表和单链表。
/*    public void printMatrix()                    //输出矩阵。使用顺序表和单链表的迭代器遍历
    {
        System.out.println("矩阵"+this.getClass().getName()+"（"+rows+"×"+columns+"）：");
//        java.util.Iterator<PolySinglyList<Triple>> seqit = this.rowlist.iterator();//获得顺序表迭代器
//        while (seqit.hasNext())                           //遍历顺序表
        for (PolySinglyList<Triple> link : this.rowlist)  //逐元循环，link获得rowlist集合中的每个元素
        {
//            java.util.Iterator<Triple> it = seqit.next().iterator();//获得当前行单链表的迭代器
            java.util.Iterator<Triple> it = link.iterator();//获得单链表的迭代器
            Triple triple= it.hasNext() ? it.next() : null;//获得迭代器的第1个元素
            for (int j=0; j<this.columns; j++)             //遍历第i行排序单链表
                if (triple!=null && triple.column==j)      //有三元组，非0元素
                {
                    System.out.print(String.format("%4d", triple.value));
                    triple= it.hasNext() ? it.next() : null;//获得迭代器的下一个元素
                }
                else System.out.print(String.format("%4d", 0));
            System.out.println();
        }
    }
}

    //使用单链表迭代器遍历，可用删除。但替换未成功。因为，查找到的元素要替换，不仅是包含。
/*    public void set(Triple tri)        //以三元组tri设置矩阵元素。若tri的行/列序号越界，抛出序号越界异常
    {
        int i=tri.row, j=tri.column;
        if (i>=0 && i<this.rows && j>=0 && j<this.columns)
        {
//            SortedSinglyList<Triple> link = this.rowlist.get(i);//获得第i行排序单链表
            java.util.Iterator<Triple> it = this.rowlist.get(i).iterator();//获得第i行排序单链表的迭代器对象
            while (it.hasNext())                           	//若有后继元素，使用迭代器遍历一个集合
//          System.out.print(it.next().toString()+" ");       	//返回后继元素
            if (tri.value==0)
                it.remove();                          //删除(i,j,?)结点（顺序查找，如果有）
//            link.remove(tri);                          //删除(i,j,?)结点（顺序查找，如果有）
            else
            {
                Node<Triple> find=link.search(tri);        //顺序查找首次出现元素
                if (find!=null)
                    find.data.value = tri.value;           //查找成功，修改矩阵元素值
                else link.insert(tri);                     //查找不成功，按(i,j)次序插入tri。// 二次遍历

//也可，一次遍历
//                Node<Triple> find = link.insertUnrepeatable(tri);    //插入不重复元素x，返回x结点
//                find.data.value = tri.value;      //修改该结点元素
            }
        }
        else throw new IndexOutOfBoundsException("i="+i+"，j="+j);//抛出序号越界异常
    }*/


