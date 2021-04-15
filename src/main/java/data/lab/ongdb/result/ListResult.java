package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.ListResult
 * @Description: TODO
 * @date 2021/4/14 17:19
 */
public class ListResult {
    public final List<Object> value;

    public ListResult(List<Object> value) {
        this.value = value;
    }
}
