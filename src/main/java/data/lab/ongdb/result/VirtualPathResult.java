package data.lab.ongdb.result;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.VirtualPathResult
 * @Description: TODO
 * @date 2021/4/2 16:24
 */
public class VirtualPathResult {
    public final Node from;
    public final Relationship rel;
    public final Node to;

    public VirtualPathResult(Node from, Relationship rel, Node to) {
        this.from = from;
        this.rel = rel;
        this.to = to;
    }

    public VirtualPathResult(VirtualNode from, VirtualRelationship rel, VirtualNode to) {
        this.from = from;
        this.rel = rel;
        this.to = to;
    }
}
