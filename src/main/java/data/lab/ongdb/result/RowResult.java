package data.lab.ongdb.result;

import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.RowResult
 * @Description: TODO
 * @date 2021/4/8 8:47
 */
public class RowResult {
    public final Map<String, Object> row;

    public RowResult(Map<String, Object> row) {
        this.row = row;
    }
}
