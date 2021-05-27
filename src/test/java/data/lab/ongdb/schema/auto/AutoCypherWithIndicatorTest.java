package data.lab.ongdb.schema.auto;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.List;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/5/26 9:53
 */
public class AutoCypherWithIndicatorTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(AutoCypherWithIndicator.class);

    @Test
    public void parseFuncVarField01() {
        AutoCypherWithIndicator withIndicator = new AutoCypherWithIndicator();
        String funcVarField = withIndicator.parseFuncVarField("custom.es.result.bool(\\'10.20.13.130:9200\\',\\'gh_ind_rel_company_guarantee_company\\',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}.hcode_1231}}]}},_source:[\\'defineDate\\',\\'amount\\']})");
        System.out.println(funcVarField);
    }

    @Test
    public void parseFuncVarField02() {
        AutoCypherWithIndicator withIndicator = new AutoCypherWithIndicator();
        String funcVarField = withIndicator.parseFuncVarField("custom.es.result.bool(\\'10.20.13.130:9200\\',\\'gh_ind_rel_company_guarantee_company\\',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}.hcode}}]}},_source:[\\'defineDate\\',\\'amount\\']})");
        System.out.println(funcVarField);
    }

    @Test
    public void parseFuncVarField03() {
        AutoCypherWithIndicator withIndicator = new AutoCypherWithIndicator();
        String funcVarField = withIndicator.parseFuncVarField("custom.es.result.bool(\\'10.20.13.130:9200\\',\\'gh_ind_rel_company_guarantee_company\\',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}.hcode_}}]}},_source:[\\'defineDate\\',\\'amount\\']})");
        System.out.println(funcVarField);
    }

    @Test
    public void parseFuncVarField04() {
        AutoCypherWithIndicator withIndicator = new AutoCypherWithIndicator();
        String funcVarField = withIndicator.parseFuncVarField("custom.es.result.bool(\\'10.20.13.130:9200\\',\\'gh_ind_rel_company_guarantee_company\\',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}._hcode}}]}},_source:[\\'defineDate\\',\\'amount\\']})");
        System.out.println(funcVarField);
    }

    @Test
    public void test() {
        String es = "custom.es.result.bool(\\'10.20.13.130:9200\\',\\'gh_ind_rel_company_guarantee_company\\',{size:1,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:{var}.hcode}}]}},_source:[\\'defineDate\\',\\'amount\\']})";
        String[] strings = es.split("\\{var\\}");
        for (String string : strings) {
            System.out.println(string);
        }
    }

    @Test
    public void name() {
        String JSON_ARRAY_PRE = "[{\"";
        String raw = "[{\"amount\":210000000,\"defineDate\":20110414000000},{\"amount\":210000000,\"defineDate\":20110414000000}]";
        System.out.println(raw.contains(JSON_ARRAY_PRE));
    }

    @Test
    public void graphResultTransfer() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        db.execute("CREATE (n:Test) SET n:Person,n.name='test',n.indicators='[{\"ind_der_name\":\"国星光电:按产品划分:其他业务:主营业务成本占比\"}]'");
        Result result = db.execute("MATCH (n) RETURN olab.result.transfer(n) AS listMap");
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) result.next().get("listMap");
        mapList.forEach(System.out::println);
    }
}


