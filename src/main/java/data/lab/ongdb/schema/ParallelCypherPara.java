package data.lab.ongdb.schema;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema
 * @Description: TODO
 * @date 2021/3/24 16:29
 */
public class ParallelCypherPara {
    public String query;
    public Map<String, Object> paras;
    public String key;

    public ParallelCypherPara(String query, Map<String, Object> paras, String key) {
        this.query = query;
        this.paras = paras;
        this.key = key;
    }
}
