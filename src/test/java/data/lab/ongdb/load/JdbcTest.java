package data.lab.ongdb.load;

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
 * @PACKAGE_NAME: data.lab.ongdb.load
 * @Description: TODO
 * @date 2021/4/8 9:33
 */
public class JdbcTest {

    @Rule
    public Neo4jRule neo4jProc = new Neo4jRule().withProcedure(Jdbc.class);

    @Test
    public void jdbc() {
        /*
        * 查询同时支持更新：
        * CALL olab.load.jdbc('jdbc:mysql://datalab-contentdb-dev.crkldnwly6ki.rds.cn-north-1.amazonaws.com.cn:3306/analytics_graph_data?user=dev&password=datalabgogo&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC','SELECT autoNodeId(?) AS autoNodeId;',['HORG20f2833a17034a812349e1933d9c5e5f1111'])
        *
        * 该存储过程支持查询更新：SELECT autoNodeId(?) AS autoNodeId;
        *
        * */
        GraphDatabaseService db = neo4jProc.getGraphDatabaseService();
        Map<String, Object> props = new HashMap<>();
        props.put("urlOrKey", "jdbc:mysql://datalab-contentdb-dev.crkldnwly6ki.rds.cn-north-1.amazonaws.com.cn:3306/analytics_graph_data?user=dev&password=datalabgogo&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        props.put("query", "SELECT autoNodeId(?) AS autoNodeId;");
        Map<String, Object> map = new HashMap<>();
        props.put("params", Collections.singletonList("HORG20f2833a17034a812349e1933d9c5e5f11"));
        props.put("config", null);
        Result res = db.execute("CALL olab.load.jdbc({urlOrKey},{query},{params},{config}) YIELD row RETURN row", props);
        System.out.println(res.resultAsString());
    }
}

