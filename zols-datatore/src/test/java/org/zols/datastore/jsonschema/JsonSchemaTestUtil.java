/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author WZ07
 */
public class JsonSchemaTestUtil {

    public static String sampleJsonSchema(String schamaName) {
        String schemaContent = null;
        try {
            schemaContent = new String(Files.readAllBytes(Paths
                    .get(ClassLoader.getSystemResource("org/zols/datastore/jsonschema/schema/" + schamaName + ".json").toURI())));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }
    
    public static String sampleJsonData(String dataName) {
        String schemaContent = null;
        try {
            schemaContent = new String(Files.readAllBytes(Paths
                    .get(ClassLoader.getSystemResource("org/zols/datastore/jsonschema/jsondata/" + dataName + ".json").toURI())));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }
}
