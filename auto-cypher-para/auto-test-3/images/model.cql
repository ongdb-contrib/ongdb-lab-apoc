CREATE (n1:公司) SET n1.name='公司' WITH n1
CREATE (n2:公司) SET n2.name='公司' WITH n1,n2
CREATE (n3:公司) SET n3.name='公司' WITH n1,n2,n3
CREATE (n4:行业) SET n4.name='行业' WITH n1,n2,n3,n4
CREATE p1=(n1)-[:持股]->(n2) WITH n1,n2,n3,n4,p1
CREATE p2=(n1)-[:担保]->(n2) WITH n1,n2,n3,n4,p1,p2
CREATE p3=(n1)-[:属于]->(n4) WITH n1,n2,n3,n4,p1,p2,p3
CREATE p4=(n1)-[:持股]->(n3) WITH n1,n2,n3,n4,p1,p2,p3,p4
CREATE p5=(n2)-[:持股]->(n3) WITH n1,n2,n3,n4,p1,p2,p3,p4,p5
CREATE p6=(n3)-[:属于]->(n4) WITH n1,n2,n3,n4,p1,p2,p3,p4,p5,p6
CREATE p7=(n2)-[:担保]->(n3) WITH n1,n2,n3,n4,p1,p2,p3,p4,p5,p6,p7
CREATE p8=(n1)-[:担保]->(n3) WITH n1,n2,n3,n4,p1,p2,p3,p4,p5,p6,p7,p8
RETURN {graph:[p1,p2,p3,p4,p5,p6,p7,p8]} AS graph
