/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.zols.datastore.DataStore;
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.everit.EveritJsonSchema;

/**
 *
 * @author sathish
 */
public class JsonSchemaUtil {
    
    public static JsonSchema getJsonSchema(Class clazz) {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            m.acceptJsonFormatVisitor(m.constructType(clazz), visitor);
        } catch (JsonMappingException ex) {
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<String, Object> jsonSchemaAsMap = asMap(visitor.finalSchema());

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
        
        if(!ids.isEmpty()) {
            jsonSchemaAsMap.put("ids", ids);
        }
        return new EveritJsonSchema(jsonSchemaAsMap,null);
    }
}
