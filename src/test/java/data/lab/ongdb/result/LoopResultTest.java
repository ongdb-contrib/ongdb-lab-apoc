package data.lab.ongdb.result;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2021/4/20 10:47
 */
public class LoopResultTest {

    @Test
    public void getPathStr() {
        LoopResult loopResult = new LoopResult(new Long[]{1L, 5L, 6L, 2L, 4L});
        System.out.println(loopResult.toString());
    }

    @Test
    public void mapSetPairs() {
        List<List<String>> listList = new ArrayList<>();
//        listList.add(new ArrayList<>(Arrays.asList("n0", "value")));
//        listList.add(new ArrayList<>(Arrays.asList("r02", "value")));
//        listList.add(new ArrayList<>(Arrays.asList("n1", "value")));
//        listList.add(new ArrayList<>(Arrays.asList("r2", "value4")));
        listList.add(new ArrayList<>(Arrays.asList("1", "value")));
        listList.add(new ArrayList<>(Arrays.asList("2", "value")));
        listList.add(new ArrayList<>(Arrays.asList("3", "value")));
        listList.add(new ArrayList<>(Arrays.asList("4", "value4")));
        LoopResult loopResult = new LoopResult();
        String str = loopResult.mapSetPairsListList(listList);
        String cypher = "RETURN " + loopResult.mapSetPairs(str);
        System.out.println(cypher);
    }
}

