package data.lab.ongdb.schema.auto;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.algo.AllPaths;
import data.lab.ongdb.structure.AdjacencyNode;
import data.lab.ongdb.structure.Triple;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO��Two vertex all paths��
 * @date 2021/4/13 14:09
 */
public class AllPathsTest {
    @Test
    public void getAllPaths_1() {
        AllPaths vertexAllPaths = new AllPaths(8);

        // ͼ��image/vertex-edge-matrix-test.png
        // ���붥��
        int[] vertexs = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < vertexs.length; i++) {
            int vertex = vertexs[i];
            System.out.println("VERTEX INDEX NUMBER:" + vertexAllPaths.insertVertex(vertex));
        }

        // �����
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
//        // �����ж���֮������·��
//        vertexAllPaths.shortestPath();
//
//        // ��һ�Զ���֮������·��
        vertexAllPaths.runAllPaths(7, 6);

    }

    @Test
    public void getAllPaths_2() {
        // ��Ӧͼ:image/vertex-edge-matrix-test.png
        AllPaths allPaths = new AllPaths(8);

        /* ����ڵ��ϵ */
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

        // ��ʼ��������·��
        allPaths.allPaths(7, 6);

        // ��ȡ����·��
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

        /* ����ڵ��ϵ */
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
        // ����ڵ�����
        for (int i = 0; i < nodeRelations.length; i++) {
            node[i] = new AdjacencyNode();
            node[i].setName(String.valueOf(i));
        }
        // ������ڵ�������Ľڵ㼯��
        for (int i = 0; i < nodeRelations.length; i++) {
            ArrayList<AdjacencyNode> List = new ArrayList<>();
            for (int j = 0; j < nodeRelations[i].length; j++) {
                List.add(node[nodeRelations[i][j]]);
            }
            node[i].setRelationNodes(List);
        }

        allPaths.setDEBUG(true);
        allPaths.initGraphAdjacencyList(node);

        // ��ʼ��������·��
        allPaths.allPaths(4, 6);

        // ��ȡ����·��
        ArrayList<Object[]> paths1 = allPaths.getAllPaths();
        ArrayList<String> paths2 = allPaths.getAllPathsStr();
        System.out.println(paths1.size());
        System.out.println(paths2.size());
    }

//    @Test
//    public void singlePath() {
//        AllPaths allPaths = new AllPaths(4);
//
//        // ���붥��
//        int[] vertexs = new int[]{1, 2, 3, 4};
//        for (int i = 0; i < vertexs.length; i++) {
//            int vertex = vertexs[i];
//            System.out.println("VERTEX INDEX NUMBER:" + allPaths.insertVertex(vertex));
//        }
//
//        // �����
//        for (int i = 0; i < vertexs.length; i++) {
//            for (int j = 0; j < vertexs.length; j++) {
//                if (
//                        ((i == 1-1 && j == 4-1) ||
//                                (i == 4-1 && j == 3-1)) ||
//                                (i == 3-1 && j == 2-1)
//                ) {
//                    allPaths.insertEdge(new Triple(i, j, 1));
//                } else {
//                    if (i != j) {
//                        allPaths.insertEdge(new Triple(i, j, -1));
//                    }
//                }
//
//            }
//        }
//
//        allPaths.setDEBUG(true);
//        allPaths.initGraphAdjacencyList(new int[][]{vertexs});
//
//        // ��ʼ��������·��
//        allPaths.allPaths(4, 6);
//
//        // ��һ�Զ���֮������·��
//        // ��ȡ����·��
//        ArrayList<Object[]> paths1 = allPaths.getAllPaths();
//        ArrayList<String> paths2 = allPaths.getAllPathsStr();
//        System.out.println(paths1.size());
//        paths2.forEach(System.out::println);
//    }
}

