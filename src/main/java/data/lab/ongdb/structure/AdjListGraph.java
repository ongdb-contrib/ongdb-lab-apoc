package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.structure.AdjListGraph
 * @Description: TODO（图的邻接表表示和实现 - 邻接表表示的带权图类 ， T表示顶点元素类型 ； 继承抽象图类）
 * @date 2021/4/13 11:28
 */
public class AdjListGraph<T> extends AbstractGraph<T> {

    /**
     * 图的邻接表，结构同矩阵行的单链表
     */
    protected LinkedMatrix adjlist;

    /**
     * 构造空图，顶点数为0，边数为0；length指定顶点顺序表容量和邻接表容量
     */
    public AdjListGraph(int length) {
        /*
         * 构造容量为length的空顺序表
         * */
        super(length);
        /*
         * 构造length×length矩阵
         * */
        this.adjlist = new LinkedMatrix(length, length);
    }

    /**
     * 以下构造方法的方法体同MatrixGraph类，省略
     * 构造空图，顶点数为0，边数为0
     */
    public AdjListGraph() {
        /*
         * 顺序表和邻接矩阵的默认容量为10
         * */
        this(10);
    }

    /**
     * 以vertices顶点集合构造图，边数为0
     */
    public AdjListGraph(T[] vertices) {
        /*
         * 构造指定容量的空图
         * */
        this(vertices.length);
        /*
         * 插入一个顶点
         * */
        for (T vertex : vertices) {
            this.insertVertex(vertex);
        }
    }

    /**
     * 以vertices顶点集合和edges边集合构造图
     */
    public AdjListGraph(T[] vertices, Triple[] edges) {
        this(vertices);
        /*
         * 插入一条边
         * */
        for (Triple edge : edges) {
            this.insertEdge(edge);
        }
    }

    /**
     * 返回图的顶点集合和邻接表描述字符串
     */
    @Override
    public String toString() {
        return super.toString() + "出边表：\n" + this.adjlist.toString();
    }

    /**
     * （1）插入边
     * 插入边〈vi,vj〉，权值为weight
     */
    public void insertEdge(int i, int j, int weight) {
        /*
         * 不能表示自身环
         * */
        if (i != j) {
            /*
             * 边的权值容错，视为无边，取值0
             * */
            if (weight < 0 || weight >= MAX_WEIGHT) {
                weight = 0;
            }
            /*
             * 设置第i条边单链表中〈vi,vj〉边的权值为weight。
             * */
            this.adjlist.set(i, j, weight);
            //若0<weight<∞，插入边或替换边的权值；若weight==0，删除该边。若i、j越界，抛出序号越界异常
        } else {
            throw new IllegalArgumentException("不能插入自身环，i=" + i + "，j=" + j);
        }
    }

    /**
     * 插入一条边。方法体同图的邻接矩阵，省略
     */
    public void insertEdge(Triple edge) {
        this.insertEdge(edge.row, edge.column, edge.value);
    }

    /**
     * （2）插入顶点
     * 插入元素为x的顶点，返回x顶点序号
     */
    @Override
    public int insertVertex(T x) {
        /*
         * 顶点顺序表尾插入顶点x，返回x顶点序号，长度加1，自动扩容
         * */
        int i = this.vertexlist.insert(x);
        /*
         * 若邻接表容量不够，
         * */
        if (i >= this.adjlist.getRows()) {
            /*
             * 则扩容，保持邻接表行数同图的顶点数
             * */
            this.adjlist.setRowsColumns(i + 1, i + 1);
        }
        /*
         * 返回插入顶点序号
         * */
        return i;
    }

    /**
     * （3）删除边
     * 删除一条边〈vi,vj〉，忽略权值
     */
    public void removeEdge(int i, int j) {
        if (i != j) {
            /*
             * 设置边的权值为0，即在第i条边单链表中删除边结点
             * */
            this.adjlist.set(new Triple(i, j, 0));
        }
    }

    /**
     * 删除一条边。方法体同图的邻接矩阵，省略
     */
    public void removeEdge(Triple edge) {
        this.removeEdge(edge.row, edge.column);
    }

    /**
     * （4）删除顶点
     * 删除顶点vi及其关联的边
     */
    @Override
    public void removeVertex(int i) {
        /*
         * 删除之前的顶点数
         * */
        int n = this.vertexCount();
        if (i >= 0 && i < n) {
            //删除与第i条边单链表中所有结点对称的边，即在第i条以外的边单链表中，删除所有以i为终点的边
            SortedSinglyList<Triple> link = this.adjlist.rowlist.get(i);
            /*
             * 遍历第i条边单链表
             * */
            for (Node<Triple> p = link.head.next; p != null; p = p.next) {
                /*
                 * 删除与p结点对称的边
                 * */
                this.removeEdge(p.data.toSymmetry());
            }

            //顶点数减1
            n--;

            // 删除行指针顺序表的第i条边单链表，其后单链表上移
            this.adjlist.rowlist.remove(i);
            //设置矩阵行列数，少一行
            this.adjlist.setRowsColumns(n, n);

            //遍历每条边单链表，将>i的顶点序号减1
            for (int j = 0; j < n; j++) {
                link = this.adjlist.rowlist.get(j);
                //遍历第j条边单链表
                for (Node<Triple> p = link.head.next; p != null; p = p.next) {
                    if (p.data.row > i) {
                        p.data.row--;
                    }
                    if (p.data.column > i) {
                        p.data.column--;
                    }
                }
            }
            //删除顶点vi，i后顶点序号减1，图顶点数减1
            this.vertexlist.remove(i);
        } else {
            //抛出序号越界异常
            throw new IndexOutOfBoundsException("i=" + i);
        }
    }

    /**
     * （5） 获得邻接顶点和边的权值属性
     * 返回<vi,vj>边的权值。用于图的最小生成树、最短路径等算法
     */
    @Override
    public int weight(int i, int j) {
        if (i == j) {
            return 0;
        }
        //返回矩阵元素[i,j]值。若i、j越界，抛出序号越界异常
        int weight = this.adjlist.get(i, j);
        //若返回0表示没有边，则边的权值返回∞
        return weight != 0 ? weight : MAX_WEIGHT;
    }

    /**
     * 返回顶点vi在vj后的后继邻接顶点序号；若j=-1，返回vi的第一个邻接顶点序号；若不存在后继邻接顶点，返回-1。
     */
    @Override
    protected int next(int i, int j) {
        int n = this.vertexCount();
        if (i >= 0 && i < n && j >= -1 && j < n && i != j) {
            //第i条排序单链表
            SortedSinglyList<Triple> link = this.adjlist.rowlist.get(i);
            //单链表第0个元素
            Node<Triple> find = link.head.next;
            if (j == -1) {
                //返回第一个邻接顶点的序号
                return find != null ? find.data.column : -1;
            }
            //顺序查找<vi,vj>边的结点
            find = link.search(new Triple(i, j, 0));
            //查找成功
            if (find != null) {
                //获得<vi,vj>边的后继结点
                find = find.next;
                if (find != null) {
                    //返回后继邻接顶点序号
                    return find.data.column;
                }
            }
        }
        return -1;
    }
}
