/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sathish_ku
 */
public class JSONUtil {

    private static final String JSON_DATA_BASE_FOLDER = "/org/jodel/store/";

    private static final String JSON_SCHEMA_BASE_FOLDER = "/org/jodel/store/schema/";

    private final ObjectMapper objectMapper;

    public JSONUtil() {
        objectMapper = new ObjectMapper();
    }

    public String getJsonSchemaAsString(String name) throws IOException {
        return getJsonString(JSON_SCHEMA_BASE_FOLDER, name);
    }

    public Map<String, Object> getJsonStringObject(String name) throws IOException {
        Map<String, Object> map;
        map = objectMapper.readValue(getJsonString(JSON_DATA_BASE_FOLDER, name),
                new TypeReference<HashMap<String, String>>() {
                });
        return map;

    }

    private String getJsonString(String baseFolder, String name) throws JsonProcessingException, IOException {
        InputStream stream = this.getClass().getResourceAsStream(baseFolder + name + ".json");
        StringBuilder builder;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            builder = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    public JsonSchema getJsonSchema(String jsonSchemaAsString) throws IOException {
        JsonSchema schema = objectMapper.readValue(jsonSchemaAsString, JsonSchema.class);
        return schema;
    }

    public String getAsJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
