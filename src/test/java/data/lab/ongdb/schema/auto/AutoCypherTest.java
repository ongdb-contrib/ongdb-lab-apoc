package data.lab.ongdb.schema.auto;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
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
 * @date 2021/4/12 19:05
 */
public class AutoCypherTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(AutoCypher.class);

    @Rule
    public Neo4jRule neo4jProc = new Neo4jRule().withProcedure(AutoCypher.class);

    String JSON_2 = "{\n" +
            "  \"graph\": {\n" +
            "    \"nodes\": [\n" +
            "      {\n" +
            "        \"id\": \"-1024\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-1026\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-1027\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"relationships\": [\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-1026\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1026\",\n" +
            "        \"endNode\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1026\",\n" +
            "        \"endNode\": \"-1027\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
    String JSON_3 = "{\n" +
            "  \"graph\": {\n" +
            "    \"nodes\": [\n" +
            "      {\n" +
            "        \"id\": \"-1024\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-1026\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"relationships\": [\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-1026\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1026\",\n" +
            "        \"endNode\": \"-70549398\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";
    String JSON_4 = "{\n" +
            "  \"graph\": {\n" +
            "    \"nodes\": [\n" +
            "      {\n" +
            "        \"id\": \"-1024\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-1026\"\n" +
            "      },\n" +
            "       {\n" +
            "        \"id\": \"-1028\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"relationships\": [\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-70549398\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-1026\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"startNode\": \"-1026\",\n" +
            "        \"endNode\": \"-1028\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    private static final String JSON_01 = "{\n" +
            "  \"graph\": {\n" +
            "    \"nodes\": [\n" +
            "      {\n" +
            "        \"id\": \"-1024\",\n" +
            "        \"labels\": [\n" +
            "          \"公司\"\n" +
            "        ],\n" +
            "        \"properties_filter\": [\n" +
            "          {\n" +
            "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"count\": \"count>=0 AND count<=10\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"es_filter\": [\n" +
            "          {\n" +
            "            \"es_url\": \"10.20.13.130:9200\",\n" +
            "            \"index_name\": \"index_name_1\",\n" +
            "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-70549398\",\n" +
            "        \"labels\": [\n" +
            "          \"品牌\"\n" +
            "        ],\n" +
            "        \"properties_filter\": [\n" +
            "          {\n" +
            "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"count\": \"count>=0 AND count<=10\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"num\": \"num>=0\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"es_filter\": [\n" +
            "          {\n" +
            "            \"es_url\": \"10.20.13.130:9200\",\n" +
            "            \"index_name\": \"index_name_1\",\n" +
            "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-1026\",\n" +
            "        \"labels\": [\n" +
            "          \"公司\"\n" +
            "        ],\n" +
            "        \"properties_filter\": [\n" +
            "          {\n" +
            "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"count\": \"count>=0 AND count<=10\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"es_filter\": [\n" +
            "          {\n" +
            "            \"es_url\": \"10.20.13.130:9200\",\n" +
            "            \"index_name\": \"index_name_1\",\n" +
            "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"relationships\": [\n" +
            "      {\n" +
            "        \"id\": \"-71148967\",\n" +
            "        \"type\": \"拥有\",\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-70549398\",\n" +
            "        \"properties_filter\": [\n" +
            "          {\n" +
            "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"count\": \"count>=0\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"es_filter\": [\n" +
            "          {\n" +
            "            \"es_url\": \"10.20.13.130:9200\",\n" +
            "            \"index_name\": \"index_name_1\",\n" +
            "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"-11148067\",\n" +
            "        \"type\": \"担保\",\n" +
            "        \"startNode\": \"-1024\",\n" +
            "        \"endNode\": \"-1026\",\n" +
            "        \"properties_filter\": [\n" +
            "          {\n" +
            "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"joint\": \"AND\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"count\": \"count>=0\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"es_filter\": [\n" +
            "          {\n" +
            "            \"es_url\": \"10.20.13.130:9200\",\n" +
            "            \"index_name\": \"index_name_1\",\n" +
            "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    @Test
    public void cypher() {
        AutoCypher autoCypher = new AutoCypher();
        String json = "{\n" +
                "  \"graph\": {\n" +
                "    \"nodes\": [\n" +
                "      {\n" +
                "        \"id\": \"-1024\",\n" +
                "        \"labels\": [\n" +
                "          \"公司\"\n" +
                "        ],\n" +
                "        \"properties_filter\": [\n" +
                "          {\n" +
                "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"count\": \"count>=0 AND count<=10\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"es_filter\": [\n" +
                "          {\n" +
                "            \"es_url\": \"10.20.13.130:9200\",\n" +
                "            \"index_name\": \"index_name_1\",\n" +
                "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"-70549398\",\n" +
                "        \"labels\": [\n" +
                "          \"品牌\"\n" +
                "        ],\n" +
                "        \"properties_filter\": [\n" +
                "          {\n" +
                "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"count\": \"count>=0 AND count<=10\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"num\": \"num>=0\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"es_filter\": [\n" +
                "          {\n" +
                "            \"es_url\": \"10.20.13.130:9200\",\n" +
                "            \"index_name\": \"index_name_1\",\n" +
                "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"-1026\",\n" +
                "        \"labels\": [\n" +
                "          \"公司\"\n" +
                "        ],\n" +
                "        \"properties_filter\": [\n" +
                "          {\n" +
                "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"count\": \"count>=0 AND count<=10\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"es_filter\": [\n" +
                "          {\n" +
                "            \"es_url\": \"10.20.13.130:9200\",\n" +
                "            \"index_name\": \"index_name_1\",\n" +
                "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"relationships\": [\n" +
                "      {\n" +
                "        \"id\": \"-71148967\",\n" +
                "        \"type\": \"拥有\",\n" +
                "        \"startNode\": \"-1024\",\n" +
                "        \"endNode\": \"-70549398\",\n" +
                "        \"properties_filter\": [\n" +
                "          {\n" +
                "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"count\": \"count>=0\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"es_filter\": [\n" +
                "          {\n" +
                "            \"es_url\": \"10.20.13.130:9200\",\n" +
                "            \"index_name\": \"index_name_1\",\n" +
                "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"-11148067\",\n" +
                "        \"type\": \"担保\",\n" +
                "        \"startNode\": \"-1024\",\n" +
                "        \"endNode\": \"-1026\",\n" +
                "        \"properties_filter\": [\n" +
                "          {\n" +
                "            \"hcreatetime\": \"hcreatetime='20201116032333'\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"joint\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"count\": \"count>=0\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"es_filter\": [\n" +
                "          {\n" +
                "            \"es_url\": \"10.20.13.130:9200\",\n" +
                "            \"index_name\": \"index_name_1\",\n" +
                "            \"query\": \"{size:1,query:{term:{product_code:\\\"PF0020020104\\\"}}}\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        // 入参JSON【暂不支持属性间布尔或条件】
        autoCypher.cypher(JSON_01, 0, 50);

    }

    @Test
    public void math() {
        double dividend = 7;    // 被除数
        double divisor = 2;        // 除数
        long limit = (long) Math.ceil(dividend / divisor);
        System.out.println(limit);
    }

    @Test
    public void isLoopGraph() {
        AutoCypher autoCypher = new AutoCypher();
        // JSON_2包含环路，JSON_3包含环路，JSON_4不包含环路
        System.out.println("JSON_2是否包含环路：" + autoCypher.isLoopGraph(JSON_2));
        System.out.println("JSON_3是否包含环路：" + autoCypher.isLoopGraph(JSON_3));
        System.out.println("JSON_4是否包含环路：" + autoCypher.isLoopGraph(JSON_4));
    }

    @Test
    public void loopGraphNodeSeqIds() {
        AutoCypher autoCypher = new AutoCypher();
        // JSON_2包含环路，JSON_3包含环路，JSON_4不包含环路
        System.out.println("JSON_2是否包含环路：" + autoCypher.loopGraphNodeSeqIds(JSON_2));
        System.out.println("JSON_3是否包含环路：" + autoCypher.loopGraphNodeSeqIds(JSON_3));
        System.out.println("JSON_4是否包含环路：" + autoCypher.loopGraphNodeSeqIds(JSON_4));
    }

    @Test
    public void countPathStr() {
        AutoCypher autoCypher = new AutoCypher();
        System.out.println(autoCypher.countPathStr("4->6->1->5->3->8->2"));
        System.out.println(autoCypher.countPathStr("4->6->1"));
        System.out.println(autoCypher.countPathStr("4->6"));
    }

    @Test
    public void schemaIsLoop() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("json", JSON_3);
        // JSON_2包含环路，JSON_3包含环路，JSON_4不包含环路
        Result result = db.execute("RETURN olab.schema.is.loop({json}) AS isLoopGraph", hashMap);
        boolean string = (boolean) result.next().get("isLoopGraph");
        System.out.println(string);
    }

    @Test
    public void schemaLoopGraph() {
        GraphDatabaseService db = neo4jProc.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        // JSON_2包含环路，JSON_3包含环路，JSON_4不包含环路
        map.put("graphData", JSON_2);
        Result res = db.execute("CALL olab.schema.loop({graphData}) YIELD loopResultList RETURN loopResultList", map);
        List<List<Long>> list = (List) res.next().get("loopResultList");
        System.out.println("打印环路分析结果：");
        for (List<Long> longs : list) {
            for (int i = 0; i < longs.size(); i++) {
                System.out.print(longs.get(i));
                if (i == longs.size() - 1) {
                    System.out.println("\n");
                } else {
                    System.out.print("->");
                }
            }
        }
    }

    @Test
    public void schemaLoopCypher() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("ids", new Integer[]{2, 104, 4, 7, 0, 9, 2});
        // JSON_2包含环路，JSON_3包含环路，JSON_4不包含环路
        Result result = db.execute("RETURN olab.schema.loop.cypher({ids}) AS CYPHER", hashMap);
        String cypher = (String) result.next().get("cypher");
        System.out.println(cypher);
    }

    @Test
    public void atomicId() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        for (; ; ) {
            Result result = db.execute("RETURN olab.schema.atomic.id() AS atomicId");
            Long cypher = (Long) result.next().get("atomicId");
            System.out.println(cypher);
        }
    }

}

