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
import org.junit.Ignore;
import org.junit.Test;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJson;
import static org.zols.datastore.jsonschema.util.JsonSchemaTestUtil.sampleJsonSchema;

/**
 *
 * @author sathish
 */
public class LocalitationUtilTest {
    

    @Test
    @Ignore
    public void testPrepareJSONMethod() {
        Map<String,Object> localizedJsonData = 
                LocalitationUtil.prepareJSON(sampleJsonSchema("sportscar_composite"), 
                        sampleJson("sportscar_composite"),
                        Locale.ITALY);
        Assert.assertTrue("Locale Specific nested Field removed", 
                ((Map<String,Object>) localizedJsonData.get("insurance")).get("company")==null);
        
    }
    
    @Test
    public void testReadJSONMethod() {
        Map<String,Object> localizedJsonData = 
                LocalitationUtil.readJSON( sampleJson("sportscar_italy"), Locale.ITALY);
        Assert.assertTrue("Locale Specific Field stripped", localizedJsonData.get("four_wheel_drive$it")==null);
        
        Assert.assertTrue("Locale Specific nested Field stripped", ((Map<String,Object>) localizedJsonData.get("insurance")).get("company")!=null);
  
        Assert.assertTrue("Locale Specific Array nested Field stripped", ((Map<String,Object>)((Collection) localizedJsonData.get("owners")).toArray()[0]).get("city") !=null);     
    }
    
    
}
