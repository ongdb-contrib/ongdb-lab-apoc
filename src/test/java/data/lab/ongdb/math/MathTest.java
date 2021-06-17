package data.lab.ongdb.math;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.math
 * @Description: TODO
 * @date 2021/6/17 13:24
 */
public class MathTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(Math.class);

    @Test
    public void log() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("value", 10000000);
        Result result = db.execute("RETURN olab.math.log10({value}) AS value", hashMap);
        Double aDouble = (Double) result.next().get("value");
        System.out.println(aDouble);
    }

    @Test
    public void log1p() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("value", 10000000);
        Result result = db.execute("RETURN olab.math.log1p({value}) AS value", hashMap);
        Double aDouble = (Double) result.next().get("value");
        System.out.println(aDouble);
    }

    @Test
    public void log10() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("value", 10000000);
        Result result = db.execute("RETURN olab.math.log10({value}) AS value", hashMap);
        Double aDouble = (Double) result.next().get("value");
        System.out.println(aDouble);
    }

    @Test
    public void logWithBase() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("value", 10000000);
        hashMap.put("base", 1000);
        Result result = db.execute("RETURN olab.math.logWithBase({value},{base}) AS value", hashMap);
        Double aDouble = (Double) result.next().get("value");
        System.out.println(aDouble);
    }

    @Test
    public void logWithBase_02() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("value", 10000);
        hashMap.put("base", 10);
        Result result = db.execute("RETURN olab.math.logWithBase({value},{base}) AS value", hashMap);
        Double aDouble = (Double) result.next().get("value");
        System.out.println(aDouble);
    }
}

