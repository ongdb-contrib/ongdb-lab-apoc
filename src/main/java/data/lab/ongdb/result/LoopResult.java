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
     * @param directionListMap:方向对应关系    包含startNode【开始节点ID】、type【关系类型】、endNode【结束节点ID】字段
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @return
     * @Description: TODO
     */
    public LoopResult(String pathStr, HashMap<Long, Long> indexNode, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap) {
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(v -> indexNode.get(Long.parseLong(v)))
                .collect(Collectors.toList());
        this.pathStr = pathStr;
        this.jointCypher = appendPathStr(nodeSeqIdList, directionListMap, idToLabel, nodeIndex, filterNodeMap);
    }

    /**
     * @param nodeSeqIdList:路径节点序列
     * @param directionListMap:关系数据【包含开始结束节点和关系类型的MAP】
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @return
     * @Description: TODO(拼接一个路径匹配模式语句)
     */
    private String appendPathStr(List<Long> nodeSeqIdList, List<Map<String, Object>> directionListMap, Map<Long, String> idToLabel, HashMap<Long, Long> nodeIndex, Map<Long, JSONObject> filterNodeMap) {
        StringBuilder builder = new StringBuilder();
        StringBuilder nodeFilter = new StringBuilder();
        StringBuilder relFilter = new StringBuilder();
        int size = nodeSeqIdList.size();
        builder.append("MATCH {var.p}=");
        for (int i = 0; i < size; i++) {
            long id = nodeSeqIdList.get(i);
            long idIdx = nodeIndex.get(id);

            // 拼接节点变量
            String nodePara = "(n" + idIdx + ":" + idToLabel.get(id) + ")";
            builder.append(nodePara);
            this.paraSeqList.add(nodePara.replace("(", "").replace(")", "").split(":")[0]);

            JSONObject nodeFilterObject = filterNodeMap.get(idIdx);
            String nodePropertiesFilter = FilterUtil.propertiesFilter("n" + idIdx, nodeFilterObject);
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
                    String relPara = "r" + idIdx + "to" + nextIdIdx;
                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
                    relFilter.append(relPropertiesFilter);
                    this.propertiesKeySize += FilterUtil.propertiesFilter(nodeFilterObject);
                    builder.append("-[").append(relPara).append(":").append(relationshipType).append("]->");
                } else {
                    String relPara = "r" + nextIdIdx + "to" + idIdx;
                    relPropertiesFilter = FilterUtil.propertiesFilter(relPara, map);
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
        if (cutCypher.contains("AND")) {
            return builder.substring(0, builder.length() - 4) + " RETURN {var.p} ";
        } else {
            return builder.append(" RETURN {var.p} ").toString();
        }
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
