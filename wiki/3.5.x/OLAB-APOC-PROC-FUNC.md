# OLAB依赖APOC的预装存储过程
>使用OLAB插件时部分函数和过程依赖下面的函数和过程，需要提前安装到集群【在CORE节点执行即可】
## custom.es.result.bool
- 功能
>通过判断ES查询结果返回FALSE或者TRUE
```
@param {es-url}-ES地址
@param {index-name}-索引名称
@param {query-dsl}-ES查询
【返回值类型：BOOLEAN】
【结果集大于0返回TRUE】
RETURN custom.es.result.bool('10.20.13.130:9200','dl_default_indicator_def',{size:1,query:{term:{product_code:"PF0020020104"}}}) AS boolean
```
- 安装方式
```
CALL apoc.custom.asFunction(
    'es.result.bool',
    'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value WITH value.hits.total.value AS count CALL apoc.case([count>0,\'RETURN TRUE AS countBool\'],\'RETURN FALSE AS countBool\') YIELD value RETURN value.countBool AS bool',
    'BOOLEAN',
    [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
    false,
    '通过判断ES查询结果返回FALSE或者TRUE【结果集大于0返回TRUE】'
);
```
## custom.es.result
- 功能
>返回ES查询结果
```
@param {es-url}-ES地址
@param {index-name}-索引名称
@param {query-dsl}-ES查询
【返回值类型：MAP】
RETURN custom.es.result({esuUrl},{indexName},{queryDsl}) AS result
RETURN custom.es.result('10.20.13.130:9200','gh_ind_rel_company_guarantee_company',{size:100,query:{bool:{filter:{bool:{must:[{range:{amount:{gte:100000}}}]}},must:[{term:{hcode:'852c1ea85d8dd6b1354aa1a786dbc1db'}}]}}}) AS result
```
- 安装方式
```
CALL apoc.custom.asFunction(
    'es.result',
    'CALL apoc.es.query($esuUrl,$indexName,\'\',null,$queryDsl) YIELD value RETURN value',
    'MAP',
    [['esuUrl','STRING'],['indexName','STRING'],['queryDsl','MAP']],
    false,
    '返回ES查询结果'
);
```




