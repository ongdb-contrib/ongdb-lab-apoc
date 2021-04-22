-- 函数安装方式
-- 使用场景：对节点和关系的指标执行过滤；挖掘满足多重指标限制的图模式；时序子图的过滤
CALL apoc.custom.asFunction(
    'es.result.bool',
    'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,\'RETURN TRUE AS countBool\'],\'RETURN FALSE AS countBool\') YIELD value RETURN value.countBool AS bool',
    'BOOLEAN',
    [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
    false,
    '通过判断ES查询结果返回FALSE或者TRUE【结果集大于0返回TRUE】'
);

-- 访问方式：RETURN custom.es.result.bool('10.20.13.130:9200','dl_default_indicator_def',{size:1,query:{term:{product_code:"PF0020020104"}}}) AS boolean






