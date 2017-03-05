/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sathish
 */
public class MapUtil {

    public static void removeNestedElements(Map<String, Object> sourceMap, String... elements) {
        if (elements != null && sourceMap != null) {
            List<String> elemenstList = Arrays.asList(elements);
            List<String> elemenstToBeRemoved = new ArrayList<>();
            sourceMap.keySet().forEach(fieldName -> {
                if (elemenstList.contains(fieldName)) {
                    elemenstToBeRemoved.add(fieldName);
                } else if (sourceMap.get(fieldName) instanceof Map) {
                    removeNestedElements((Map<String, Object>) sourceMap.get(fieldName), elements);
                } else if (sourceMap.get(fieldName) instanceof Collection) {
                    ((Collection) sourceMap.get(fieldName)).forEach(collectionData -> {
                        if (collectionData instanceof Map) {
                            removeNestedElements((Map<String, Object>) collectionData, elements);
                        }
                    });
                }
            });
            elemenstToBeRemoved.forEach(element -> sourceMap.remove(element));
        }

    }
}
