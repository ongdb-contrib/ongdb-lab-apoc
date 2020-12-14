package data.lab.ongdb.procedures;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import data.lab.ongdb.util.IDSUtil;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.procedures
 * @Description: TODO
 * @date 2020/11/18 10:58
 */
public class FunctionPartition {
    /**
     * @param fields:字段列表
     * @param items:数据集合列表
     * @return
     * @Description: TODO(CSV格式转为mapList)
     */
    @UserFunction(name = "olab.structure.mergeToListMap")
    @Description("【CSV格式转为mapList】【数据封装格式转换】@olab.structure.mergeToListMap(['area_code','author'],[['001','HORG001'],['002','HORG002']])")
    public String structureMergeToListMap(@Name("fields") List<Object> fields, @Name("items") List<List<Object>> items) {
        List<Map<Object, Object>> mapList = new ArrayList<>();
        int size = fields.size();
        for (List<Object> list : items) {
            Map<Object, Object> map = new HashMap<>();
            for (int i = 0; i < size; i++) {
                map.put(fields.get(i), list.get(i));
            }
            mapList.add(map);
        }
        return JSONArray.parseArray(JSON.toJSONString(mapList)).toJSONString();
    }

    /**
     * @param min:最小ID
     * @param max:最大ID
     * @param batch:每个batch的大小
     * @return
     * @Description: TODO(指定最小ID和最大ID ， 生成N个指定SIZE的列表 【 每个列表只拿最大最小ID 】)
     */
    @UserFunction(name = "olab.ids.batch")
    @Description("指定最小ID和最大ID，生成N个指定SIZE的列表【每个列表只拿最大最小ID】")
    public List<List<Long>> idsBatch(@Name("min") Long min, @Name("max") Long max, @Name("batch") Number batch) {
        return IDSUtil.idsBatchOptimizeList(min, max, batch.intValue());
    }

    /**
     * @param string:原始字符串
     * @param replaceListMap:需要替换的字符串[{raw:'{url}',rep:'\'test-url\''},{raw:'{sql}',rep:'\''+loadSql+'\''}]
     *                      raw:需要被替换的参数
     *                      rep:替换值
     * @return
     * @Description: TODO(字符串替换 - 按照传入的map替换)
     */
    @UserFunction(name = "olab.replace")
    @Description("字符串替换 - 按照传入的map替换")
    public String replaceString(@Name("string") String string, @Name("replaceListMap") List<Map<String, Object>> replaceListMap) {
        for (Map<String, Object> map : replaceListMap) {
            if (!map.containsKey("raw") || !map.containsKey("rep")) {
                return "Missing necessary fields ’raw‘ or ’rep‘!";
            } else {
                Object raw = map.get("raw");
                Object rep = map.get("rep");
                string = string.replace(String.valueOf(raw), String.valueOf(rep));
            }
        }
        return string;
    }

    /**
     * @param string:原始字符串
     * @return
     * @Description: TODO(对传入的字符串执行 ’ \ ’ ‘ 转义操作)
     */
    @UserFunction(name = "olab.escape")
    @Description("对传入的字符串执行’\\’‘转义操作")
    public String escape(@Name("string") String string) {
        return string != null ? string.replace("'", "\\'") : string;
    }
}



