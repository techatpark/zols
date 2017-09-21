/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.zols.jsonschema.JsonSchemaTest;

/**
 *
 * @author sathish
 */
public class TestUtil {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final Map<String, Map<String, Object>> schemasMap = new HashMap<>();

    public static Map<String, Object> getTestSchema(String schemaPath) {
        Map<String, Object> schemaMap = schemasMap.get(schemaPath);
        if (schemaMap == null && schemaPath != null) {
            InputStream inputStream = TestUtil.class.getResourceAsStream("/schema/" + schemaPath + ".json");
            // Not Available in file system. Internet ?
            if (inputStream == null) {

                try {
                    URL obj = new URL(schemaPath);
                    HttpURLConnection con;

                    con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    int responseCode = con.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = con.getInputStream();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TestUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
                schemaMap = new JSONObject(buffer.lines().collect(Collectors.joining("\n"))).toMap();
                
                schemasMap.put(schemaPath, schemaMap);

            } catch (IOException ex) {
                Logger.getLogger(JsonSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return schemaMap;
    }

    public static Map<String, Object> getTestData(String pathOfData) {
        InputStream inputStream = TestUtil.class.getResourceAsStream("/jsondata/" + pathOfData + ".json");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            return new JSONObject(buffer.lines().collect(Collectors.joining("\n"))).toMap();
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
