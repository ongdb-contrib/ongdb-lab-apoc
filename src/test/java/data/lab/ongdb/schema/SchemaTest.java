package data.lab.ongdb.schema;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.schema
 * @Description: TODO
 * @date 2021/3/24 15:59
 */
public class SchemaTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withProcedure(Schema.class);

    private final static String schemaMapStr = "{\n" +
            "  \"stdDataSchemaMap\": {\n" +
            "\"(PREPCODE)-[中文名称]->(PRE公司中文名称)\": \"(PREPCODE)-[中文名称]->(PRE公司中文名称)\",\n" +
            "\"(公司)-[中标]->(事件)\": \"(HCCGPBidOrg)-[中标]->(HEvent)\",\n" +
            "\"(证券)-[股票上市]->(事件)\": \"(HEventBond)-[股票上市]->(HEvent)\",\n" +
            "\"(公司)-[营收]->(产品)\": \"(HORGProductCalc)-[营收]->(主营产品)\",\n" +
            "\"(公司)-[出质]->(公司)\": \"(PledgorV001)-[出质]->(PawneeV001)\",\n" +
            "\"(公司)-[拥有]->(App)\": \"(UDCompany)-[拥有]->(UDApp)\",\n" +
            "\"(原子指标)-[派生]->(衍生指标)\": \"(UDAtomicIndicator)-[派生]->(UDDerivedIndicator)\",\n" +
            "\"(公司)-[拥有]->(品牌)\": \"(UDCompany)-[拥有]->(UDBrand)\",\n" +
            "\"(公司)-[失信]->(事件)\": \"(HEventOrg)-[失信]->(HEvent)\",\n" +
            "\"(非标产品)-[非标违约]->(事件)\": \"(HEventNonStdPro)-[非标违约]->(HEvent)\",\n" +
            "\"(原子指标分类)-[NEXT]->(原子指标分类)\": \"(UDIndCategory)-[NEXT]->(UDIndCategory)\",\n" +
            "\"(公司)-[发行证券]->(证券)\": \"(HBondOrg)-[发行证券]->(HEventBond)\",\n" +
            "\"(PREPCODE)-[国税登记号码]->(PRE国税登记号码)\": \"(PREPCODE)-[国税登记号码]->(PRE国税登记号码)\",\n" +
            "\"(App)-[BELONG_TO]->(行业)\": \"(UDApp)-[BELONG_TO]->(UDIndustry)\",\n" +
            "\"(公司)-[担保]->(公司)\": \"(HORGGuaranteeV003)-[担保]->(HORGGuaranteeV003)\",\n" +
            "\"(证券)-[债券上市]->(事件)\": \"(HEventBond)-[债券上市]->(HEvent)\",\n" +
            "\"(公司)-[发行非标产品]->(非标产品)\": \"(HNonStdProOrg)-[发行非标产品]->(HEventNonStdPro)\",\n" +
            "\"(公司)-[发布项目]->(事件)\": \"(HCCGPPurchaserOrg)-[发布项目]->(HEvent)\",\n" +
            "\"(PREPCODE)-[地税登记号码]->(PRE地税登记号码)\": \"(PREPCODE)-[地税登记号码]->(PRE地税登记号码)\",\n" +
            "\"(公司)-[应收账款]->(公司)\": \"(HORGReceivable)-[应收账款]->(HORGReceivable)\",\n" +
            "\"(App分类)-[NEXT]->(App分类)\": \"(UDAppCategory)-[NEXT]->(UDAppCategory)\",\n" +
            "\"(证券)-[债券价格低于90]->(事件)\": \"(HEventBond)-[债券价格低于90]->(HBondPriceEvent)\",\n" +
            "\"(证券)-[中债隐含评级低于A]->(事件)\": \"(HEventBond)-[中债隐含评级低于A]->(HEvent)\",\n" +
            "\"(PREPCODE)-[英文名称]->(PRE英文名称)\": \"(PREPCODE)-[英文名称]->(PRE英文名称)\",\n" +
            "\"(行业)-[NEXT]->(行业)\": \"(UDIndustry)-[NEXT]->(UDIndustry)\",\n" +
            "\"(产品)-[BELONG_TO]->(行业)\": \"(UDProduct)-[BELONG_TO]->(UDIndustry)\",\n" +
            "\"(PREPCODE)-[营业执照号码]->(PRE营业执照号码)\": \"(PREPCODE)-[营业执照号码]->(PRE营业执照号码)\",\n" +
            "\"(公司)-[衍生]->(衍生指标)\": \"(UDCompany)-[衍生]->(UDDerivedIndicator)\",\n" +
            "\"(公司)-[采购]->(公司)\": \"(HORGPurchase)-[采购]->(HORGPurchase)\",\n" +
            "\"(产品)-[包含]->(品牌)\": \"(UDProduct)-[包含]->(UDBrand)\",\n" +
            "\"(PREPCODE)-[统一社会信用代码]->(PRE统一社会信用代码)\": \"(PREPCODE)-[统一社会信用代码]->(PRE统一社会信用代码)\",\n" +
            "\"(证券)-[债券违约]->(事件)\": \"(HEventBond)-[债券违约]->(HEvent)\",\n" +
            "\"(公司)-[持股]->(公司)\": \"(HORGShareHoldV002)-[持股]->(HORGShareHoldV002)\",\n" +
            "\"(App)-[BELONG_TO]->(App分类)\": \"(UDApp)-[BELONG_TO]->(UDAppCategory)\",\n" +
            "\"(PREPCODE)-[曾用名]->(PRE曾用名)\": \"(PREPCODE)-[曾用名]->(PRE曾用名)\",\n" +
            "\"(App)-[衍生]->(衍生指标)\": \"(UDApp)-[衍生]->(UDDerivedIndicator)\",\n" +
            "\"(PREPCODE)-[公司注册号]->(PRE公司注册号)\": \"(PREPCODE)-[公司注册号]->(PRE公司注册号)\",\n" +
            "\"(证券)-[债券退市]->(事件)\": \"(HEventBond)-[债券退市]->(HEvent)\",\n" +
            "\"(品牌)-[衍生]->(衍生指标)\": \"(UDBrand)-[衍生]->(UDDerivedIndicator)\",\n" +
            "\"(证券)-[股票退市]->(事件)\": \"(HEventBond)-[股票退市]->(HEvent)\",\n" +
            "\"(PREPCODE)-[纳税人识别号]->(PRE纳税人识别号)\": \"(PREPCODE)-[纳税人识别号]->(PRE纳税人识别号)\",\n" +
            "\"(原子指标)-[BELONG_TO]->(原子指标分类)\": \"(UDAtomicIndicator)-[BELONG_TO]->(UDIndCategory)\"\n" +
            "  },\n" +
            "  \"dataStdSchemaMap\": {\n" +
            "\"(PREPCODE)-[中文名称]->(PRE公司中文名称)\": \"(PREPCODE)-[中文名称]->(PRE公司中文名称)\",\n" +
            "\"(HEventBond)-[债券违约]->(HEvent)\": \"(证券)-[债券违约]->(事件)\",\n" +
            "\"(UDProduct)-[包含]->(UDBrand)\": \"(产品)-[包含]->(品牌)\",\n" +
            "\"(UDApp)-[BELONG_TO]->(UDIndustry)\": \"(App)-[BELONG_TO]->(行业)\",\n" +
            "\"(UDCompany)-[拥有]->(UDBrand)\": \"(公司)-[拥有]->(品牌)\",\n" +
            "\"(HCCGPBidOrg)-[中标]->(HEvent)\": \"(公司)-[中标]->(事件)\",\n" +
            "\"(HORGShareHoldV002)-[持股]->(HORGShareHoldV002)\": \"(公司)-[持股]->(公司)\",\n" +
            "\"(HORGProductCalc)-[营收]->(主营产品)\": \"(公司)-[营收]->(产品)\",\n" +
            "\"(HBondOrg)-[发行证券]->(HEventBond)\": \"(公司)-[发行证券]->(证券)\",\n" +
            "\"(HEventBond)-[债券价格低于90]->(HBondPriceEvent)\": \"(证券)-[债券价格低于90]->(事件)\",\n" +
            "\"(UDApp)-[BELONG_TO]->(UDAppCategory)\": \"(App)-[BELONG_TO]->(App分类)\",\n" +
            "\"(UDCompany)-[衍生]->(UDDerivedIndicator)\": \"(公司)-[衍生]->(衍生指标)\",\n" +
            "\"(HEventBond)-[债券上市]->(HEvent)\": \"(证券)-[债券上市]->(事件)\",\n" +
            "\"(PREPCODE)-[国税登记号码]->(PRE国税登记号码)\": \"(PREPCODE)-[国税登记号码]->(PRE国税登记号码)\",\n" +
            "\"(PledgorV001)-[出质]->(PawneeV001)\": \"(公司)-[出质]->(公司)\",\n" +
            "\"(UDApp)-[衍生]->(UDDerivedIndicator)\": \"(App)-[衍生]->(衍生指标)\",\n" +
            "\"(PREPCODE)-[地税登记号码]->(PRE地税登记号码)\": \"(PREPCODE)-[地税登记号码]->(PRE地税登记号码)\",\n" +
            "\"(HORGGuaranteeV001)-[担保]->(HORGGuaranteeV001)\": \"(公司)-[担保]->(公司)\",\n" +
            "\"(HEventBond)-[中债隐含评级低于A]->(HEvent)\": \"(证券)-[中债隐含评级低于A]->(事件)\",\n" +
            "\"(HEventBond)-[股票退市]->(HEvent)\": \"(证券)-[股票退市]->(事件)\",\n" +
            "\"(HCCGPPurchaserOrg)-[发布项目]->(HEvent)\": \"(公司)-[发布项目]->(事件)\",\n" +
            "\"(UDIndCategory)-[NEXT]->(UDIndCategory)\": \"(原子指标分类)-[NEXT]->(原子指标分类)\",\n" +
            "\"(HORGReceivable)-[应收账款]->(HORGReceivable)\": \"(公司)-[应收账款]->(公司)\",\n" +
            "\"(HEventBond)-[股票上市]->(HEvent)\": \"(证券)-[股票上市]->(事件)\",\n" +
            "\"(HEventOrg)-[失信]->(HEvent)\": \"(公司)-[失信]->(事件)\",\n" +
            "\"(HNonStdProOrg)-[发行非标产品]->(HEventNonStdPro)\": \"(公司)-[发行非标产品]->(非标产品)\",\n" +
            "\"(UDCompany)-[拥有]->(UDApp)\": \"(公司)-[拥有]->(App)\",\n" +
            "\"(UDAtomicIndicator)-[派生]->(UDDerivedIndicator)\": \"(原子指标)-[派生]->(衍生指标)\",\n" +
            "\"(PREPCODE)-[英文名称]->(PRE英文名称)\": \"(PREPCODE)-[英文名称]->(PRE英文名称)\",\n" +
            "\"(UDBrand)-[衍生]->(UDDerivedIndicator)\": \"(品牌)-[衍生]->(衍生指标)\",\n" +
            "\"(PREPCODE)-[营业执照号码]->(PRE营业执照号码)\": \"(PREPCODE)-[营业执照号码]->(PRE营业执照号码)\",\n" +
            "\"(UDProduct)-[BELONG_TO]->(UDIndustry)\": \"(产品)-[BELONG_TO]->(行业)\",\n" +
            "\"(HORGPurchase)-[采购]->(HORGPurchase)\": \"(公司)-[采购]->(公司)\",\n" +
            "\"(HEventBond)-[债券退市]->(HEvent)\": \"(证券)-[债券退市]->(事件)\",\n" +
            "\"(PREPCODE)-[统一社会信用代码]->(PRE统一社会信用代码)\": \"(PREPCODE)-[统一社会信用代码]->(PRE统一社会信用代码)\",\n" +
            "\"(HORGGuaranteeV003)-[担保]->(HORGGuaranteeV003)\": \"(公司)-[担保]->(公司)\",\n" +
            "\"(HORGShareHoldV001)-[持股]->(HORGShareHoldV001)\": \"(公司)-[持股]->(公司)\",\n" +
            "\"(UDAppCategory)-[NEXT]->(UDAppCategory)\": \"(App分类)-[NEXT]->(App分类)\",\n" +
            "\"(PREPCODE)-[曾用名]->(PRE曾用名)\": \"(PREPCODE)-[曾用名]->(PRE曾用名)\",\n" +
            "\"(PREPCODE)-[公司注册号]->(PRE公司注册号)\": \"(PREPCODE)-[公司注册号]->(PRE公司注册号)\",\n" +
            "\"(HEventNonStdPro)-[非标违约]->(HEvent)\": \"(非标产品)-[非标违约]->(事件)\",\n" +
            "\"(PREPCODE)-[纳税人识别号]->(PRE纳税人识别号)\": \"(PREPCODE)-[纳税人识别号]->(PRE纳税人识别号)\",\n" +
            "\"(UDIndustry)-[NEXT]->(UDIndustry)\": \"(行业)-[NEXT]->(行业)\",\n" +
            "\"(UDAtomicIndicator)-[BELONG_TO]->(UDIndCategory)\": \"(原子指标)-[BELONG_TO]->(原子指标分类)\"\n" +
            "  }\n" +
            "}";
    private final static String dataSchemaListStr = "[\n" +
            "{\n" +
            "  \"from\": \"HORGGuaranteeV001\",\n" +
            "  \"to\": \"HORGGuaranteeV001\",\n" +
            "  \"relationship\": \"担保\",\n" +
            "  \"hcode\": \"(HORGGuaranteeV001)-[担保]->(HORGGuaranteeV001)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGGuaranteeV003\",\n" +
            "  \"to\": \"HORGGuaranteeV003\",\n" +
            "  \"relationship\": \"担保\",\n" +
            "  \"hcode\": \"(HORGGuaranteeV003)-[担保]->(HORGGuaranteeV003)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGShareHoldV001\",\n" +
            "  \"to\": \"HORGShareHoldV001\",\n" +
            "  \"relationship\": \"持股\",\n" +
            "  \"hcode\": \"(HORGShareHoldV001)-[持股]->(HORGShareHoldV001)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGShareHoldV002\",\n" +
            "  \"to\": \"HORGShareHoldV002\",\n" +
            "  \"relationship\": \"持股\",\n" +
            "  \"hcode\": \"(HORGShareHoldV002)-[持股]->(HORGShareHoldV002)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HBondOrg\",\n" +
            "  \"to\": \"HEventBond\",\n" +
            "  \"relationship\": \"发行证券\",\n" +
            "  \"hcode\": \"(HBondOrg)-[发行证券]->(HEventBond)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"股票上市\",\n" +
            "  \"hcode\": \"(HEventBond)-[股票上市]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"股票退市\",\n" +
            "  \"hcode\": \"(HEventBond)-[股票退市]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"债券上市\",\n" +
            "  \"hcode\": \"(HEventBond)-[债券上市]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"债券退市\",\n" +
            "  \"hcode\": \"(HEventBond)-[债券退市]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"债券违约\",\n" +
            "  \"hcode\": \"(HEventBond)-[债券违约]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HBondPriceEvent\",\n" +
            "  \"relationship\": \"债券价格低于90\",\n" +
            "  \"hcode\": \"(HEventBond)-[债券价格低于90]->(HBondPriceEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventBond\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"中债隐含评级低于A\",\n" +
            "  \"hcode\": \"(HEventBond)-[中债隐含评级低于A]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventNonStdPro\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"非标违约\",\n" +
            "  \"hcode\": \"(HEventNonStdPro)-[非标违约]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HEventOrg\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"失信\",\n" +
            "  \"hcode\": \"(HEventOrg)-[失信]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HNonStdProOrg\",\n" +
            "  \"to\": \"HEventNonStdPro\",\n" +
            "  \"relationship\": \"发行非标产品\",\n" +
            "  \"hcode\": \"(HNonStdProOrg)-[发行非标产品]->(HEventNonStdPro)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PledgorV001\",\n" +
            "  \"to\": \"PawneeV001\",\n" +
            "  \"relationship\": \"出质\",\n" +
            "  \"hcode\": \"(PledgorV001)-[出质]->(PawneeV001)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HCCGPPurchaserOrg\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"发布项目\",\n" +
            "  \"hcode\": \"(HCCGPPurchaserOrg)-[发布项目]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HCCGPBidOrg\",\n" +
            "  \"to\": \"HEvent\",\n" +
            "  \"relationship\": \"中标\",\n" +
            "  \"hcode\": \"(HCCGPBidOrg)-[中标]->(HEvent)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE公司中文名称\",\n" +
            "  \"relationship\": \"中文名称\",\n" +
            "  \"hcode\": \"(PREPCODE)-[中文名称]->(PRE公司中文名称)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE英文名称\",\n" +
            "  \"relationship\": \"英文名称\",\n" +
            "  \"hcode\": \"(PREPCODE)-[英文名称]->(PRE英文名称)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE曾用名\",\n" +
            "  \"relationship\": \"曾用名\",\n" +
            "  \"hcode\": \"(PREPCODE)-[曾用名]->(PRE曾用名)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE纳税人识别号\",\n" +
            "  \"relationship\": \"纳税人识别号\",\n" +
            "  \"hcode\": \"(PREPCODE)-[纳税人识别号]->(PRE纳税人识别号)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE公司注册号\",\n" +
            "  \"relationship\": \"公司注册号\",\n" +
            "  \"hcode\": \"(PREPCODE)-[公司注册号]->(PRE公司注册号)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE地税登记号码\",\n" +
            "  \"relationship\": \"地税登记号码\",\n" +
            "  \"hcode\": \"(PREPCODE)-[地税登记号码]->(PRE地税登记号码)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE国税登记号码\",\n" +
            "  \"relationship\": \"国税登记号码\",\n" +
            "  \"hcode\": \"(PREPCODE)-[国税登记号码]->(PRE国税登记号码)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE营业执照号码\",\n" +
            "  \"relationship\": \"营业执照号码\",\n" +
            "  \"hcode\": \"(PREPCODE)-[营业执照号码]->(PRE营业执照号码)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"PREPCODE\",\n" +
            "  \"to\": \"PRE统一社会信用代码\",\n" +
            "  \"relationship\": \"统一社会信用代码\",\n" +
            "  \"hcode\": \"(PREPCODE)-[统一社会信用代码]->(PRE统一社会信用代码)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGProductCalc\",\n" +
            "  \"to\": \"主营产品\",\n" +
            "  \"relationship\": \"营收\",\n" +
            "  \"hcode\": \"(HORGProductCalc)-[营收]->(主营产品)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGReceivable\",\n" +
            "  \"to\": \"HORGReceivable\",\n" +
            "  \"relationship\": \"应收账款\",\n" +
            "  \"hcode\": \"(HORGReceivable)-[应收账款]->(HORGReceivable)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"HORGPurchase\",\n" +
            "  \"to\": \"HORGPurchase\",\n" +
            "  \"relationship\": \"采购\",\n" +
            "  \"hcode\": \"(HORGPurchase)-[采购]->(HORGPurchase)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDCompany\",\n" +
            "  \"to\": \"UDBrand\",\n" +
            "  \"relationship\": \"拥有\",\n" +
            "  \"hcode\": \"(UDCompany)-[拥有]->(UDBrand)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDCompany\",\n" +
            "  \"to\": \"UDApp\",\n" +
            "  \"relationship\": \"拥有\",\n" +
            "  \"hcode\": \"(UDCompany)-[拥有]->(UDApp)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDCompany\",\n" +
            "  \"to\": \"UDDerivedIndicator\",\n" +
            "  \"relationship\": \"衍生\",\n" +
            "  \"hcode\": \"(UDCompany)-[衍生]->(UDDerivedIndicator)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDBrand\",\n" +
            "  \"to\": \"UDDerivedIndicator\",\n" +
            "  \"relationship\": \"衍生\",\n" +
            "  \"hcode\": \"(UDBrand)-[衍生]->(UDDerivedIndicator)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDApp\",\n" +
            "  \"to\": \"UDDerivedIndicator\",\n" +
            "  \"relationship\": \"衍生\",\n" +
            "  \"hcode\": \"(UDApp)-[衍生]->(UDDerivedIndicator)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDAtomicIndicator\",\n" +
            "  \"to\": \"UDDerivedIndicator\",\n" +
            "  \"relationship\": \"派生\",\n" +
            "  \"hcode\": \"(UDAtomicIndicator)-[派生]->(UDDerivedIndicator)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDProduct\",\n" +
            "  \"to\": \"UDBrand\",\n" +
            "  \"relationship\": \"包含\",\n" +
            "  \"hcode\": \"(UDProduct)-[包含]->(UDBrand)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDProduct\",\n" +
            "  \"to\": \"UDIndustry\",\n" +
            "  \"relationship\": \"BELONG_TO\",\n" +
            "  \"hcode\": \"(UDProduct)-[BELONG_TO]->(UDIndustry)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDIndustry\",\n" +
            "  \"to\": \"UDIndustry\",\n" +
            "  \"relationship\": \"NEXT\",\n" +
            "  \"hcode\": \"(UDIndustry)-[NEXT]->(UDIndustry)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDApp\",\n" +
            "  \"to\": \"UDAppCategory\",\n" +
            "  \"relationship\": \"BELONG_TO\",\n" +
            "  \"hcode\": \"(UDApp)-[BELONG_TO]->(UDAppCategory)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDAppCategory\",\n" +
            "  \"to\": \"UDAppCategory\",\n" +
            "  \"relationship\": \"NEXT\",\n" +
            "  \"hcode\": \"(UDAppCategory)-[NEXT]->(UDAppCategory)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDApp\",\n" +
            "  \"to\": \"UDIndustry\",\n" +
            "  \"relationship\": \"BELONG_TO\",\n" +
            "  \"hcode\": \"(UDApp)-[BELONG_TO]->(UDIndustry)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDIndCategory\",\n" +
            "  \"to\": \"UDIndCategory\",\n" +
            "  \"relationship\": \"NEXT\",\n" +
            "  \"hcode\": \"(UDIndCategory)-[NEXT]->(UDIndCategory)\"\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "  \"from\": \"UDAtomicIndicator\",\n" +
            "  \"to\": \"UDIndCategory\",\n" +
            "  \"relationship\": \"BELONG_TO\",\n" +
            "  \"hcode\": \"(UDAtomicIndicator)-[BELONG_TO]->(UDIndCategory)\"\n" +
            "}\n" +
            "]";

    @Test
    public void parse() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();
        Map<String, Map<String, String>> schemaMap = JSONObject.parseObject(schemaMapStr,Map.class);
        List<Map> dataSchemaList = JSONArray.parseArray(dataSchemaListStr,Map.class);
        String query = "MATCH p=(:公司)-[:担保]->(org:公司)-[:拥有]->(:品牌)<-[:包含]-(:产品) WHERE org.hcode='HORGcadb43d05c7d7596e5df3135d476424b' RETURN p LIMIT 10";
        Map<String,Object> map = new HashMap<>();
        map.put("query",query);
        map.put("schemaMap",schemaMap);
        map.put("dataSchemaList",dataSchemaList);
        Result res =   db.execute("CALL olab.schema.parse({query},{schemaMap},{dataSchemaList}) YIELD query,paras,key RETURN query,paras,key",map);
        System.out.println(res.resultAsString());
    }

}

