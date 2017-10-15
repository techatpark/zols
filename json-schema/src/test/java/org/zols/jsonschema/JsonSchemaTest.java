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
import java.util.Set;
import javax.validation.ConstraintViolation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import static org.zols.jsonschema.JsonSchema.LOCALE_SEPARATOR;
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

        assertEquals("Getting parents of computer", 2, jsonScherma.getParents().size());
    }

    @Test
    public void testParent() {
        JsonSchema jsonScherma = new EveritJsonSchema("computer", TestUtil::getTestSchema);

        assertEquals("Getting parent of computer", "device", jsonScherma.getParent().getId());
    }

    @Test
    public void testRoot() {
        JsonSchema jsonScherma = new EveritJsonSchema("computer", TestUtil::getTestSchema);

        assertEquals("Getting root of computer", "product", jsonScherma.getRoot().getId());
    }

    @Test
    public void testGetSchemaOf() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        JsonSchema jsonSchemaSeller = jsonSchemaComputer.getSchemaOf("sellers");
        assertEquals("Getting schema of seller from computer", "seller", jsonSchemaSeller.getId());
    }

    @Test
    public void testGetPropertyOf() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> warehouseLocationsProperty = jsonSchemaComputer.getPropertyOf("warehouseLocations");
        assertEquals("Getting Property of seller from computer", "array", warehouseLocationsProperty.get("type"));
    }

    @Test
    public void testGetLocalizedPropertyNames() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertTrue("Getting Localized Properties of computer", jsonSchemaComputer.getLocalizedPropertyNames().containsAll(Arrays.asList("title", "brand")));
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

        assertEquals("get definitions of computer", 4, ((Map) compositeSchema.get("definitions")).size());
    }

    @Test
    public void testLocalizeData() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> jsonData = getTestData("computer");
        Map<String, Object> localizedJsonData = jsonSchemaComputer.localizeData(jsonData, Locale.ITALY);
        Map<String, Object> n;
        assertFalse("Purity of localizeData", jsonData == localizedJsonData);

        assertNull("Removing Localized field", localizedJsonData.get("title"));
        assertEquals("Replacing Localized field", "HP Laptop", localizedJsonData.get("title" + LOCALE_SEPARATOR + "it"));

        assertNull("Removing Nested Localized field", ((Map) localizedJsonData.get("prefererredSeller")).get("name"));
        assertEquals("Replacing Nested Localized field", "Muthu", ((Map) localizedJsonData.get("prefererredSeller")).get("name" + LOCALE_SEPARATOR + "it"));

        assertNull("Removing Nested Array Localized field", ((Map) ((List) localizedJsonData.get("sellers")).get(0)).get("name"));
        assertEquals("Replacing Nested Array Localized field", "HP Showroom", ((Map) ((List) localizedJsonData.get("sellers")).get(0)).get("name" + LOCALE_SEPARATOR + "it"));

    }

    @Test
    public void testLocalizeDataKeepDefault() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> jsonData = getTestData("computer");
        Map<String, Object> localizedJsonData = jsonSchemaComputer.localizeData(jsonData, Locale.ITALY,true);
        Map<String, Object> n;
        assertFalse("Purity of localizeData", jsonData == localizedJsonData);

        assertEquals("Retaining Localized field",  "HP Laptop", localizedJsonData.get("title"));
        assertEquals("Replacing Localized field", "HP Laptop", localizedJsonData.get("title" + LOCALE_SEPARATOR + "it"));

        assertEquals("Retaining Nested Localized field",  "Muthu",((Map) localizedJsonData.get("prefererredSeller")).get("name"));
        assertEquals("Replacing Nested Localized field", "Muthu", ((Map) localizedJsonData.get("prefererredSeller")).get("name" + LOCALE_SEPARATOR + "it"));

        assertEquals("Retaining Nested Array Localized field","HP Showroom",  ((Map) ((List) localizedJsonData.get("sellers")).get(0)).get("name"));
        assertEquals("Replacing Nested Array Localized field", "HP Showroom", ((Map) ((List) localizedJsonData.get("sellers")).get(0)).get("name" + LOCALE_SEPARATOR + "it"));

    }

    @Test
    public void testDelocalizeData() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> jsonData = getTestData("computer_full");
        Map<String, Object> delocalizedJsonData = jsonSchemaComputer.delocalizeData(jsonData, Locale.ITALY);

        assertFalse("puririty of delocalization", jsonData == delocalizedJsonData);

        assertNull("Removing Localized field", delocalizedJsonData.get("title" + LOCALE_SEPARATOR + "it"));
        assertEquals("Keeping Localized field value into default locale value", "Italy title", delocalizedJsonData.get("title"));

        assertNull("Removing Nested Localized field", ((Map) delocalizedJsonData.get("prefererredSeller")).get("name" + LOCALE_SEPARATOR + "it"));
        assertEquals("Keeping Nested Localized field value into default locale value", "Italy name",
                ((Map) delocalizedJsonData.get("prefererredSeller")).get("name"));
        assertEquals("Retaining default locale value for Nested Array Localized field", "More Showroom", ((Map) ((List) delocalizedJsonData.get("sellers")).get(1)).get("name"));
    }

    @Test
    public void testGetIdPropertyNames() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        assertEquals("Checking id property size", 1, jsonSchemaComputer.getIdPropertyNames().size());
        assertEquals("Checking id property order", Arrays.asList("id"), jsonSchemaComputer.getIdPropertyNames());
    }

    @Test
    public void testGetIdValues() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> jsonData = getTestData("computer");
        assertEquals("Checking id property value", 1, jsonSchemaComputer.getIdValues(jsonData)[0]);
    }

    @Test
    public void testValidate() {
        JsonSchema jsonSchemaComputer = new EveritJsonSchema("computer", TestUtil::getTestSchema);
        Map<String, Object> jsonData = getTestData("computer");
        Set<ConstraintViolation> cv = jsonSchemaComputer.validate(jsonData);
        //assertEquals("Checking id property value", 1, cv.size());
    }

}
