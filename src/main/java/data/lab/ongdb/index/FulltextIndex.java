package data.lab.ongdb.index;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.message.NodeIndexMessage;
import data.lab.ongdb.result.ChineseHit;
import data.lab.ongdb.result.OutputWords;
import data.lab.ongdb.result.OutputWordsCouple;
import data.lab.ongdb.result.OutputWordsTriple;
import data.lab.wltea.analyzer.cfg.Configuration;
import data.lab.wltea.analyzer.core.IKSegmenter;
import data.lab.wltea.analyzer.core.Lexeme;
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.index.lucene.ValueContext;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

//import apoc.ApocKernelExtensionFactory;
//import apoc.Pools;
//import static apoc.util.AsyncStream.async;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.index.FulltextIndex
 * @Description: TODO(基于中文分词的ONgDB全文检索)
 * @date 2020/5/22 10:25
 */
public class FulltextIndex {

    private static final String tokenFilter = "data.lab.wltea.analyzer.lucene.IKAnalyzer";

    // 提供支持索引自动更新的类使用
    protected static final Map<String, String> FULL_INDEX_CONFIG = MapUtil
            .stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext", "analyzer", tokenFilter);

    /**
     * 运行环境/上下文
     */
    @Context
    public GraphDatabaseAPI db;

    @Context
    public Log log;

    public static class IndexStats {
        public final String label;
        public final String property;
        public final long nodeCount;

        private IndexStats(String label, String property, long nodeCount) {
            this.label = label;
            this.property = property;
            this.nodeCount = nodeCount;
        }
    }

    /**
     * @param text:待分词文本
     * @param isSmart:分词模式-true:智能呢个分词，false:细粒度分词
     * @return
     * @Description: TODO
     */
    @Procedure(value = "olab.iKAnalyzer", mode = Mode.READ)
    @Description("CALL olab.iKAnalyzer({text},true) YIELD words RETURN words")
    public Stream<OutputWords> chineseFulltextIndexSearch(@Name("text") String text, @Name("isSmart") boolean isSmart) {
        PropertyConfigurator.configureAndWatch("dic" + File.separator + "log4j.properties");
        Configuration cfg = new Configuration(isSmart);

        StringReader input = new StringReader(text.trim());
        IKSegmenter ikSegmenter = new IKSegmenter(input, cfg);

        List<String> results = new ArrayList<>();
        try {
            for (Lexeme lexeme = ikSegmenter.next(); lexeme != null; lexeme = ikSegmenter.next()) {
                results.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.of(new OutputWords(results));
    }

    /**
     * @param words:词列表
     * @return
     * @Description: TODO(列表中的词两两组合)
     */
    @Procedure(value = "olab.ik.combination.couple", mode = Mode.READ)
    @Description("CALL olab.ik.combination.couple(words) YIELD wordF,wordT RETURN wordF,wordT")
    public Stream<OutputWordsCouple> combinationWordsCouple(@Name("words") List<String> words) {
        List<String> wordF = new ArrayList<>();
        List<String> wordT = new ArrayList<>();
        for (String word1 : words) {
            for (String word2 : words) {
                wordF.add(word1);
                wordT.add(word2);
            }
        }
        return Stream.of(new OutputWordsCouple(wordF, wordT));
    }

    /**
     * @param words:词列表
     * @return
     * @Description: TODO(列表中的词组合三列)
     */
    @Procedure(value = "olab.ik.combination.triple", mode = Mode.READ)
    @Description("CALL olab.ik.combination.triple(words) YIELD wordF,wordT RETURN wordF,wordT,wordR")
    public Stream<OutputWordsTriple> combinationWordsTriple(@Name("words") List<String> words) {
        List<String> wordF = new ArrayList<>();
        List<String> wordT = new ArrayList<>();
        List<String> wordR = new ArrayList<>();
        for (String word1 : words) {
            for (String word2 : words) {
                for (String word3 : words) {
                    wordF.add(word1);
                    wordT.add(word2);
                    wordR.add(word3);
                }
            }
        }
        return Stream.of(new OutputWordsTriple(wordF, wordT,wordR));
    }

    /**
     * @param indexName:索引名称
     * @param query:查询（可直接使用索引查询更底层功能）
     * @param limit:限制数据量
     * @return
     * @Description: TODO(查询中文全文索引 - 不区分标签返回)
     */
    @Procedure(value = "olab.index.chineseFulltextIndexSearch", mode = Mode.WRITE)
    @Description("CALL olab.index.chineseFulltextIndexSearch(String indexName, String query, long limit) YIELD node,weight RETURN node,weight" +
            "执行LUCENE全文检索，返回前{limit个结果}")
    public Stream<ChineseHit> chineseFulltextIndexSearch(@Name("indexName") String indexName,
                                                         @Name("query") String query, @Name("limit") long limit) {
        if (!db.index().existsForNodes(indexName)) {
            log.debug("如果索引不存在则跳过本次查询：`%s`", indexName);
            return Stream.empty();
        }
        QueryContext queryParam;
        if (limit == -1) {
            queryParam = new QueryContext(query).sortByScore();
        } else {
            queryParam = new QueryContext(query).sortByScore().top((int) limit);
        }
        return toWeightedNodeResult(db.index().forNodes(indexName, FULL_INDEX_CONFIG).query(queryParam));
    }

    /**
     * @param indexName:索引名称
     * @param labelName:标签名称
     * @param propKeys:属性名称列表
     * @return
     * @Description: TODO(创建中文全文索引 - 不支持自动更新 ( 不区分标签 - 多个标签使用相同的索引名称即可同时检索))
     */
    @Procedure(value = "olab.index.addChineseFulltextIndex", mode = Mode.WRITE)
    @Description("CALL olab.index.addChineseFulltextIndex(String indexName, String labelName, List<String> propKeys) YIELD message RETURN message," +
            "为一个标签下的所有节点的指定属性添加索引")
    public Stream<NodeIndexMessage> addChineseFulltextIndex(@Name("indexName") String indexName,
                                                            @Name("properties") List<String> propKeys,
                                                            @Name(value = "labelName", defaultValue = "") String labelName) {
        List<NodeIndexMessage> output = new ArrayList<>();

        // 构建索引并返回MESSAGE - 不支持自动更新 autoUpdate
        String message = chineseFulltextIndex(indexName, labelName, propKeys);

        NodeIndexMessage indexMessage = new NodeIndexMessage(message);
        output.add(indexMessage);
        return output.stream();
    }

    /**
     * @param
     * @return
     * @Description: TODO(节点单独更新到索引)
     */
    @Procedure(value = "olab.index.addNodeChineseFulltextIndex", mode = Mode.WRITE)
    @Description("apoc.index.addNodeChineseFulltextIndex(node,['prop1',...]) add node to an index for each label it has - CALL apoc.index.addNodeChineseFulltextIndex(joe, ['name','age','city'])")
    public void addNodeChineseFulltextIndex(@Name("node") Node node, @Name("properties") List<String> propKeys) {
        for (Label label : node.getLabels()) {
            addNodeByLabel(label.name(), node, propKeys);
        }
    }

    private void addNodeByLabel(String label, Node node, List<String> propKeys) {
        indexEntityProperties(node, propKeys, getNodeIndex(label, FULL_INDEX_CONFIG));
    }

    private <T extends PropertyContainer> void indexEntityProperties(T pc, List<String> propKeys, org.neo4j.graphdb.index.Index<T> index) {
        Map<String, Object> properties = pc.getProperties(propKeys.toArray(new String[propKeys.size()]));
        indexEntityWithMap(pc, properties, index);
    }

    private <T extends PropertyContainer> void indexEntityWithMap(T pc, Map<String, Object> document, Index<T> index) {
        index.remove(pc);
        document.forEach((key, value) -> {
            index.remove(pc, key);
            index.add(pc, key, value);
            System.out.println("Node:" + pc + "," + key + ":" + value);
        });
    }

    /**
     * @param
     * @return
     * @Description: TODO(索引预配置)
     */
    private Index<Node> getNodeIndex(String name, Map<String, String> config) {
        IndexManager mgr = db.index();
        return config == null ? mgr.forNodes(name) : mgr.forNodes(name, config);
    }
//    /**
//     * @param indexName:索引名称
//     * @param labelName:标签名称
//     * @param propKeys:属性名称列表
//     * @param options:配置参数
//     * @return
//     * @Description: TODO(创建中文全文索引 - 支持自动更新 ( 不区分标签))
//     */
//    @Procedure(value = "olab.index.addChineseFulltextAutoIndex", mode = Mode.WRITE)
//    @Description("CALL olab.index.addChineseFulltextAutoIndex(String indexName, String labelName, List<String> propKeys) YIELD label,property,nodeCount - create a free chinese text search index " +
//            "为一个标签下的所有节点的指定属性添加索引")
//    public Stream<NodeIndexMessage> addChineseFulltextAutoIndex(@Name("indexName") String indexName,
//                                                                @Name("properties") List<String> propKeys,
//                                                                @Name(value = "labelName") String labelName,
//                                                                @Name(value = "options", defaultValue = "") Map<String, String> options) {
//
//        List<NodeIndexMessage> output = new ArrayList<>();
//
//        if (propKeys.isEmpty()) {
//            throw new IllegalArgumentException("No structure given.");
//        }
//
//        // 构建索引并返回MESSAGE - 支持自动更新 autoUpdate
//        String message = chineseFulltextAutoIndex(indexName, labelName, propKeys, options);
//
//        NodeIndexMessage indexMessage = new NodeIndexMessage(message);
//        output.add(indexMessage);
//        return output.stream();
//    }

//    /**
//     * @param indexName:索引名称
//     * @param labelName:标签名称
//     * @param propKeys:属性名称列表
//     * @param options:配置参数
//     * @return
//     * @Description: TODO(创建中文全文索引 - 支持自动更新 ( 不区分标签))
//     */
//    @Procedure(value = "olab.index.addChineseFulltextAutoIndex", mode = Mode.WRITE)
//    @Description("CALL olab.index.addChineseFulltextAutoIndex(String indexName, String labelName, List<String> propKeys) YIELD label,property,nodeCount - create a free chinese text search index " +
//            "为一个标签下的所有节点的指定属性添加索引")
//    public Stream<IndexStats> addChineseFulltextAutoIndex(@Name("indexName") String indexName,
//                                                          @Name("properties") List<String> propKeys,
//                                                          @Name(value = "labelName", defaultValue = "") String labelName,
//                                                          @Name(value = "options", defaultValue = "") Map<String, String> options) {
//
//        if (propKeys.isEmpty()) {
//            throw new IllegalArgumentException("No structure given.");
//        }
//        return async(executor(), "Creating chinese index '" + indexName + "'", result -> {
//            populate(index(indexName, propKeys, options), propKeys, result);
//        });
//    }

    /**
     * @param
     * @return
     * @Description: TODO(构建索引并返回MESSAGE - 支持自动更新)
     */
    private String chineseFulltextAutoIndex(String indexName, String labelName, List<String> propKeys, Map<String, String> options) {

        Label label = Label.label(labelName);

        // 按照标签找到该标签下的所有节点
        ResourceIterator<Node> nodes = db.findNodes(label);

        // 合并用户配置
        FULL_INDEX_CONFIG.putAll(options);

        int nodesSize = 0;
        int propertiesSize = 0;
        while (nodes.hasNext()) {
            nodesSize++;
            Node node = nodes.next();
            System.out.println("current nodes:" + node.toString());

            // 每个节点上需要添加索引的属性
            Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0])).entrySet();
            System.out.println("current node properties" + properties);

            // 查询该节点是否已有索引，有的话删除
            if (db.index().existsForNodes(indexName)) {
                Index<Node> oldIndex = db.index().forNodes(indexName);
                System.out.println("current node index" + oldIndex);
                oldIndex.remove(node);
            }

            // 为该节点的每个需要添加索引的属性添加全文索引
            Index<Node> nodeIndex = db.index().forNodes(indexName, FULL_INDEX_CONFIG);
            for (Map.Entry<String, Object> property : properties) {
                propertiesSize++;
                nodeIndex.add(node, property.getKey(), property.getValue());
            }
        }

        String message = "IndexName:" + indexName + ",LabelName:" + labelName + ",NodesSize:" + nodesSize + ",PropertiesSize:" + propertiesSize;
        return message;
    }

    /**
     * @param
     * @return
     * @Description: TODO(构建索引并返回MESSAGE - 不支持自动更新)
     */
    private String chineseFulltextIndex(String indexName, String labelName, List<String> propKeys) {

        Label label = Label.label(labelName);

        int nodesSize = 0;
        int propertiesSize = 0;

        // 按照标签找到该标签下的所有节点
        ResourceIterator<Node> nodes = db.findNodes(label);
        Transaction tx = db.beginTx();
        try {
            int batch = 0;
            long startTime = System.nanoTime();
            while (nodes.hasNext()) {
                nodesSize++;
                Node node = nodes.next();

                boolean indexed = false;
                // 每个节点上需要添加索引的属性
                Set<Map.Entry<String, Object>> properties = node.getProperties(propKeys.toArray(new String[0])).entrySet();

                // 查询该节点是否已有索引，有的话删除
                if (db.index().existsForNodes(indexName)) {
                    Index<Node> oldIndex = db.index().forNodes(indexName);
                    oldIndex.remove(node);
                }

                // 为该节点的每个需要添加索引的属性添加全文索引
                Index<Node> nodeIndex = db.index().forNodes(indexName, FULL_INDEX_CONFIG);
                for (Map.Entry<String, Object> property : properties) {
                    indexed = true;
                    propertiesSize++;
                    nodeIndex.add(node, property.getKey(), property.getValue());
                }
                // 批量提交事务
                if (indexed) {
                    if (++batch == 50_000) {
                        batch = 0;
                        tx.success();
                        tx.close();
                        tx = db.beginTx();

                        // 计算耗时
                        startTime = indexConsumeTime(startTime, nodesSize, propertiesSize);
                    }
                }
            }
            tx.success();
            // 计算耗时
            indexConsumeTime(startTime, nodesSize, propertiesSize);
        } finally {
            tx.close();
        }

        String message = "IndexName:" + indexName + ",LabelName:" + labelName + ",NodesSize:" + nodesSize + ",PropertiesSize:" + propertiesSize;
        return message;
    }

    /**
     * @param
     * @return
     * @Description: TODO(计算索引耗时)
     */
    private long indexConsumeTime(long startTime, int nodesSize, int propertiesSize) {
        long endTime = System.nanoTime();
        long consume = (endTime - startTime) / 1000000;
        System.out.println("Build index-nodeSize:" + nodesSize + ",propertieSize:" + propertiesSize + ",consume:" + consume + "ms");
        return System.nanoTime();
    }

    /**
     * @param
     * @return
     * @Description: TODO(开始添加索引)
     */
    private void populate(Index<Node> index, List<String> config, Consumer<IndexStats> result) {
        String[] structure = config.toArray(new String[config.size()]);
        Map<LabelProperty, Counter> stats = new HashMap<>();
        Transaction tx = db.beginTx();
        try {
            int batch = 0;
            for (Node node : db.getAllNodes()) {
                boolean indexed = false;

                // 给节点的每个标签都添加索引
                for (Label label : node.getLabels()) {
                    String[] keys = structure;
                    if (keys == null) continue;
                    indexed = true;

                    // 通过标签控制为哪些属性添加索引 （默认为所有节点属性添加索引）
                    Map<String, Object> properties = keys.length == 0 ? node.getAllProperties() : node.getProperties(keys);

                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        Object value = entry.getValue();

                        // 添加索引
                        index.add(node, entry.getKey(), value.toString());
                        if (value instanceof Number) {
                            value = ValueContext.numeric(((Number) value).doubleValue());
                        }

                        // 节点标签下添加索引
                        index.add(node, label.name() + "." + entry.getKey(), value);

                        stats.computeIfAbsent(new LabelProperty(label.name(), entry.getKey()), x -> new Counter()).count++;
                    }
                }
                if (indexed) {
                    if (++batch == 50_000) {
                        batch = 0;
                        tx.success();
                        tx.close();
                        tx = db.beginTx();
                    }
                }
            }
            tx.success();
        } finally {
            tx.close();
        }
        stats.forEach((key, counter) -> result.accept(key.stats(counter)));
    }

    /**
     * @param
     * @return
     * @Description: TODO(全文检索节点结果输出)
     */
    private Stream<ChineseHit> toWeightedNodeResult(IndexHits<Node> hits) {
        List<ChineseHit> results = new ArrayList<>();
        while (hits.hasNext()) {
            Node node = hits.next();
            results.add(new ChineseHit(node, hits.currentScore()));
        }
        return results.stream();
    }

    private static class LabelProperty {
        private final String label, property;

        LabelProperty(String label, String property) {
            this.label = label;
            this.property = property;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LabelProperty that = (LabelProperty) o;
            return Objects.equals(label, that.label) &&
                    Objects.equals(property, that.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, property);
        }

        IndexStats stats(Counter counter) {
            return new IndexStats(label, property, counter.count);
        }
    }

    private static class Counter {
        long count;
    }

//    /**
//     * @param
//     * @return
//     * @Description: TODO(对需要索引的节点先进行预配置)
//     */
//    private Index<Node> index(String index, List<String> structure, final Map<String, String> options) {
//        Map<String, String> config = new HashMap<>(FulltextIndex.FULL_INDEX_CONFIG);
//        try (Transaction tx = db.beginTx()) {
//            if (db.index().existsForNodes(index)) {
//                Index<Node> old = db.index().forNodes(index);
//                Map<String, String> oldConfig = new HashMap<>(db.index().getConfiguration(old));
//                log.info("Dropping existing index '%s', with config: %s", index, oldConfig);
//                old.delete();
//            }
//            tx.success();
//        }
//        try (Transaction tx = db.beginTx()) {
////            updateConfigFromParameters(config, structure);
//
//            /* add options to the parameters */
//            options.forEach((k, v) -> {
//                        config.put(k, String.valueOf(v)); // explicit conversion to String
//                    }
//            );
//            log.info("Creating or updating index '%s' with config '%s'", index, config);
//            Index<Node> nodeIndex = db.index().forNodes(index, config);
//
//            resetIndexUpdateConfiguration();
//            tx.success();
//            return nodeIndex;
//        }
//    }

//    /**
//     * @param
//     * @return
//     * @Description: TODO(重置更新索引配置)
//     */
//    private void resetIndexUpdateConfiguration() {
//        try {
//            ApocKernelExtensionFactory.ApocLifecycle apocLifecycle = db.getDependencyResolver().resolveDependency(ApocKernelExtensionFactory.ApocLifecycle.class);
//            if (apocLifecycle != null) {
//                apocLifecycle.getIndexUpdateLifeCycle().resetConfiguration();
//            }
//        } catch (Exception e) {
//            log.error("failed to reset index update configuration", e);
//        }
//    }
//
//    /**
//     * @param
//     * @return
//     * @Description: TODO(初始化线程池)
//     */
//    private Executor executor() {
//        return Pools.DEFAULT;
//    }

}

