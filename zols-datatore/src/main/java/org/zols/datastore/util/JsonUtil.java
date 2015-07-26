/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zols.datastore.jsonschema.JSONSchema;

/**
 *
 * @author WZ07
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Object> asMap(String jsonData) {
        try {
            return MAPPER.readValue(jsonData,
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException ex) {
            Logger.getLogger(JSONSchema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String asString(Map<String, Object> jsonData) {
        String jsonAsStr = null ;
        try {
            jsonAsStr = MAPPER.writeValueAsString(jsonData);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonAsStr;
    }
    
    public static Map<String, Object> asMap(Object object) {
        return MAPPER.convertValue(object, Map.class);
    }
}
