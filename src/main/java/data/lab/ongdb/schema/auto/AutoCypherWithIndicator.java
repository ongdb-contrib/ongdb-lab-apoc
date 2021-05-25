package data.lab.ongdb.schema.auto;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.lab.ongdb.result.VirtualNode;
import data.lab.ongdb.result.VirtualPathResult;
import data.lab.ongdb.result.VirtualRelationship;
import org.neo4j.graphdb.*;
import org.neo4j.procedure.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO(图模型分析函数与过程)
 * @date 2021/5/25 10:04
 */
public class AutoCypherWithIndicator {

    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseService db;

    /**
     * 过滤器
     */
    private final static String ES_FILTER = "es_filter";

    private final static String VFMAP_KEY_PREFIX = "_";

    private final static String FUNC_CUSTOM_ES_RESULT_BOOL = "custom.es.result.bool";
    private final static String FUNC_CUSTOM_ES_RESULT = "custom.es.result";

    private final static String FUNC_ES_RAW_SIZE = "{size:1,query";
    private final static String FUNC_ES_SIZE = "{size:1000,query";

    private final static String FUNC_VAR_MARK = "{var}.hcode";

    /*
    * 数据建模时指定的唯一码值
    * */
    private final static String HCODE = "hcode";
    private final static String HCODE_DETAIL = "hcode_detail";

    /**
     * @param relationship:关系
     * @param atomicId:原子性ID【对一条关系中的关系ID和节点ID都乘这个值】
     * @param vFMap:过滤器和ID的绑定格式样例                   {
     *                                              "96767732": [],
     *                                              "1112350872": [
     *                                              {
     *                                              "es_filter": "custom.es.result.bool('10.20.13.130:9200','gh_ind_rel_company_guarantee_company',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}.hcode}}]}}})"
     *                                              }
     *                                              ]
     *                                              }
     *                                              >解析es_filter生成一个可执行的查询ES的语句
     *                                              ```
     *                                              es_filter_execute：
     *                                              custom.es.result.bool 替换为 custom.es.result
     *                                              {size:1,query 替换为 {size:10000,query
     *                                              {var}.hcode 替换为生成值的查询
     *                                              ```
     * @return
     * @Description: TODO(生成虚拟图时挂上指标数据 - 需要预装函数：custom.es.result)
     *
     * ```
     * CALL apoc.custom.asFunction(
     *     'es.result',
     *     'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value RETURN value',
     *     'MAP',
     *     [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
     *     false,
     *     '返回ES查询结果'
     * );
     * ```
     * ```
     * RETURN custom.es.result({esuUrl},{indexName},{queryDsl}) AS result
     * RETURN custom.es.result('10.20.13.130:9200','gh_ind_rel_company_guarantee_company',{size:100,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:'852c1ea85d8dd6b1354aa1a786dbc1db'}}]}}}) AS result
     * ```
     *
     */
    @Procedure(name = "olab.schema.loop.vpath.ind", mode = Mode.READ)
    @Description("CALL olab.schema.loop.vpath.ind({relationship},{atomicId},{vFMap}) YIELD from,rel,to RETURN from,rel,to")
    public Stream<VirtualPathResult> vpath(@Name("relationship") Relationship relationship, @Name("atomicId") Long atomicId, @Name(value = "vFMap", defaultValue = "{}") Map<String, Object> vFMap) {
        if (vFMap == null || vFMap.isEmpty()) {
            RelationshipType type = relationship.getType();

            Node fromNode = relationship.getStartNode();
            long fromId = fromNode.getId() * atomicId;
            VirtualNode from = new VirtualNode(fromId, fromNode.getLabels(), fromNode.getAllProperties(), null);

            Node toNode = relationship.getEndNode();
            long toId = toNode.getId() * atomicId;
            VirtualNode to = new VirtualNode(toId, toNode.getLabels(), toNode.getAllProperties(), null);

            Relationship rel = new VirtualRelationship(relationship.getId() * atomicId, from, to, type).withProperties(relationship.getAllProperties());
            return Stream.of(new VirtualPathResult(from, rel, to));
        } else {
            return vpathInd(relationship, atomicId, vFMap);
        }
    }

    /**
     * @param relationship:关系
     * @param atomicId:原子性ID【对一条关系中的关系ID和节点ID都乘这个值】
     * @param vFMap:过滤器和ID的绑定格式样例
     * @return
     * @Description: TODO
     */
    private Stream<VirtualPathResult> vpathInd(Relationship relationship, Long atomicId, Map<String, Object> vFMap) {
        /*
         * 过滤vFMap生成revFMap
         * 生成挂好指标的indWithVFMap
         * */
        Node fromNode = relationship.getStartNode();
        long fId = fromNode.getId();
        Node toNode = relationship.getEndNode();
        long tId = toNode.getId();
        long rId = relationship.getId();

        Map<String, Map<String, Object>> revFMap = new HashMap<>();
        revFMap.put(VFMAP_KEY_PREFIX + fId, excuteQueryInd(parseESQuery(vFMap.get(VFMAP_KEY_PREFIX + fId), fromNode.getAllProperties().get(HCODE))));
        revFMap.put(VFMAP_KEY_PREFIX + tId, excuteQueryInd(parseESQuery(vFMap.get(VFMAP_KEY_PREFIX + tId), toNode.getAllProperties().get(HCODE))));
        revFMap.put(VFMAP_KEY_PREFIX + rId, excuteQueryInd(parseESQuery(vFMap.get(VFMAP_KEY_PREFIX + rId), relationship.getAllProperties().get(HCODE))));

        VirtualNode from = new VirtualNode(fId * atomicId, fromNode.getLabels(), addMap(fromNode.getAllProperties(), revFMap.get(VFMAP_KEY_PREFIX + fId)), null);
        VirtualNode to = new VirtualNode(tId * atomicId, toNode.getLabels(), addMap(toNode.getAllProperties(), revFMap.get(VFMAP_KEY_PREFIX + tId)), null);
        Relationship rel = new VirtualRelationship(relationship.getId() * atomicId, from, to, relationship.getType()).withProperties(addMap(relationship.getAllProperties(), revFMap.get(VFMAP_KEY_PREFIX + rId)));

        return Stream.of(new VirtualPathResult(from, rel, to));
    }

    /**
     * @param allProperties:属性
     * @param stringObjectMap:其它指标属性【ES挂载的指标属性】
     * @return
     * @Description: TODO(合并属性MAP)
     */
    private Map<String, Object> addMap(Map<String, Object> allProperties, Map<String, Object> stringObjectMap) {
        // .value.hits.hits _source
        if (stringObjectMap != null && !stringObjectMap.isEmpty()) {
            JSONObject object = JSONObject.parseObject(JSON.toJSONString(stringObjectMap));
            JSONArray jsonArray = object.getJSONObject("value").getJSONObject("hits").getJSONArray("hits")
                    .stream().map(v -> {
                        JSONObject jsonObject = (JSONObject) v;
                        return jsonObject.getJSONObject("_source");
                    }).collect(Collectors.toCollection(JSONArray::new));
            allProperties.put(HCODE_DETAIL, jsonArray.toJSONString());
        }
        return allProperties;
    }

    /**
     * @param
     * @return
     * @Description: TODO(执行挂指标的查询语句)
     */
    private Map<String, Object> excuteQueryInd(String parseESQuery) {
        Map<String, Object> map = new HashMap<>();
        if (parseESQuery != null && !"".equals(parseESQuery)) {
            try (Transaction tx = db.beginTx()) {
                Result result = db.execute(parseESQuery);
                map = (Map<String, Object>) result.next().get("result");
                tx.success();
            }
        }
        return map;
    }

    /**
     * @param
     * @return
     * @Description: TODO(解析过滤器中的ES查询语句)
     */
    private String parseESQuery(Object object, Object entityUniqueCode) {
        List<Map<String, String>> filterList = (List<Map<String, String>>) object;
        Optional<Map<String, String>> rawEsFilterMap = filterList.stream().filter(v -> v.containsKey(ES_FILTER)).findFirst();
        return rawEsFilterMap.map(stringStringMap -> parseExeEsQuery(stringStringMap.get(ES_FILTER), entityUniqueCode)).orElse(null);
    }

    /**
     * @param
     * @return
     * @Description: TODO(将esFilterQuery进行替换操作)
     */
    private String parseExeEsQuery(String esFilterQuery, Object entityUniqueCode) {
        if (entityUniqueCode != null && !"".equals(entityUniqueCode)) {
            return "RETURN " + esFilterQuery.replace(FUNC_CUSTOM_ES_RESULT_BOOL, FUNC_CUSTOM_ES_RESULT)
                    .replace(FUNC_ES_RAW_SIZE, FUNC_ES_SIZE)
                    .replace(FUNC_VAR_MARK, "'" + entityUniqueCode + "'")
                    + " AS result";
        }
        return null;
    }

    /**
     * @param esFilter:ES返回布尔值的过滤器
     * @param entityUniqueCode:实体ID与ES绑定的唯一代码
     * @return
     * @Description: TODO(ES过滤器转换为ES查询语句)
     */
    @UserFunction(name = "olab.es.filter.transfer")
    @Description("RETURN olab.es.filter.transfer({esFilter},{entityUniqueCode}) AS esQuery")
    public String esFilterTransfer(@Name("esFilter") String esFilter,@Name("entityUniqueCode") String entityUniqueCode) {
        return parseExeEsQuery(esFilter,entityUniqueCode);
    }
}


