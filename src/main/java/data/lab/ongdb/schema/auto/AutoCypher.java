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
import data.lab.ongdb.util.ArrayUtils;
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

    /**
     * 连接节点的子查询
     * */
    //private final static String CYPHER_JOINT_ALL = "UNION ALL";
    private final static String CYPHER_JOINT = "UNION";

    private final static String START_NODE = "startNode";
    private final static String END_NODE = "endNode";
    private final static String TYPE = "type";
    private final static String LABELS = "labels";

    /**
     * 过滤器
     * */
    private final static String PROPERTIES_FILTER = "properties_filter";
    private final static String ES_FILTER = "es_filter";

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

//    /**
//     * @param graphPaths:STRING类型路径列表
//     * @param indexNode:索引与节点的ID对应关系
//     * @param directionListMap:方向对应关系    包含startNode【开始节点ID】、type【关系类型】、endNode【结束节点ID】字段
//     * @param idToLabel:索引节点ID对应的节点标签MAP
//     * @param nodeIndex:节点ID和索引ID对应关系
//     * @param filterNodeMap:属性过滤器
//     * @return
//     * @Description: TODO(拿到路径列表 - 并将索引ID替换为节点ID)
//     */
//    private List<LoopResult> replaceIndexId(List<String> graphPaths, HashMap<Long, Long> indexNode, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap) {
//        /*
//         * 拿到路径列表-并将索引ID替换为节点ID
//         * */
//        return graphPaths
//                .parallelStream()
//                .map(v -> new LoopResult(v, indexNode, directionListMap, idToLabel, nodeIndex, filterNodeMap))
//                .collect(Collectors.toList());
//    }

    /**
     * @param graphPaths:STRING类型路径列表
     * @param indexNode:索引与节点的ID对应关系
     * @param reFDirectionListMap:方向对应关系 包含startNode【开始节点ID】、type【关系类型】、endNode【结束节点ID】字段
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @return
     * @Description: TODO(拿到路径列表 - 并将索引ID替换为节点ID)
     */
    private List<LoopResult> replaceIndexId(List<String> graphPaths, HashMap<Long, Long> indexNode, List<Map<String, Object>> reFDirectionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap) {
        /*
         * 拿到路径列表-并将索引ID替换为节点ID
         * */
        List<LoopResult> loopResultList = new ArrayList<>();
        for (String pathStr : graphPaths) {

            /*
             * directionListMap使用开始结束节点，还有关系类型排重统计 KEY:startNode-id-endNode-id VALUE:directionListMapList
             * 过滤出与当前路径相关的directionListMap
             * */
            List<Map<String, Object>> directionListMap = filter(reFDirectionListMap, pathStr, indexNode);
            List<AuDirection> mapList = countDirectionListMap(directionListMap);

            /*
             * 1、directionListMap进行统计排重设置一个size字段、listMap字段
             * 2、对SIZE>1的map进行笛卡尔积组合，生成num【num为mapList中size的乘积】
             * 3、对于size>1的map生成边的组合对列表【笛卡尔积的组合对】
             * 4、从directionListMap过滤掉组合对之外的元素
             * 5、生成LoopResult
             * 6、排重LoopResult List之后返回
             * */

            /*
             * 笛卡尔路径组合
             * 原始map经过统计之后 ， 生成笛卡尔积列表组合
             *
             * */
            List<String[]> cartesianList = cartesianList(mapList);
            if (!cartesianList.isEmpty()) {
                int num = cartesianList.size();
                for (int i = 0; i < num; i++) {
                    /*
                     * 原始map经过统计之后 ， 使用笛卡尔积列表组合生成新的directionListMap【每次使用时多条边的只留一条】
                     * 使用笛卡尔积列表对SIZE大于1的对象进行筛选
                     * */
                    List<Map<String, Object>> resetMapList = resetMapList(mapList, cartesianList.get(i));
                    LoopResult loopResult = new LoopResult(pathStr, indexNode, resetMapList, idToLabel, nodeIndex, filterNodeMap);
                    loopResultList.add(loopResult);
                }
            } else {
                /*
                 * 列表中有没有SIZE大于1的元素，如果有的话，增加一个循环
                 * */
                List<AuDirection> rawMapList = mapList.stream().filter(v -> v.getSize() > 1).collect(Collectors.toList());
                if (rawMapList.size() > 0) {
                    List<AuDirection> listMap = rawMapList.get(0).listMap;
                    for (AuDirection auDirection : listMap) {
                        /*
                         * 重置directionListMap
                         * */
                        List<Map<String, Object>> reDirectionListMap = reDirectionListMap(auDirection, directionListMap);
                        LoopResult loopResult = new LoopResult(pathStr, indexNode, reDirectionListMap, idToLabel, nodeIndex, filterNodeMap);
                        loopResultList.add(loopResult);
                    }
                } else {
                    /*
                     * 拿到路径列表-并将索引ID替换为节点ID
                     * */
                    LoopResult loopResult = new LoopResult(pathStr, indexNode, directionListMap, idToLabel, nodeIndex, filterNodeMap);
                    loopResultList.add(loopResult);
                }
            }
        }
        return loopResultList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * @param reFDirectionListMap:方向对应关系 包含startNode【开始节点ID】、type【关系类型】、endNode【结束节点ID】字段
     * @param pathStr:路径串
     * @param indexNode:索引与节点的ID对应关系
     * @return
     * @Description: TODO
     */
    private List<Map<String, Object>> filter(List<Map<String, Object>> reFDirectionListMap, String pathStr, HashMap<Long, Long> indexNode) {
        List<Map<String, Object>> directionListMap = new ArrayList<>();
        String[] pathElements = pathStr.split(PATH_REL_JOINT);
        for (int i = 0; i < pathElements.length - 1; i++) {
            String startNode = String.valueOf(indexNode.get(Long.parseLong(pathElements[i])));
            String endNode = String.valueOf(indexNode.get(Long.parseLong(pathElements[i + 1])));
            List<Map<String, Object>> maps = reFDirectionListMap.stream().filter(v -> {
                String startNodeMap = String.valueOf(v.get(START_NODE));
                String endNodeMap = String.valueOf(v.get(END_NODE));
                return (startNode.equals(startNodeMap) && endNode.equals(endNodeMap)) || (startNode.equals(endNodeMap) && endNode.equals(startNodeMap));
            }).collect(Collectors.toList());
            directionListMap.addAll(maps);
        }
        return directionListMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(在directionListMap中移除auDirection)
     */
    private List<Map<String, Object>> reDirectionListMap(AuDirection auDirection, List<Map<String, Object>> directionListMap) {
        List<Map<String, Object>> reDirectionListMap = new ArrayList<>();
        for (Map<String, Object> map : directionListMap) {
            String startNode = auDirection.getStartNode();
            String endNode = auDirection.getEndNode();
            String type = auDirection.getType();
            String startNodeMap = String.valueOf(map.get(START_NODE));
            String endNodeMap = String.valueOf(map.get(END_NODE));
            String typeMap = String.valueOf(map.get(TYPE));
            if (!(startNode.equals(startNodeMap) && endNode.equals(endNodeMap) && type.equals(typeMap))) {
                reDirectionListMap.add(map);
            }
        }
        return reDirectionListMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(AuDirection对象转为MAP对象)
     */
    private Map<String, Object> packMap(AuDirection auDirection) {
        Map<String, Object> map = new HashMap<>();
        map.put(START_NODE, auDirection.getStartNode());
        map.put(END_NODE, auDirection.getEndNode());
        map.put(TYPE, auDirection.getType());
        map.put(PROPERTIES_FILTER, auDirection.getPropertiesFilter());
        map.put(ES_FILTER, auDirection.getEsFilter());
        return map;
    }

    /**
     * @param mapList:mapList包含size字段、listMap字段
     * @return
     * @Description: TODO(原始map经过统计之后 ， 生成笛卡尔积列表组合)
     */
    private List<String[]> cartesianList(List<AuDirection> mapList) {

        // 列表中的数组长度-表示mapList中size>1的元素的个数
        int greaterOneSize = Math.toIntExact(mapList.stream().filter(v -> v.getSize() > 1).count());
        if (greaterOneSize < 2) {
            return new ArrayList<>();
        }

        List<AuDirection> reMapList = mapList.stream().filter(v -> v.getSize() > 1).collect(Collectors.toList());
        Map<Object, List<Map<String, Object>>> modelMap = new HashMap<>();
        for (int i = 0; i < greaterOneSize; i++) {
            AuDirection reMapListTwo = reMapList.get(i);
            List<Map<String, Object>> conMapListTwo = new ArrayList<>();
            for (int j = 0; j < reMapListTwo.getSize(); j++) {
                Map<String, Object> map = new HashMap<>();
                map.put("seq", j);
                conMapListTwo.add(map);
            }
            modelMap.put(i, conMapListTwo);
        }
        List<List<Map<String, Object>>> descartes = new ArrayUtils().descartes(modelMap);
        return descartes.stream()
                .map(v -> v.stream().map(para -> String.valueOf(para.get("seq"))).toArray(String[]::new))
                .collect(Collectors.toList());
    }

    /**
     * @param mapList:mapList包含size字段、listMap字段
     * @param cartesian:size>1的map组合序列【序列为mapList中listMap字段的元素索引】【表示本次生成LoopResult使用哪个索引的元素】【本次笛卡尔积序列】
     * @return
     * @Description: TODO(原始map经过统计之后 ， 使用笛卡尔积列表组合生成新的directionListMap)
     */
    private List<Map<String, Object>> resetMapList(List<AuDirection> mapList, String[] cartesian) {

        List<AuDirection> reMapList = mapList.stream().filter(v -> v.getSize() > 1).collect(Collectors.toList());
        List<AuDirection> list = mapList.stream().filter(v -> v.getSize() < 2).collect(Collectors.toList());

        int num = reMapList.size();
        for (int i = 0; i < num; i++) {
            AuDirection auDirection = reMapList.get(i).getListMap().get(Integer.parseInt(cartesian[i]));
            /*
             * 移除掉与当前对象重复的元素，并将选举出的元素添加到列表
             * */
            list = removeAuDirection(list, auDirection);
            list.add(auDirection);
        }
        return list.stream().map(this::packMap).collect(Collectors.toList());
    }

    /**
     * @param
     * @return
     * @Description: TODO(从list中移除与auDirection对象重复的元素)
     */
    private List<AuDirection> removeAuDirection(List<AuDirection> list, AuDirection auDirection) {
        return list.stream().filter(v -> !auDirection.equals(v)).collect(Collectors.toList());
    }

    /**
     * @param
     * @return
     * @Description: TODO(directionListMap使用开始结束节点 ， 还有关系类型排重统计 KEY : startNode - id - endNode - id VALUE : directionListMapList)
     */
    private List<AuDirection> countDirectionListMap(List<Map<String, Object>> directionListMap) {
        List<AuDirection> mapList = new ArrayList<>();
        for (Map<String, Object> map : directionListMap) {
            AuDirection auDirection = new AuDirection(
                    String.valueOf(map.get(START_NODE)),
                    String.valueOf(map.get(END_NODE)),
                    String.valueOf(map.get(TYPE)),
                    "null".equals(String.valueOf(map.get(PROPERTIES_FILTER))) ? null : String.valueOf(map.get(PROPERTIES_FILTER)),
                    "null".equals(String.valueOf(map.get(ES_FILTER))) ? null : String.valueOf(map.get(ES_FILTER))
            );
            if (mapList.contains(auDirection)) {
                /*
                 * 封装数据
                 * */
                getAuDirection(mapList, auDirection);
            } else {
                /*
                 * 初始化列表
                 * */
                auDirection.setSize(1);
                List<AuDirection> listMap = new ArrayList<>();
                listMap.add(auDirection);
                auDirection.setListMap(listMap);
            }
            mapList.add(auDirection);
        }
        return mapList;
    }

    /**
     * @param mapList:列表
     * @param auDirection:当前对象
     * @return
     * @Description: TODO(封装数据 - 当前对象合并到列表)
     */
    private void getAuDirection(List<AuDirection> mapList, AuDirection auDirection) {
        for (AuDirection direction : mapList) {
            if (direction.equals(auDirection)) {
                direction.setSize(direction.getSize() + 1);
                direction.getListMap().add(auDirection);
            }
        }
    }

    /**
     * @param analysisNodeIds:两两定点求全路径的顶点列表
     * @param relationships:关系集
     * @param nodeIndex:节点和索引的对应关系
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
                    /*
                     * 清空反向搜索的结果集
                     * */
                    allPaths.clear();
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
     * @param json:入参              #############
     *                             属性过滤器属性之间过滤，连接方式只支持AND
     *                             #############
     *                             属性过滤器(properties_filter)：
     *                             包含【STRING】：‘ name CONTAINS '北京' '
     *                             等于【STRING、INT、LONG】：‘ name='北京' ’  ‘ count=0 ’
     *                             大于【INT、LONG】：‘ count>0 ’
     *                             小于【INT、LONG】：‘ count<0 ’
     *                             大于等于【INT、LONG】：‘ count>=0 ’
     *                             小于等于【INT、LONG】：‘ count>=0 ’
     *                             范围-左闭右闭【INT、LONG】：‘ count>=0 AND count<=10 ’
     *                             范围-左闭右开【INT、LONG】：‘ count>=0 AND count<10 ’
     *                             范围-左开右闭【INT、LONG】：‘ count>0 AND count<=10 ’
     *                             范围-左开右开【INT、LONG】：‘ count>0 AND count<10 ’
     *                             #############
     *                             ES-QUERY
     *                             #############
     *                             ES过滤器(es_filter)：
     *                             ES-QUERY-DSL【去掉JSON引号的查询语句】eg.{size:1,query:{term:{product_code:"PF0020020104"}}}
     * @param limit:限制参数【表示匹配多个子图】
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
            "RETURN olab.schema.auto.cypher({JSON},{LIMIT}) AS cypher\n" +
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
         * v1.0实现CYPHER的自动生成，模式匹配到的子图使用graph对象返回【RETURN {graph:[path1,path2]} AS graph LIMIT 1】
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

        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);
        if (relationships == null || relationships.isEmpty()) {
            if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD)) {
                throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " field!");
            }
            // 单节点拼接
            return cypherAppendJustNodes(nodes, limit);
        } else {
            if (!graphData.containsKey(GRAPH_DATA_NODES_FIELD) || !graphData.containsKey(GRAPH_DATA_RELATIONSHIPS_FIELD)) {
                throw new IllegalArgumentException("GraphData is no " + GRAPH_DATA_NODES_FIELD + " or " + GRAPH_DATA_RELATIONSHIPS_FIELD + " field!");
            }
            return cypherAppendNotJustNodes(graphData, limit);
        }
    }

    /**
     * @param graphData:图对象
     * @return
     * @Description: TODO(图对象转换为CYPHER语句)
     */
    private String cypherAppendNotJustNodes(JSONObject graphData, long limit) {
        /*
         * 1、确定顶点【一度连边顶点】
         * 2、检测graphData是一个连通图【求弱连通分量】
         * 3、找到所有路径
         * 4、排除重复路径【主要是正序与逆序路径】
         * 5、拼接路径【拼接优先级：路径越长越优先；过滤条件越多越优先】
         * 6、返回对象【graph为满足该图模式的子图】：RETURN {graph:[path1,path2...]} AS graph LIMIT 1
         * */
        /*
         * 过滤出顶点集合
         * */
        /*
         * 过滤出一度连边的节点
         * List<Long> analysisNodeIds = filterAnalysisNodeIds(graphData);
         * */
        List<Long> analysisNodeIds = allAnalysisNodeIds(graphData);

        /*
         * 转换图结构为矩阵寻找所有子图路径：
         * */
        // 生成虚拟节点ID-使用INDEX替换
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        HashMap<String, HashMap<Long, Long>> idMap = transferNodeIndex(nodes);
        HashMap<Long, Long> nodeIndex = idMap.get(ID_MAP_TO_VID);
        HashMap<Long, Long> indexNode = idMap.get(VID_MAP_TO_ID);

        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);

        /*
         * 当前图的所有路径集合【如果发现不是连通图则报错】
         * */
        List<String> graphPaths = filterGraphSchemaRedundancyPath(getGraphPathsWcc(analysisNodeIds, relationships, nodeIndex));

        List<Map<String, Object>> directionListMap = relationships.stream()
                .map(v -> {
                    JSONObject obj = (JSONObject) v;
                    Map<String, Object> map = new HashMap<>();
                    map.put(START_NODE, obj.getString(START_NODE));
                    map.put(END_NODE, obj.getString(END_NODE));
                    map.put(TYPE, obj.getString(TYPE));
                    map.put(PROPERTIES_FILTER, obj.getJSONArray(PROPERTIES_FILTER));
                    map.put(ES_FILTER, obj.getJSONArray(ES_FILTER));
                    return map;
                }).collect(Collectors.toList());
        Map<Long, String> idToLabel = new HashMap<>();
        Map<Long, JSONObject> filterNodeMap = new HashMap<>();
        for (long i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(Math.toIntExact(i));
            idToLabel.put(node.getLongValue(ID), node.getJSONArray(LABELS).getString(0));
            JSONObject object = new JSONObject();
            object.put(PROPERTIES_FILTER, node.getJSONArray(PROPERTIES_FILTER));
            object.put(ES_FILTER, node.getJSONArray(ES_FILTER));
            filterNodeMap.put(i, object);
        }
        /*
         * 拼接的路径列表【路径拼接DEBUG】
         * */
        List<LoopResult> graphNodeIdSeqPaths = replaceIndexId(graphPaths, indexNode, directionListMap, idToLabel, nodeIndex, filterNodeMap);
        /*
         * 根据查询代码自动优化拼接多条path
         * */
        return generateCypher(graphNodeIdSeqPaths, limit);
    }

    /**
     * @param graphPathsWcc:弱连通图中的路径
     * @return
     * @Description: TODO(过滤掉图模式匹配中的冗余路径)
     */
    private List<String> filterGraphSchemaRedundancyPath(List<String> graphPathsWcc) {
        List<Integer> idCollect = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        List<String> graphFilterPathsWcc = new ArrayList<>();
        for (String path : graphPathsWcc) {
            List<Integer> indexes = Arrays.stream(path.split(PATH_REL_JOINT))
                    .map(Integer::parseInt)
                    .distinct()
                    .collect(Collectors.toList());
            for (Integer idx : indexes) {
                if (!ids.contains(idx) && !graphFilterPathsWcc.contains(path)) {
                    graphFilterPathsWcc.add(path);
                }
            }
            idCollect.addAll(indexes);
            ids = idCollect.stream().distinct().collect(Collectors.toList());
        }
        return graphFilterPathsWcc;
    }

    /**
     * @param
     * @return
     * @Description: TODO({ var.p } - 生成子图模式匹配语句)
     */
    private String generateCypher(List<LoopResult> graphNodeIdSeqPaths, long limit) {
        /*
         * 长路径优先
         * 过滤属性数量优先
         * */
        List<LoopResult> graphNodeIdSeqPathsSort = graphNodeIdSeqPaths.stream()
                // 路径长度排序与属性数量排序
                .sorted((v1, v2) -> {
                    Integer v1Int = v1.getNodeSeqIdList().size() + v1.getPropertiesKeySize();
                    Integer v2Int = v2.getNodeSeqIdList().size() + v2.getPropertiesKeySize();
                    return v2Int.compareTo(v1Int);
                })
                .collect(Collectors.toList());

        /*
         * 对路径进行编码：替换`{var.p}`变量标记
         * 拼接形成一个CYPHER：子图模式匹配语句
         * */
        StringBuilder builder = new StringBuilder();
        StringBuilder pathParas = new StringBuilder();
        StringBuilder withParasBuilder = new StringBuilder();
        int size = graphNodeIdSeqPathsSort.size();
        for (int i = 0; i < size; i++) {
            LoopResult loopResult = graphNodeIdSeqPathsSort.get(i);
            // 替换`{var.p}`变量标记
            String para = "p" + i;
            String path = loopResult.getJointCypher().replace("{var.p}", para);
            pathParas.append(para);
            if (i < size - 1) {
                pathParas.append(",");
            }

            /*
             * 拼接`WITH`,提取变量
             * 准备RETURN GRAPH
             * */
            withParasBuilder.append(jointParas(loopResult.getParaSeqList()) + "," + para);
            String withPara = jointDuplicationParas(withParasBuilder.toString());
            withParasBuilder.append(",");
            builder.append(path.replace("RETURN " + para, "WITH " + withPara));
            builder.append("\n");
        }
        builder.append(appendReturnGraph(pathParas.toString(), limit));
        /*
         * 拼接序列中的CYPHER
         * */
        return builder.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(排重变量名称)
     */
    private String jointDuplicationParas(String paras) {
        List<String> finalParas = Arrays.stream(paras.split(",")).distinct().collect(Collectors.toList());
        StringBuilder parasStr = new StringBuilder();
        int size = finalParas.size();
        for (int i = 0; i < size; i++) {
            parasStr.append(finalParas.get(i));
            if (i < size - 1) {
                parasStr.append(",");
            }
        }
        return parasStr.toString();
    }

    /**
     * @param paraSeqList:变量序列
     * @return
     * @Description: TODO(拼接变量序列)
     */
    private String jointParas(List<String> paraSeqList) {
        int size = paraSeqList.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i < size - 1) {
                builder.append(paraSeqList.get(i));
                builder.append(",");
            } else {
                builder.append(paraSeqList.get(i));
            }
        }
        return builder.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(准备RETURN GRAPH)
     */
    private String appendReturnGraph(String pathParas, long limit) {
        if (limit > 0) {
            return "RETURN {graph:[" + pathParas + "]} AS graph LIMIT " + limit;
        } else {
            return "RETURN {graph:[" + pathParas + "]} AS graph";
        }
    }

    /**
     * @param analysisNodeIds:两两定点求全路径的顶点列表
     * @param relationships:关系集
     * @param nodeIndex:节点和索引的对应关系
     * @return
     * @Description: TODO(获取路径串 【 如果发现不是连通图则报错 】)
     */
    private List<String> getGraphPathsWcc(List<Long> analysisNodeIds, JSONArray relationships, HashMap<Long, Long> nodeIndex) {
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
                    if (!allPaths.getAllPathsStr().isEmpty()) {
                        graphPaths.addAll(allPaths.getAllPathsStr());
                    } else {
                        throw new IllegalArgumentException("The graph is not a weakly connected graph!");
                    }
                    /*
                     * 清空正向搜索的结果集
                     * */
                    allPaths.clear();
                }
            }
        }
        return graphPaths;
    }

    /**
     * @param graphData:图对象
     * @return
     * @Description: TODO(找到所有顶点ID ： 找到一度连边顶点 ： 与该点相连的边只有一个)
     */
    private List<Long> filterAnalysisNodeIds(JSONObject graphData) {
        ArrayList<Long> analysisNodeIds = new ArrayList<>();
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        JSONArray relationships = graphData.getJSONArray(GRAPH_DATA_RELATIONSHIPS_FIELD);
        ArrayList<Long> allAnalysisNodeIds = new ArrayList<>();
        for (Object obj : nodes) {
            JSONObject object = (JSONObject) obj;
            long id = object.getLongValue(ID);
            allAnalysisNodeIds.add(id);
            int relCount = 0;
            for (Object objRel : relationships) {
                JSONObject objectRel = (JSONObject) objRel;
                long startNode = objectRel.getLongValue(START_NODE);
                long endNode = objectRel.getLongValue(END_NODE);
                if (startNode == id || endNode == id) {
                    relCount++;
                }
            }
            if (relCount == 0) {
                throw new IllegalArgumentException("The GraphData contains isolated nodes!");
            } else if (relCount == 1) {
                analysisNodeIds.add(id);
            }
        }
        /*
         * 如果单连边节点为空，则将全部节点纳入analysisNodeIds
         * */
        if (analysisNodeIds.size() < 3) {
            return allAnalysisNodeIds.stream().distinct().collect(Collectors.toList());
        } else {
            return analysisNodeIds.stream().distinct().collect(Collectors.toList());
        }
    }

    /**
     * @param graphData:图对象
     * @return
     * @Description: TODO(找到所有顶点ID)
     */
    private List<Long> allAnalysisNodeIds(JSONObject graphData) {
        JSONArray nodes = graphData.getJSONArray(GRAPH_DATA_NODES_FIELD);
        ArrayList<Long> allAnalysisNodeIds = new ArrayList<>();
        for (Object obj : nodes) {
            JSONObject object = (JSONObject) obj;
            long id = object.getLongValue(ID);
            allAnalysisNodeIds.add(id);
        }
        return allAnalysisNodeIds.stream().distinct().collect(Collectors.toList());
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
        return cypherBuilder.substring(0, cypherBuilder.length() - (CYPHER_JOINT.length() + 2));
    }

    /**
     * @param
     * @return
     * @Description: TODO(确定限制参数)
     */
    private long calLimit(long limit, int size) {
        if (limit < size) {
            return -1;
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
        String label = nodeObject.getJSONArray("labels").getString(0);
        String properties_filter = FilterUtil.propertiesFilter("n", nodeObject.getJSONArray("properties_filter"));
        // custom.es.result.bool({es-url},{index-name},{query-dsl})
        String es_filter = FilterUtil.esFilter("n", nodeObject.getJSONArray("es_filter"));
        if ("".equals(es_filter) && !"".equals(properties_filter)) {
            if (limit > 0) {
                return "MATCH (n:" + label + ") WHERE " + properties_filter + " RETURN n LIMIT " + limit;
            } else {
                return "MATCH (n:" + label + ") WHERE " + properties_filter + " RETURN n";
            }
        } else if (!"".equals(es_filter) && "".equals(properties_filter)) {
            if (limit > 0) {
                return "MATCH (n:" + label + ") WHERE " + es_filter + " RETURN n LIMIT " + limit;
            } else {
                return "MATCH (n:" + label + ") WHERE " + es_filter + " RETURN n";
            }
        } else if (!"".equals(properties_filter)) {
            if (limit > 0) {
                return "MATCH (n:" + label + ") WHERE " + properties_filter + " AND " + es_filter + " RETURN n LIMIT " + limit;
            } else {
                return "MATCH (n:" + label + ") WHERE " + properties_filter + " AND " + es_filter + " RETURN n";
            }
        } else {
            if (limit > 0) {
                return "MATCH (n:" + label + ") RETURN n LIMIT " + limit;
            } else {
                return "MATCH (n:" + label + ") RETURN n";
            }
        }
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
            return object1.getInteger(ID) - object2.getInteger(ID);
        }).collect(Collectors.toCollection(JSONArray::new));

        for (long i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(Math.toIntExact(i));
            nodeIndex.put(node.getLongValue(ID), i);
            indexNode.put(i, node.getLongValue(ID));
        }
        idsMap.put(ID_MAP_TO_VID, nodeIndex);
        idsMap.put(VID_MAP_TO_ID, indexNode);
        return idsMap;
    }

    private static JSONArray transferRelations(HashMap<Long, Long> nodeIndex, JSONArray relationships) {
        return relationships.parallelStream()
                .map(v -> {
                    JSONObject edge = (JSONObject) v;
                    edge.put(START_V_IDX_ID, nodeIndex.get(edge.getLongValue(START_NODE)));
                    edge.put(END_V_IDX_ID, nodeIndex.get(edge.getLongValue(END_NODE)));
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
     * @param atomicId:原子性ID【对一条关系中的关系ID和节点ID都乘这个值】
     * @return
     * @Description: TODO(生成虚拟图)
     */
    @Procedure(name = "olab.schema.loop.vpath", mode = Mode.READ)
    @Description("CALL olab.schema.loop.vpath({relationship},{atomicId}]) YIELD from,rel,to RETURN from,rel,to")
    public Stream<VirtualPathResult> isLoopGraph(@Name("relationship") Relationship relationship, @Name("atomicId") Long atomicId) {
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

    static class AuDirection {
        private String startNode;
        private String endNode;
        private String type;
        private String propertiesFilter;
        private String esFilter;
        private int size;
        private List<AuDirection> listMap;

        public AuDirection(String startNode, String endNode, String type, String propertiesFilter, String esFilter) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.type = type;
            this.propertiesFilter = propertiesFilter;
            this.esFilter = esFilter;
        }

        public AuDirection(String startNode, String endNode, String type, String propertiesFilter, String esFilter, int size, List<AuDirection> listMap) {
            this.startNode = startNode;
            this.endNode = endNode;
            this.type = type;
            this.propertiesFilter = propertiesFilter;
            this.esFilter = esFilter;
            this.size = size;
            this.listMap = listMap;
        }

        public String getStartNode() {
            return startNode;
        }

        public void setStartNode(String startNode) {
            this.startNode = startNode;
        }

        public String getEndNode() {
            return endNode;
        }

        public void setEndNode(String endNode) {
            this.endNode = endNode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPropertiesFilter() {
            return propertiesFilter;
        }

        public void setPropertiesFilter(String propertiesFilter) {
            this.propertiesFilter = propertiesFilter;
        }

        public String getEsFilter() {
            return esFilter;
        }

        public void setEsFilter(String esFilter) {
            this.esFilter = esFilter;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public List<AuDirection> getListMap() {
            return listMap;
        }

        public void setListMap(List<AuDirection> listMap) {
            this.listMap = listMap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AuDirection that = (AuDirection) o;
            return (Objects.equals(startNode, that.startNode) || Objects.equals(startNode, that.endNode)) &&
                    (Objects.equals(endNode, that.endNode) || Objects.equals(endNode, that.startNode));
        }

        @Override
        public int hashCode() {
            return Objects.hash(startNode, endNode, type);
        }
    }
}

