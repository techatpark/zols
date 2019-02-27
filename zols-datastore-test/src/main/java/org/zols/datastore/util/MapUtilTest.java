/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import static org.zols.datastore.util.MapUtil.getFieldValue;

/**
 *
 * @author sathish
 */
public class MapUtilTest {

    @Test
    public void testSomeMethod() {
        Map<String, Object> computer = sampleJson("computer_full");

        assertEquals("ios", getFieldValue(computer, "os"), "Retrieving Basic Primitive");

        assertEquals(3, getFieldValue(computer, "prefererredSeller.id"), "Retrieving Inner Primitive");

        System.out.println(computer);
    }

}
