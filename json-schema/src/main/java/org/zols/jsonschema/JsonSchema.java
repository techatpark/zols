/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author sathish
 */
public class JsonSchema {
    
    private final Schema schema;
    
    private final Map<String, Object> schemaMap;
    
    public JsonSchema(InputStream inputStream, Function<String, String> jsonSchemaSupplier) {
        
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        schemaMap = rawSchema.toMap();
        schema = SchemaLoader.load(rawSchema, (String schameRef) -> new ByteArrayInputStream(jsonSchemaSupplier.apply(schameRef).getBytes(StandardCharsets.UTF_8)));
    }
    
    public void validate(Map<String, Object> jsonData) {
        schema.validate(new JSONObject(jsonData));
    }
    
    public Map<String, Object> getLocalizedData(Map<String, Object> jsonData, Locale locale) {
        
        List<String> localizedKeys = (List<String>) schemaMap.get("localized");
        
        if (localizedKeys != null && !localizedKeys.isEmpty()) {
            
            localizedKeys.forEach(localizedKey -> {
                String givenLocaleKey = localizedKey + "_" + locale.getLanguage();
                
                if (jsonData.containsKey(givenLocaleKey)) {
                    jsonData.replace(localizedKey, jsonData.remove(givenLocaleKey));
                   
                }
            });
            
            
            
            
        }
        return jsonData;
    }
    
}
