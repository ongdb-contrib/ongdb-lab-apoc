package data.lab.ongdb.algo;

import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/4/13 14:25
 */
public class FloydShortestPathTest {

    @Test
    public void floyd() {
        int[][] matrix = {
                {0, 5, FloydShortestPath.MAX, 7},
                {FloydShortestPath.MAX, 0, 4, 2},
                {3, 3, 0, 2},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, 1, 0}
        };
        FloydShortestPath.print(matrix);

        FloydShortestPath demo = new FloydShortestPath(4);
        demo.floyd(matrix);
        FloydShortestPath.print(demo.dist);
        FloydShortestPath.print(demo.path);
        demo.findPath(1, 0);
        demo.findPath(2, 0);
    }

    @Test
    public void floyd_2() {
        // vi/vj: 1,2,4,5,8
        int[][] matrix = {
                {FloydShortestPath.MAX, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {1, FloydShortestPath.MAX, 1, 1, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, 1},
                {FloydShortestPath.MAX, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, 1},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, 1, 1, FloydShortestPath.MAX},
        };

        FloydShortestPath.print(matrix);

        FloydShortestPath floydShortestPath = new FloydShortestPath(5);
        floydShortestPath.floyd(matrix);
        FloydShortestPath.print(floydShortestPath.dist);
        FloydShortestPath.print(floydShortestPath.path);
        floydShortestPath.findPath(4, 0);
        floydShortestPath.findPath(2, 0);
        floydShortestPath.findPath(3, 4);
        floydShortestPath.findPath(3, 4);

        floydShortestPath.findPath(1, 4);
        floydShortestPath.findPath(4, 1);
    }

    @Test
    public void floyd_3() {
        // vi/vj: 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19
        int[][] matrix = {
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, 1, 1, 1, 1, 1, 1, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {1, 1, 1, 1, 1, 1, FloydShortestPath.MAX, 1, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, FloydShortestPath.MAX, 1, 1, 1, FloydShortestPath.MAX, 1, 1, 1, FloydShortestPath.MAX, FloydShortestPath.MAX, 1, 1, 1, 1},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
                {FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX, FloydShortestPath.MAX},
        };

        FloydShortestPath.print(matrix);

        FloydShortestPath floydShortestPath = new FloydShortestPath(20);
        floydShortestPath.floyd(matrix);
        FloydShortestPath.print(floydShortestPath.dist);
        FloydShortestPath.print(floydShortestPath.path);

        floydShortestPath.findPath(15, 10);
        floydShortestPath.findPath(12, 10);
    }
}

