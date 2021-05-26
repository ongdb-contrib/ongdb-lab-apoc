package data.lab.ongdb.schema.auto;

import org.junit.Test;

import static org.junit.Assert.*;

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
}


