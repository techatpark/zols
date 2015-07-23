/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.jsonschema.util;

import java.io.IOException;
import static java.lang.ClassLoader.getSystemResource;
import java.net.URISyntaxException;
import static java.nio.file.Files.readAllBytes;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zols.datastore.util.JsonUtil;

/**
 *
 * @author WZ07
 */
public class JsonSchemaTestUtil {

    public static String sampleJsonSchema(String schamaName) {
        String schemaContent = null;
        try {
            schemaContent = new String(readAllBytes(Paths
                    .get(getSystemResource("org/zols/datastore/jsonschema/schema/" + schamaName + ".json").toURI())));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static String sampleJsonText(String dataName) {
        String schemaContent = null;
        try {
            schemaContent = new String(readAllBytes(Paths
                    .get(getSystemResource("org/zols/datastore/jsonschema/jsondata/" + dataName + ".json").toURI())));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(JsonSchemaTestUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemaContent;
    }

    public static Map<String, Object> sampleJson(String dataName) {
        return JsonUtil.asMap(sampleJsonText(dataName));
    }
}
