package data.lab.ongdb.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util
 * @Description: TODO
 * @date 2020/12/10 19:30
 */
public class IDSUtilTest {

    @Test
    public void ids() {
        IDSUtil.ids(1, 100).forEach(System.out::println);
    }

    @Test
    public void idsSize() {
        IDSUtil.ids(1, 100, 25).forEach(System.out::println);
    }

    @Test
    public void idsSplit() {
        // 指定最大ID和最小ID ， 生成N个指定SIZE的列表
        IDSUtil.idsSplit(1, 100, 25)
                .forEach(v -> System.out.println(JSONArray.parseArray(JSON.toJSONString(v))));
    }

    @Test
    public void idsBatch() {
        // 指定最大ID和最小ID ， 生成N个指定SIZE的列表 - 只拿最大最小返回
        IDSUtil.idsBatch(1, 100, 25)
                .forEach(v -> System.out.println(JSONArray.parseArray(JSON.toJSONString(v))));
    }

    @Test
    public void idsBatchOptimize() {
        // 指定最大ID和最小ID ， 生成N个指定SIZE的列表 - 只拿最大最小返回
        IDSUtil.idsBatchOptimize(1, 100, 25)
                .forEach(v -> System.out.println(JSONArray.parseArray(JSON.toJSONString(v))));
    }

    @Test
    public void idsBatchOptimizeTest01() {
        // 指定最大ID和最小ID ， 生成N个指定SIZE的列表 - 只拿最大最小返回
        IDSUtil.idsBatchOptimize(1, 1000000000, 5000000)
                .forEach(v -> System.out.println(JSONArray.parseArray(JSON.toJSONString(v))));
    }
}

