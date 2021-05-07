package data.lab.ongdb.util;

import org.apache.commons.compress.utils.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util
 * @Description: TODO
 * @date 2021/5/7 15:54
 */
public class ArrayUtilsTest {

    @Test
    public void descartes() {
        List<Model> modelList = Lists.newArrayList();
        modelList.add(new Model(1, "a", "zhuoli", 1));
        modelList.add(new Model(2, "b", "zhuoli", 1));
        modelList.add(new Model(3, "c", "Alice", 1));

        modelList.add(new Model(4, "d", "zhuoli", 2));

        modelList.add(new Model(5, "e", "zhuoli", 3));
        modelList.add(new Model(6, "f", "Michael", 3));
        modelList.add(new Model(7, "g", "Michael", 3));

        // 按指定字段（type）分组
        Map<Integer, List<Model>> modelMap = modelList.stream().collect(Collectors.groupingBy(Model::getType));
        Collection<List<Model>> mapValues = modelMap.values();
        // 原List
        List<List<Model>> dimensionValue = new ArrayList<>(mapValues);

        // 返回集合
        List<List<Model>> result = new ArrayList<>();
        new ArrayUtils().descartes(dimensionValue, result, 0, new ArrayList<>());

        for (List<Model> models : result) {
            List<Integer> resList = new ArrayList<>();
            for (Model model : models) {
                resList.add(model.getId());
            }
            System.out.println(resList.toString());
        }
    }

    static class Model {
        private Integer id;

        private String name;

        private String author;

        private Integer type;

        public Model(Integer id, String name, String author, Integer type) {
            this.id = id;
            this.name = name;
            this.author = author;
            this.type = type;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }
}

