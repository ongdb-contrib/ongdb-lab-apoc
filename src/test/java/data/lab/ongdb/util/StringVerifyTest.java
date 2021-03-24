package data.lab.ongdb.util;

import data.lab.ongdb.schema.Schema;
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
 * @date 2020/5/26 18:28
 */
public class StringVerifyTest {

    @Test
    public void isEnglish() {
        System.out.println(StringVerify.isEnglish("公司股票于1996年7月15日上市")); // false
        System.out.println(StringVerify.isChinese("公司股票于1996年7月15日上市》*&……876")); // true

        System.out.println(StringVerify.isEnglish("GoogleMInc")); // true
        System.out.println(StringVerify.isChinese("GoogleMInc.")); // false
    }

    @Test
    public void parseCypherPattern() {
        String query="MATCH p=(:公司)-[:担保]->(org:公司)-[:拥有]->(:品牌)<-[:包含]-(:产品) ";
        char[] chars=query.toCharArray();
        System.out.println(query.charAt(8));
        new Schema().parse(query,null,null);
    }

}


