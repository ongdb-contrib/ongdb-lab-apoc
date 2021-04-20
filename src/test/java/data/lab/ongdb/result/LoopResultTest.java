package data.lab.ongdb.result;

import org.junit.Test;

import static org.junit.Assert.*;

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
}