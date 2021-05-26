package data.lab.ongdb.schema.auto;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/4/20 17:33
 */
public class FilterUtil {

    /**
     * 变量代码
     */
    private final static String VAR_NAME = "{var}.";

    /**
     * 集群需要预安装此函数
     */
    private final static String ES_RESULT_BOOL_FILTER = "custom.es.result.bool({es-url},{index-name},{query-dsl})";

    /**
     * 过滤器
     */
    private final static String PROPERTIES_FILTER = "properties_filter";
    private final static String ES_FILTER = "es_filter";

    /**
     * @param varName:变量名
     * @param propertiesFilter:属性过滤条件
     * @return
     * @Description: TODO(拼接属性过滤条件)
     */
    public static String propertiesFilter(String varName, JSONArray propertiesFilter) {
        StringBuilder builder = new StringBuilder();
        if (propertiesFilter != null && !propertiesFilter.isEmpty()) {
            for (Object obj : propertiesFilter) {
                JSONObject object = (JSONObject) obj;
                for (String key : object.keySet()) {
                    builder.append(object.getString(key).replace(VAR_NAME, varName + "."));
                    builder.append(" AND ");
                }
            }
            return builder.substring(0, builder.length() - 5);
        }
        return "";
    }

    /**
     * @param
     * @return
     * @Description: TODO(封装ES过滤器)
     */
    public static String esFilter(String varName, JSONArray esFilter) {
        if (esFilter != null && !esFilter.isEmpty()) {
            for (Object obj : esFilter) {
                JSONObject object = (JSONObject) obj;
                String esUrl = object.getString("es_url");
                String indexName = object.getString("index_name");
                String query = object.getString("query");
                if (query == null || !query.contains(":{var}")) {
                    throw new IllegalArgumentException("ES Filter error - Missing：{term:{*:{var}.*}}");
                }
                return ES_RESULT_BOOL_FILTER.replace("{es-url}", "'" + esUrl + "'").replace("{index-name}", "'" + indexName + "'").replace("{query-dsl}", query.replace("{var}", "" + varName));
            }
        }
        return "";
    }

    /**
     * @param varName:变量名
     * @param propertiesFilter:属性过滤条件
     * @return
     * @Description: TODO(拼接属性过滤条件)
     */
    public static String propertiesFilter(String varName, JSONObject propertiesFilter) {
        if (propertiesFilter == null || propertiesFilter.isEmpty()) {
            return "";
        }
        String proFilter = propertiesFilter(varName, propertiesFilter.getJSONArray(PROPERTIES_FILTER));
        String esFilter = esFilter(varName, propertiesFilter.getJSONArray(ES_FILTER));
        return appendFilter(proFilter, esFilter);
    }

    /**
     * @param varName:变量名
     * @param propertiesFilter:属性过滤条件
     * @return
     * @Description: TODO(拼接属性过滤条件)
     */
    public static String propertiesFilterObj(String varName, JSONObject propertiesFilter) {
        if (propertiesFilter == null || propertiesFilter.isEmpty()) {
            return "";
        }
        String proFilter = propertiesFilter(varName, propertiesFilter.getJSONArray(PROPERTIES_FILTER));
        String esFilter = esFilter(varName, propertiesFilter.getJSONArray(ES_FILTER));
        return appendFilterObj(proFilter, esFilter);
    }

    private static String appendFilterObj(String proFilter, String esFilter) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (proFilter != null && !"".equals(proFilter)) {
            builder.append("{");
            builder.append(PROPERTIES_FILTER).append(":");
            builder.append("'");
            builder.append(escape(proFilter));
            builder.append("'");
            builder.append("}");
        }
        if (esFilter != null && !"".equals(esFilter)) {
            if (proFilter != null && !"".equals(proFilter)) {
                builder.append(",");
            }
            builder.append("{");
            builder.append(ES_FILTER).append(":");
            builder.append("'");
            builder.append(escape(esFilter));
            builder.append("'");
            builder.append("}");
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(字符串转义)
     */
    private static String escape(String rawString) {
        return rawString != null ? rawString.replace("'", "\\'") : rawString;
    }

    /**
     * @param propertiesFilter:属性过滤条件
     * @return
     * @Description: TODO(属性过滤个数 - ES的query计算为一个过滤)
     */
    public static int propertiesFilter(JSONObject propertiesFilter) {
        if (propertiesFilter == null || propertiesFilter.isEmpty()) {
            return 0;
        }
        JSONArray propertiesFilterJSONArray = propertiesFilter.getJSONArray(PROPERTIES_FILTER);
        JSONArray esFilterJArray = propertiesFilter.getJSONArray(ES_FILTER);
        int proFilter = propertiesFilterJSONArray == null ? 0 : propertiesFilterJSONArray.size();
        int esFilter = esFilterJArray == null ? 0 : esFilterJArray.size();
        return proFilter + esFilter;
    }

    /**
     * @param varName:变量名
     * @param map:属性过滤条件
     * @return
     * @Description: TODO(拼接属性过滤条件)
     */
    public static String propertiesFilter(String varName, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String proFilter = propertiesFilter(varName, (JSONArray) map.get(PROPERTIES_FILTER));
        String esFilter = esFilter(varName, (JSONArray) map.get(ES_FILTER));
        return appendFilter(proFilter, esFilter);
    }

    /**
     * @param varName:变量名
     * @param map:属性过滤条件
     * @return
     * @Description: TODO(拼接属性过滤条件)
     */
    public static String propertiesFilterObj(String varName, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String proFilter = propertiesFilter(varName, (JSONArray) map.get(PROPERTIES_FILTER));
        String esFilter = esFilter(varName, (JSONArray) map.get(ES_FILTER));
        return appendFilterObj(proFilter, esFilter);
    }

    /**
     * @param
     * @return
     * @Description: TODO(拼接属性过滤器和ES过滤器)
     */
    private static String appendFilter(String proFilter, String esFilter) {
        StringBuilder builder = new StringBuilder();
        if (!"".equals(proFilter) && !"".equals(esFilter)) {
            builder.append(proFilter).append(" AND ").append(esFilter);
        } else if (!"".equals(proFilter)) {
            builder.append(proFilter);
        } else if (!"".equals(esFilter)) {
            builder.append(esFilter);
        }
        return builder.toString();
    }
}

