package data.lab.ongdb.clustering;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.procedure.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.clustering
 * @Description: TODO(聚类过程)
 * @date 2020/5/29 13:38
 */
public class Cluster {
    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    /**
     * @param masterNodeLabelList:对此类标签的节点进行聚类计算
     * @param relWeightMap:权重分值分配map【权重为-1表示此类节点关系之间不计算相似度直接分类】
     * @param clusterFocusNodeLabel:生成聚类中心的节点标签
     * @param threshold:最小相似权重设置(关系权重加总之后的得分不能小于这个值)
     * @param slavesMarkField:对所有的从节点设置主节点的ID作为分簇标识
     * @return
     * @Description: TODO(对指定关系模式的节点进行聚类操作 - 并对所有从节点设置主节点的唯一ID - 默认支持两层关系碰撞做聚类)
     */
    @Procedure(name = "olab.cluster.collision", mode = Mode.WRITE)
    @Description("CALL olab.cluster.collision({masterNodeLabel},{relName1:weightiness,relName2:weightiness...},{clusterFocusNodeLabel},{threshold},{slavesMarkField}) YIELD clusterNum")
    public Stream<ClusterResult> cluster(@Name("masterNodeLabel") List<String> masterNodeLabelList,
                                         @Name("relWeightMap") Map<String, Number> relWeightMap,
                                         @Name("clusterFocusNodeLabel") String clusterFocusNodeLabel,
                                         @Name("threshold") Number threshold,
                                         @Name("slavesMarkField") String slavesMarkField) {
        StringBuilder builder = new StringBuilder();
        for (String label : masterNodeLabelList) {
            builder.append(label).append(":");
        }
        String masterNodeLabel = builder.substring(0, builder.length() - 1);
        String readCypher;
        String writeClusterFocusCypher;
        String writeClusterIdCypher;
        long count = 0;
        Result resultCount = db.execute("MATCH (n:" + masterNodeLabel + ") RETURN COUNT(*) AS count");
        if (resultCount.hasNext()) {
            count = (long) resultCount.next().get("count");
        }
        for (int i = 0; i < count; i++) {
//            readCypher = "MATCH (n:" + masterNodeLabel + ") WITH n SKIP " + i + " LIMIT 1\n" +
//                    "MATCH (m:" + masterNodeLabel + ") WHERE n<>m WITH n,m\n" +
//                    "MATCH p=(n)-[*..2]-(m) WHERE n<>m \n" +
//                    "WITH [r IN relationships(p) | type(r)] AS relList,n,m\n" +
//                    "WITH collect(relList) AS collectList,n,m\n" +
//                    "CALL olab.similarity.collision(n,m,collectList," + JSONTool.removeJSONObjKeyDoubleQuotationMark(JSONObject.parseObject(JSON.toJSONString(relWeightMap))) + ") YIELD similarity,startNode,endNode \n" +
//                    "WHERE toint(similarity)>" + threshold + " " +
//                    "RETURN startNode.name,endNode.name,similarity ORDER BY similarity DESC";
//            Result result = db.execute(readCypher);

            readCypher = "MATCH (n:" + masterNodeLabel + ") WITH n SKIP " + i + " LIMIT 1\n" +
                    "MATCH (m:" + masterNodeLabel + ") WHERE n<>m WITH n,m\n" +
                    "MATCH p=(n)-[*..2]-(m) WHERE n<>m \n" +
                    "WITH [r IN relationships(p) | type(r)] AS relList,n,m\n" +
                    "RETURN collect(relList) AS collectList,n,m";
            Result result = db.execute(readCypher);

            /////
            List<Long[]> rawLongList = new ArrayList<>();
            while (result.hasNext()) {
                long idN = (long) result.next().get("idN");
                long idM = (long) result.next().get("idM");
                List<List<String>> collectList = (List<List<String>>) result.next().get("collectList");
                long similarity = getWeightinessSimilarity(relWeightMap, collectList);
                rawLongList.add(new Long[]{idN, idM, similarity});
            }

            // 通过阈值过滤
            List<Long[]> filterLongList = rawLongList.stream().filter(v -> v[2] > threshold.longValue())
                    .sorted(Comparator.comparing(v -> v[2])).collect(Collectors.toList());
            /////

            // 相似节点列表
//            List<Long> longList = new ArrayList<>();
//            while (result.hasNext()) {
//                Node startNode = (Node) result.next().get("startNode");
//                Node endNode = (Node) result.next().get("endNode");
//                long startNodeId = startNode.getId();
//                long endNodeId = endNode.getId();
//                longList.add(endNodeId);
//                if (!longList.contains(startNodeId)) {
//                    longList.add(startNodeId);
//                }
//            }
            List<Long> longList = filterLongList

            if (!longList.isEmpty()) {
                JSONArray longListArray = JSONArray.parseArray(JSON.toJSONString(longList));
                // 找到信息量最大的点做为聚类中心并设置标签
                writeClusterFocusCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " REMOVE n:" + clusterFocusNodeLabel + " WITH n,size((n)-[]-()) AS size ORDER BY size DESC LIMIT 1 \n" +
                        "SET n:" + clusterFocusNodeLabel + " RETURN id(n) AS id";
                Result focusResult = db.execute(writeClusterFocusCypher);
                long clusterId = -1;
                if (focusResult.hasNext()) {
                    clusterId = (long) focusResult.next().get("id");
                }

                // 为所有点设置聚簇ID
                writeClusterIdCypher = "MATCH (n) WHERE id(n) IN " + longListArray.toJSONString() + " SET n." + slavesMarkField + "=" + clusterId;
                db.execute(writeClusterIdCypher);
            }
        }
        Result result = db.execute("MATCH (n:" + clusterFocusNodeLabel + ") RETURN COUNT(*) AS count");
        long clusterNum = 0;
        if (result.hasNext()) {
            clusterNum = (long) result.next().get("count");
        }
        return Stream.of(new ClusterResult(clusterNum));
    }

    private long getWeightinessSimilarity(Map<String, Number> weightinessMap, List<List<String>> relPathList) {
        int similarity = 0;
        for (List<String> pathRelList : relPathList) {
            String relName = pathRelList.get(0);
            if (pathRelList.size() == 1) {
                if (weightinessMap.containsKey(relName)) {
                    similarity += weightinessMap.get(relName).intValue();
                }
            } else if (pathRelList.size() == 2 && pathRelList.get(0).equals(pathRelList.get(1))) {
                if (weightinessMap.containsKey(relName)) {
                    similarity += weightinessMap.get(relName).intValue();
                }
            }
        }
        return similarity;
    }

    public static class ClusterResult {
        public final Number clusterNum;

        public ClusterResult(Number clusterNum) {
            this.clusterNum = clusterNum;
        }
    }
}

