package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util
 * @Description: TODO
 * @date 2020/12/10 19:11
 */
public class IDSUtil {

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成一个IDS列表)
     */
    public static List<Long> ids(long min, long max) {
        List<Long> ids = new ArrayList<>();
        for (long i = min; i <= max; i++) {
            ids.add(i);
        }
        return ids;
    }

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成一个IDS列表)
     */
    public static List<Long> ids(long min, long max, int batchSize) {
        List<Long> ids = new ArrayList<>();
        ids.add(min);
        for (long i = min; i < max; i++) {
            i += batchSize - 1;
            if (i < max) {
                ids.add(i);
            }
        }
        ids.add(max);
        return ids;
    }

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成N个指定SIZE的列表)
     */
    public static Stream<List<Long>> idsSplit(long min, long max, int batchSize) {
        List<Long> ids = ids(min, max);
        int total = ids.size();
        int pages = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        return IntStream.range(0, pages).parallel().boxed()
                .map(page -> {
                    int from = page * batchSize;
                    return ids.subList(from, Math.min(from + batchSize, total));
                });
    }

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成指定SIZE的列表 - 只拿最大最小返回)【使用时左闭右闭规则】
     */
    public static Stream<List<Long>> idsBatch(long min, long max, int batchSize) {
        List<Long> ids = ids(min, max);
        int total = ids.size();
        int pages = total % batchSize == 0 ? total / batchSize : total / batchSize + 1;
        return IntStream.range(0, pages).parallel().boxed()
                .map(page -> {
                    int from = page * batchSize;
                    List<Long> innerIds = ids.subList(from, Math.min(from + batchSize, total));
                    return Arrays.asList(innerIds.get(0), innerIds.get(innerIds.size() - 1))
                            .parallelStream()
                            .distinct().collect(Collectors.toList());
                });
    }

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成指定SIZE的列表 - 只拿最大最小返回)【使用时左闭右闭规则】
     */
    public static Stream<List<Long>> idsBatchOptimize(long min, long max, int batchSize) {
        List<Long> ids = ids(min, max, batchSize);
        int pages = ids.size() - 1;
        return IntStream.range(0, pages).parallel().boxed()
                .map(page -> Arrays.asList(ids.get(page), ids.get(page + 1)));
    }

    /**
     * @param
     * @return
     * @Description: TODO(指定最大ID和最小ID ， 生成指定SIZE的列表 - 只拿最大最小返回)【使用时左闭右闭规则】
     */
    public static List<List<Long>> idsBatchOptimizeList(long min, long max, int batchSize) {
        return idsBatchOptimize(min, max, batchSize).collect(Collectors.toList());
    }
}


