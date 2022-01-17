/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import org.zols.jsonschema.util.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author sathish
 */
public class MapUtil {

    public static Object getFieldValue(final Map<String, Object> o,
                                                 final String s) {
        if (s == null || o == null) {
            return null;
        }
        Object value = o.get(s);
        if (value == null && s.contains(".")) {
            String field = s.substring(0, s.indexOf("."));
            Object firstValue = o.get(field);
            if (firstValue == null) {
                return null;
            } else if (firstValue instanceof Map) {
                return getFieldValue((Map<String, Object>) firstValue,
                        s.substring(s.indexOf(".") + 1));
            }

        }
        return value;
    }

    public static void deepRemove(final Map<String, Object> sourceMap,
                                  final String... elements) {
        if (elements != null && sourceMap != null) {
            List<String> elemenstList = Arrays.asList(elements);
            List<String> elemenstToBeRemoved = new ArrayList<>();
            sourceMap.keySet().forEach(fieldName -> {
                if (elemenstList.contains(fieldName)) {
                    elemenstToBeRemoved.add(fieldName);
                } else if (sourceMap.get(fieldName) instanceof Map) {
                    deepRemove((Map<String, Object>) sourceMap.get(fieldName),
                            elements);
                } else if (sourceMap.get(fieldName) instanceof Collection) {
                    ((Collection) sourceMap.get(fieldName)).forEach(
                            collectionData -> {
                                if (collectionData instanceof Map) {
                                    deepRemove(
                                            (Map<String, Object>) collectionData,
                                            elements);
                                }
                            });
                }
            });
            elemenstToBeRemoved.forEach(sourceMap::remove);
        }

    }

    public static <T> T asObject(final Class<T> clazz,
                                 final Map<String, Object> dataAsMap) {
        if (dataAsMap == null) {
            return null;
        }
        dataAsMap.remove("$type");
        return JsonUtil.asObject(clazz, dataAsMap);
    }
}
