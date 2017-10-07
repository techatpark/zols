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
import static org.zols.datastore.util.JsonUtil.asMap;
import org.zols.datatore.exception.DataStoreException;

/**
 *
 * @author WZ07
 */
public class JsonSchemaTestUtil {

    public static Map<String, Object> getJsonSchemaAsMap(String dataName) {
        return asMap(getJsonSchemaAsText(dataName));
    }

    public static String getJsonSchemaAsText(String schamaName) {
        String schemaContent = null;
        try {

            schemaContent = getFile("../json-schema/src/test/resources/schema/" + schamaName + ".json");
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static String getJsonDataAsText(String dataName) {
        String schemaContent = null;
        try {
            schemaContent = getFile("../json-schema/src/test/resources/jsondata/" + dataName + ".json");
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

        
        
        File file = new File(fileName);
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
        dataStore.createSchema(getJsonSchemaAsText("geo"));
        
        dataStore.createSchema(getJsonSchemaAsText("seller"));
        
        dataStore.createSchema(getJsonSchemaAsText("product"));
        
        dataStore.createSchema(getJsonSchemaAsText("device"));
        
        dataStore.createSchema(getJsonSchemaAsText("computer"));
        
        dataStore.createSchema(getJsonSchemaAsText("mobile"));
    }
    
    public static void deleteAllSchema(DataStore dataStore) throws DataStoreException {
        dataStore.delete("geo");
        dataStore.deleteSchema(getJsonSchemaAsText("geo"));
        
        dataStore.delete("seller");
        dataStore.deleteSchema(getJsonSchemaAsText("seller"));
        
        dataStore.delete("product");
        dataStore.deleteSchema(getJsonSchemaAsText("product"));
        
        
        dataStore.delete("device");
        dataStore.deleteSchema(getJsonSchemaAsText("device"));
        
        dataStore.delete("computer");
        dataStore.deleteSchema(getJsonSchemaAsText("computer"));
        
        dataStore.delete("mobile");
        dataStore.deleteSchema(getJsonSchemaAsText("mobile"));
    }
    
    
    public static void createAllData(DataStore dataStore) throws DataStoreException {
        dataStore.create("computer",sampleJson("computer"));
        dataStore.create("mobile",sampleJson("mobile"));
    }
}
