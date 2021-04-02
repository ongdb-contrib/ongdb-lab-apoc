package data.lab.ongdb.schema;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema
 * @Description: TODO
 * @date 2021/3/24 15:59
 */
public class Schema {

    private final static List<Character> CHAR_LIST = Arrays.asList('(', ':', ')', '-', '>', '=', '<', '[', ']');

//    @Procedure(name = "olab.schema.parse", mode = Mode.READ)
//    @Description("CALL olab.schema.parse('CYPHER-PATTERN-QUERY',schemaMap,dataSchemaList) YIELD query,paras,key RETURN query,paras,key")
//    public Stream<ParallelCypherPara> parse(@Name("query") String query, @Name("schemaMap") Map<String, Map<String, String>> schemaMap, @Name("dataSchemaList") List<Map<String, String>> dataSchemaList) {
//        /*
//         * 1、替换STD_SCHEMA标签为DATA_SCHEMA标签
//         * 2、拆分CYPHER-PATTERN
//         * 3、解析参数
//         * */
//
//        /*
//         * 1、替换STD_SCHEMA标签为DATA_SCHEMA标签
//         *  <1>识别三元组模式
//         *  <2>对模式中的标签和关系进行替换
//         * */
//        System.out.println("Query" + query);
//
//        /*
//        * 解析原始图模型路径，列表顺序与路径顺序一致
//        * */
//        List<Map<String, TripleData>> pathParseList = new ArrayList<>();
//
//        /*
//        * 使用正则提取固定模式的字符串
//        * */
//
//        char[] chars = query.toCharArray();
//        int temp = 0;
//        for (int i = 0; i < chars.length; i++) {
//            if (CHAR_LIST.contains(chars[i]) && temp==0){
//
//                temp++;
//            }
//        }
//
//        return null;
//    }

    @Procedure(name = "olab.schema.parse", mode = Mode.READ)
    @Description("CALL olab.schema.parse('CYPHER-PATTERN-QUERY',schemaMap,dataSchemaList) YIELD query,paras,key RETURN query,paras,key")
    public Stream<ParallelCypherPara> parse(@Name("query") String query, @Name("schemaMap") Map<String, Map<String, String>> schemaMap, @Name("dataSchemaList") List<Map<String, String>> dataSchemaList) {
        /*
         * 1、替换STD_SCHEMA标签为DATA_SCHEMA标签
         * 2、拆分CYPHER-PATTERN
         * 3、解析参数
         * */

        /*
         * 1、替换STD_SCHEMA标签为DATA_SCHEMA标签
         *  <1>识别三元组模式
         *  <2>对模式中的标签和关系进行替换
         * */
        System.out.println("Query" + query);

        /*
         * 解析原始图模型路径，列表顺序与路径顺序一致
         * */
        List<Map<String, TripleData>> pathParseList = new ArrayList<>();

        /*
         * 使用正则提取固定模式的字符串
         * */

        char[] chars = query.toCharArray();
        int temp = 0;
        for (int i = 0; i < chars.length; i++) {
            if (CHAR_LIST.contains(chars[i]) && temp==0){

                temp++;
            }
        }
        return null;
    }
}

