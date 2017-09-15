/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.zols.jsonschema.JsonSchemaTest;

/**
 *
 * @author sathish
 */
public class TestUtil {

    public static Map<String, Object> getTestSchema(String nameOfSchema) {
        if(nameOfSchema != null ) {
            InputStream inputStream = TestUtil.class.getResourceAsStream("/schema/" + nameOfSchema + ".json");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            return new JSONObject(buffer.lines().collect(Collectors.joining("\n"))).toMap();
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
        return null;
    }
    
    
    public static Map<String, Object> getTestData(String pathOfData) {
        InputStream inputStream = TestUtil.class.getResourceAsStream("/jsondata/" + pathOfData + ".json");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            return new JSONObject(buffer.lines().collect(Collectors.joining("\n"))).toMap();
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
