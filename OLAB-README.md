# ONgDB自定义函数与过程插件
>apoc包过程函数命名域：olab.*

> 此插件对应版本：ONgDB-3.5.X

> PLUGIN安装：MAVEN INSTALL之后在target目录下生成的JAR包安装到ONgDB安装目录下的PLUGIN目录，将dic文件夹移动到ONgDB安装根目录即可。

## 自定义中文全文检索

中文分词：需要新增的词表在user_defined.dic新增或者在cfg.xml文件中配置即可

```
# 版本信息：
 
LUCENE-5.5.0 
     
IKAnalyzer-5.0
```

## 自定义过程和函数

>过程：用 Call com.xxxx.xx （参数）来调用执行。

>函数：可以用在cypher中任何可以使用方法的地方如where子句，return子句中。如match (n) wehre com.xxx.xx(n) return n。

### 计算IDS中ID的个数
```cql
RETURN olab.getEventIdsSize("123123,123123,2131,12321,23424,123123,2331") as value
match p=(n:LABEL1)<-[r:REL]-(m:LABEL2) where n.name='新闻_1432' and r.eventTargetIds IS NOT NULL return p ORDER BY olab.getEventIdsSize(r.eventTargetIds) DESC limit 10
```

### 列表数字降序排列
```cql
RETURN olab.sortDESC([4,3,5,1,6,8,7]) as descList
```

### 打印HELLO WORLD
```cql
RETURN olab.hello("world") as greeting
```

### 创建测试节点
```cql
CALL olab.createCustomer('Test') YIELD node RETURN node
```

### 离差标准化函数
```cql
olab.scorePercentage
```

### 移动小数点
```cql
olab.moveDecimalPoint
```

### 中文分词 *-true 智能分词，false 细粒度分词
```cql
RETURN olab.index.iKAnalyzer('复联终章快上映了好激动，据说知识图谱与人工智能技术应用到了那部电影！吖啶基氨基甲烷磺酰甲氧基苯胺是一种药嘛？',true) AS words
```
- 组合切词结果后进行查询
```cql
CALL olab.iKAnalyzer('北京基金赵总',true) YIELD words WITH words
CALL olab.ik.combination.couple(words) YIELD wordF,wordT WITH wordF,wordT
UNWIND wordF AS row1
UNWIND wordT AS row2
MATCH p=(n)-[*..2]-(m) WHERE n.name CONTAINS row1 AND m.name CONTAINS row2 RETURN p LIMIT 100
```
```cql
CALL olab.iKAnalyzer('东城基金赵总',true) YIELD words WITH words
CALL olab.ik.combination.triple(words) YIELD wordF,wordT,wordR WITH wordF,wordT,wordR
UNWIND wordF AS row1
UNWIND wordT AS row2
UNWIND wordR AS row3
MATCH p=(n)-[*..2]-(f)-[*..2]-(m) WHERE n.name CONTAINS row1 AND m.name CONTAINS row2 AND f.name CONTAINS row3 RETURN p LIMIT 100
```

### 创建中文全文索引（不同标签使用相同的索引名即可支持跨标签类型检索）
```cql
CALL olab.index.addChineseFulltextIndex('IKAnalyzer', ['description'], 'Loc') YIELD message RETURN message
CALL olab.index.addChineseFulltextIndex('IKAnalyzer',['description','year'], 'Loc') YIELD message RETURN message
CALL olab.index.addChineseFulltextIndex('IKAnalyzer', ['description','year'],'LocProvince') YIELD message RETURN message

```

### 中文全文索引查询（可跨标签类型检索）- *-1表示数据量不做限制返回全部 *-lucene查询示例 
```cql
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node RETURN node
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', 100) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', 'description:吖啶基氨基甲烷磺酰甲氧基苯胺', -1) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(description:复联) AND -(_entity_name:美国)',10) YIELD node,weight RETURN node,weight
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(site_name:东方网) OR +(_entity_name:东方网)',10) YIELD node,weight RETURN node,weight
-- 包含小和合 不包含婷、诗和Jason Lim
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '_entity_name:(+小 +合 -"婷" -诗 -"Jason Lim")',10) YIELD node,weight RETURN node,weight
-- 范围查询
CALL olab.index.chineseFulltextIndexSearch('IKAnalyzer', '+(name:东方网) AND +(testTime:[1582279892461 TO 1582279892461])',10) YIELD node,weight RETURN node,weight

```

### 为节点添加索引
```cql
MATCH (n) WHERE n.name='A' WITH n CALL olab.index.addNodeChineseFulltextIndex(n, ['description']) RETURN *
```

### 生成JSON-从CYPHER直接生成JSON【支持节点转换/属性转换/路径转换】
```cql
match (n) return olab.convert.json(n) limit 10
match p=(n)-[]-() return olab.convert.json(p) limit 1
match (n) return olab.convert.json(properties(n)) limit 10
RETURN apoc.convert.fromJsonList(olab.convert.json(['21','123',123]))
```

### 更多过程与函数请参考源码和测试...

## IKAnalyzer分词

分词步骤：词典加载、预处理、分词器分词、歧义处理、结尾处理（处理遗漏中文字符/处理数量词）

分词模式：SMART模式（歧义判断）与非SMART模式（最小力度的分词）
```
具体的实例：
     张三说的确实在理

smart模式的下分词结果为：  
     张三 | 说的 | 确实 | 在理

而非smart模式下的分词结果为：
     张三 | 三 | 说的 | 的确 | 的 | 确实 | 实在 | 在理
```

### 生成文本指纹
```cql
RETURN olab.simhash('公司系经长春经济体制改革委员会长体改(1993)165号文批准') AS simHash
╒══════════════════════════════════════════════════════════════════╕
│"simHash"                                                         │
╞══════════════════════════════════════════════════════════════════╡
│"1010111110110100111001000100110010000110100110101110101110000110"│
└──────────────────────────────────────────────────────────────────┘
```

### 计算两个节点simhash相似度
>返回值只返回本次新建的关系，每次操作都有返回值，无新建则返回空
- 创建三个两两之间简介相似的组织机构节点
```cql
MERGE (n:组织机构:中文名称 {name:'阿里'}) SET n.brief='阿里巴巴(中国)网络技术有限公司成立于1999年09月09日，注册地位于浙江省杭州市滨江区网商路699号，法定代表人为戴珊。经营范围包括开发、销售计算机网络应用软件；设计、制作、加工计算机网络产品并提供相关技术服务和咨询服务；',n.simhash=olab.simhash(n.brief)
MERGE (m:组织机构:中文名称 {name:'阿里巴巴'}) SET m.brief='阿里巴巴(中国)网络技术有限公司成立于1999年09月09日，注册地位于浙江省杭州市滨江区网商路699号，法定代表人为戴珊。成年人的非证书劳动职业技能培训（涉及许可证的除外）。（依法须经批准的项目，经相关部门批准后方可开展经营活动）阿里巴巴(中国)网络技术有限公司对外投资86家公司，具有37处分支机构。',m.simhash=olab.simhash(n.brief)
MERGE (f:组织机构:中文名称 {name:'Alibaba'}) SET f.brief='经营范围包括开发、销售计算机网络应用软件；设计、制作、加工计算机网络产品并提供相关技术服务和咨询服务；服务：自有物业租赁，翻译，成年人的非证书劳动职业技能培训（涉及许可证的除外）。（依法须经批准的项目，经相关部门批准后方可开展经营活动）阿里巴巴(中国)网络技术有限公司对外投资86家公司，具有37处分支机构。',f.simhash=olab.simhash(n.brief)
RETURN n,m,f
```
- 创建三个两两之间简介不相似的组织机构节点【短文本的阈值设置hammingDistance】
```
MERGE (n:组织机构:中文名称 {name:'天猫'}) SET n.brief='37处分支机构。',n.simhash=olab.simhash(n.brief)
MERGE (m:组织机构:中文名称 {name:'阿猫'}) SET m.brief='1999年09月09日',m.simhash=olab.simhash(m.brief)
MERGE (f:组织机构:中文名称 {name:'猫猫'}) SET f.brief='自有物业租赁',f.simhash=olab.simhash(f.brief)
RETURN n,m,f
```
- 生成组织机构之间的‘相似简介‘的关系
```cql
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似简介]-(m))
CALL olab.simhash.build.rel(n,m,'simhash','simhash','相似简介',3) YIELD pathJ RETURN pathJ
```
- 生成组织机构之间的‘相似简介‘的关系 - 两组属性列表中任意一组SimHash属性相似即判定为相似
```cql
MERGE (n:组织机构:中文名称 {name:'Alibaba天猫'}) SET n.brief_intro_cn=olab.simhash('37处分支机构'),n.brief_intro_en=olab.simhash('37处分支机构')
MERGE (m:组织机构:中文名称 {name:'Alibaba阿猫'}) SET n.brief_intro_cn=olab.simhash('1999年09月09日'),n.brief_intro_en=olab.simhash('1999年09月09日'),n.business_intro_cn=olab.simhash('37处分支机构')
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似简介]-(m))
CALL olab.simhash.build.rel.cross(n,m,['brief_intro_cn','brief_intro_en'],['brief_intro_cn','business_intro_cn','brief_intro_en','business_intro_en'],'相似简介',3) YIELD pathJ RETURN pathJ
```
- 批量并发迭代计算’相似简介‘关系
```cql
CALL apoc.periodic.iterate("MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) WHERE n<>m AND NOT ((n)-[:相似简介]-(m)) RETURN n,m", "WITH {n} AS n,{m} AS m CALL olab.simhash.build.rel(n,m,'simhash','simhash','相似简介',3,false) YIELD pathJ RETURN pathJ", {parallel:true,batchSize:10000}) YIELD  batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations RETURN batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations
```
### 计算两个节点编辑距离相似度
>返回值只返回本次新建的关系，每次操作都有返回值，无新建则返回空
>阈值参考：英文0.9，中文0.8
- 【英文计算结果较好】计算两个节点的编辑距离相似度，相似则建立’相似名称‘关系
```
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似名称]-(m))
CALL olab.editDistance.build.rel(n,m,'editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ
```
- 批量并发迭代计算’相似名称‘关系
```cql
CALL apoc.periodic.iterate("MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) WHERE n<>m AND NOT ((n)-[:相似名称]-(m)) RETURN n,m", "WITH {n} AS n,{m} AS m CALL olab.editDistance.build.rel(n,m,'editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ", {parallel:true,batchSize:10000}) YIELD  batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations RETURN batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations
```
- 拿取节点关联的别名，两两交叉计算编辑距离相似度，相似度最高值如果满足阈值则新建关系
```
CREATE (n {editDis:'Google M Inc.'}) SET n:组织机构:中文名称 
CREATE (m {editDis:'Google T Inc.'}) SET m:组织机构:中文名称 
CREATE (n1 {name:'谷歌'}) SET n1:组织机构:中文简称 
CREATE (n2 {name:'Google'}) SET n2:组织机构:英文简称 
CREATE (m1 {name:'谷歌M'}) SET m1:组织机构:中文简称 CREATE (m2 {name:'谷歌M'}) SET m2:组织机构:英文简称 
CREATE (n)-[:关联别名]->(n1) 
CREATE (n)-[:关联别名]->(n2) 
CREATE (m)-[:关联别名]->(m1) 
CREATE (m)-[:关联别名]->(m2) 
```
- 英文中文使用相同权重
```
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似名称]-(m))
CALL olab.editDistance.build.rel.cross(n,m,'关联别名','name','editDis','editDis','相似名称',0.9,true) YIELD pathJ RETURN pathJ
```
- 自动区分英文和中文，使用不同阈值进行计算
```
MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) 
WHERE n<>m AND NOT ((n)-[:相似名称]-(m))
CALL olab.editDistance.build.rel.cross.encn(n,m,'关联别名','name','editDis','editDis','相似名称',0.9,0.8,true) YIELD pathJ RETURN pathJ
```
```
CALL apoc.periodic.iterate("MATCH (n:组织机构:中文名称),(m:组织机构:中文名称) WHERE n<>m AND NOT ((n)-[:相似名称]-(m)) RETURN n,m", "WITH {n} AS n,{m} AS m CALL olab.editDistance.build.rel.cross.encn(n,m,'关联别名','name','name','name','相似名称',0.9,0.8,true) YIELD pathJ RETURN pathJ", {parallel:true,batchSize:10000}) YIELD  batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations RETURN batches,total,timeTaken,committedOperations,failedOperations,failedBatches,retries,errorMessages,batch,operations
```
- 多个关联名称关系
```
CALL olab.editDistance.build.rel.cross.encn.multirel(n,m,['关联别名','英文名称'],'name','editDis','editDis','相似名称',0.9,0.8,true) YIELD pathJ RETURN pathJ
```
## 根据关系模式计算两个节点相似度
```
MATCH (n:`组织机构`:`中文名称`) WITH n SKIP 0 LIMIT 100
MATCH (m:`组织机构`:`中文名称`) WHERE n<>m WITH n,m
MATCH p=(n)-[*..2]-(m) WHERE n<>m 
WITH [r IN relationships(p) | TYPE(r)] AS relList,n,m
WITH collect(relList) AS collectList,n,m
CALL olab.similarity.collision(n,m,collectList,{关联人:3,关联网址:3,关联城市:1}) YIELD similarity,startNode,endNode 
RETURN startNode.name,endNode.name,similarity ORDER BY similarity DESC LIMIT 100
```

## 根据关系模式相似度聚类节点
- 建索引
```
CREATE INDEX ON :PREClusterHeart公司(cluster_id);
```
- 运行聚类算法
```
CALL olab.cluster.collision(['组织机构','中文名称'],{关联人:3,关联网址:3,关联城市:1},'PREClusterHeart公司',2,'cluster_id') YIELD clusterNum RETURN clusterNum
```
- 获取‘5301’这个簇的所有节点
```
MATCH (n:`组织机构`:`中文名称`)  WHERE n.cluster_id=5301 RETURN n LIMIT 25
```
- 查看所有聚簇
```
MATCH (n:PREClusterHeart公司) WITH n.cluster_id AS clusterId
MATCH (m:`组织机构`:`中文名称`) WHERE m.cluster_id=clusterId 
RETURN clusterId AS master,COUNT(m) AS slaveCount,COLLECT(id(m)+'-'+m.name) AS slaves
```
- 后台任务的方式运行聚类算法
>权重为-1表示不计算相似直接划分到同一个簇
```
CALL apoc.periodic.submit('writeOrgClusterTask','CALL olab.cluster.collision([\'组织机构\',\'中文名称\'],{关联人:3,关联网址:3,关联城市:1},\'PREClusterHeart公司\',2,\'cluster_id\')')
CALL apoc.periodic.list()
```
## 增加HTTP调用函数-支持API绝对地址
```
RETURN olab.http.post('api-address','input')
RETURN olab.http.get('api-address')
RETURN olab.http.put('api-address','input')
RETURN olab.http.delete('api-address','input')
```
## 用正则串过滤字段值 ， 并返回过滤之后的VALUE ； 保留空格
```
RETURN REPLACE(olab.replace.regexp('"TMC Rus" Limited Liability Company','[`~!@#$%^&*()+=|{}\':;\',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。 ，、？"-]')," ","")
RETURN olab.replace.regexp('2020年07月23号','[A-Za-z_\u4e00-\u9fa5]+')
```
## 对存列表的属性字段进行排重【字段存储JSON列表对象】【返回排重后的数据】
```
WITH '[{"investType":"-1","amount":102,"updateDate":20011019121208,},{"investType":"-1","amount":-1,"updateDate":20041014104446,},{"investType":"-1","amount":-1,"updateDate":20011019170043,}]' AS jsonString
WITH ['investType','amount'] AS keyFields,jsonString
WITH olab.remove.duplicate(jsonString,keyFields) AS value 
RETURN apoc.convert.fromJsonList(value) AS jsonValue
```
## 分析输入节点PATH按照关系层级分类节点【输入一个完整的计算逻辑图】【输出层级执行顺序LIST】
```
# RETURN olab.parse.path(path,)
```
## 分析输入节点PATH按照关系层级分类节点【输入一个完整的计算逻辑图】【输出层级执行顺序LIST】
```
# RETURN olab.operator.sort()
```
## 解析JSONArray ， 通过传入字段排序array ， 并返回前N个结果
```
RETURN olab.sort.jsonArray({jsonString},{sortField},{sort},{returnSize}) AS value
```
## 解析JSONArray, 进行采样
```
RETURN olab.sampling.jsonArray({jsonString},{samplingType},{samplingSize}) AS value
```
## 解析JSONArray, 进行采样 ： 从列表中选举距离当前时间最近的对象
```
apoc.convert.fromJsonMap()
# 【小于指定时间找最近】
RETURN olab.samplingByDate.jsonArray({jsonString},{dateValue},{dateField}) AS value
# 【基于时间距离长度找最近时间】
RETURN olab.samplingByDate.dis.jsonArray({jsonString},{dateValue},{dateField}) AS value
```
## 字符串处理
```
#提取英文中文
RETURN olab.string.matchCnEn('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
#大写转为小写
RETURN olab.string.toLowerCase('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
#繁体转为简体
RETURN olab.string.toSimple('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
#【提取英文中文】【大写转为小写】【繁体转为简体】
RETURN olab.string.matchCnEnRinse('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
#【直接编码】默认编码为中文
RETURN olab.string.encode('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
#【先提取中文英文】默认编码为中文
RETURN olab.string.encodeEncCnc('國際生打撒3.$#%@#$GuangDong Rongjun Co') AS value;
```
## 集合转换【CSV格式转为mapList】【数据封装格式转换】
```
RETURN olab.structure.mergeToListMap(['area_code','author'],[['001','HORG001'],['002','HORG002']])
```

## 【CSV格式转为mapList】【数据封装格式转换】
```
RETURN olab.structure.mergeToListMap({fields},{items}) AS value
RETURN olab.structure.mergeToListMap(['area_code','author'],[['001','HORG001'],['002','HORG002']])
```

## 标准化时间字段【可选是否对无效时间对象是否去噪】【保留14位LONG类型数字】
```
RETURN olab.standardize.date({object},{isStdDate},{selection}) AS value
RETURN olab.standardize.date(202011,true,NULL);
RETURN olab.standardize.date('202011',true,NULL);
RETURN olab.standardize.date('2020-11-26 08:47:38.0',true,NULL);
RETURN olab.standardize.date('2020-11-26T08:47:38',true,NULL);
RETURN olab.standardize.date([20201201,-1,201912,2020,"dasd"],TRUE,'ASC')
```

## 重置MAP - 移除传入的KEY
```
RETURN olab.reset.map({map},{keys}) AS value
RETURN olab.reset.map({total: 1,committed: 1,failed: 0},['total','failed'])
```

## 解析JSONArray从列表中选举距离当前时间最近的对象【选举之前增加其他过滤条件】
```
# 【小于指定时间找最近】
RETURN olab.samplingByDate.filter.jsonArray({jsonString},{dateValue},{dateField},{filterMap}) AS value
# 【基于时间距离长度找最近时间】
RETURN olab.samplingByDate.dis.filter.jsonArray({jsonString},{dateValue},{dateField},{filterMap}) AS value
```
```
filterMap的设置方式：
MAP中KEY为字段名，VALUE为过滤条件
数值类型支持的过滤方式：
/**
 * 搜索大于某值的字段，不包含该值本身
 **/
GT("gt", ">"),
/**
 * 搜索大于某值的字段，包含该值本身
 **/
GTE("gte", ">="),
/**
 * 搜索小于某值的字段，不包含该值本身
 **/
LT("lt", "<"),
/**
 * 搜索小于某值的字段，包含该值本身
 **/
LTE("lte", "<=");
字符串类型支持的过滤方式，目前只支持全等【过滤字符时‘condition’填写以上枚举条件不包含的值即可】
# 过滤ratio大于-1的值
{ratio:{value:-1,condition":">"}}
# 过滤ratio大于-1，并且src等于‘caihui2’，并且amount大于等于10000000
{ratio:{value:-1,condition":">"},src:{value:"caihui2",condition":"STR"},amount:{value:10000000,condition":">="}}
MATCH p=(n:HORGShareHold)-[r]->() WHERE apoc.convert.fromJsonMap(olab.samplingByDate.filter.jsonArray(r.shareholding_detail,'releaseDate',20200415000000,{ratio:{value:-1,condition:">"},src:{value:"caihui2",condition:"STR"},amount:{value:10000000,condition:">="}})).ratio > 0
RETURN r.shareholding_detail LIMIT 1
```

## 指定最小ID和最大ID，生成N个指定SIZE的列表【每个列表只拿最大最小ID】
```
RETURN olab.ids.batch({min},{max},{batch}) AS value
WITH olab.ids.batch(1,10000000000,50000000) AS value
UNWIND value AS list
RETURN list[0] AS min,list[1] AS max
```

## 重置MAP - 为map新增传入KEY和VALUE
```
RETURN olab.add.map({map},{key:value,key2:value}) AS value
RETURN olab.add.map({total: 1,committed: 1,failed: 0},{key:1,key2:200})
```

## 字符串替换 - 按照传入的map替换
```
RETURN olab.repalce({string},{replaceListMap})
RETURN olab.replace('RETURN {url} AS url,{sql} AS sql',[{raw:'{url}',rep:'\'test-url\''},{raw:'{sql}',rep:'\'test-sql\''}])
```
```
WITH 'test-url' AS url
RETURN olab.replace('RETURN {url} AS url,{sql} AS sql',[{raw:'{url}',rep:'\'`'+url+'`\''},{raw:'{sql}',rep:'\'test-sql\''}])
```
```
WITH 'test-url' AS url
RETURN olab.replace('RETURN {url} AS url,{sql} AS sql',[{raw:'{url}',rep:'\'\''+url+'\'\''},{raw:'{sql}',rep:'\'test-sql\''}])
```

## 对传入的字符串执行’\’‘转义操作
```
WITH 'SELECT parent_pcode AS `name`,CONVERT(DATE_FORMAT(hupdatetime,\'%Y%m%d%H%i%S\'),UNSIGNED INTEGER) AS hupdatetime FROM MSTR_ORG_PRE' AS loadSql
RETURN olab.escape(loadSql)
```

## 指定ID的虚拟节点数据
- 指定ID生成虚拟节点
```
//CALL apoc.create.vNode(['Person'],{name:'John'}) YIELD node RETURN node
CALL olab.create.vNode(['行业'],{hcode:'HINDUS',name:'轻工'},-109) YIELD node RETURN node
RETURN olab.create.vNode(['行业'],-109,{hcode:'HINDUS',name:'轻工'}) AS node
```
- 指定节点ID生成虚拟PATH
```
//CALL apoc.create.vPatternFull(['Person'],{name:'John'},'KNOWS',{since:2010},['Person'],{name:'Jane'}) YIELD from,rel,to RETURN from,rel,to
CALL olab.create.vPatternFull(['Person'],{name:'John'},-109,'KNOWS',{since:2010},['Person'],{name:'Jane'},-110) YIELD from,rel,to WITH (from)-[rel]->(to) AS path RETURN path
UNION 
CALL olab.create.vPatternFull(['Person'],{name:'John'},-109,'KNOWS',{since:2010},['Person'],{name:'Jane'},-111) YIELD from,rel,to WITH (from)-[rel]->(to) AS path RETURN path
```

