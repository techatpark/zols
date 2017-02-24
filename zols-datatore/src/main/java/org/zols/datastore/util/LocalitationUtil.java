package org.zols.datastore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author sathish
 */
public class LocalitationUtil {

    public static Map<String, Object> prepareJSON(Map<String, Object> jsonSchema,
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();
        Map<String, Object> propertiesMap = new HashMap<>();
        preparePropertiedMap(jsonSchema, jsonSchema, propertiesMap);
        propertiesMap.keySet().forEach(fieldName -> {
            Map<String, Object> propertyMap = ((Map<String, Object>) propertiesMap.get(fieldName));
            Object reference = propertiesMap.get("$ref");
            if (reference == null) {
                Boolean isLocalized = (Boolean) propertyMap.get("localized");
                if (isLocalized != null && isLocalized) {
                    Object obj = jsonData.remove(fieldName);
                    jsonData.put(fieldName + "$" + localeCode, obj);
                }
            } else {
                
            }

        });
        return jsonData;
    }

    /**
     * Load Properties from Super Types (Car is Base Type, Vehicle is super
     * type)
     *
     * @param jsonSchema
     * @param currentSchema
     * @param propertiesMap
     */
    private static void preparePropertiedMap(Map<String, Object> jsonSchema, Map<String, Object> currentSchema, Map<String, Object> propertiesMap) {
        if (jsonSchema != null) {
            propertiesMap.putAll((Map<String, Object>) currentSchema.get("properties"));
            Object reference = currentSchema.get("$ref");
            if (reference != null) {
                preparePropertiedMap(jsonSchema, getReferencedSchema(jsonSchema,reference), propertiesMap);
            }
        }

    }

    private static Map<String, Object> getReferencedSchema(Map<String, Object> jsonSchema, Object refValue) {
        Map<String, Object> refSchema = jsonSchema;
        String[] paths = refValue.toString().split("/");
        for (int i = 1; i < paths.length; i++) {
            refSchema = (Map<String, Object>) refSchema.get(paths[i]);
        }
        return refSchema;
    }

    public static Map<String, Object> readJSON(
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();

        List<String> localeFileds = new ArrayList();
        jsonData.keySet().forEach(fieldName -> {
            if (fieldName.endsWith("$" + localeCode)) {
                localeFileds.add(fieldName);
            } else if (jsonData.get(fieldName) instanceof Map) {
                readJSON((Map<String, Object>) jsonData.get(fieldName), locale);
            } else if (jsonData.get(fieldName) instanceof Collection) {
                ((Collection) jsonData.get(fieldName)).forEach(collectionData -> {
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
