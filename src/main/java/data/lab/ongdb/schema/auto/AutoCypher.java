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
import data.lab.ongdb.result.*;
import data.lab.ongdb.structure.AdjacencyNode;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.procedure.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/4/12 18:43
 */
public class AutoCypher {

    private static AtomicLong MIN_ID = new AtomicLong(-1);

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

    private final static String GRAPH_DATA_FIELD = "graph";
    private final static String GRAPH_DATA_NODES_FIELD = "nodes";
    private final static String GRAPH_DATA_RELATIONSHIPS_FIELD = "relationships";
    private final static String ID = "id";
    private final static String PATH_REL_JOINT = "->";
    private final static String CYPHER_JOINT = "UNION ALL";

    /**
     * @param json: "{"graph":{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}}"
     *              "{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}"
     * @return
     * @Description: TODO(图中是否包含环路)
     */
    @UserFunction(name = "olab.schema.is.loop")
    @Description("RETURN olab.schema.is.loop({JSON}) AS isLoopGraph")
    public boolean isLoopGraph(@Name("json") String json) {
        if (Objects.isNull(json) || "".equals(json)) {
            return false;
        }
        JSONObject paras = JSONObject.parseObject(json);
        if (!paras.containsKey(GRAPH_DATA_FIELD) && paras.containsKey(GRAPH_DATA_NODES_FIELD) && paras.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            JSONObject paraObj = new JSONObject();
            paraObj.put(GRAPH_DATA_FIELD, paras);
            paras = paraObj;
        } else if (!paras.containsKey(GRAPH_DATA_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_FIELD + " field!");
        }
        JSONObject graphData = paras.getJSONObject(GRAPH_DATA_FIELD);

        if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD) || !graphData.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " or " + GRAPH_DATA_RELATIONSHIPS_FIELD + " field!");
        }
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        if (nodes.size() < 3) {
            throw new IllegalArgumentException("Two vertex loop analysis is not supported!");
        }
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);

        /*
         * 过滤出顶点集合
         * */
        ArrayList<Long> analysisNodeIds = nodes.stream().map(v -> {
            JSONObject obj = (JSONObject) v;
            return obj.getLongValue(ID);
        }).collect(Collectors.toCollection(ArrayList::new));
        /*
         * 转换图结构为矩阵寻找所有子图路径：
         * */
        // 生成虚拟节点ID-使用INDEX替换
        HashMap<String, HashMap<Long, Long>> idMap = transferNodeIndex(nodes);
        HashMap<Long, Long> nodeIndex = idMap.get(ID_MAP_TO_VID);

        /*
         * 当前图的所有路径集合
         * */
        List<String> graphPaths = getGraphPaths(analysisNodeIds, relationships, nodeIndex);
        /*
         * 顶点之间的所有路径集中寻找环路
         * */
        return loopGraphParse(graphPaths.parallelStream().distinct().collect(Collectors.toList()));
    }

    /**
     * @param json: "{"graph":{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}}"
     *              "{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}"
     * @return
     * @Description: TODO(图中环路序列列表)
     */
    @Procedure(name = "olab.schema.loop", mode = Mode.READ)
    @Description("CALL olab.schema.loop(graphData) YIELD loopResultList RETURN loopResultList -- 输出环路节点序列")
    public Stream<LoopListResult> loopGraphNodeSeqIds(@Name("json") String json) {
        if (Objects.isNull(json) || "".equals(json)) {
            return null;
        }
        JSONObject paras = JSONObject.parseObject(json);
        if (!paras.containsKey(GRAPH_DATA_FIELD) && paras.containsKey(GRAPH_DATA_NODES_FIELD) && paras.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            JSONObject paraObj = new JSONObject();
            paraObj.put(GRAPH_DATA_FIELD, paras);
            paras = paraObj;
        } else if (!paras.containsKey(GRAPH_DATA_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_FIELD + " field!");
        }
        JSONObject graphData = paras.getJSONObject(GRAPH_DATA_FIELD);

        if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD) || !graphData.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " or " + GRAPH_DATA_RELATIONSHIPS_FIELD + " field!");
        }
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        if (nodes.size() < 3) {
            throw new IllegalArgumentException("Two vertex loop analysis is not supported!");
        }
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);

        /*
         * 过滤出顶点集合
         * */
        ArrayList<Long> analysisNodeIds = nodes.stream().map(v -> {
            JSONObject obj = (JSONObject) v;
            return obj.getLongValue(ID);
        }).collect(Collectors.toCollection(ArrayList::new));

        /*
         * 转换图结构为矩阵寻找所有子图路径：
         * */
        // 生成虚拟节点ID-使用INDEX替换
        HashMap<String, HashMap<Long, Long>> idMap = transferNodeIndex(nodes);
        HashMap<Long, Long> nodeIndex = idMap.get(ID_MAP_TO_VID);
        HashMap<Long, Long> indexNode = idMap.get(VID_MAP_TO_ID);

        /*
         * 当前图的所有路径集合
         * */
        List<String> graphPaths = getGraphPaths(analysisNodeIds, relationships, nodeIndex);
        /*
         * 顶点之间的所有路径集中寻找环路
         * */
        return Stream.of(new LoopListResult(loopGraphParseNodeSeqIds(graphPaths.parallelStream().distinct().collect(Collectors.toList()), indexNode)));
    }

    /**
     * @param json: "{"graph":{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}}"
     *              "{"nodes":[{"id":"-1024"},{"id":"-70549398"}],"relationships":[{"startNode":"-1024","endNode":"-70549398"}]}"
     * @return
     * @Description: TODO(图中环路序列列表)
     */
    @Procedure(name = "olab.schema.all.path", mode = Mode.READ)
    @Description("CALL olab.schema.all.path(graphData) YIELD loopResultList RETURN loopResultList -- 输出路径节点序列")
    public Stream<LoopListResult> allPathSeqNodeIds(@Name("json") String json) {
        if (Objects.isNull(json) || "".equals(json)) {
            return null;
        }
        JSONObject paras = JSONObject.parseObject(json);
        if (!paras.containsKey(GRAPH_DATA_FIELD) && paras.containsKey(GRAPH_DATA_NODES_FIELD) && paras.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            JSONObject paraObj = new JSONObject();
            paraObj.put(GRAPH_DATA_FIELD, paras);
            paras = paraObj;
        } else if (!paras.containsKey(GRAPH_DATA_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_FIELD + " field!");
        }
        JSONObject graphData = paras.getJSONObject(GRAPH_DATA_FIELD);

        if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD) || !graphData.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " or " + GRAPH_DATA_RELATIONSHIPS_FIELD + " field!");
        }
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        if (nodes.size() < 3) {
            throw new IllegalArgumentException("Two vertex loop analysis is not supported!");
        }
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);

        /*
         * 过滤出顶点集合
         * */
        ArrayList<Long> analysisNodeIds = nodes.stream().map(v -> {
            JSONObject obj = (JSONObject) v;
            return obj.getLongValue(ID);
        }).collect(Collectors.toCollection(ArrayList::new));

        /*
         * 转换图结构为矩阵寻找所有子图路径：
         * */
        // 生成虚拟节点ID-使用INDEX替换
        HashMap<String, HashMap<Long, Long>> idMap = transferNodeIndex(nodes);
        HashMap<Long, Long> nodeIndex = idMap.get(ID_MAP_TO_VID);
        HashMap<Long, Long> indexNode = idMap.get(VID_MAP_TO_ID);

        /*
         * 当前图的所有路径集合
         * */
        List<String> graphPaths = getGraphPaths(analysisNodeIds, relationships, nodeIndex);
        /*
         * 顶点之间的所有路径集中寻找环路
         * */
        return Stream.of(new LoopListResult(replaceIndexId(graphPaths.parallelStream().distinct().collect(Collectors.toList()), indexNode)));
    }

    /**
     * @param graphPaths:STRING类型路径列表
     * @param indexNode:索引与节点的ID对应关系
     * @return
     * @Description: TODO(拿到路径列表 - 并将索引ID替换为节点ID)
     */
    private List<LoopResult> replaceIndexId(List<String> graphPaths, HashMap<Long, Long> indexNode) {
        /*
         * 拿到路径列表-并将索引ID替换为节点ID
         * */
        return graphPaths
                .parallelStream()
                .map(v -> new LoopResult(v, indexNode))
                .collect(Collectors.toList());
    }

    /**
     * @param analysisNodeIds:两两定点求全路径的顶点列表
     * @param relationships:关系集
     * @return
     * @Description: TODO(获取路径串)
     */
    private List<String> getGraphPaths(ArrayList<Long> analysisNodeIds, JSONArray relationships, HashMap<Long, Long> nodeIndex) {

        // 虚拟节点数据增加到关系数据-使用INDEX替换关系中节点ID
        JSONArray transferRelations = transferRelations(nodeIndex, relationships);

        // 分析路径
        // 初始化矩阵
        int initVertex = nodeIndex.size();
        int[][] relationsMatrix = initRelationsMatrix(transferRelations, initVertex);
        // 定义节点的邻接表
        AdjacencyNode[] adjacencyNodes = initAdjacencyMatrix(relationsMatrix);

        // 初始化图邻接表
        AllPaths allPaths = new AllPaths(relationsMatrix.length);
        allPaths.initGraphAdjacencyList(adjacencyNodes);

        // 开始搜索所有路径
        // 顶点之间的所有路径寻找
        List<String> graphPaths = new ArrayList<>();
        for (long i = 0; i < analysisNodeIds.size(); i++) {
            for (long j = i + 1; j < analysisNodeIds.size(); j++) {
                long startIndex = nodeIndex.get(analysisNodeIds.get(Math.toIntExact(i)));
                long endIndex = nodeIndex.get(analysisNodeIds.get(Math.toIntExact(j)));
                // BEGIN SEARCH SUB-GRAPH ALL PATHS
                if (startIndex != endIndex) {
                    /*
                     * 正向搜索
                     * */
                    allPaths.allPaths(Math.toIntExact(startIndex), Math.toIntExact(endIndex));
                    graphPaths.addAll(allPaths.getAllPathsStr());
                    /*
                     * 清空正向搜索的结果集
                     * */
                    allPaths.clear();
                    /*
                     * 反向搜索
                     * */
                    allPaths.allPaths(Math.toIntExact(endIndex), Math.toIntExact(startIndex));
                    graphPaths.addAll(allPaths.getAllPathsStr());
                }
            }
        }
        return graphPaths;
    }

    /**
     * @param graphPaths:路径串列表
     * @param indexNode:索引值与节点ID的对应关系
     * @return
     * @Description: TODO(解析路径串并找到环路的节点序列)
     */
    private List<LoopResult> loopGraphParseNodeSeqIds(List<String> graphPaths, HashMap<Long, Long> indexNode) {
        /*
         * 路径ID序列结果列表
         * */
        List<LoopResult> loopResultList = new ArrayList<>();
        /*
         * 获取一层路径
         * */
        List<String> graphOneHicPaths = graphPaths.stream()
                .filter(this::countPathStr).distinct().collect(Collectors.toList());
        Map<String, List<String>> graphPathsCountMap = new HashMap<>();
        for (String oneHicPathStr : graphOneHicPaths) {
            graphPathsCountMap.put(oneHicPathStr, new ArrayList<>());
        }

        /*
         * 过滤一层路径之后的结果
         * */
        List<String> graphFilterOneHicPaths = graphPaths.stream()
                .filter(v -> !countPathStr(v)).distinct().collect(Collectors.toList());

        /*
         * 找到和一层路径构成环路的路径
         * */
        for (String pathStr : graphFilterOneHicPaths) {
            char[] chars = pathStr.toCharArray();
            int charSize = chars.length;
            char start = chars[0];
            char end = chars[charSize - 1];
            String cutPathStr = start + PATH_REL_JOINT + end;
            if (graphPathsCountMap.containsKey(cutPathStr)) {
                List<String> list = graphPathsCountMap.get(cutPathStr);
                String loopPathStr = pathStr + PATH_REL_JOINT + start;
                if (!list.contains(loopPathStr)) {
                    list.add(loopPathStr);
                }
            }
        }
        /*
         * 拿出环路列表-并将索引ID替换为节点ID
         * */
        for (String key : graphPathsCountMap.keySet()) {
            loopResultList.addAll(
                    graphPathsCountMap.get(key)
                            .parallelStream()
                            .map(v -> new LoopResult(v, indexNode))
                            .collect(Collectors.toList())
            );
        }
        return loopResultList;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断包含特定字符串的个数 ： 路径串 ： ‘ 7 - > 2 - > 8 - > 3 - > 5 - > 4 - > 6 - > 1 ’ ， 判断 ’ - > ‘ 层数)
     */
    protected boolean countPathStr(String res) {
        String hic = PATH_REL_JOINT;
        int count = 0;
        while (res.contains(hic)) {
            res = res.substring(res.indexOf(hic) + 2);
            ++count;
            if (count > 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param graphPaths:路径串列表
     * @return
     * @Description: TODO(解析路径串判断是否包含环路)
     */
    private boolean loopGraphParse(List<String> graphPaths) {
        int init = graphPaths.size();
        List<String> cutPaths = new ArrayList<>();
        for (String path : graphPaths) {
            char[] chars = path.toCharArray();
            int charSize = chars.length;
            char start = chars[0];
            char end = chars[charSize - 1];
            cutPaths.add(String.valueOf(start) + end);
        }
        List<String> cutDisPaths = cutPaths.parallelStream().distinct().collect(Collectors.toList());
        int cutDisPathsSize = cutDisPaths.size();
        return init != cutDisPathsSize;
    }

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
    public String cypher(@Name("json") String json, @Name(value = "limit", defaultValue = "100") long limit) {

        /*
         * 是否只包含节点：
         *   是：封装节点的查询【多个节点返回UNION ALL语句】
         *   否：是否既包含孤立节点也包含路径：
         *       是：返回报错信息
         *       否：所有节点都有路径则按照路径逻辑生成查询
         *           生成路径查询：
         *                   1.找到单路径节点作为顶点；
         *                   2、获取不重叠路径；
         *                   3、最长路径放在CYPHER第一层，由此类推；
         *                   4、输出按照节点的索引序号【可选是否获取对应指标】
         * */
        /*
         * v1.0实现CYPHER的自动生成
         * */
        if (Objects.isNull(json) || "".equals(json)) {
            return null;
        }
        JSONObject paras = JSONObject.parseObject(json);
        if (!paras.containsKey(GRAPH_DATA_FIELD) && paras.containsKey(GRAPH_DATA_NODES_FIELD) && paras.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            JSONObject paraObj = new JSONObject();
            paraObj.put(GRAPH_DATA_FIELD, paras);
            paras = paraObj;
        } else if (!paras.containsKey(GRAPH_DATA_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_FIELD + " field!");
        }
        JSONObject graphData = paras.getJSONObject(GRAPH_DATA_FIELD);

        if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD) || !graphData.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
            throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " or " + GRAPH_DATA_RELATIONSHIPS_FIELD + " field!");
        }
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);
        if (relationships == null || relationships.isEmpty()) {
            // 单节点拼接
            return cypherAppendJustNodes(nodes, limit);
        } else {
            return cypherAppendNotJustNodes();
        }
    }

    private String cypherAppendNotJustNodes() {
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(图模型只有节点)
     */
    private String cypherAppendJustNodes(JSONArray nodes, long limit) {
        StringBuilder cypherBuilder = new StringBuilder();
        long calLimit = calLimit(limit, nodes.size());
        for (Object obj : nodes) {
            JSONObject nObj = (JSONObject) obj;
            String cypher = nodeCypher(nObj, calLimit);
            cypherBuilder.append(cypher);
            cypherBuilder.append(" \n");
            cypherBuilder.append(CYPHER_JOINT);
            cypherBuilder.append(" \n");
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(确定限制参数)
     */
    private long calLimit(long limit, int size) {
        if (limit < size) {
            return 1;
        } else {
            return (long) Math.ceil(limit / size);
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点过滤CYPHER生成)
     */
    private String nodeCypher(JSONObject nodeObject, long limit) {
        String templateProFilter = "MATCH (n:{Label}) WHERE {proFilter} RETURN n LIMIT " + limit;
        String templateEsFilter = "MATCH (n:{Label}) WHERE {esFilter} RETURN n LIMIT " + limit;
        String templateProEsFilter = "MATCH (n:{Label}) WHERE {proEsFilter} RETURN n LIMIT " + limit;
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

    /**
     * @param ids:节点序列-环路节点序列
     * @return
     * @Description: TODO(返回一个查询环路的CYPHER语句)
     */
    @UserFunction(name = "olab.schema.loop.cypher")
    @Description("RETURN olab.schema.loop.cypher(ids) AS cypher")
    public String schemaLoopCypher(@Name("ids") List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH path=");
        int size = ids.size();
        for (int i = 0; i < size; i++) {
            int nodeNum = i + 1;
            if (i == size - 1) {
                builder.append("(n").append(nodeNum).append(")");
            } else {
                builder.append("(n").append(nodeNum).append(")--");
            }
        }
        builder.append(" WHERE ");
        for (int i = 0; i < size; i++) {
            int nodeNum = i + 1;
            if (i == size - 1) {
                builder.append("ID(n").append(nodeNum).append(")=").append(ids.get(i));
            } else {
                builder.append("ID(n").append(nodeNum).append(")=").append(ids.get(i));
                builder.append(" AND ");
            }
        }
        builder.append(" RETURN path");
        return builder.toString();
    }

    /**
     * @return
     * @Description: TODO(返回一个原子性ID)
     */
    @UserFunction(name = "olab.schema.atomic.id")
    @Description("RETURN olab.schema.atomic.id() AS atomicId")
    public Long atomicId() {
        return MIN_ID.getAndDecrement();
    }

    /**
     * @param relationship:关系
     * @param idsSeqLoopGraph:路径序列ID
     * @param atomicId:原子性ID
     * @return
     * @Description: TODO(生成虚拟图)
     */
    @Procedure(name = "olab.schema.loop.vpath", mode = Mode.READ)
    @Description("CALL olab.schema.loop.vpath(path) YIELD vpath RETURN vpath")
    public Stream<VirtualPathResult> isLoopGraph(@Name("relationship") Relationship relationship, @Name("idsSeqLoopGraph") List<Long> idsSeqLoopGraph, @Name("atomicId") Long atomicId) {
        RelationshipType type = relationship.getType();

        Node fromNode = relationship.getStartNode();
        long fromId = fromNode.getId() * atomicId;
        VirtualNode from = new VirtualNode(fromId, fromNode.getLabels(), fromNode.getAllProperties(), null);

        Node toNode = relationship.getEndNode();
        long toId = toNode.getId() * atomicId;
        VirtualNode to = new VirtualNode(toId, toNode.getLabels(), toNode.getAllProperties(), null);

        Relationship rel = new VirtualRelationship(relationship.getId() * atomicId, from, to, type).withProperties(relationship.getAllProperties());
        return Stream.of(new VirtualPathResult(from, rel, to));
    }
}

