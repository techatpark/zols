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
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;
import org.zols.jsonschema.everit.EveritJsonSchema;

/**
 *
 * @author sathish
 */
public class JsonSchemaTest {

    @Test
    public void testValidation() {
        JsonSchema jsonScherma = new EveritJsonSchema("person", this::getSchema);

        JSONObject jsonData = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/jsondata/person.json")));
     
        jsonScherma.validate(jsonData.toMap());

    }
    
    @Test(expected = ValidationException.class)
    public void testInvalidation() {
        JsonSchema jsonScherma = new EveritJsonSchema("person", this::getSchema);

        JSONObject jsonData = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/jsondata/person_invalid.json")));
     
        jsonScherma.validate(jsonData.toMap());

    }
    
    @Test
    public void testGetLocalizedData() {
        JsonSchema jsonScherma = new EveritJsonSchema("person", this::getSchema);

        JSONObject jsonData = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/jsondata/person_zh.json")));

        Map<String,Object> ld = jsonScherma.localizeData(jsonData.toMap(), Locale.CHINESE);
        
        System.out.println("ld" + ld);
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
