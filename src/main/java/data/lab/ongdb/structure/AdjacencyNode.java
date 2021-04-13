package data.lab.ongdb.structure;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.ArrayList;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.structure.AdjacencyNode
 * @Description: TODO(表示一个节点以及和这个节点相连的所有节点)
 * @date 2021/4/13 13:50
 */
public class AdjacencyNode {
    public String name = null;
    public ArrayList<AdjacencyNode> relationNodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AdjacencyNode> getRelationNodes() {
        return relationNodes;
    }

    public void setRelationNodes(ArrayList<AdjacencyNode> relationNodes) {
        this.relationNodes = relationNodes;
    }
}
