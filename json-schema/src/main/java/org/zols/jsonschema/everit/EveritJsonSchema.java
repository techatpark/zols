/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.everit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toSet;
import javax.validation.ConstraintViolation;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.violations.JsonSchemaConstraintViolation;

/**
 *
 * @author sathish
 */
public class EveritJsonSchema extends JsonSchema {

    private final Schema schema;

    //To Cache Shemastreams
    private final Map<String, InputStream> schemaStreams;
    
    public EveritJsonSchema(Map<String, Object> schemaMap, Function<String, Map<String, Object>> schemaSupplier) {
        super(schemaMap,schemaSupplier);
        schemaStreams = new HashMap<>();
        schema = SchemaLoader.load(new JSONObject(schemaMap), this::getSchemaInputStream);
    }

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
    public Set<ConstraintViolation> validate(Map<String, Object> jsonData) {
        try {
            schema.validate(new JSONObject(jsonData));
        } catch (ValidationException ve) {
            if (ve.getCausingExceptions().isEmpty()) {
                Set<ConstraintViolation> constraintViolations = new HashSet<>();
                constraintViolations.add(getConstraintViolation(ve));
                return constraintViolations;
            } else {
                return ve.getCausingExceptions().stream().map(this::getConstraintViolation).collect(toSet());
            }

        }

        return new HashSet<>();
    }

    private JsonSchemaConstraintViolation getConstraintViolation(ValidationException ve) {
        return null;

    }

    @Override
    protected String asString() {
        return schema.toString();
    }

}
