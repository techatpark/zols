/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.zols.jsonschema.everit.EveritJsonSchema;

/**
 *
 * @author sathish
 */
public class JsonSchemaTest {
    
    @Test
    public void testGetParents() {
        JsonSchema jsonScherma = new EveritJsonSchema("mobile", this::getSchema);
        
        Assert.assertEquals("Getting parents of mobile", 2, jsonScherma.getParents().size());
        Assert.assertEquals("Getting first parent of mobile", "device", jsonScherma.getParents().get(0).getId());
        Assert.assertEquals("Getting second parent of mobile", "product", jsonScherma.getParents().get(1).getId());
    }

    private Map<String,Object> getSchema(String nameOfSchema) {
        InputStream inputStream = getClass().getResourceAsStream("/schema/" + nameOfSchema + ".json");

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            return new JSONObject(buffer.lines().collect(Collectors.joining("\n"))).toMap();
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    

}
