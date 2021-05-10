CREATE (org1:公司) SET org1.name='公司' WITH org1
CREATE (org2:公司) SET org2.name='公司' WITH org1,org2
CREATE (org3:公司) SET org3.name='公司' WITH org1,org2,org3
CREATE (loc:位置) SET loc.name='位置' WITH org1,org2,org3,loc
CREATE (pro:产品) SET pro.name='产品' WITH org1,org2,org3,loc,pro
CREATE (idu1:行业) SET idu1.name='行业' WITH org1,org2,org3,loc,pro,idu1
CREATE (idu2:行业) SET idu2.name='行业' WITH org1,org2,org3,loc,pro,idu1,idu2
CREATE p1=(org1)-[:持股]->(org2) WITH org1,org2,org3,loc,pro,idu1,idu2,p1
CREATE p2=(org1)-[:担保]->(org3) WITH org1,loc,pro,idu1,idu2,p1,p2
CREATE p3=(org1)-[:属于]->(loc) WITH org1,pro,idu1,idu2,p1,p2,p3
CREATE p4=(org1)-[:生产]->(pro) WITH org1,idu1,idu2,p1,p2,p3,p4
CREATE p5=(org1)-[:属于]->(idu1) WITH org1,idu1,idu2,p1,p2,p3,p4,p5
CREATE p6=(idu1)-[:上下游]->(idu2) WITH p1,p2,p3,p4,p5,p6
RETURN {graph:[p1,p2,p3,p4,p5,p6]} AS graph

