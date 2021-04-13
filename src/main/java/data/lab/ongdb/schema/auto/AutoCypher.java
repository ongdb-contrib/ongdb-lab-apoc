package data.lab.ongdb.schema.auto;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.algo.AllPaths;
import data.lab.ongdb.algo.FloydShortestPath;
import data.lab.ongdb.structure.AdjacencyNode;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/4/12 18:43
 */
public class AutoCypher {

    /**
     * DEFAULT PATH WEIGHT
     */
    private final static int DEFAULT_PATH_WEIGHT = 10;

    /**
     * 表示无穷大即不可达
     */
    private final static int MAX_DIST = Integer.MAX_VALUE;

    private final static String START_V_IDX_ID = "startVIdxId";

    private final static String END_V_IDX_ID = "endVIdxId";

    private final static String ID_MAP_TO_VID = "idToVIDXid";
    private final static String VID_MAP_TO_ID = "vIDXidToId";

    /**
     * @param json:入参    #############
     *                   属性过滤器属性之间过滤，连接方式只支持AND
     *                   #############
     *                   属性过滤器(properties_filter)：
     *                   包含【STRING】：‘ name CONTAINS '北京' '
     *                   等于【STRING、INT、LONG】：‘ name='北京' ’  ‘ count=0 ’
     *                   大于【INT、LONG】：‘ count>0 ’
     *                   小于【INT、LONG】：‘ count<0 ’
     *                   大于等于【INT、LONG】：‘ count>=0 ’
     *                   小于等于【INT、LONG】：‘ count>=0 ’
     *                   范围-左闭右闭【INT、LONG】：‘ count>=0 AND count<=10 ’
     *                   范围-左闭右开【INT、LONG】：‘ count>=0 AND count<10 ’
     *                   范围-左开右闭【INT、LONG】：‘ count>0 AND count<=10 ’
     *                   范围-左开右开【INT、LONG】：‘ count>0 AND count<10 ’
     *                   #############
     *                   ES-QUERY
     *                   #############
     *                   ES过滤器(es_filter)：
     *                   ES-QUERY-DSL【去掉JSON引号的查询语句】eg.{size:1,query:{term:{product_code:"PF0020020104"}}}
     * @param skip:翻页参数
     * @param limit:限制参数
     * @return
     * @Description: TODO
     * <p>
     * 使用前先安装APOC用过程‘apoc.custom.asFunction’生成函数‘custom.es.result.bool’【简化ES访问语句】
     * ```
     * RETURN custom.es.result.bool({es-url},{index-name},{query-dsl}) AS boolean
     * RETURN custom.es.result.bool('10.20.13.130:9200','dl_default_indicator_def',{size:1,query:{term:{product_code:"PF0020020104"}}}) AS boolean
     * ```
     * ```
     * CALL apoc.es.query('10.20.13.130:9200','dl_default_indicator_def','',null,{size:1,query:{term:{product_code:"PF0020020104"}}}) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,'RETURN TRUE AS countBool'],'RETURN FALSE AS countBool') YIELD value RETURN value.countBool AS bool
     * ```
     * ```
     * CALL apoc.custom.asFunction(
     * 'es.result.bool',
     * 'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,\'RETURN TRUE AS countBool\'],\'RETURN FALSE AS countBool\') YIELD value RETURN value.countBool AS bool',
     * 'BOOLEAN',
     * [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
     * false,
     * '通过判断ES查询结果返回FALSE或者TRUE【结果集大于0返回TRUE】'
     * );
     * ```
     */
    @UserFunction(name = "olab.schema.auto.cypher")
    @Description("```\n" +
            "RETURN olab.schema.auto.cypher({JSON},{SKIP},{LIMIT}) AS cypher\n" +
            "```\n" +
            "```\n" +
            "输入：\n" +
            "    JSON参数\n" +
            "       【{包含节点、关系、属性过滤器、ES过滤器}、{SKIP参数}、{LIMIT参数(-1表示返回全部)}】\n" +
            "    过滤器设计：传入查询碎片直接拼接查询碎片\n" +
            "输出：拼接好的CYPHER语句\n" +
            "```")
    public String cypher(@Name("json") String json, @Name("skip") long skip, @Name("limit") long limit) {

        JSONObject paras = JSONObject.parseObject(json);
        JSONArray nodes = paras.getJSONObject("graph").getJSONArray("nodes");
        JSONArray relationships = paras.getJSONObject("graph").getJSONArray("relationships");

        /*
         * 转换图结构为矩阵寻找所有子图路径
         * */
        // 生成虚拟节点ID-使用INDEX替换
        HashMap<String, HashMap<Long, Long>> idMap = transferNodeIndex(nodes);
        HashMap<Long, Long> nodeIndex = idMap.get(ID_MAP_TO_VID);
        HashMap<Long, Long> indexNode = idMap.get(VID_MAP_TO_ID);

        // 虚拟节点数据增加到关系数据-使用INDEX替换关系中节点ID
        JSONArray transferRelations = transferRelations(nodeIndex, relationships);

        // 分析路径
        // 初始化矩阵
        int initVertex = nodeIndex.size();
        int[][] relationsMatrix = initRelationsMatrix(transferRelations, initVertex);
        AdjacencyNode[] adjacencyNodes = initAdjacencyMatrix(relationsMatrix);

        AllPaths allPaths = new AllPaths(relationsMatrix.length);
        allPaths.initGraphAdjacencyList(adjacencyNodes);

        // 开始搜索所有路径
        // 两两之间的所有短路径寻找(JUST ONE SHORTEST PATH)
        List<String> graphPaths = new ArrayList<>();
        ArrayList<Long> analysisNodeIds = nodes.stream().map(v -> {
            JSONObject obj = (JSONObject) v;
            return obj.getLongValue("id");
        }).collect(Collectors.toCollection(ArrayList::new));
        for (long i = 0; i < analysisNodeIds.size(); i++) {
            for (long j = i + 1; j < analysisNodeIds.size(); j++) {
                long startIndex = nodeIndex.get(analysisNodeIds.get(Math.toIntExact(i)));
                long endIndex = nodeIndex.get(analysisNodeIds.get(Math.toIntExact(j)));
                // BEGIN SEARCH SUB-GRAPH ALL PATHS
                if (startIndex != endIndex) {
                    allPaths.allPaths(Math.toIntExact(startIndex), Math.toIntExact(endIndex));
                    graphPaths.addAll(allPaths.getAllPathsStr());
                }
            }
        }
        // 获取所有路径
        System.out.println(graphPaths.size());
        return null;
    }

    /**
     * @param relationships:本次分析的关系图路径
     * @param initMatrixNum:初始化矩阵大小
     * @return
     * @Description: TODO(初始化矩阵图 - 默认路径权重)
     */
    private static int[][] initRelationsMatrix(JSONArray relationships, int initMatrixNum) {
        int[][] matrix = new int[initMatrixNum][initMatrixNum];
        for (int i = 0; i < relationships.size(); i++) {
            JSONObject object = relationships.getJSONObject(i);
            int startNodeIdIndex = object.getIntValue(START_V_IDX_ID);
            int endNodeIdIndex = object.getIntValue(END_V_IDX_ID);
            // 使用一般默认权重
            matrix[startNodeIdIndex][endNodeIdIndex] = DEFAULT_PATH_WEIGHT;
            matrix[endNodeIdIndex][startNodeIdIndex] = DEFAULT_PATH_WEIGHT;
        }
        for (int i = 0; i < initMatrixNum; i++) {
            for (int j = 0; j < initMatrixNum; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = FloydShortestPath.MAX;
                }
            }
        }
        return matrix;
    }

    private static AdjacencyNode[] initAdjacencyMatrix(int[][] relationsMatrix) {
        AdjacencyNode[] node = new AdjacencyNode[relationsMatrix.length];
        // 定义节点数组
        for (int i = 0; i < relationsMatrix.length; i++) {
            node[i] = new AdjacencyNode();
            node[i].setName(String.valueOf(i));
        }
        // 定义与节点相关联的节点集合
        for (int i = 0; i < relationsMatrix.length; i++) {
            ArrayList<AdjacencyNode> list = new ArrayList<>();
            for (int j = 0; j < relationsMatrix[i].length; j++) {
                int value = relationsMatrix[i][j];
                if (value != -1 && value != MAX_DIST) {
                    list.add(node[j]);
                }
            }
            node[i].setRelationNodes(list);
        }
        return node;
    }

    /**
     * @param
     * @return map的KEY是节点的ID，value对应节点的索引位
     * @Description: TODO(节点集合使用索引索引位来替换)
     */
    private static HashMap<String, HashMap<Long, Long>> transferNodeIndex(JSONArray nodes) {
        HashMap<String, HashMap<Long, Long>> idsMap = new HashMap<>();

        HashMap<Long, Long> nodeIndex = new HashMap<>();
        HashMap<Long, Long> indexNode = new HashMap<>();

        // NODE ID升序排序
        nodes = nodes.parallelStream().sorted((v1, v2) -> {
            JSONObject object1 = (JSONObject) v1;
            JSONObject object2 = (JSONObject) v2;
            return object1.getInteger("id") - object2.getInteger("id");
        }).collect(Collectors.toCollection(JSONArray::new));

        for (long i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(Math.toIntExact(i));
            nodeIndex.put(node.getLongValue("id"), i);
            indexNode.put(i, node.getLongValue("id"));
        }
        idsMap.put(ID_MAP_TO_VID, nodeIndex);
        idsMap.put(VID_MAP_TO_ID, indexNode);
        return idsMap;
    }

    private static JSONArray transferRelations(HashMap<Long, Long> nodeIndex, JSONArray relationships) {
        return relationships.parallelStream()
                .map(v -> {
                    JSONObject edge = (JSONObject) v;
                    edge.put(START_V_IDX_ID, nodeIndex.get(edge.getLongValue("startNode")));
                    edge.put(END_V_IDX_ID, nodeIndex.get(edge.getLongValue("endNode")));
                    return edge;
                })
                .collect(Collectors.toCollection(JSONArray::new));
    }
}

