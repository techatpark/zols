/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.bind.annotation.Localized;
import org.zols.jsonschema.everit.EveritJsonSchema;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Json schema util.
 *
 * @author sathish
 */
public final class JsonSchemaUtil {

    /**
     * initialises JSONSCHEMA_FOR_SCHEMA.
     */
    private static JsonSchema JSONSCHEMA_FOR_SCHEMA = null;

    /**
     * private constructor.
     */
    private JsonSchemaUtil() {
        throw new UnsupportedOperationException("This is a utility"
                + " class and cannot be instantiated");
    }

    private static String getContentFromClasspath(final String resourcePath) {
        InputStream inputStream =
                JsonSchemaUtil.class.getResourceAsStream(resourcePath);
        String theString;
        try (java.util.Scanner scanner = new java.util.Scanner(inputStream,
                StandardCharsets.UTF_8).useDelimiter("\\A")) {
            theString = scanner.hasNext() ? scanner.next() : "";
        }
        return theString;
    }

    /**
     * Json schema for schema json schema.
     *
     * @return the json schema
     */
    public static JsonSchema jsonSchemaForSchema() {
        if (JSONSCHEMA_FOR_SCHEMA == null) {

            Map<String, Object> schema;
            try {
                schema = new ObjectMapper().readValue(getContentFromClasspath(
                                "/org/zols/jsonschema/schema.json"),
                        new TypeReference<HashMap<String, Object>>() {
                        });
                JSONSCHEMA_FOR_SCHEMA = new EveritJsonSchema(schema, null);
            } catch (IOException ex) {
                Logger.getLogger(JsonSchemaUtil.class.getName())
                        .log(Level.SEVERE, null, ex);
            }

        }

        return JSONSCHEMA_FOR_SCHEMA;
    }

    /**
     * Validate schema set.
     *
     * @param jsonData the json data
     * @return the set
     */
    public static Set<ConstraintViolation> validateSchema(
            final Map<String, Object> jsonData) {
        return jsonSchemaForSchema().validate(jsonData);
    }

    /**
     * Gets json schema.
     *
     * @param clazz the clazz
     * @return the json schema
     */
    public static JsonSchema getJsonSchema(final Class clazz) {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
        } catch (JsonMappingException ex) {
            Logger.getLogger(JsonSchemaUtil.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        Map<String, Object> jsonSchemaAsMap =
                new ObjectMapper().convertValue(visitor.finalSchema(),
                        Map.class);

        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = (Entity) clazz.getAnnotation(Entity.class);
            jsonSchemaAsMap.put("name", entity.name());
        } else {
            jsonSchemaAsMap.put("name", jsonSchemaAsMap.get("id"));
        }

        List<String> ids = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                ids.add(field.getName());
            }
        }
        if (!ids.isEmpty()) {
            jsonSchemaAsMap.put("ids", ids);
        }

        List<String> localized = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Localized.class)) {
                localized.add(field.getName());
            }
        }
        if (!localized.isEmpty()) {
            jsonSchemaAsMap.put("localized", localized);
        }

        jsonSchemaAsMap.put("$id",
                jsonSchemaAsMap.remove("id").toString().replaceAll(":", "")
                        .toLowerCase());

        return new EveritJsonSchema(jsonSchemaAsMap, null);
    }
}
