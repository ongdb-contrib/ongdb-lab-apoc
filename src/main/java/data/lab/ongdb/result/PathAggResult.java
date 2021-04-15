package data.lab.ongdb.result;

/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.graphdb.Path;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2020/5/25 20:52
 */
public class PathAggResult {
    public Path path;

    public Object pathJ;

    public Number count;

    public PathAggResult() {
    }

    public PathAggResult(Path path) {
        this.path = path;
    }

    public PathAggResult(Object path) {
        this.pathJ = path;
    }

    public PathAggResult(int count) {
        this.count = count;
    }
}
