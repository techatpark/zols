/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.everit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.zols.jsonschema.JsonSchema;

/**
 *
 * @author sathish
 */
public class EveritJsonSchema extends JsonSchema {

    private final Schema schema;

    //To Cache Shemastreams
    private final Map<String, InputStream> schemaStreams;

    public EveritJsonSchema(String schemaId, Function<String, Map<String, Object>> schemaSupplier) {
        super(schemaId, schemaSupplier);
        schemaStreams = new HashMap<>();
        schema = SchemaLoader.load(new JSONObject(schemaMap), this::getSchemaInputStream);
    }

    private InputStream getSchemaInputStream(String schemaId) {
        InputStream inputStream = schemaStreams.get(schemaId);
        if (inputStream == null) {
            inputStream = new ByteArrayInputStream(new JSONObject(schemaSupplier.apply(schemaId)).toString().getBytes());
            schemaStreams.put(schemaId, inputStream);
        }
        return inputStream;
    }

    @Override
    public void validate(Map<String, Object> jsonData) {
        schema.validate(new JSONObject(jsonData));
    }

    @Override
    public String getId() {
        return (String) schemaMap.get("id");
    }

    @Override
    public String getTitle() {
        return (String) schemaMap.get("title");
    }

    @Override
    public String getDescription() {
        return (String) schemaMap.get("description");
    }

}
