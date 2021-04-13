package data.lab.ongdb.algo;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.structure.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO（Two vertex all paths）
 * @date 2021/4/13 11:24
 */
public class AllPaths extends AdjListGraph {

    /**
     * 临时保存路径节点的栈
     */
    private final Stack<AdjacencyNode> stack = new Stack<>();

    /**
     * 存储路径的集合
     */
    private final ArrayList<Object[]> allPaths = new ArrayList<>();

    /**
     * 存储路径的集合
     */
    private final ArrayList<String> allPathsStr = new ArrayList<>();

    private AdjacencyNode[] node;

    public AllPaths(int vertexNum) {
        super(vertexNum);
        this.node = new AdjacencyNode[vertexNum];
    }

    public ArrayList<Object[]> getAllPaths() {
        return allPaths;
    }

    public ArrayList<String> getAllPathsStr() {
        return allPathsStr;
    }

    /**
     * @param
     * @return
     * @Description: TODO(初始化图邻接表)
     */
    public void initGraphAdjacencyList(int[][] nodeRelations) {
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
        if (super.isDEBUG()) {
            printInitGraphAdjacencyList();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(初始化图邻接表)
     */
    public void initGraphAdjacencyList(AdjacencyNode[] nodes) {
        this.node = nodes;
        if (super.isDEBUG()) {
            printInitGraphAdjacencyList();
        }
    }

    private void printInitGraphAdjacencyList() {
        for (AdjacencyNode nd : node) {
            System.out.print("Node-Index:" + nd.getName() + " AdjacencyList:");
            ArrayList<AdjacencyNode> nodeArrayList = nd.getRelationNodes();
            System.out.print("[ ");
            for (int j = 0; j < nodeArrayList.size(); j++) {
                AdjacencyNode nd2 = nodeArrayList.get(j);
                System.out.print(nd2.getName() + " ");
            }
            System.out.println("]");
        }
    }

    /**
     * 判断节点是否在栈中
     */
    public boolean isNodeInStack(AdjacencyNode node) {
        Iterator<AdjacencyNode> it = stack.iterator();
        while (it.hasNext()) {
            AdjacencyNode node1 = it.next();
            if (node == node1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 此时栈中的节点组成一条所求路径，转储并打印输出
     */
    public void showAndSavePath() {
        StringBuilder builder = new StringBuilder();
        Object[] o = stack.toArray();
        for (int i = 0; i < o.length; i++) {
            AdjacencyNode nNode = (AdjacencyNode) o[i];

            if (i < (o.length - 1)) {
                if (super.isDEBUG()) {
                    System.out.print(nNode.getName() + "->");
                }
                builder.append(nNode.getName() + "->");
            } else {
                if (super.isDEBUG()) {
                    System.out.print(nNode.getName());
                }
                builder.append(nNode.getName());
            }
        }
        // 存储路径
        allPaths.add(o);
        allPathsStr.add(builder.toString());
        if (super.isDEBUG()) {
            System.out.println("\n");
        }
    }

    public void allPaths(int startNodeIndex, int endNodeIndex) {
        AdjacencyNode startNode = getNode(startNodeIndex);
        AdjacencyNode endNode = getNode(endNodeIndex);
        recursionAllPaths(startNode, null, startNode, endNode);
    }

    private AdjacencyNode getNode(int index) {
        for (int i = 0; i < node.length; i++) {
            if (i == index) {
                return node[i];
            }
        }
        return null;
    }

    /**
     * @param cNode:当前的起始节点currentNode
     * @param pNode:当前起始节点的上一节点previousNode
     * @param sNode:最初的起始节点startNode
     * @param eNode:终点endNode
     * @return
     * @Description: TODO(寻找路径的方法)
     */
    private boolean recursionAllPaths(AdjacencyNode cNode, AdjacencyNode pNode, AdjacencyNode sNode, AdjacencyNode eNode) {
        /**
         * 1.整理节点间的关系，为每个节点建立邻接表，该集合中保存所有与该节点直接相连的节点（不包括该节点自身）；
         * 2.定义两点一个为起始节点，另一个为终点，求解两者之间的所有路径的问题可以被分解为如下所述的子问题：
         *   对每一个与起始节点直接相连的节点，求解它到终点的所有路径（路径上不包括起始节点）得到一个路径集合，将这些路径集合相加就可以得到起始节点到终点的所有路径；
         *   依次类推就可以应用递归的思想，层层递归直到终点，若发现希望得到的一条路径，则转储并打印输出；
         *   若发现环路，或发现死路，则停止寻路并返回；  
         * 3.用栈保存当前已经寻到的路径（不是完整路径）上的节点，在每一次寻到完整路径时弹出栈顶节点；
         *   而在遇到从栈顶节点无法继续向下寻路时也弹出该栈顶节点，从而实现回溯。
         *
         * */
        AdjacencyNode nNode;
        /* 如果符合条件判断说明出现环路，不能再顺着该路径继续寻路，返回false */
        if (cNode != null && pNode != null && cNode == pNode) {
            return false;
        }

        if (cNode != null) {
            int i = 0;
            /* 起始节点入栈 */
            stack.push(cNode);
            /* 如果该起始节点就是终点，说明找到一条路径 */
            if (cNode == eNode) {
                /* 转储并打印输出该路径，返回true */
                showAndSavePath();
                return true;
            }
            /* 如果不是,继续寻路 */
            else {
                /*
                 * 从与当前起始节点cNode有连接关系的节点集中按顺序遍历得到一个节点
                 * 作为下一次递归寻路时的起始节点
                 */
                nNode = cNode.getRelationNodes().get(i);
                while (nNode != null) {
                    /*
                     * 如果nNode是最初的起始节点或者nNode就是cNode的上一节点或者nNode已经在栈中 ，
                     * 说明产生环路 ，应重新在与当前起始节点有连接关系的节点集中寻找nNode
                     */
                    if (pNode != null
                            && (nNode == sNode || nNode == pNode || isNodeInStack(nNode))) {
                        i++;
                        if (i >= cNode.getRelationNodes().size()) {
                            nNode = null;
                        } else {
                            nNode = cNode.getRelationNodes().get(i);
                        }
                        continue;
                    }
                    /* 以nNode为新的起始节点，当前起始节点cNode为上一节点，递归调用寻路方法 */
                    if (recursionAllPaths(nNode, cNode, sNode, eNode))
                        /* 递归调用 */ {
                        /* 如果找到一条路径，则弹出栈顶节点 */
                        stack.pop();
                    }
                    /* 继续在与cNode有连接关系的节点集中测试nNode */
                    i++;
                    if (i >= cNode.getRelationNodes().size()) {
                        nNode = null;
                    } else {
                        nNode = cNode.getRelationNodes().get(i);
                    }
                }
                /*
                 * 当遍历完所有与cNode有连接关系的节点后，
                 * 说明在以cNode为起始节点到终点的路径已经全部找到
                 */
                stack.pop();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param startNode:开始节点
     * @param endNode:结束节点
     * @return
     * @Description: TODO(求一对顶点之间的所有路径)
     */
    public void runAllPaths(int startNode, int endNode) {

        System.out.println(super.adjlist.toString());

        /**
         * 测试图例image\vertex-edge-matrix-test.png
         * 顶点对应的索引值：
         * 1 VERTEX INDEX NUMBER:0
         * 2 VERTEX INDEX NUMBER:1
         * 3 VERTEX INDEX NUMBER:2
         * 4 VERTEX INDEX NUMBER:3
         * 5 VERTEX INDEX NUMBER:4
         * 6 VERTEX INDEX NUMBER:5
         * 7 VERTEX INDEX NUMBER:6
         * 8 VERTEX INDEX NUMBER:7
         * 邻接表
         * 0 -> ((0,1,1),(0,2,1))
         * 1 -> ((1,0,1),(1,3,1),(1,4,1))
         * 2 -> ((2,0,1),(2,5,1),(2,6,1))
         * 3 -> ((3,1,1),(3,7,1))
         * 4 -> ((4,1,1),(4,7,1))
         * 5 -> ((5,2,1))
         * 6 -> ((6,2,1))
         * 7 -> ((7,3,1),(7,4,1))
         * 8 -> ()
         * 9 -> ()
         * **/

        /**
         * （例如寻找(1)-[]-(8)的所有路径）：图初始化时索引值对应（(0)-[]-(7)）
         *       将起点0入栈，并将起点0标记为入栈状态
         * repeat:A、从起点0开始寻找，找到结点0的第一个非入栈状态的邻结点1，将结点1标记为入栈状态；
         *        B、从起点1开始寻找，找到结点1的第一个非入栈状态的邻结点3，将结点3标记为入栈状态；
         *        C、从起点3开始寻找，找到结点3的第一个非入栈状态的邻结点7，将结点7标记为入栈状态；
         *        D、栈顶结点7是终点，那么我们就找到了一条起点到终点的路径，输出这条路径；
         *        E、从栈顶弹出结点7，将7标记为非入栈状态；
         *        F、现在栈顶结点为3， 结点3没有除终点外的非入栈状态的结点，所以从栈顶将结点3弹出；
         *        G、现在栈顶结点为1，结点1除了刚出栈的结点3之外，还有非入栈状态的结点4，那么我们将结点4入栈；
         *        H、现在栈顶为结点4，找到结点4的第一个非入栈状态的邻结点7，将结点7标记为入栈状态，即找到了第二条路径，输出整个栈，即为第二条路径；
         * until:重复步骤A-H，就可以找到从起点3 到终点6 的所有路径,直到栈为空，算法结束。
         * **/
        // 保存路径的栈
        Stack<Integer> path = new Stack<>();

        // 保存已标记结点的数组 0表示未访问，1表示已访问
        int[] visit = new int[super.vertexCount()];

        path.push(startNode);
        visit[startNode] = 1;

        // 邻接表
        SeqList<SortedSinglyList<Triple>> rowList = super.adjlist.getRowlist();

        // 保存所有路径的列表
        List<Stack> allPaths = new ArrayList<>();

        while (!path.isEmpty()) {

            // 栈顶元素
            int topStackVertex = path.peek();


            nextPathNode(rowList, path, visit, topStackVertex);

            // 输出路径
            if (path.peek() == endNode) {
                Stack<Integer> savePath = new Stack<>();
                savePath.addAll(path);
                allPaths.add(savePath);

                // 弹出栈顶结点
                int top = path.pop();
                visit[top] = 0;
            }

            int topStackNextVertex = path.peek();
            // 获取不到除终点外的非入栈状态的结点，弹出
            SortedSinglyList<Triple> sortedNextSinglyList = rowList.get(topStackNextVertex);
            int mark = 0;
            for (int i = 0; i < sortedNextSinglyList.size(); i++) {
                Triple triple = sortedNextSinglyList.get(i);
                int column = triple.getColumn();
                if (visit[column] == 0 && column != endNode) {
                    path.push(column);
                    visit[column] = 1;
                    mark = 1;
                    break;
                }
            }
            if (mark == 0) {
                int popNode = path.pop();
                // 路径中包含了和它平行的点（路径可替代点）
//                if (pathContainsParallelNode(popNode, allPaths)) {
//                    visit[popNode] = 0;
//                }
                if (!path.isEmpty()) {
                    nextPathNode(rowList, path, visit, path.peek());
                }
            }
        }

        for (int i = 0; i < allPaths.size(); i++) {
            Stack stack = allPaths.get(i);
            System.out.println(stack.toString());
        }

    }

    /**
     * @param rowList:邻接表
     * @param path:路径栈
     * @param visit:是否访问标记
     * @param topStackVertex:获取哪个顶点的邻接表
     * @return
     * @Description: TODO(生成路径)
     */
    private Stack<Integer> nextPathNode(SeqList<SortedSinglyList<Triple>> rowList, Stack<Integer> path, int[] visit, int topStackVertex) {
        // 获取当前顶点的邻接表
        SortedSinglyList<Triple> sortedSinglyList = rowList.get(topStackVertex);

        // 获取未访问的下一个结点
        for (int i = 0; i < sortedSinglyList.size(); i++) {
            Triple triple = sortedSinglyList.get(i);
            int column = triple.getColumn();
            if (visit[column] == 0) {
                path.push(column);
                // 标记为入栈状态
                visit[column] = 1;
                break;
            }
        }
        return path;
    }
}

