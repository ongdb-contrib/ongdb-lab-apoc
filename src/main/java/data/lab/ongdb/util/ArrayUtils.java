package data.lab.ongdb.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.util
 * @Description: TODO
 * @date 2021/5/7 15:52
 */
public class ArrayUtils {

    /**
     *
     * @param dimensionValue 原List
     * @param result         通过乘积转化后的数组
     * @param layer          中间参数
     * @param currentList    中间参数
     */
    /**
     * @param dimensionValue 原List
     * @param result         通过乘积转化后的数组
     * @param layer          中间参数
     * @param currentList    中间参数
     * @return
     * @Description: TODO(笛卡尔乘积算法 【对列表中实体使用指定字段进行分组，并进行笛卡尔乘积运算进行组合】)
     * 把一个List{[1,2],[A,B],[a,b]} 转化成
     * List{[1,A,a],[1,A,b],[1,B,a],[1,B,b],[2,A,a],[2,A,b],[2,B,a],[2,B,b]} 数组输出
     */
    public <T> void descartes(List<List<T>> dimensionValue, List<List<T>> result, int layer, List<T> currentList) {
        if (layer < dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                descartes(dimensionValue, result, layer + 1, currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<T>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    descartes(dimensionValue, result, layer + 1, list);
                }
            }
        } else if (layer == dimensionValue.size() - 1) {
            if (dimensionValue.get(layer).size() == 0) {
                result.add(currentList);
            } else {
                for (int i = 0; i < dimensionValue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<T>(currentList);
                    list.add(dimensionValue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }
}
