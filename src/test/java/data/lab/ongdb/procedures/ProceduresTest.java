package data.lab.ongdb.procedures;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.*;

import static org.junit.Assert.assertEquals;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures.ProceduresTest
 * @Description: TODO(自定义函数测试)
 * @date 2020/5/22 10:51
 */
public class ProceduresTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(Procedures.class);

    @Test
    public void shouldGreetWorld() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try (Transaction tx = db.beginTx()) {
            String name = "World";

            Map<String, Object> params = new HashMap<>();
            params.put("name", name);

            Result result = db.execute("RETURN olab.hello({name}) AS greeting", params);

            String greeting = (String) result.next().get("greeting");
            assertEquals("Hello, " + name, greeting);
        }
    }

    @Test
    public void test01() {
        System.out.println(String.format("Hello, %s", "neo4j"));
    }

    @Test
    public void sortDESC() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            Map<String, Object> params = new HashMap<>();
            List<Integer> list = new ArrayList<>();
            list.add(4);
            list.add(1);
            list.add(2);
            list.add(3);
            params.put("list", list);
            Result result = db.execute("RETURN olab.sortDESC({list}) AS descList", params);
            List<Integer> descList = (List<Integer>) result.next().get("descList");
            System.out.println(descList);
        }
    }

    @Test
    public void getEventIdsSize() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String eventIds = "123213,234324,4354353,1231231,2132131";
            Map<String, Object> params = new HashMap<>();
            params.put("eventIds", eventIds);
            Result result = db.execute("RETURN olab.getEventIdsSize({eventIds}) AS value", params);
            int eventIdsSize = (int) result.next().get("value");
            System.out.println(eventIdsSize);

// 首先自定义SIZE函数，然后通过关系的属性进行排序：
// match p=(n:事)<-[r:命中关键词]-(m:虚拟账号ID) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY olab.getEventIdsSize(r.eventTargetIds) DESC limit 10

        }
    }

    @Test
    public void getInitAnnualTime() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String startTime = "2006-05-01 00:00:00";
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startTime);
            Result result = db.execute("RETURN olab.initAnnualTime({startTime}) AS value", params);
            long initStartTime = (long) result.next().get("value");
            System.out.println(initStartTime);
        }
    }

    @Test
    public void presentStringToDate() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            String present = "Present";
            Map<String, Object> params = new HashMap<>();
            params.put("present", present);
            Result result = db.execute("RETURN olab.presentStringToDate({present}) AS value", params);
            String initStartTime = (String) result.next().get("value");
            System.out.println(initStartTime);
        }
    }

    @Test
    public void getStringSize() throws Exception {
        String string = "213123,123123,123123,123123,12312";

    }

    @Test
    public void matchTimeZone() throws Exception {
        // 测试查询：
        // MATCH p=(n:手机号)-[r:通联]-(m:手机号) WHERE n.name='13910317532' AND m.name='13910272362' AND olab.matchTimeZone({timeList:r.dateHistory,startTime:'2018-03-05 15:37:42',stopTime:'2018-03-05 15:37:43'})=0 RETURN p
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            String dateHistory = "2018-03-25 17:49:39,2018-03-22 23:13:56,2018-03-05 21:13:05,2018-03-01 17:01:25,2018-03-01 10:48:08,2018-03-01 10:48:03,2018-03-01 10:48:02";
            Map<String, String> map = new HashMap<>();

            map.put("timeList", dateHistory);

            map.put("startTime", "2018-03-25 10:49:39");
            map.put("stopTime", "2018-03-25 20:49:39");

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN olab.matchTimeZone({paras}) AS value", params);

            int eventIdsSize = (int) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void matchTimeListString() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            String dateHistory = "2018-03-25 17:49:39,2018-03-22 23:13:56,2018-03-05 21:13:05,2018-03-01 17:01:25,2018-03-01 10:48:08,2018-03-01 10:48:03,2018-03-01 10:48:02";
            Map<String, String> map = new HashMap<>();

            map.put("timeList", dateHistory);

            map.put("startTime", "2018-03-25 10:49:39");
            map.put("stopTime", "2018-03-25 20:49:39");

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN olab.matchTimeListString({paras}) AS value", params);

            String eventIdsSize = (String) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void percentageInfluenceScore() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

            Map<String, Object> map = new HashMap<>();

            map.put("minScore", 15);
            map.put("maxScore", 79);
            map.put("currentScore", 77);

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN olab.scorePercentage({paras}) AS value", params);

            double eventIdsSize = (double) result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    //return olab.moveDecimalPoint({scoreObject:21313777.48543,moveLength:100.0})
    @Test
    public void moveDecimalPoint() throws Exception {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {

//            Map<String, Object> map = new HashMap<>();
//            map.put("scoreObject", 0.15213123);
//            map.put("moveLength", 100);

            Map<String, Object> map = new HashMap<>();
            map.put("scoreObject", 21313777.48543);
            map.put("moveLength", 100.0);

//            Map<String, Object> map = new HashMap<>();
//            map.put("scoreObject",  0.98364);
//            map.put("moveLength", 1000000);

            Map<String, Object> params = new HashMap<>();
            params.put("paras", map);

            Result result = db.execute("RETURN olab.moveDecimalPoint({paras}) AS value", params);

            Object eventIdsSize = result.next().get("value");
            System.out.println(eventIdsSize);

        }
    }

    @Test
    public void timeCrossOrNot() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("r1Start", "2012-01-01 00:00:00");
        map.put("r1Stop", "2015-01-01 00:00:00");
        map.put("r2Start", "2013-02-01 00:00:00");
        map.put("r2Stop", "2016-01-01 00:00:00");

        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);

        Result result = db.execute("RETURN olab.timeCrossOrNot({paras}) AS value", params);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void isContainsString() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("original0", "0Chinese,English");
        map.put("original1", "1Chinese,English");
        map.put("original2", "2Chinese,English");
        map.put("original3", "3Chinese,English");
        map.put("original4", "4Chinese,English");
        map.put("original5", "5Chinese,English");
        map.put("original6", "6Chinese,English");
        map.put("original7", "7Chinese,English");
        map.put("original8", "8Chinese,English");
        map.put("original9", "9Chinese,English");
        map.put("input", "Chinese");
        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);
        Result result = db.execute("RETURN olab.isContainsString({paras}) AS value", params);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void stringCharCount() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> map = new HashMap<>();
        map.put("original", "0Chinese,English,Chinese,China,Hadoop,Spark");
        map.put("char", "English");
        Map<String, Object> params = new HashMap<>();
        params.put("paras", map);
        Result result = db.execute("RETURN olab.stringCharCount({paras}) AS value", params);
        long num = (long) result.next().get("value");
        System.out.println(num);
    }

    @Test
    public void relatCalculateRestrict() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        List<String> nLabels = new ArrayList<>();
        nLabels.add("Facebook发帖");

        List<String> mLabels = new ArrayList<>();
        mLabels.add("Facebook发帖");

        String strict = "FacebookID||Facebook发帖";

        Map<String, Object> map = new HashMap<>();
        map.put("nLabels", nLabels);
        map.put("mLabels", mLabels);
        map.put("strictLabels", strict);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

        Result result = db.execute("RETURN olab.relatCalculateRestrict({nLabels},{mLabels},{strictLabels}) AS value", map);
        boolean bool = (boolean) result.next().get("value");
        System.out.println(bool);
    }

    @Test
    public void dataType() {
        long data = 1231231l;
//        double data = 234324.3432;
//        int data = 21312;
//        float data = 21312;

        Object dataObject = data;
        if (dataObject instanceof Long) {
            System.out.println("long");
        } else if (dataObject instanceof Double) {
            System.out.println("double");
        } else if (dataObject instanceof Integer) {
            System.out.println("int");
        } else if (dataObject instanceof Float) {
            System.out.println("float");
        }
    }


    @Test
    public void test10() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode();
            node.setProperty("name", "Test");
            node.setProperty("key1", "Test，");
            node.setProperty("born", "的");
            node.setProperty("age", 12323);

            Map<String, Object> map = new HashMap<>();
            map.put("node", node);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

            Result result = db.execute("RETURN olab.isContainChinese({node}) AS value", map);
            long bool = (long) result.next().get("value");
            System.out.println(bool);
        }
    }

    @Test
    public void test11() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node node = db.createNode();
            String prefix = "sysuser_id_";
            node.setProperty("name", "Test");
            node.setProperty("key1", "Test");
            node.setProperty("born", "的撒");
//            node.setProperty(prefix+"sadsad32432c", "sadsad32432c");
            Map<String, Object> map = new HashMap<>();
            map.put("node", node);

//        Map<String, Object> params = new HashMap<>();
//        params.put("paras",map);

            Result result = db.execute("RETURN olab.isContainAuthority({node}) AS value", map);
            boolean bool = (boolean) result.next().get("value");
            System.out.println(bool);
        }
    }

    @Test
    public void test12() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node nodesOne = db.createNode();
            nodesOne.setProperty("name", 60667);

            Node nodesTwo = db.createNode();
            nodesTwo.setProperty("name", 60652);

            Node nodesThree = db.createNode();
            nodesThree.setProperty("name", 60669);

            Node nodesFour = db.createNode();
            nodesFour.setProperty("name", 80988);

            // sourceList

            List<Node> sourceList = new ArrayList<>();
            sourceList.add(nodesOne);
            sourceList.add(nodesOne);
            sourceList.add(nodesTwo);
            sourceList.add(nodesTwo);

            List<Node> targetList = new ArrayList<>();
            targetList.add(nodesThree);
            targetList.add(nodesFour);
            targetList.add(nodesOne);
            targetList.add(nodesThree);

            Map<String, Object> map = new HashMap<>();
            map.put("nodeCollections", new Object[]{sourceList, targetList});

//        Map<String, Object> params = new HashMap<>();
//        params.put("nodeCollections",map);

            Result result = db.execute("RETURN olab.mergeNodes({nodeCollections}) AS value", map);
            List<Node> bool = (List<Node>) result.next().get("value");
            System.out.println(bool.size());
        }
    }

    @Test
    public void test13() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            Node nodesOne = db.createNode();
            nodesOne.setProperty("name", 60667);
            nodesOne.setProperty("Hometown", "BeiJing");
            nodesOne.setProperty("location", "Oak Ridge, Tennessee");
            nodesOne.setProperty("addresses", "Oak Ridge, Tennessee");

            Map<String, Object> map = new HashMap<>();
            map.put("node", nodesOne);
            List<String> list = new ArrayList<>();
            list.add("Hometown1");
            list.add("location1");
            list.add("");
            list.add("locality1");
            list.add("addresses");
            list.add("userCardLocation1");
            map.put("locMultiFields", list);//new String[]{"CurrentCity","Location"}

//        Map<String, Object> params = new HashMap<>();
//        params.put("nodeCollections",map);

//            Result result = db.execute("RETURN olab.locMultiFieldsFullTextSearchCondition({node},{locMultiFields}) AS value", map);
//            String str = (String) result.next().get("value");
//            System.out.println(str);

            Result result = db.execute("RETURN olab.nodeIsContainsKey({node},{locMultiFields}) AS value", map);
            boolean str = (boolean) result.next().get("value");
            System.out.println(str);
        }
    }

    @Test
    public void test14() {
        Procedures zdrProcedures = new Procedures();
        String str = "test,中文s，！";
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            System.out.println("rawChar:" + c + " bool:");
        }
    }

    @Test
    public void test15() {
//        olab.removeIdsFromRawList
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        try (Transaction tx = db.beginTx()) {
            List<Long> rawIDs = new ArrayList<>();
//            rawIDs.add(60667l);
//            rawIDs.add(60667l);
//            rawIDs.add(60652l);
//            rawIDs.add(60652l);

            List<Long> ids = new ArrayList<>();
            ids.add(60667l);
            ids.add(60652l);
            Map<String, Object> map = new HashMap<>();
            map.put("rawIDs", rawIDs);
            map.put("ids", ids);

            Result result = db.execute("RETURN olab.removeIdsFromRawList({rawIDs},{ids}) AS value", map);
            List<Long> rawIds = (List<Long>) result.next().get("value");
            if (rawIds != null) {
                for (int i = 0; i < rawIds.size(); i++) {
                    long aLong = rawIds.get(i);
                    System.out.println(aLong);
                }
            }
        }
    }

    @Test
    public void regexp() {
        String FILTER_SPECIAL_CHARACTER = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。 ，、？\"-]";
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("string", "\"TMC Rus\" Limited Liability Company");
        hashMap.put("regexp", FILTER_SPECIAL_CHARACTER);
        Result result = db.execute("RETURN olab.replace.regexp({string},{regexp}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void removeDuplicate() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        String rawJson = "[{\"investType\":\"-1\",\"amount\":-1,\"updateDate\":20011019121208,\"votingRights\":100.0,\"investDate\":-1,\"releaseDate\":19950425000000,\"src\":\"db\",\"holdAmount\":-1,\"currency\":\"CNY\",\"dataSource\":\"Table1\",\"holderType\":\"-1\",\"ratio\":-1.0},{\"investType\":\"-1\",\"amount\":-1,\"updateDate\":20041014104446,\"votingRights\":100.0,\"investDate\":-1,\"releaseDate\":19960420000000,\"src\":\"db\",\"holdAmount\":-1,\"currency\":\"CNY\",\"dataSource\":\"Table2\",\"holderType\":\"-1\",\"ratio\":-1.0},{\"investType\":\"-1\",\"amount\":-1,\"updateDate\":20011019170043,\"votingRights\":100.0,\"investDate\":-1,\"releaseDate\":19970430000000,\"src\":\"db\",\"holdAmount\":-1,\"currency\":\"CNY\",\"dataSource\":\"Table4\",\"holderType\":\"-1\",\"ratio\":-1.0},{\"investType\":\"-1\",\"amount\":-1,\"updateDate\":20011019151250,\"votingRights\":100.0,\"investDate\":-1,\"releaseDate\":19970430000000,\"src\":\"db\",\"holdAmount\":-1,\"currency\":\"CNY\",\"dataSource\":\"Table4\",\"holderType\":\"-1\",\"ratio\":-1.0}]";
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("jsonString", rawJson);
        hashMap.put("keyFields", JSONArray.parseArray("['dataSource','releaseDate']"));
        Result result = db.execute("RETURN olab.remove.duplicate({jsonString},{keyFields}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void sortDetailJsonArray() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        String jsonString = "[\n" +
                "  {\n" +
                "    \"amount\": 17500000,\n" +
                "    \"updateDate\": 20180730163342,\n" +
                "    \"guarantyType\": \"-1\",\n" +
                "    \"endDate\": -1,\n" +
                "    \"releaseDate\": 20180730000000,\n" +
                "    \"src\": \"wind\",\n" +
                "    \"guarantyStyle\": \"-1\",\n" +
                "    \"currency\": \"CNY\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"CbondGuaranteeDetail{BDAA7D08-93CE-11E8-9CAA-448A5B4D64D9}\",\n" +
                "    \"startDate\": 20180331000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20200509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506544\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20190509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506588\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  }\n" +
                "]";
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("jsonString", jsonString);
        // 超过范围默认返回全部
        hashMap.put("returnSize", 10);
        hashMap.put("sortField", "releaseDate");
        // ASC DESC
        hashMap.put("sort", "DESC");

        Result result = db.execute("RETURN olab.sort.jsonArray({jsonString},{sortField},{sort},{returnSize}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void samplingJsonArray() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        String jsonString = "[\n" +
                "  {\n" +
                "    \"amount\": 17500000,\n" +
                "    \"updateDate\": 20180730163342,\n" +
                "    \"guarantyType\": \"-1\",\n" +
                "    \"endDate\": -1,\n" +
                "    \"releaseDate\": 20180730000000,\n" +
                "    \"src\": \"wind\",\n" +
                "    \"guarantyStyle\": \"-1\",\n" +
                "    \"currency\": \"CNY\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"CbondGuaranteeDetail{BDAA7D08-93CE-11E8-9CAA-448A5B4D64D9}\",\n" +
                "    \"startDate\": 20180331000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20200509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506544\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20190509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506588\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  }\n" +
                "]";
        Map<String, Object> hashMap = new HashMap<>();
        // 需要被采样的array
        hashMap.put("jsonString", jsonString);
        // 采样类型
        hashMap.put("samplingType", "YRE");

        // 采样尺寸
        hashMap.put("samplingSize", 10);

        Result result = db.execute("RETURN olab.sampling.jsonArray({jsonString},{samplingType},{samplingSize}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void random() {
        Random random = new Random();
        // < 【不包含max】
        int max = 4;
        // >=
        int min = 0;
        for (int i = 0; i < 100; i++) {

            int num = random.nextInt(max - min) + min;

            System.out.println(num);
        }
    }

    @Test
    public void dateSamplingJsonArray() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        String jsonString = "[\n" +
                "  {\n" +
                "    \"amount\": 17500000,\n" +
                "    \"updateDate\": 20180730163342,\n" +
                "    \"guarantyType\": \"-1\",\n" +
                "    \"endDate\": -1,\n" +
                "    \"releaseDate\": 20180730000000,\n" +
                "    \"src\": \"wind\",\n" +
                "    \"guarantyStyle\": \"-1\",\n" +
                "    \"currency\": \"CNY\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"CbondGuaranteeDetail{BDAA7D08-93CE-11E8-9CAA-448A5B4D64D9}\",\n" +
                "    \"startDate\": 20180331000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20200509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506544\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20230509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币A\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506588\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  }\n" +
                "]";
        Map<String, Object> hashMap = new HashMap<>();
        // 需要被采样的array
        hashMap.put("jsonString", jsonString);
        // 时间值
        hashMap.put("dateValue", 20240730000000L);

        // 时间字段
        hashMap.put("dateField", "releaseDate");

        // 过滤条件
        Map<String, Object> filterMap = new HashMap<>();
        Map<String, Object> filterMapDic = new HashMap<>();
        filterMapDic.put("value", -1);
        filterMapDic.put("condition", ">");

        // 暂时不支持范围过滤
//        Map<String, Object> range = new HashMap<>();
//        range.put(">=",0);
//        range.put("<=",1);
//        filterMapDic.put("range", range);

        filterMap.put("ratio", filterMapDic);
        hashMap.put("filterMap", filterMap);

//        filterMapDic.put("value", "人民币A");
//        filterMapDic.put("condition", "STR");
//        filterMap.put("currency", filterMapDic);
//        hashMap.put("filterMap", filterMap);

        // RETURN apoc.convert.fromJsonMap()
//        Result result = db.execute("RETURN olab.samplingByDate.jsonArray({jsonString},{dateField},{dateValue}) AS value", hashMap);
        Result result = db.execute("RETURN olab.samplingByDate.filter.jsonArray({jsonString},{dateField},{dateValue},{filterMap}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void olabString() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("string","國際生打撒3.$#%@#$GuangDong Rongjun Co");
//        Result result = db.execute("RETURN olab.string.matchCnEn({string}) AS value", hashMap);
//        Result result = db.execute("RETURN olab.string.toLowerCase({string}) AS value", hashMap);
//        Result result = db.execute("RETURN olab.string.toSimple({string}) AS value", hashMap);
//        Result result = db.execute("RETURN olab.string.matchCnEnRinse({string}) AS value", hashMap);
//        Result result = db.execute("RETURN olab.string.encode({string}) AS value", hashMap);
        Result result = db.execute("RETURN olab.string.encodeEncCnc({string}) AS value", hashMap);
        String string = (String) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void randomMapTest() {
        String jsonString = "[\n" +
                "  {\n" +
                "    \"amount\": 17500000,\n" +
                "    \"updateDate\": 20180730163342,\n" +
                "    \"guarantyType\": \"-1\",\n" +
                "    \"endDate\": -1,\n" +
                "    \"releaseDate\": 20180730000000,\n" +
                "    \"src\": \"wind\",\n" +
                "    \"guarantyStyle\": \"-1\",\n" +
                "    \"currency\": \"CNY\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"CbondGuaranteeDetail{BDAA7D08-93CE-11E8-9CAA-448A5B4D64D9}\",\n" +
                "    \"startDate\": 20180331000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20200509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506544\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  },\n" +
                "  {\n" +
                "    \"amount\": 189000000,\n" +
                "    \"updateDate\": 20200725165544,\n" +
                "    \"guarantyType\": \"null\",\n" +
                "    \"endDate\": 20230615000000,\n" +
                "    \"releaseDate\": 20230509141552,\n" +
                "    \"src\": \"caihui2\",\n" +
                "    \"guarantyStyle\": \"null\",\n" +
                "    \"currency\": \"人民币\",\n" +
                "    \"multiGuarantyOrgs\": \"-1\",\n" +
                "    \"dataSource\": \"TCR0009506588\",\n" +
                "    \"startDate\": 20180713000000,\n" +
                "    \"multiGuaranty\": false,\n" +
                "    \"ratio\": -1.0\n" +
                "  }\n" +
                "]";
        // 把LIST中所有KEY合并到同一个MAP中，值置为无效
        System.out.println(randomMap(JSON.parseArray(jsonString)));
    }

    private JSONObject randomMap(JSONArray rawJson) {
        JSONObject object = new JSONObject();
        for (Object obj : rawJson) {
            object.putAll((Map<? extends String, ? extends Object>) obj);
            break;
        }
        /**
         * 置为无效值
         * **/
        for (String key : object.keySet()) {
            Object value = object.get(key);
            if (value instanceof String) {
                object.put(key, "");
            } else {
                object.put(key, 0);
            }
        }
        return object;
    }

    @Test
    public void standardizeDate() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("object","20200110");
        hashMap.put("isStdDate",false);
        Result result = db.execute("RETURN olab.standardize.date({object},{isStdDate},NULL) AS value", hashMap);
        long string = (long) result.next().get("value");
        System.out.println(string);
    }

    @Test
    public void standardizeDateList() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Object> hashMap = new HashMap<>();
        List<Object> list = Arrays.asList(new Long[]{20201201L,201912L,2020L});
        hashMap.put("object",list);
        hashMap.put("isStdDate",false);
        hashMap.put("selection","ASC");
        Result result = db.execute("RETURN olab.standardize.date({object},{isStdDate},{selection}) AS value", hashMap);
        long string = (long) result.next().get("value");
        System.out.println(string);
    }
}

