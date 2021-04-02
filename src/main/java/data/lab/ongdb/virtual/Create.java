package data.lab.ongdb.virtual;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.ongdb.result.NodeResult;
import data.lab.ongdb.result.VirtualNode;
import data.lab.ongdb.result.VirtualPathResult;
import data.lab.ongdb.result.VirtualRelationship;
import data.lab.ongdb.util.Util;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.procedure.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.neo4j.graphdb.RelationshipType.withName;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.virtual
 * @Description: TODO
 * @date 2021/4/2 15:46
 */
public class Create {

    @Context
    public GraphDatabaseService db;

    /**
     * @param labelNames:标签列表
     * @param props:属性MAP
     * @param identity:给节点指定的唯一ID
     * @return
     * @Description: TODO
     */
    @Procedure(name = "olab.create.vNode", mode = Mode.READ)
    @Description("olab.create.vNode(['Label'], {key:value,...}, identity) returns a virtual node")
    public Stream<NodeResult> vNode(@Name("label") List<String> labelNames, @Name("props") Map<String, Object> props, @Name("identity") Long identity) {
        return Stream.of(new NodeResult(vNodeFunction(labelNames, identity, props)));
    }

    /**
     * @param labelNames:标签列表
     * @param props:属性MAP
     * @param identity:给节点指定的唯一ID
     * @return
     * @Description: TODO
     */
    @UserFunction("olab.create.vNode")
    @Description("olab.create.vNode(['Label'], {key:value,...}, identity) returns a virtual node")
    public Node vNodeFunction(@Name("label") List<String> labelNames, @Name(value = "identity") Long identity, @Name(value = "props", defaultValue = "{}") Map<String, Object> props) {
        return new VirtualNode(identity, Util.labels(labelNames), props, db);
    }

    /**
     * @param labelsN:标签列表
     * @param n:属性MAP
     * @param identityN:给节点指定的唯一ID
     * @param relType:关系类型
     * @param props:属性MAP
     * @param labelsM:标签列表
     * @param m:属性MAP
     * @param identityM:给节点指定的唯一ID
     * @return
     * @Description: TODO
     */
    @Procedure(name = "olab.create.vPatternFull", mode = Mode.READ)
    @Description("olab.create.vPatternFull(['LabelA'],{key:value},'KNOWS',{key:value,...},['LabelB'],{key:value}) returns a virtual pattern")
    public Stream<VirtualPathResult> vPatternFull(@Name("labelsN") List<String> labelsN, @Name("n") Map<String, Object> n, @Name("identityN") Long identityN,
                                                  @Name("relType") String relType, @Name("props") Map<String, Object> props,
                                                  @Name("labelsM") List<String> labelsM, @Name("m") Map<String, Object> m, @Name("identityM") Long identityM) {
        RelationshipType type = withName(relType);
        VirtualNode from = new VirtualNode(identityN, Util.labels(labelsN), n, db);
        VirtualNode to = new VirtualNode(identityM, Util.labels(labelsM), m, db);
        Relationship rel = new VirtualRelationship(from, to, type).withProperties(props);
        return Stream.of(new VirtualPathResult(from, rel, to));
    }
}

