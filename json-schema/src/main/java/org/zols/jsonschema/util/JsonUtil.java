/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Json util.
 *
 * @author WZ07
 */
public class JsonUtil {

    /**
     * Initialize ObjectMapper.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Clone map map.
     *
     * @param map the map
     * @return the map
     */
    public static Map<String, Object> cloneMap(final Map<String, Object> map) {
        return asMap(asString(map));
    }

    /**
     * As map map.
     *
     * @param inputStream the input stream
     * @return the map
     */
    public static Map<String, Object> asMap(final InputStream inputStream) {
        try {
            return MAPPER.readValue(inputStream,
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * As map map.
     *
     * @param jsonData the json data
     * @return the map
     */
    public static Map<String, Object> asMap(final String jsonData) {
        try {
            return MAPPER.readValue(jsonData,
                    new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * As string string.
     *
     * @param jsonData the json data
     * @return the string
     */
    public static String asString(final Object jsonData) {
        String jsonAsStr = null;
        try {
            jsonAsStr = MAPPER.writeValueAsString(jsonData);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return jsonAsStr;
    }

    /**
     * As map map.
     *
     * @param object the object
     * @return the map
     */
    public static Map<String, Object> asMap(final Object object) {
        return MAPPER.convertValue(object, Map.class);
    }

    /**
     * As object t.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param map   the map
     * @return the t
     */
    public static <T> T asObject(final Class<T> clazz, final Map<String, Object> map) {
        return MAPPER.convertValue(map, clazz);
    }

    /**
     * As list list.
     *
     * @param jsonArrayData the json array data
     * @return the list
     */
    public static List<Object> asList(final String jsonArrayData) {
        try {
            return MAPPER.readValue(jsonArrayData,
                    new TypeReference<List<Object>>() {
                    });
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
