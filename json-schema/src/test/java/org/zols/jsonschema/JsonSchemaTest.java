/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.util.Arrays;
import java.util.Locale;
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
        JsonSchema jsonScherma = new EveritJsonSchema("mobile", TestUtil::getTestSchema);

        Assert.assertEquals("Getting parents of mobile", 2, jsonScherma.getParents().size());
        Assert.assertEquals("Getting first parent of mobile", "device", jsonScherma.getParents().get(0).getId());
        Assert.assertEquals("Getting second parent of mobile", "product", jsonScherma.getParents().get(1).getId());
        JsonSchema jsonSchemaMobile = new EveritJsonSchema("mobile", TestUtil::getTestSchema);

        assertEquals("Getting parents of mobile", 2, jsonSchemaMobile.getParents().size());
        assertEquals("Getting first parent id of mobile", "device", jsonSchemaMobile.getParents().get(0).getId());
        assertEquals("Getting last parent id of mobile", "product", jsonSchemaMobile.getParents().get(1).getId());

    }

    @Test
    public void testLocalizeData() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);

        Assert.assertNotNull("Localizing Computer data with 'name' field",
                jsonSchemaComputer.localizeData(getTestData("computer"), Locale.CHINESE).get("name_zh"));

        Assert.assertNull("Localizing Computer data with 'name' field",
                jsonSchemaComputer.localizeData(getTestData("computer"), Locale.CHINESE).get("name"));

    }

    @Test
    public void testGetLocalizedProperties() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertTrue("Getting Localized Properties of computer", jsonSchemaComputer.getLocalizedProperties().containsAll(Arrays.asList("name", "brand")));
    }

    @Test
    public void testDelocalizeData() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertEquals("Getting De-localized Data of computer", getTestData("computer_zh"), jsonSchemaComputer.delocalizeData(getTestData("computer_full"), Locale.CHINESE));

    }

}
