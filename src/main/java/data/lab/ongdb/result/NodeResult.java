package data.lab.ongdb.result;

import org.neo4j.graphdb.Node;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.NodeResult
 * @Description: TODO
 * @date 2021/4/2 15:56
 */
public class NodeResult {
    public final Node node;

    public NodeResult(Node node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass() && node.equals(((NodeResult) o).node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
