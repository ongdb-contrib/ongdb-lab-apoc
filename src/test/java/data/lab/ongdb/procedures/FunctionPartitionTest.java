package data.lab.ongdb.procedures;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.*;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures
 * @Description: TODO
 * @date 2020/11/18 11:06
 */
public class FunctionPartitionTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(FunctionPartition.class);

    @Test
    public void structureMergeToListMap() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("fields", Arrays.asList("area_code", "author", 123));
        hashMap.put("items", Arrays.asList(Arrays.asList("001", "HORG001", 234), Arrays.asList("002", "HORG002", 344)));

        Result result = db.execute("RETURN olab.structure.mergeToListMap({fields},{items}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void idsBatch() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("min", 1);
        hashMap.put("max", 1000000000);
        hashMap.put("batch", 500_0000);

        Result result = db.execute("RETURN olab.ids.batch({min},{max},{batch}) AS value", hashMap);
        List<List<Long>> ids = (List<List<Long>>) result.next().get("value");
        System.out.println(ids);
    }

    @Test
    public void olabRepalceTest() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        List<Map<String, Object>> replaceListMap = new ArrayList<>();
        Map<String, Object> reMap1 = new HashMap<>();
        reMap1.put("raw","{url}");
        reMap1.put("rep","'test-url'");
        Map<String, Object> reMap2 = new HashMap<>();
        reMap2.put("raw","{sql}");
        reMap2.put("rep","'test-sql'");
        replaceListMap.add(reMap1);
        replaceListMap.add(reMap2);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("string", "RETURN {url} AS url,{sql} AS sql");
        hashMap.put("replaceListMap", replaceListMap);

        Result result = db.execute("RETURN olab.replace({string},{replaceListMap}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void olabRepalceTest02() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        List<Map<String, Object>> replaceListMap = new ArrayList<>();
        Map<String, Object> reMap1 = new HashMap<>();
        reMap1.put("raw","{url}");
        reMap1.put("rep","'test-url'");
        Map<String, Object> reMap2 = new HashMap<>();
        reMap2.put("raw","{sql}");
        reMap2.put("rep","'SELECT parent_pcode AS `name`,CONVERT(DATE_FORMAT(hupdatetime,\\'%Y%m%d%H%i%S\\'),UNSIGNED INTEGER) AS hupdatetime FROM MSTR_ORG_PRE'");
        reMap2.put("escape",true);
        replaceListMap.add(reMap1);
        replaceListMap.add(reMap2);

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("string", "RETURN {url} AS url,{sql} AS sql");
        hashMap.put("replaceListMap", replaceListMap);

        Result result = db.execute("RETURN olab.replace({string},{replaceListMap}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void escape() {
        String str = "CONVERT(DATE_FORMAT(hupdatetime,'%Y%m%d%H%i%S'),UNSIGNED INTEGER)";
        System.out.println(str.replace("'","\\'"));
    }

    @Test
    public void escapeTest01() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
//        hashMap.put("string", "CONVERT(DATE_FORMAT(hupdatetime,\'%Y%m%d%H%i%S\'),UNSIGNED INTEGER)");
//        hashMap.put("string", "");
        hashMap.put("string", null);
        Result result = db.execute("RETURN olab.escape({string}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }
}

