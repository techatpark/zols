package org.zols.datastore.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sathish
 */
public class LocalitationUtil {

    public static Map<String, Object> prepareJSON(Map<String, Object> jsonSchema,
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();
        Map<String, Object> propertiesMap = (Map<String, Object>) jsonSchema.get("properties");
        propertiesMap.keySet().forEach(fieldName -> {
            Boolean isLocalized = (Boolean) ((Map<String, Object>) propertiesMap.get(fieldName)).get("localized");
            if (isLocalized != null && isLocalized) {
                Object obj = jsonData.remove(fieldName);
                jsonData.put(fieldName + "$" + localeCode, obj);

            }
        });
        return jsonData;
    }

    public static Map<String, Object> readJSON(
            Map<String, Object> jsonData,
            Locale locale) {
        String localeCode = locale.getLanguage();

        List<String> localeFileds = new ArrayList();
        jsonData.keySet().forEach(fieldName -> {
            if (fieldName.endsWith("$" + localeCode)) {
                localeFileds.add(fieldName);
            } else {
                if(jsonData.get(fieldName) instanceof Map) {
                    readJSON((Map<String, Object>) jsonData.get(fieldName),locale);
                }
            }

        });
        localeFileds.forEach(fieldName -> {

            Object obj = jsonData.remove(fieldName);
            jsonData.put(fieldName.substring(0, fieldName.indexOf("$")), obj);

        });
        return jsonData;
    }

}
