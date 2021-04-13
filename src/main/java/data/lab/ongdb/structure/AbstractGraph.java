package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

//  图的表示和实现
//  图的遍历
//非连通图的深度优先搜索遍历和广度优先搜索遍历

//.1   图的邻接矩阵表示和实现 //2.  声明抽象图类表示顶点集合
public abstract class AbstractGraph<T>                     //抽象图类，T表示顶点元素类型
{
    private boolean DEBUG = true;

    protected static final int MAX_WEIGHT = 99999;//0x0000ffff;      //最大权值（表示无穷大∞），不能用Integer.MAX_VALUE;
    protected SeqList<T> vertexlist;                       //顶点顺序表，存储图的顶点集合

    public AbstractGraph(int length)                       //构造空图，顶点数为0，length指定顶点顺序表容量
    {
        this.vertexlist = new SeqList<T>(length);          //构造容量为length的空顺序表。//若length<0，Java抛出负数组长度异常
    }

    public AbstractGraph()                                 //构造空图，顶点数为0
    {
        this(10);                                          //顺序表默认容量为10
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }

    public int vertexCount()                               //返回图的顶点数
    {
        return this.vertexlist.size();                     //返回顶点顺序表的元素个数
    }

    @Override
    public String toString()                               //返回图的顶点集合描述字符串
    {
        return "顶点集合：" + this.vertexlist.toString() + "\n";
    }

    public T getVertex(int i)                              //返回顶点vi元素
    {
        return this.vertexlist.get(i);                     //若i越界，则返回null
    }//遍历用

    public void setVertex(int i, T x)                      //设置顶点vi元素为x
    {
        this.vertexlist.set(i, x);                          //若i越界，则抛出异常
    }

    //以下抽象方法没有方法体，由子类提供实现
    public abstract int insertVertex(T x);                 //尾插入元素为x的顶点，返回x顶点序号

    public abstract void removeVertex(int i);              //删除顶点vi及其所有关联的边

    public abstract int weight(int i, int j);              //返回<vi,vj>边的权值

    //返回vi在vj后的后继邻接顶点序号 ；若j=-1，返回vi的第一个邻接顶点序号；若不存在后继邻接顶点，返回-1。
    protected abstract int next(int i, int j);


    //   图的遍历
    //   图的深度优先搜索遍历
    public void DFSTraverse(int i)                         //非连通图的一次深度优先搜索遍历，从顶点vi出发
    {
        boolean[] visited = new boolean[this.vertexCount()]; //访问标记数组，元素初值为false，表示未被访问
        int j = i;
        do {
            if (!visited[j])                               //若顶点vj未被访问。若i越界，Java将抛出数组下标序号越界异常
            {
                System.out.print("{ ");
                this.depthfs(j, visited);                  //从顶点vj出发的一次深度优先搜索
                System.out.print("} ");
            }
            j = (j + 1) % this.vertexCount();                //在其他连通分量中寻找未被访问顶点
        } while (j != i);
        System.out.println();
    }

    //从顶点vi出发的一次深度优先搜索，遍历一个连通分量；visited指定访问标记数组。递归算法
    private void depthfs(int i, boolean[] visited) {
        System.out.print(this.getVertex(i) + " ");           //访问顶点vi
        visited[i] = true;                                 //设置访问标记
        for (int j = this.next(i, -1); j != -1; j = this.next(i, j))//j依次获得vi的所有邻接顶点序号
        {
            if (!visited[j])                                //若邻接顶点vj未被访问
            {
                depthfs(j, visited);                       //从vj出发的深度优先搜索遍历，递归调用
            }
        }
    }

    //7.3.2   图的广度优先搜索遍历
    public void BFSTraverse(int i)                         //非连通图的一次广度优先搜索遍历，从顶点vi出发
    {
        boolean[] visited = new boolean[this.vertexCount()]; //访问标记数组
        int j = i;
        do {
            if (!visited[j])                               //若顶点vj未被访问
            {
                System.out.print("{ ");
                breadthfs(j, visited);                     //从vj出发的一次广度优先搜索
                System.out.print("} ");
            }
            j = (j + 1) % this.vertexCount();                //在其他连通分量中寻找未被访问顶点
        } while (j != i);
        System.out.println();
    }

    //从顶点vi出发的一次广度优先搜索，遍历一个连通分量，使用队列
    private void breadthfs(int i, boolean[] visited) {
        System.out.print(this.getVertex(i) + " ");           //访问顶点vi
        visited[i] = true;                                 //设置访问标记
//        SeqQueue<Integer> que = new SeqQueue<Integer>(this.vertexCount());   //创建顺序队列
        LinkedQueue<Integer> que = new LinkedQueue<Integer>();   //创建链式队列
        que.add(i);                                        //访问过的顶点vi序号入队，自动转换成Integer(i))
        while (!que.isEmpty())                             //当队列不空时循环
        {
            i = que.poll();                                //出队，自动转换成int;
            for (int j = next(i, -1); j != -1; j = next(i, j))     //j依次获得vi的所有邻接顶点
            {
                if (!visited[j])                           //若顶点vj未访问过
                {
                    System.out.print(this.getVertex(j) + " ");//访问顶点vj
                    visited[j] = true;
                    que.add(j);                            //访问过的顶点vj序号入队
//                    System.out.println("顶点队列："+que.toString());
                }
            }
        }
    }

    //   最小生成树的构造算法
    //Prim算法，构造带权无向图的最小生成树，输出最小生成树的各边及代价
    public void minSpanTree() {
        Triple[] mst = new Triple[vertexCount() - 1];        //最小生成树的边集合，边数为顶点数n-1
        for (int i = 0; i < mst.length; i++)                   //边集合初始化，从顶点v0出发构造
        {
            mst[i] = new Triple(0, i + 1, this.weight(0, i + 1));   //保存从v0到其他各顶点的边
        }

        for (int i = 0; i < mst.length; i++)                   //选择n-1条边，每趟确定一条权值最小的边。一趟选择排序算法
        {
            System.out.print("mst边集合：");
            for (int j = 0; j < mst.length; j++)
                System.out.print(mst[j].toString() + ",");
            System.out.println();

//            int minweight=MAX_WEIGHT, min=i;               //最小权值及边的下标
            int min = i;//, minweight=mst[i].value;               //最小权值及边的下标
            for (int j = i + 1; j < mst.length; j++)             //在i～n-1范围内，寻找权值最小的边
            {
                if (mst[j].value < mst[min].value)//minweight)              //若存在更小权值，则更新最小值变量
                {
//                    minweight = mst[j].value;              //最小权值
                    min = j;                               //保存当前权值最小边的序号
                }
            }

            //将权值最小的边（由min记得）交换到第i个元素，表示该边加入TE集合
            Triple edge = mst[min];
            if (min != i) {
                mst[min] = mst[i];
                mst[i] = edge;
            }

            //将i+1～n-1的其他边用权值更小的边替换
            int tv = edge.column;                          //刚并入TV的顶点
            for (int j = i + 1; j < mst.length; j++) {
                int v = mst[j].column;                     //原边在V-TV中的终点
                int weight = this.weight(tv, v);
                if (weight < mst[j].value)                   //若(tv,v)边比第j条边的权值更小，则替换
                {
                    mst[j] = new Triple(tv, v, weight);
                }
            }
        }

        System.out.print("\n最小生成树的边集合：");
        int mincost = 0;
        for (int i = 0; i < mst.length; i++)                   //输出最小生成树的边集合和代价
        {
            System.out.print(mst[i] + " ");
            mincost += mst[i].value;
        }
        System.out.println("，最小代价为" + mincost);
    }

    //   最短路径
    //   非负权值的单源最短路径（Dijkstra算法）
    public void shortestPath(int i)              //求带权图中顶点vi的单源最短路径，Dijkstra算法
    {
        int n = this.vertexCount();              //图的顶点数
        boolean[] vset = new boolean[n];         //已求出最短路径的顶点集合，初值全为false
        vset[i] = true;                          //标记源点vi在集合S中。若i越界，Java抛出序号越界异常
        int[] dist = new int[n];                 //最短路径长度
        int[] path = new int[n];                 //最短路径的终点的前一个顶点
        for (int j = 0; j < n; j++)                  //初始化dist和path数组
        {
            dist[j] = this.weight(i, j);
            path[j] = (j != i && dist[j] < MAX_WEIGHT) ? i : -1;
        }
//        System.out.print("\nvset数组"+toString(vset));
//        System.out.print("\tpath数组"+toString(path));
//        System.out.print("\tdist数组"+toString(dist));

        for (int j = (i + 1) % n; j != i; j = (j + 1) % n)     //寻找从vi到vj的最短路径，vj在V-S集合中
        {
            int mindist = MAX_WEIGHT, min = 0;       //求路径长度最小值及其下标
            for (int k = 0; k < n; k++) {
                if (!vset[k] && dist[k] < mindist) {
                    mindist = dist[k];           //路径长度最小值
                    min = k;                     //路径长度最小值下标
                }
            }
            if (mindist == MAX_WEIGHT)             //若没有其他最短路径则算法结束； 此语句对非连通图必需
            {
                break;
            }
            vset[min] = true;                    //确定一条最短路径的终点min并入集合S
            for (int k = 0; k < n; k++)              //调整从vi到V-S中其他顶点的最短路径及长度
            {
                if (!vset[k] && this.weight(min, k) < MAX_WEIGHT && dist[min] + this.weight(min, k) < dist[k]) {
                    dist[k] = dist[min] + this.weight(min, k);//用更短路径替换
                    path[k] = min;                   //最短路径经过min顶点
                }
            }
//            System.out.print("\nvset数组"+toString(vset));
//            System.out.print("\tpath数组"+toString(path));
//            System.out.print("\tdist数组"+toString(dist));
        }

        System.out.print(this.getVertex(i) + "的单源最短路径：");
        for (int j = 0; j < n; j++)                            //输出顶点vi的单源最短路径
        {
            if (j != i) {
                SinglyList<T> pathlink = new SinglyList<T>();//路径单链表，记录最短路径经过的各顶点，用于反序
                pathlink.insert(0, this.getVertex(j));     //单链表插入最短路径终点vj
                for (int k = path[j]; k != i && k != j && k != -1; k = path[k]) {
                    pathlink.insert(0, this.getVertex(k)); //单链表头插入经过的顶点，反序
                }
                pathlink.insert(0, this.getVertex(i));     //最短路径的起点vi
                System.out.print(pathlink.toString() + "长度" + (dist[j] == MAX_WEIGHT ? "∞" : dist[j]) + "，");
            }
        }
        System.out.println();
    }

    private static String toString(int[] value)        //输出数组值
    {
        if (value != null && value.length > 0) {
            String str = "{";
            int i = 0;
            for (i = 0; i < value.length - 1; i++) {
                str += (value[i] == MAX_WEIGHT ? "∞" : value[i]) + ",";
            }
            return str + (value[i] == MAX_WEIGHT ? "∞" : value[i]) + "}";
        }
        return null;
    }

    //   每对顶点间的最短路径（Floyd算法）
    public void shortestPath()                  //求带权图每对顶点间的最短路径及长度，Floyd算法
    {
        int n = this.vertexCount();                          //图的顶点数
        Matrix path = new Matrix(n), dist = new Matrix(n);     //最短路径及长度矩阵，初值为0
        for (int i = 0; i < n; i++)                            //初始化dist、path矩阵
        {
            for (int j = 0; j < n; j++) {
                int w = this.weight(i, j);
                dist.set(i, j, w);                           //dist初值是图的邻接矩阵
                path.set(i, j, (i != j && w < MAX_WEIGHT ? i : -1));
            }
        }
        if (DEBUG) {
            System.out.println("dist" + dist.toString() + "path" + path.toString() + "路径矩阵：");
        }
        printPathAll(path);

        for (int k = 0; k < n; k++)                            //以vk作为其他路径的中间顶点
        {
            if (DEBUG) {
                System.out.println("\n以" + this.getVertex(k) + "作为中间顶点，替换路径如下：");
            }
            for (int i = 0; i < n; i++)                        //测试每对从vi到vj路径长度是否更短
            {
                if (i != k) {
                    for (int j = 0; j < n; j++) {
                        if (j != k && j != i) {
                            if (DEBUG) {
                                System.out.print(toPath(path, i, j) + "路径长度" + dist.get(i, j) + "，替换为" +
                                        toPath(path, i, k) + "," + toPath(path, k, j) + "路径长度" + (dist.get(i, k) + dist.get(k, j)) + "？");
                            }
                            if (j != k && j != i && dist.get(i, j) > dist.get(i, k) + dist.get(k, j))//若更短，则替换
                            {
                                dist.set(i, j, dist.get(i, k) + dist.get(k, j));
                                path.set(i, j, path.get(k, j));
                                if (DEBUG) {
                                    System.out.println("是，d" + i + j + "=" + dist.get(i, j) + "，p" + i + j + "=" + path.get(i, j));
                                }
                            } else if (DEBUG) {
                                System.out.println("否");
                            }
                        }
                    }
                }
            }
            if (DEBUG) {
                System.out.println("dist" + dist.toString() + "path" + path.toString() + "路径矩阵：");
            }
            printPathAll(path);
        }

        if (DEBUG) {
            System.out.println("\n每对顶点间的最短路径如下：");
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (DEBUG) {
                        System.out.print(toPath(path, i, j) + "长度" + (dist.get(i, j) == MAX_WEIGHT ? "∞" : dist.get(i, j)) + "，");
                    }
                }
            }
            if (DEBUG) {
                System.out.println();
            }
        }
    }
//    System.out.print(pathlink.toString()+"长度"+(dist[j]==MAX_WEIGHT ? "∞" : dist[j])+"，");

    private String toPath(Matrix path, int i, int j)           //返回path路径矩阵中从顶点vi到vj的一条路径字符串
    {
        SinglyList<T> pathlink = new SinglyList<T>();//路径单链表，记录最短路径经过的各顶点，用于反序
        pathlink.insert(0, this.getVertex(j));     //单链表插入最短路径终点vj
        for (int k = path.get(i, j); k != i && k != j && k != -1; k = path.get(i, k)) {
            pathlink.insert(0, this.getVertex(k)); //单链表头插入经过的顶点，反序
        }
        pathlink.insert(0, this.getVertex(i));     //最短路径的起点vi
        return pathlink.toString();
    }

    private void printPathAll(Matrix path)                 //输出path路径矩阵中每对顶点间的路径字符串
    {
        for (int i = 0; i < path.getRows(); i++) {
            for (int j = 0; j < path.getRows(); j++) {
                if (DEBUG) {
                    System.out.print(toPath(path, i, j) + " ");
                }
            }
            if (DEBUG) {
                System.out.println();
            }
        }
    }
}

