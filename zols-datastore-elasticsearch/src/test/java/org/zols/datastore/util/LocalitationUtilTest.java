/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;

/**
 *
 * @author sathish
 */
public class LocalitationUtilTest {
    

    @Test
    public void testPrepareJSONMethod() {
        Map<String,Object> localizedJsonData = 
                LocalitationUtil.prepareJSON(jsonSchema(sampleJsonSchema("raw/teacher")), 
                        sampleJson("teacher"),
                        Locale.ITALY);
        
        Assert.assertEquals("Preparation of Locale Specific Data", sampleJson("teacher$it"), localizedJsonData);
        
        
    }
    
    @Test
    public void testReadJSONMethod() {
        Map<String,Object> localizedJsonData = 
                LocalitationUtil.readJSON( sampleJson("sportscar_italy"), Locale.ITALY);
        
        Map<String,Object> plainJsonData = 
                LocalitationUtil.readJSON( sampleJson("sportscar_italy"));
        
        Assert.assertTrue("Locale Specific Field stripped", localizedJsonData.get("four_wheel_drive$it")==null);
        
        Assert.assertTrue("Locale Specific nested Field stripped", ((Map<String,Object>) localizedJsonData.get("insurance")).get("company")!=null);
  
        Assert.assertTrue("Locale Specific Array nested Field stripped", ((Map<String,Object>)((Collection) localizedJsonData.get("owners")).toArray()[0]).get("city") !=null);     
    }
    
    
}
