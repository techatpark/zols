package org.zols.datastore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.zols.datastore.jsonschema.JSONSchema;
import static org.zols.datastore.jsonschema.JSONSchema.jsonSchema;

/**
 *
 * @author sathish
 */
public class LocalitationUtil {

    public static Map<String, Object> prepareJSON(Map<String, Object> jsonSchemaAsMap,
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();
        JSONSchema jSONSchema = jsonSchema(jsonSchemaAsMap);
        Map<String, Object> localizedProperties = jSONSchema.getLocalizedProperties();
        localizedProperties.entrySet().stream().forEach((localizedPropertiesEntry) -> {
            jsonData.put(localizedPropertiesEntry.getKey() + "$" + localeCode, jsonData.remove(localizedPropertiesEntry.getKey()));
        });

        Map<String, Object> consolidatedProperties = jSONSchema.getConsolidatedProperties();
        jsonData.entrySet().forEach(property -> {
            String fieldName = property.getKey();
            Object fieldValue = property.getValue();
            if (fieldValue instanceof Map) {
                Map<String, Object> propertySchema = (Map<String, Object>) consolidatedProperties.get(fieldName);
                Object reference = propertySchema.get("$ref");
                if (reference != null) {
                    String schemaName = reference.toString().replaceAll("#/definitions/", "");
                    prepareJSON(jSONSchema.getJsonSchema(schemaName), (Map<String, Object>) fieldValue, locale);
                }
            }

        });

        return jsonData;
    }

    

    public static Map<String, Object> readJSON(
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();

        List<String> localeFileds = new ArrayList();
        jsonData.entrySet().forEach(property -> {
            String fieldName = property.getKey();
            if (fieldName.endsWith("$" + localeCode)) {
                localeFileds.add(fieldName);
            } else if (property.getValue() instanceof Map) {
                readJSON((Map<String, Object>) property.getValue(), locale);
            } else if (property.getValue() instanceof Collection) {
                ((Collection) property.getValue()).forEach(collectionData -> {
                    if (collectionData instanceof Map) {
                        readJSON((Map<String, Object>) collectionData, locale);
                    }
                });
            }

        });
        localeFileds.forEach(fieldName -> {

            Object obj = jsonData.remove(fieldName);
            jsonData.put(fieldName.substring(0, fieldName.indexOf("$")), obj);

        });
        return jsonData;
    }

}
