/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zols.jsonschema.everit.EveritJsonSchema;
import org.zols.jsonschema.util.TestUtil;

/**
 *
 * @author sathish
 */
public class JsonSchemaTest {

    @Test
    public void testGetParents() {
        JsonSchema jsonScherma = new EveritJsonSchema("computer", TestUtil::getTestSchema);

        Assert.assertEquals("Getting parents of mobile", 2, jsonScherma.getParents().size());
    }

    @Test
    public void testGetSchemaOf() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        JsonSchema jsonSchemaSeller = jsonSchemaComputer.getSchemaOf("seller");
        Assert.assertEquals("Getting schema of seller from computer", "seller", jsonSchemaSeller.getId());
    }

    @Test
    public void testGetPropertyOf() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> warehouseLocationsProperty = jsonSchemaComputer.getPropertyOf("warehouseLocations");
        Assert.assertEquals("Getting Property of seller from computer", "array", warehouseLocationsProperty.get("type"));
    }

    @Test
    public void testGetLocalizedProperties() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertTrue("Getting Localized Properties of computer", jsonSchemaComputer.getLocalizedProperties().containsAll(Arrays.asList("title", "brand")));
    }

    @Test
    public void testGetProperties() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertEquals("get properties of computer", 8, jsonSchemaComputer.getProperties().size());
    }
    
    @Test
    public void testGetDefinitions() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        
        Map<String, Map<String,Object>> definitions = jsonSchemaComputer.getDefinitions();
        assertEquals("get definitions of computer", 4, definitions.size());
    }

}
