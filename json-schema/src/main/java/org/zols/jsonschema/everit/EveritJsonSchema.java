/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.everit;

import java.io.ByteArrayInputStream;
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
public class EveritJsonSchema extends JsonSchema{
    
    private final Schema schema;

    public EveritJsonSchema(String schemaId, Function<String, Map<String, Object>> jsonSchemaSupplier) {
        super(schemaId,jsonSchemaSupplier);
        schema = SchemaLoader.load(new JSONObject(schemaMap), (String schemaId1) -> new ByteArrayInputStream(new JSONObject(jsonSchemaSupplier.apply(schemaId1)).toString().getBytes()));
    }

    @Override
    public void validate(Map<String, Object> jsonData) {
        schema.validate(new JSONObject(jsonData));
    }

    
}
