package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.schema.auto.FilterUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO(环路检测)
 * @date 2021/4/14 10:54
 */
public class LoopResult {

    /**
     * PATH NODE SEQUENCE:0->7->2->8->3->5->4->6->1->0
     */
    private final static String PATH_REL_JOINT = "->";
    private final static String START_NODE = "startNode";
    private final static String END_NODE = "endNode";
    private final static String TYPE = "type";

    /**
     * isOutputFilter:是否返回过滤器与变量的绑定
     */
    private static final String varFilterMap = "WITH {} AS vFMap";
    private static final String varFilterMapPara = "vFMap";

    /**
     * 环路的节点序列
     */
    private List<Long> nodeSeqIdList;

    /**
     * 环路的节点序列
     */
    private String pathStr;

    /**
     * 拼接的CYPHER语句
     */
    private String jointCypher;

    /**
     * 路径属性过滤的数量
     */
    private int propertiesKeySize;

    private List<String> paraSeqList = new ArrayList<>();

    private String skeletonPathStr;

    public LoopResult() {
    }

    public LoopResult(Long[] nodeSeqIds) {
        this.nodeSeqIdList = Arrays.asList(nodeSeqIds);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < nodeSeqIds.length; i++) {
            builder.append(nodeSeqIds[i]);
            if (i != nodeSeqIds.length - 1) {
                builder.append(PATH_REL_JOINT);
            }
        }
        this.pathStr = builder.toString();
    }

    public LoopResult(List<Long> nodeSeqIds) {
        this.nodeSeqIdList = nodeSeqIds;
        StringBuilder builder = new StringBuilder();
        int size = nodeSeqIds.size();
        for (int i = 0; i < size; i++) {
            builder.append(nodeSeqIds.get(i));
            if (i != size - 1) {
                builder.append(PATH_REL_JOINT);
            }
        }
        this.pathStr = builder.toString();
    }

    public LoopResult(String pathStr) {
        this.pathStr = pathStr;
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public LoopResult(String pathStr, Map<Long, Long> indexNode) {
        this.pathStr = pathStr;
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(v -> indexNode.get(Long.parseLong(v)))
                .collect(Collectors.toList());
    }

    public LoopResult(List<Long> nodeSeqIdList, String pathStr, int propertiesKeySize) {
        this.nodeSeqIdList = nodeSeqIdList;
        this.pathStr = pathStr;
        this.propertiesKeySize = propertiesKeySize;
    }

    /**
     * @param pathStr:路径串
     * @param indexNode:索引和节点ID对应关系
     * @param directionListMap:方向对应关系                                   包含startNode【开始节点ID】、type【关系类型】、endNode【结束节点ID】字段
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @param isOutputFilter:是否返回过滤器与变量的绑定，默认false【在WITH中返回变量ID与过滤器的绑定】
     * @return
     * @Description: TODO
     */
    public LoopResult(String pathStr, HashMap<Long, Long> indexNode, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap, boolean isOutputFilter) {
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(v -> indexNode.get(Long.parseLong(v)))
                .collect(Collectors.toList());
        this.pathStr = pathStr;
        this.jointCypher = appendPathStr(nodeSeqIdList, directionListMap, idToLabel, nodeIndex, filterNodeMap, isOutputFilter);
    }

//    /**
//     * @param nodeSeqIdList:路径节点序列
//     * @param directionListMap:关系数据【包含开始结束节点和关系类型的MAP】
//     * @param idToLabel:索引节点ID对应的节点标签MAP
//     * @param nodeIndex:节点ID和索引ID对应关系
//     * @param filterNodeMap:属性过滤器
//     * @return
//     * @Description: TODO(拼接一个路径匹配模式语句)
//     */
//    private String appendPathStr(List<Long> nodeSeqIdList, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap) {
//        StringBuilder builder = new StringBuilder();
//        StringBuilder nodeFilter = new StringBuilder();
//        StringBuilder relFilter = new StringBuilder();
//        int size = nodeSeqIdList.size();
//        builder.append("MATCH {var.p}=");
//        for (int i = 0; i < size; i++) {
//            long id = nodeSeqIdList.get(i);
//            long idIdx = nodeIndex.get(id);
//
//            // 拼接节点变量
//            String nodePara = "(n" + idIdx + ":" + idToLabel.get(id) + ")";
//            builder.append(nodePara);
//            this.paraSeqList.add(nodePara.replace("(", "").replace(")", "").split(":")[0]);
//
//            JSONObject nodeFilterObject = filterNodeMap.get(idIdx);
//            String nodePropertiesFilter = FilterUtil.propertiesFilter("n" + idIdx, nodeFilterObject);
//            nodeFilter.append(nodePropertiesFilter);
//            this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
//            if (i < size - 1) {
//                // 拼接AND时增加一个判断逻辑
//                if (nodePropertiesFilter.length() > 1) {
//                    nodeFilter.append(" AND ");
//                }
//                long nextId = nodeSeqIdList.get(i + 1);
//                long nextIdIdx = nodeIndex.get(nextId);
//                Map<String, Object> map = relationshipType(id, nextId, directionListMap);
//                String relationshipType = String.valueOf(map.get(TYPE));
//                long startNode = Long.parseLong(String.valueOf(map.get(START_NODE)));
//                long endNode = Long.parseLong(String.valueOf(map.get(END_NODE)));
//                String relPropertiesFilter;
//                if (startNode == id && endNode == nextId) {
//                    String relPara = "r" + idIdx + "to" + nextIdIdx;
//                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
//                    relFilter.append(relPropertiesFilter);
//                    this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
//                    builder.append("-[").append(relPara).append(":").append(relationshipType).append("]->");
//                } else {
//                    String relPara = "r" + nextIdIdx + "to" + idIdx;
//                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
//                    relFilter.append(relPropertiesFilter);
//                    this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
//                    builder.append("<-[").append(relPara).append(":").append(relationshipType).append("]-");
//                }
//                // 拼接AND时增加一个判断逻辑
//                if (relPropertiesFilter.length() > 1) {
//                    relFilter.append(" AND ");
//                }
//            }
//        }
//        if (appendWhereCondition(nodeFilter, relFilter)) {
//            String relFilterStr = relFilter.length() > 5 ? relFilter.substring(0, relFilter.length() - 5) : "";
//            builder.append(" WHERE ");
//            if (filterInvalidStr(nodeFilter).length() > 1 && filterInvalidStr(relFilterStr).length() > 1) {
//                builder.append(nodeFilter);
//                if (!nodeFilter.substring(nodeFilter.length() - 4, nodeFilter.length()).contains("AND")) {
//                    builder.append(" AND ");
//                }
//                builder.append(relFilterStr);
//            } else if (filterInvalidStr(nodeFilter).length() > 1) {
//                builder.append(nodeFilter);
//            } else if (filterInvalidStr(relFilterStr).length() > 1) {
//                builder.append(relFilter);
//            }
//        }
//        String cutCypher = builder.substring(builder.length() - 4, builder.length());
//        if (cutCypher.contains("AND")) {
//            return builder.substring(0, builder.length() - 4) + " RETURN {var.p} ";
//        } else {
//            return builder.append(" RETURN {var.p} ").toString();
//        }
//    }

    /**
     * @param nodeSeqIdList:路径节点序列
     * @param directionListMap:关系数据【包含开始结束节点和关系类型的MAP】
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @return
     * @para isOutputFilter:是否返回过滤器与变量的绑定，默认false【在WITH中返回变量ID与过滤器的绑定】
     * @Description: TODO(拼接一个路径匹配模式语句)
     */
    private String appendPathStr(List<Long> nodeSeqIdList, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap, boolean isOutputFilter) {
        StringBuilder builder = new StringBuilder();
        StringBuilder nodeFilter = new StringBuilder();
        StringBuilder relFilter = new StringBuilder();
        int size = nodeSeqIdList.size();
        builder.append("MATCH {var.p}=");
        // listList.add(new ArrayList<>(Arrays.asList("n0", "value")))
        List<List<String>> listList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            long id = nodeSeqIdList.get(i);
            long idIdx = nodeIndex.get(id);

            // 拼接变量：拼接节点变量
            String nodePara = "(n" + idIdx + ":" + idToLabel.get(id) + ")";
            builder.append(nodePara);
            this.paraSeqList.add(nodePara.replace("(", "").replace(")", "").split(":")[0]);

            JSONObject nodeFilterObject = filterNodeMap.get(idIdx);
            String nodePropertiesFilter = FilterUtil.propertiesFilter("n" + idIdx, nodeFilterObject );
            // 是否增加到ID和过滤条件绑定列表
            if (isOutputFilter) {
//                listList.add(new ArrayList<>(Arrays.asList("n" + idIdx, FilterUtil.propertiesFilter("{var}", nodeFilterObject ))));
                listList.add(new ArrayList<>(Arrays.asList("n" + idIdx, FilterUtil.propertiesFilterObj("{var}", nodeFilterObject ))));
            }
            nodeFilter.append(nodePropertiesFilter);
            this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
            if (i < size - 1) {
                // 拼接AND时增加一个判断逻辑
                if (nodePropertiesFilter.length() > 1) {
                    nodeFilter.append(" AND ");
                }
                long nextId = nodeSeqIdList.get(i + 1);
                long nextIdIdx = nodeIndex.get(nextId);
                Map<String, Object> map = relationshipType(id, nextId, directionListMap);
                String relationshipType = String.valueOf(map.get(TYPE));
                long startNode = Long.parseLong(String.valueOf(map.get(START_NODE)));
                long endNode = Long.parseLong(String.valueOf(map.get(END_NODE)));
                String relPropertiesFilter;
                if (startNode == id && endNode == nextId) {
                    // 拼接变量：拼接关系变量
                    String relPara = "r" + idIdx + "to" + nextIdIdx;
                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
                    // 是否增加到ID和过滤条件绑定列表
                    if (isOutputFilter) {
//                        listList.add(new ArrayList<>(Arrays.asList(relPara, FilterUtil.propertiesFilter("{var}", map))));
                        listList.add(new ArrayList<>(Arrays.asList(relPara, FilterUtil.propertiesFilterObj("{var}", map))));
                    }
                    relFilter.append(relPropertiesFilter);
                    this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
                    builder.append("-[").append(relPara).append(":").append(relationshipType).append("]->");
                } else {
                    // 拼接变量：拼接关系变量
                    String relPara = "r" + nextIdIdx + "to" + idIdx;
                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
                    // 是否增加到ID和过滤条件绑定列表
                    if (isOutputFilter) {
                        listList.add(new ArrayList<>(Arrays.asList(relPara, FilterUtil.propertiesFilterObj("{var}", map))));
                    }
                    relFilter.append(relPropertiesFilter);
                    this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
                    builder.append("<-[").append(relPara).append(":").append(relationshipType).append("]-");
                }
                // 拼接AND时增加一个判断逻辑
                if (relPropertiesFilter.length() > 1) {
                    relFilter.append(" AND ");
                }
            }
        }
        if (appendWhereCondition(nodeFilter, relFilter)) {
            String relFilterStr = relFilter.length() > 5 ? relFilter.substring(0, relFilter.length() - 5) : "";
            builder.append(" WHERE ");
            if (filterInvalidStr(nodeFilter).length() > 1 && filterInvalidStr(relFilterStr).length() > 1) {
                builder.append(nodeFilter);
                if (!nodeFilter.substring(nodeFilter.length() - 4, nodeFilter.length()).contains("AND")) {
                    builder.append(" AND ");
                }
                builder.append(relFilterStr);
            } else if (filterInvalidStr(nodeFilter).length() > 1) {
                builder.append(nodeFilter);
            } else if (filterInvalidStr(relFilterStr).length() > 1) {
                builder.append(relFilter);
            }
        }
        String cutCypher = builder.substring(builder.length() - 4, builder.length());
        // List<List<String>> listList = new ArrayList<>();
        List<List<String>> reListList = listList.stream().filter(v -> !"".equals(v.get(1))).collect(Collectors.toList());
        if (cutCypher.contains("AND")) {
            if (isOutputFilter) {
                if (!reListList.isEmpty()) {
                    return builder.substring(0, builder.length() - 4) + " RETURN {var.p}," + mapSetPairs(mapSetPairsListList(reListList)) + " ";
                } else {
                    return builder.substring(0, builder.length() - 4) + " RETURN {var.p}," + varFilterMapPara + " ";
                }
            } else {
                return builder.substring(0, builder.length() - 4) + " RETURN {var.p} ";
            }
        } else {
            if (isOutputFilter) {
                if (!reListList.isEmpty()) {
                    return builder.append(" RETURN {var.p},").append(mapSetPairs(mapSetPairsListList(reListList))).append(" ").toString();
                } else {
                    return builder.append(" RETURN {var.p},").append(varFilterMapPara).append(" ").toString();
                }
            } else {
                return builder.append(" RETURN {var.p} ").toString();
            }
        }
    }

//    /**
//     * @param
//     * @return
//     * @Description: TODO(拼接apoc.map.setPairs函数参数列表)
//     */
//    protected String mapSetPairsListList(List<List<String>> setPairsListList) {
//        StringBuilder builder = new StringBuilder();
//        int size = setPairsListList.size();
//        for (int i = 0; i < size; i++) {
//            List<String> list = setPairsListList.get(i);
//            if (i == 0) {
//                builder.append("[");
//            }
//            if (list.size() == 2) {
//                builder.append("[").append("TOSTRING(ID(").append(list.get(0)).append("))").append(",").append("'").append(escape(list.get(1))).append("'").append("]");
//            } else {
//                throw new RuntimeException("Append apoc.map.setPairs paras error! Parameter list size is not equal to two!" + list.size());
//            }
//            if (i < size - 1) {
//                builder.append(",");
//            }
//            if (i == size - 1) {
//                builder.append("]");
//            }
//        }
//        return builder.toString();
//    }

    /**
     * @param
     * @return
     * @Description: TODO(拼接apoc.map.setPairs函数参数列表)
     */
    protected String mapSetPairsListList(List<List<String>> setPairsListList) {
        StringBuilder builder = new StringBuilder();
        int size = setPairsListList.size();
        for (int i = 0; i < size; i++) {
            List<String> list = setPairsListList.get(i);
            if (i == 0) {
                builder.append("[");
            }
            if (list.size() == 2) {
                builder.append("[").append("'_'+ID(").append(list.get(0)).append(")").append(",").append(list.get(1)).append("]");
            } else {
                throw new RuntimeException("Append apoc.map.setPairs paras error! Parameter list size is not equal to two!" + list.size());
            }
            if (i < size - 1) {
                builder.append(",");
            }
            if (i == size - 1) {
                builder.append("]");
            }
        }
        return builder.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(生成MAP的函数 ： RETURN apoc.map.setPairs ( { }, [[TOSTRING ( 1),'value'],[TOSTRING(2),'value'],[TOSTRING(1),'value2']]))
     */
    protected String mapSetPairs(String listListStr) {
        // WITH {dasd:2,sad:3} AS map
        // MATCH (n) WITH apoc.map.setPairs(map,[[TOSTRING(ID(n)),[{filter:'es',query:''}]]]) AS map LIMIT 10
        // MATCH (n) WITH apoc.map.setPairs(map,[[TOSTRING(ID(n)),[{filter:'pro',query:''}]]]) AS map SKIP 0 LIMIT 10
        // RETURN map
        return "apoc.map.setPairs(" + varFilterMapPara + "," + listListStr + ") AS " + varFilterMapPara;
    }

    /**
     * @param
     * @return
     * @Description: TODO(是否执行WHERE条件的拼接)
     */
    private boolean appendWhereCondition(StringBuilder nodeFilter, StringBuilder relFilter) {
        return (!"".equals(filterInvalidStr(nodeFilter))) || (!"".equals(filterInvalidStr(relFilter)));
    }

    /**
     * @param
     * @return
     * @Description: TODO(清除字符串中的 ‘ AND ’ 字符)
     */
    private String filterInvalidStr(Object object) {
        return object.toString().replace(" ", "")
                .replace("A", "")
                .replace("N", "")
                .replace("D", "");
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取指定ID的MAP)
     */
    private Map<String, Object> relationshipType(Long id, Long nextId, List<Map<String, Object>> directionListMap) {
        return directionListMap.stream()
                .filter(v -> {
                    long startNode = Long.parseLong(String.valueOf(v.get(START_NODE)));
                    long endNode = Long.parseLong(String.valueOf(v.get(END_NODE)));
                    return (startNode == id && endNode == nextId) || (endNode == id && startNode == nextId);
                })
                .findFirst()
                .orElse(new HashMap<>());
    }

    public List<Long> getNodeSeqIdList() {
        return nodeSeqIdList;
    }

    public void setNodeSeqIdList(List<Long> nodeSeqIdList) {
        this.nodeSeqIdList = nodeSeqIdList;
    }

    public String getPathStr() {
        return pathStr;
    }

    public void setPathStr(String pathStr) {
        this.pathStr = pathStr;
    }

    public int getPropertiesKeySize() {
        return propertiesKeySize;
    }

    public void setPropertiesKeySize(int propertiesKeySize) {
        this.propertiesKeySize = propertiesKeySize;
    }

    public List<String> getParaSeqList() {
        return paraSeqList;
    }

    public void setParaSeqList(List<String> paraSeqList) {
        this.paraSeqList = paraSeqList;
    }

    public String getJointCypher() {
        return jointCypher;
    }

    public void setJointCypher(String jointCypher) {
        this.jointCypher = jointCypher;
    }

    public String getSkeletonPathStr() {
        return skeletonPathStr;
    }

    public void setSkeletonPathStr(String skeletonPathStr) {
        this.skeletonPathStr = skeletonPathStr;
    }

    @Override
    public String toString() {
        return "LoopResult{" +
                "nodeSeqIdList=" + nodeSeqIdList +
                ", pathStr='" + pathStr + '\'' +
                ", jointCypher='" + jointCypher + '\'' +
                ", propertiesKeySize=" + propertiesKeySize +
                ", paraSeqList=" + paraSeqList +
                '}';
    }
}
