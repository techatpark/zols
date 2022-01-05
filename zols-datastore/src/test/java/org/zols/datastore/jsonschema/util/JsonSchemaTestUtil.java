/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zols.datastore.DataStore;
import org.zols.datastore.DataStoreException;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.everit.EveritJsonSchema;
import static org.zols.jsonschema.util.JsonUtil.asMap;

/**
 *
 * @author WZ07
 */
public class JsonSchemaTestUtil {
    
    public static JsonSchema getJsonSchema(String schamaName) {
        return new EveritJsonSchema(schamaName, JsonSchemaTestUtil::getJsonSchemaAsMap);
    }

    public static Map<String, Object> getJsonSchemaAsMap(String dataName) {
        return asMap(getJsonSchemaAsText(dataName));
    }

    public static String getJsonSchemaAsText(String schamaName) {
        String schemaContent = null;
        try {
            schemaContent = getFile("json-schema/src/test/resources/schema/" + schamaName + ".json");
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static String getJsonDataAsText(String dataName) {
        String schemaContent = null;
        try {

            schemaContent = getFile("json-schema/src/test/resources/jsondata/" + dataName + ".json");
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static Map<String, Object> sampleJson(String dataName) {
        return asMap(getJsonDataAsText(dataName));
    }

    private static String getFile(String fileName) throws IOException {

        StringBuilder result = new StringBuilder();

        File rootFolder = new File(System.getProperty("user.dir"));
            
            while(!rootFolder.getName().equals("zols")) {
                rootFolder = rootFolder.getParentFile();
            }
        
        File file = new File(rootFolder.getAbsolutePath() + File.separator + fileName);
        InputStream fileStream = new FileInputStream(file);

        try (Scanner scanner = new Scanner(fileStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }

        return result.toString();

    }
    
    public static void createAllSchema(DataStore dataStore) throws DataStoreException {
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("geo"));
        
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("seller"));
        
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("product"));
        
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("device"));
        
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("computer"));
        
        dataStore.getSchemaManager().create(getJsonSchemaAsMap("mobile"));
    }
    
    public static void deleteAllData(DataStore dataStore) throws DataStoreException {
        dataStore.delete("geo");
        dataStore.delete("seller");
        dataStore.delete("computer");
        dataStore.delete("mobile");
        dataStore.delete("device");
        dataStore.delete("product");
        
    }
    
    public static void deleteAllSchema(DataStore dataStore) throws DataStoreException {
        
        dataStore.getSchemaManager().delete("geo");
        
        
        dataStore.getSchemaManager().delete("seller");
        
        
        dataStore.getSchemaManager().delete("computer");
        
        
        dataStore.getSchemaManager().delete("mobile");
        
        
        dataStore.getSchemaManager().delete("device");
        
        
        dataStore.getSchemaManager().delete("product");
        
    }
    
    
    public static void createAllData(DataStore dataStore) throws DataStoreException {
        dataStore.create("computer",sampleJson("computer"));
        dataStore.create("mobile",sampleJson("mobile"));
    }
}
