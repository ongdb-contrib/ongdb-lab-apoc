package data.lab.ongdb.math;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.math
 * @Description: TODO
 * @date 2021/6/17 11:37
 */
public class Math {

    /**
     * @param
     * @return
     * @Description: TODO(计算对数)
     */
    @UserFunction(name = "olab.math.log")
    @Description("计算对数：返回 （ 底数是 e ） double 值的自然对数 ； 等价于 ： x = ln10 或 x = loge ( 10 ） ， 即以e为底的自然对数 。")
    public double log(@Name("value") double value) {
        return java.lang.Math.log(value);
    }

    /**
     * @param
     * @return
     * @Description: TODO(计算对数)
     */
    @UserFunction(name = "olab.math.log1p")
    @Description("计算对数：返回参数与1的和的自然对数。")
    public double log1p(@Name("value") double value) {
        return java.lang.Math.log1p(value);
    }

    /**
     * @param
     * @return
     * @Description: TODO(计算对数)
     */
    @UserFunction(name = "olab.math.log10")
    @Description("计算对数：以10为底的对数")
    public double log10(@Name("value") double value) {
        return java.lang.Math.log10(value);
    }

    /**
     * @param
     * @return
     * @Description: TODO(计算对数)
     */
    @UserFunction(name = "olab.math.logWithBase")
    @Description("计算对数：以base为底value的对数")
    public double logWithBase(@Name("value") double value, @Name("base") double base) {
        return java.lang.Math.log(value) / java.lang.Math.log(base);
    }
}


