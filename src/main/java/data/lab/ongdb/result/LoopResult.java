package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.schema.auto.FilterUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 过滤器
     */
    private final static String PROPERTIES_FILTER = "properties_filter";
    private final static String ES_FILTER = "es_filter";

    /**
     * 环路的节点序列
     */
    private List<Long> nodeSeqIdList;

    /**
     * 环路的节点序列
     */
    private String pathStr;

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
        this.pathStr = appendPathStr(nodeSeqIdList, directionListMap, idToLabel, nodeIndex, filterNodeMap);
    }

    /**
     * @param nodeSeqIdList:路径节点序列
     * @param directionListMap:关系数据【包含开始结束节点和关系类型的MAP】
     * @param idToLabel:索引节点ID对应的节点标签MAP
     * @param nodeIndex:节点ID和索引ID对应关系
     * @param filterNodeMap:属性过滤器
     * @return
     * @Description: TODO
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
            builder.append("(n").append(idIdx).append(":").append(idToLabel.get(idIdx)).append(")");
            JSONObject nodeFilterObject = filterNodeMap.get(idIdx);
            nodeFilter.append(FilterUtil.propertiesFilter("n" + idIdx, nodeFilterObject));
            if (i < size - 1) {
                nodeFilter.append(" AND ");
                long nextId = nodeSeqIdList.get(i + 1);
                long nextIdIdx = nodeIndex.get(nextId);
                Map<String, Object> map = relationshipType(id, nextId, directionListMap);
                String relationshipType = String.valueOf(map.get(TYPE));
                long startNode = Long.parseLong(String.valueOf(map.get(START_NODE)));
                long endNode = Long.parseLong(String.valueOf(map.get(END_NODE)));
                if (startNode == id && endNode == nextId) {
                    String relPara = "r" + idIdx + "to" + nextIdIdx;
                    relFilter.append(FilterUtil.propertiesFilter(relPara, map));
                    builder.append("-[").append(relPara).append(":").append(relationshipType).append("]->");
                } else {
                    String relPara = "r" + nextIdIdx + "to" + idIdx;
                    relFilter.append(FilterUtil.propertiesFilter(relPara, map));
                    builder.append("<-[").append(relPara).append(":").append(relationshipType).append("]-");
                }
                relFilter.append(" AND ");
            }
        }
        if (nodeFilter.length() > 1 || relFilter.length() > 1) {
            String relFilterStr = relFilter.substring(0, relFilter.length() - 5);
            builder.append(" WHERE ");
            if (nodeFilter.length() > 1 && relFilterStr.length() > 1) {
                builder.append(nodeFilter);
                builder.append(" AND ");
                builder.append(relFilterStr);
            } else if (nodeFilter.length() > 1) {
                builder.append(nodeFilter);
            } else if (relFilter.length() > 1) {
                builder.append(relFilter);
            }
        }
        builder.append(" RETURN {var.p} ");
        return builder.toString();
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

    @Override
    public String toString() {
        return "LoopResult{" +
                "nodeSeqIdList=" + nodeSeqIdList +
                ", pathStr='" + pathStr + '\'' +
                '}';
    }
}
