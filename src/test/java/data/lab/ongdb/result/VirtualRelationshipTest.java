package data.lab.ongdb.result;

import org.junit.Test;

import static org.junit.Assert.*;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2021/4/2 17:57
 */
public class VirtualRelationshipTest {

    @Test
    public void getId() {
        new VirtualRelationship(null,null,null,null);
        new VirtualRelationship(0L,null,null,null);
    }
}