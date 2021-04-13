package data.lab.ongdb.algo;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto.FloydShortestPath
 * @Description: TODO(Floyd Shortest Path)
 * @date 2021/4/13 14:25
 */
public class FloydShortestPath {

    /**
     * vi表示矩阵横列
     * vj表示矩阵纵列
     **/

    // 表示无穷大即不可达
    public final static int MAX = Integer.MAX_VALUE;

    // 距离矩阵
    public int[][] dist;

    // 路径矩阵
    public int[][] path;

    public FloydShortestPath(int vertexNum) {
        this.dist = new int[vertexNum][vertexNum];
        this.path = new int[vertexNum][vertexNum];
    }

    /**
     * @param
     * @return
     * @Description: TODO(为D矩阵插入元素)
     */
    public void insertDTwinDisMatrix(int vi, int vj, int value) {
        this.dist[vi][vj] = value;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取D矩阵元素)
     */
    public Object getDTwinDisMatrix(int vi, int vj) {
        return this.dist[vi][vj];
    }

    /**
     * @param
     * @return
     * @Description: TODO(为P矩阵插入元素)
     */
    public void insertPTwinDisMatrix(int vi, int vj, int value) {
        this.path[vi][vj] = value;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取P矩阵元素)
     */
    public Object getPTwinDisMatrix(int vi, int vj) {
        return this.path[vi][vj];
    }


    public int[][] getDMatrix() {
        return this.dist;
    }

    public int[][] getPMatrix() {
        return this.path;
    }

    public static void print(int[][] matrix) {
        System.out.println("------------------------");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                int element = matrix[i][j];
                if (i == j) {
                    System.out.print("-1 ");
                } else if (element == FloydShortestPath.MAX) {
                    System.out.print("∞ ");
                } else {
                    System.out.print(element + " ");
                }

            }
            System.out.println();
        }
        System.out.println("------------------------");
    }

    /**
     * @param
     * @return
     * @Description: TODO(构建距离矩阵和路径矩阵)
     */
    public void floyd(int[][] matrix) {
        // matrix和path length不一致可处理异常
        int size = matrix.length;
        //初始化 dist 和 path
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                path[i][j] = -1;
                dist[i][j] = matrix[i][j];
            }
        }
        // 核心算法
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    // 判断如果 ik距离可达且 kj距离可达 且 i和j的距离是否大于 i-> k 与 k->j的距离和
                    if (dist[i][k] != MAX && dist[k][j] != MAX && dist[i][j] > (dist[i][k] + dist[k][j])) {
                        path[i][j] = k;
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(查找i到j的路径)
     */
    public void findPath(int i, int j) {
        StringBuilder pathStr = new StringBuilder(i + " -> ");
        toFind(i, j, pathStr);
        pathStr.append(j);
        System.out.println(i + " TO " + j + ":");
        System.out.println("Final path:" + pathStr.toString());
        System.out.println("Final distance:" + dist[i][j]);
    }

    /**
     * @param
     * @return
     * @Description: TODO(查找i到j的路径)
     */
    public String findPathToStr(int i, int j) {
        StringBuilder pathStr = new StringBuilder(i + " -> ");
        toFind(i, j, pathStr);
        pathStr.append(j);
        System.out.println(i + " TO " + j + ":");
        System.out.println("Final path:" + pathStr.toString());
        System.out.println("Final distance:" + dist[i][j]);
        return pathStr.toString();
    }

    public void toFind(int i, int j, StringBuilder pathStr) {
        if (path[i][j] != -1) {
            pathStr.append(path[i][j] + " -> ");
            toFind(path[i][j], j, pathStr);
        }
    }

}

