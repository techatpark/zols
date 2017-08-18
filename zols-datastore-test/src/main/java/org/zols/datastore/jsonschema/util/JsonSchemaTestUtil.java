/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.ClassLoader.getSystemResource;
import java.net.URISyntaxException;
import static java.nio.file.Files.readAllBytes;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.zols.datastore.util.JsonUtil.asMap;

/**
 *
 * @author WZ07
 */
public class JsonSchemaTestUtil {

    public static Map<String, Object> sampleJsonSchema(String dataName) {
        return asMap(sampleJsonSchemaText(dataName));
    }

    public static String sampleJsonSchemaText(String schamaName) {
        String schemaContent = null;
        try {
            schemaContent = getFile("org/zols/datastore/jsonschema/schema/" + schamaName + ".json");
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static String sampleJsonText(String dataName) {
        String schemaContent = null;
        try {
            schemaContent = getFile("org/zols/datastore/jsonschema/jsondata/" + dataName + ".json");
        } catch (IOException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static Map<String, Object> sampleJson(String dataName) {
        return asMap(sampleJsonText(dataName));
    }

    private static String getFile(String fileName) throws IOException {

        StringBuilder result = new StringBuilder();

        //Get file from resources folder
        ClassLoader classLoader = JsonSchemaTestUtil.class.getClassLoader();
        InputStream fileStream = classLoader.getResourceAsStream(fileName);

        try (Scanner scanner = new Scanner(fileStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }

        return result.toString();

    }
}
