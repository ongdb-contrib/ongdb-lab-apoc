package data.lab.ongdb.util;

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
 * @PACKAGE_NAME: data.lab.ongdb.util.DateHandleTest
 * @Description: TODO
 * @date 2020/5/22 10:52
 */
public class DateUtilTest {
    @Test
    public void test01() {
        String input = "origina||english";
        Arrays.stream(input.split("\\|\\|")).parallel().forEach(c -> {
            System.out.println(c);
        });
    }

    @Test
    public void test02() {
        String input = "origina&&english";
        Arrays.stream(input.split("&&")).parallel().forEach(c -> {
            System.out.println(c);
        });
    }

    @Test
    public void test03() {
        System.out.println(DateUtil.standardizeDate("asdsa", false));
        System.out.println(DateUtil.standardizeDate("2020", false));
        System.out.println(DateUtil.standardizeDate("202012", false));
        System.out.println(DateUtil.standardizeDate("20201206", false));
        System.out.println(DateUtil.standardizeDate("2020120612", false));
        System.out.println(DateUtil.standardizeDate("202012061201", false));
        System.out.println(DateUtil.standardizeDate("asdsa", true));
        System.out.println(DateUtil.standardizeDate("2020", true));
        System.out.println(DateUtil.standardizeDate("202012", true));
        System.out.println(DateUtil.standardizeDate("20201206", true));
        System.out.println(DateUtil.standardizeDate("2020120612", true));
        System.out.println(DateUtil.standardizeDate("202012061201", true));
        System.out.println(DateUtil.standardizeDate("202012061201", true));
        System.out.println(DateUtil.standardizeDate("2020-11-26 08:47:38.0", true));
        System.out.println(DateUtil.standardizeDate("2020-11-26T08:47:38", true));
        System.out.println(DateUtil.standardizeDate("2020-11-26 08:47:38", false));
        System.out.println(DateUtil.standardizeDate("2020-11-26 08:47:38", true));
        System.out.println(DateUtil.standardizeDate("", false));
        System.out.println(DateUtil.standardizeDate(null, false));
        System.out.println(DateUtil.standardizeDate("", true));
        System.out.println(DateUtil.standardizeDate(null, true));
        System.out.println(DateUtil.standardizeDate(20181030, true));
        System.out.println(DateUtil.standardizeDate("20180130", false));
        System.out.println(DateUtil.standardizeDate(20180530, true));
        System.out.println(DateUtil.standardizeDate("20180530", false));
    }

    @Test
    public void test04() {
        System.out.println(DateUtil.standardizeDate(20181030, true));
        System.out.println(DateUtil.standardizeDate("20180130", false));
    }

    @Test
    public void test05() {
        List<Object> objectList = new ArrayList<>();
        objectList.add("20181030");
        objectList.add(-1);
        objectList.add("2020-11-26 08:47:38.0");
        /**
         * 传入参数为LIST的时候
         * 1、LIST中所有字段无效才无效【都无效再补充系统时间】
         * 2、
         * **/
        /**
         * @param object:时间相关的对象
         * @param isStdDate:无效OBJECT是否默认补充系统时间
         * @param sort:降序排列还是升序排列 ASC('asc') DESC('desc') RANDOM('random')
         * @return
         * @Description: TODO(标准化时间字段)
         */
        long time = DateUtil.standardizeDate(objectList, true, "ASC");
        System.out.println(time);
    }
}



