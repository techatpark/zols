/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zols.jsonschema.everit.EveritJsonSchema;
import org.zols.jsonschema.util.TestUtil;
import static org.zols.jsonschema.util.TestUtil.getTestData;

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
        JsonSchema jsonSchemaSeller = jsonSchemaComputer.getSchemaOf("sellers");
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
        assertEquals("get properties of computer", 9, jsonSchemaComputer.getProperties().size());
    }
    
    @Test
    public void testCompositeSchema() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        
        Map<String, Object> compositeSchema = jsonSchemaComputer.getCompositeSchema();
        
        
        assertEquals("get definitions of computer", 4, ((Map)compositeSchema.get("definitions")).size());
    }
    
    @Test
    public void testLocalizeData() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String,Object> jsonData = getTestData("computer");
        Map<String,Object> localizedJsonData = jsonSchemaComputer.localizeData(jsonData, Locale.ITALY);
        Assert.assertFalse("Purity of localizeData",jsonData == localizedJsonData);
        
        Assert.assertNull("Removing Localized field",localizedJsonData.get("title"));
        Assert.assertNotNull("Replacing Localized field",localizedJsonData.get("title_it"));
        
        
        Assert.assertNull("Removing Nested Localized field",((Map)localizedJsonData.get("prefererredSeller")).get("name"));
        Assert.assertNotNull("Replacing Nested Localized field",((Map)localizedJsonData.get("prefererredSeller")).get("name_it"));
        
        Assert.assertNull("Removing Nested Array Localized field",((Map)((List)localizedJsonData.get("sellers")).get(0)).get("name"));
        Assert.assertNotNull("Replacing Nested Array Localized field",((Map)((List)localizedJsonData.get("sellers")).get(0)).get("name_it"));
        
    }

}
