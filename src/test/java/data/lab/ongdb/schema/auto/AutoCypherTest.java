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
 * @date 2021/4/12 19:05
 */
public class AutoCypherTest {

    @Test
    public void cypher() {
        AutoCypher autoCypher = new AutoCypher();
        String json="{\n" +
                "  \"graph\": {\n" +
                "    \"nodes\": [\n" +
                "      {\n" +
                "        \"id\": \"-1024\",\n" +
                "        \"labels\": [\n" +
                "          \"��˾\"\n" +
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
                "          \"Ʒ��\"\n" +
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
                "      }\n" +
                "    ],\n" +
                "    \"relationships\": [\n" +
                "      {\n" +
                "        \"id\": \"-71148967\",\n" +
                "        \"type\": \"�Ǳ�ΥԼ\",\n" +
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
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        // ���JSON���ݲ�֧�����Լ䲼����������
        autoCypher.cypher(json,0,50);
    }
}

