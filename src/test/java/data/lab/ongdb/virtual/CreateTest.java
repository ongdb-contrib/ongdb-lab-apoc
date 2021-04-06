package data.lab.ongdb.virtual;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.virtual
 * @Description: TODO
 * @date 2021/4/2 16:02
 */
public class CreateTest {

    @Rule
    public Neo4jRule neo4jProc = new Neo4jRule().withProcedure(Create.class);

    @Rule
    public Neo4jRule neo4jFunction = new Neo4jRule().withFunction(Create.class);

    @Test
    public void vNode() {
        GraphDatabaseService db = neo4jProc.getGraphDatabaseService();
        Map<String, Object> props = new HashMap<>();
        props.put("hcode", "HINDUS");
        props.put("name", "轻工");
        Map<String, Object> map = new HashMap<>();
        map.put("labels", Collections.singletonList("行业"));
        map.put("props", props);
        map.put("identity", -109);
        Result res = db.execute("CALL olab.create.vNode({labels},{props},{identity}) YIELD node RETURN node", map);
        System.out.println(res.resultAsString());
    }

    @Test
    public void vNodeFunction() {
        GraphDatabaseService db = neo4jFunction.getGraphDatabaseService();
        Map<String, Object> props = new HashMap<>();
        props.put("hcode", "HINDUS");
        props.put("name", "轻工");
        Map<String, Object> map = new HashMap<>();
        map.put("labels", Collections.singletonList("行业"));
        map.put("props", props);
        map.put("identity", -109);
        Result res = db.execute("RETURN olab.create.vNode({labels},{identity},{props}) AS node", map);
        System.out.println(res.resultAsString());
    }

    @Test
    public void vPatternFull() {
        GraphDatabaseService db = neo4jProc.getGraphDatabaseService();
        Map<String, Object> props = new HashMap<>();
        props.put("hcode", "HPER");
        props.put("name", "John");
        Map<String, Object> map = new HashMap<>();
        map.put("labelsN", Collections.singletonList("Person"));
        map.put("n", props);
        map.put("identityN", -109);
        map.put("relType", "KNOWS");
        map.put("props", props);
        map.put("identityRel", null);
        map.put("labelsM", Collections.singletonList("Person"));
        map.put("m", props);
        map.put("identityM", -110);
        Result res = db.execute("CALL olab.create.vPatternFull({labelsN},{n},{identityN},{relType},{props},{identityRel},{labelsM},{m},{identityM}) YIELD from,rel,to RETURN from,rel,to", map);
        System.out.println(res.resultAsString());
    }
}

