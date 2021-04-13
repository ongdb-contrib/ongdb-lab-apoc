package data.lab.ongdb.schema.auto;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema.auto
 * @Description: TODO
 * @date 2021/4/12 18:43
 */
public class AutoCypher {

    /**
     * @param json:入参    #############
     *                   属性过滤器属性之间过滤，连接方式只支持AND
     *                   #############
     *                   属性过滤器(properties_filter)：
     *                   包含【STRING】：‘ name CONTAINS '北京' '
     *                   等于【STRING、INT、LONG】：‘ name='北京' ’  ‘ count=0 ’
     *                   大于【INT、LONG】：‘ count>0 ’
     *                   小于【INT、LONG】：‘ count<0 ’
     *                   大于等于【INT、LONG】：‘ count>=0 ’
     *                   小于等于【INT、LONG】：‘ count>=0 ’
     *                   范围-左闭右闭【INT、LONG】：‘ count>=0 AND count<=10 ’
     *                   范围-左闭右开【INT、LONG】：‘ count>=0 AND count<10 ’
     *                   范围-左开右闭【INT、LONG】：‘ count>0 AND count<=10 ’
     *                   范围-左开右开【INT、LONG】：‘ count>0 AND count<10 ’
     *                   #############
     *                   ES-QUERY
     *                   #############
     *                   ES过滤器(es_filter)：
     *                   ES-QUERY-DSL【去掉JSON引号的查询语句】eg.{size:1,query:{term:{product_code:"PF0020020104"}}}
     * @param skip:翻页参数
     * @param limit:限制参数
     * @return
     * @Description: TODO
     * <p>
     * 使用前先安装APOC用过程‘apoc.custom.asFunction’生成函数‘custom.es.result.bool’【简化ES访问语句】
     * ```
     * RETURN custom.es.result.bool({es-url},{index-name},{query-dsl}) AS boolean
     * RETURN custom.es.result.bool('10.20.13.130:9200','dl_default_indicator_def',{size:1,query:{term:{product_code:"PF0020020104"}}}) AS boolean
     * ```
     * ```
     * CALL apoc.es.query('10.20.13.130:9200','dl_default_indicator_def','',null,{size:1,query:{term:{product_code:"PF0020020104"}}}) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,'RETURN TRUE AS countBool'],'RETURN FALSE AS countBool') YIELD value RETURN value.countBool AS bool
     * ```
     * ```
     * CALL apoc.custom.asFunction(
     * 'es.result.bool',
     * 'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,\'RETURN TRUE AS countBool\'],\'RETURN FALSE AS countBool\') YIELD value RETURN value.countBool AS bool',
     * 'BOOLEAN',
     * [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
     * false,
     * '通过判断ES查询结果返回FALSE或者TRUE【结果集大于0返回TRUE】'
     * );
     * ```
     */
    @UserFunction(name = "olab.schema.auto.cypher")
    @Description("```\n" +
            "RETURN olab.schema.auto.cypher({JSON},{SKIP},{LIMIT}) AS cypher\n" +
            "```\n" +
            "```\n" +
            "输入：\n" +
            "    JSON参数\n" +
            "       【{包含节点、关系、属性过滤器、ES过滤器}、{SKIP参数}、{LIMIT参数(-1表示返回全部)}】\n" +
            "    过滤器设计：传入查询碎片直接拼接查询碎片\n" +
            "输出：拼接好的CYPHER语句\n" +
            "```")
    public String cypher(@Name("json") String json, @Name("skip") long skip, @Name("limit") long limit) {
        JSONObject paras = JSONObject.parseObject(json);
        /*
        * 转换图结构为矩阵寻找所有子图路径
        * */

        return null;
    }
}

