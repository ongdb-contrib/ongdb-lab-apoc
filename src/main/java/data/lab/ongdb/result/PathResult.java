package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import org.neo4j.graphdb.Path;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result.PathResult
 * @Description: TODO
 * @date 2021/4/14 17:05
 */
public class PathResult {
    public Path path;

    public PathResult(Path path) {
        this.path = path;
    }
}
