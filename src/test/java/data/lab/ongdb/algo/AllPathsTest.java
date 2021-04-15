package data.lab.ongdb.algo;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.structure.AdjacencyNode;
import data.lab.ongdb.structure.Triple;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO（Two vertex all paths）
 * @date 2021/4/13 14:09
 */
public class AllPathsTest {
    @Test
    public void getAllPaths_1() {
        AllPaths vertexAllPaths = new AllPaths(8);

        // 图：image/vertex-edge-matrix-test.png
        // 插入顶点
        int[] vertexs = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < vertexs.length; i++) {
            int vertex = vertexs[i];
            System.out.println("VERTEX INDEX NUMBER:" + vertexAllPaths.insertVertex(vertex));
        }

        // 插入边
        for (int i = 0; i < vertexs.length; i++) {
            for (int j = 0; j < vertexs.length; j++) {
                if (
                        ((i == 0 && j == 1) || (i == 1 && j == 0)) ||
                                ((i == 0 && j == 2) || (i == 2 && j == 0)) ||
                                ((i == 1 && j == 3) || (i == 3 && j == 1)) ||
                                ((i == 1 && j == 4) || (i == 4 && j == 1)) ||
                                ((i == 3 && j == 7) || (i == 7 && j == 3)) ||
                                ((i == 4 && j == 7) || (i == 7 && j == 4)) ||
                                ((i == 2 && j == 5) || (i == 5 && j == 2)) ||
                                ((i == 2 && j == 6) || (i == 6 && j == 2)) ||
                                ((i == 5 && j == 6) || (i == 6 && j == 5))
                ) {
                    vertexAllPaths.insertEdge(new Triple(i, j, 1));
                } else {
                    if (i != j) {
                        vertexAllPaths.insertEdge(new Triple(i, j, -1));
                    }
                }

            }
        }

//        vertexAllPaths.setDEBUG(true);
//        // 求所有顶点之间的最短路径
//        vertexAllPaths.shortestPath();
//
//        // 求一对顶点之间的最短路径
        vertexAllPaths.runAllPaths(7, 6);

    }

    @Test
    public void getAllPaths_2() {
        // 对应图:image/vertex-edge-matrix-test.png
        AllPaths allPaths = new AllPaths(8);

        /* 定义节点关系 */
        int nodeRelations[][] =
                {
                        {1, 2},      //0
                        {0, 3, 4},//1
                        {0, 5, 6},    //2
                        {1, 7},    //3
                        {1, 7},  //4
                        {2, 6},     //5
                        {2, 5},     //6
                        {3, 4}     //7
                };

        allPaths.setDEBUG(true);
        allPaths.initGraphAdjacencyList(nodeRelations);

        // 开始搜索所有路径
        allPaths.allPaths(7, 6);

        // 获取所有路径
        allPaths.getAllPathsStr();
        /**
         * 7->3->1->0->2->5->6
         * 7->3->1->0->2->6
         * 7->4->1->0->2->5->6
         * 7->4->1->0->2->6
         *
         */
    }

    @Test
    public void getAllPaths_3() {
        AllPaths allPaths = new AllPaths(8);

        /* 定义节点关系 */
        int nodeRelations[][] =
                {
                        {1, 2},      //0
                        {0, 3, 4},//1
                        {0, 5, 6},    //2
                        {1, 7},    //3
                        {1, 7},  //4
                        {2, 6},     //5
                        {2, 5},     //6
                        {3, 4}     //7
                };

        AdjacencyNode[] node = new AdjacencyNode[8];
        // 定义节点数组
        for (int i = 0; i < nodeRelations.length; i++) {
            node[i] = new AdjacencyNode();
            node[i].setName(String.valueOf(i));
        }
        // 定义与节点相关联的节点集合
        for (int i = 0; i < nodeRelations.length; i++) {
            ArrayList<AdjacencyNode> List = new ArrayList<>();
            for (int j = 0; j < nodeRelations[i].length; j++) {
                List.add(node[nodeRelations[i][j]]);
            }
            node[i].setRelationNodes(List);
        }

        allPaths.setDEBUG(true);
        allPaths.initGraphAdjacencyList(node);

        // 开始搜索所有路径
        allPaths.allPaths(4, 6);

        // 获取所有路径
        ArrayList<Object[]> paths1 = allPaths.getAllPaths();
        ArrayList<String> paths2 = allPaths.getAllPathsStr();
        System.out.println(paths1.size());
        System.out.println(paths2.size());
    }

    /**
     * @param
     * @return
     * @Description: TODO(单子图模式测试)
     */
    @Test
    public void singlePath() {
        AllPaths allPaths = new AllPaths(7);

        /* 定义节点关系 */
        int nodeRelations[][] =
                {
                        {3},      //0
                        {2, 6},//1
                        {1, 3, 4},    //2
                        {0, 2, 5},    //3
                        {2, 6},    //4
                        {3},    //5
                        {1, 4, 6},    //6
                };

        allPaths.setDEBUG(true);
        allPaths.initGraphAdjacencyList(nodeRelations);

        // 开始搜索所有路径
        // 定义两个顶点【不区分方向可达即为路径】【不含回路】
        allPaths.allPaths(0, 3);

        // 获取所有路径
        // 求一对顶点之间的最短路径
        // 获取所有路径
        ArrayList<Object[]> paths1 = allPaths.getAllPaths();
        ArrayList<String> paths2 = allPaths.getAllPathsStr();
        System.out.println(paths1.size());
        paths2.forEach(System.out::println);
    }
}

