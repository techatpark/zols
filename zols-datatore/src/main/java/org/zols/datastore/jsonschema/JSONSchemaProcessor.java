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
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author wz07
 */
public class JSONSchemaProcessor {

    static JSONSchema jsonSchema(String jsonSchema) {
        return new JSONSchema(jsonSchema);
    }

    static class JSONSchema {

        private final String jsonSchemaAsTxt;

        private JSONSchema(String jsonSchema) {
            this.jsonSchemaAsTxt = jsonSchema;
        }

        private static ScriptEngine _scriptEngine;
        private static Object JSON ;

        private ScriptEngine scriptEngine() throws ScriptException, IOException, URISyntaxException {
            if (_scriptEngine == null) {
                _scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
                _scriptEngine.eval(new String(Files.readAllBytes(Paths
                        .get(ClassLoader.getSystemResource("org/zols/datastore/jsonschema/tv4.js").toURI()))));
                JSON = _scriptEngine.eval("JSON");
            }
            return _scriptEngine;
        }

        public boolean validate(String jsonData) {
            try {
                ScriptEngine scriptEngine = scriptEngine();
                Object TV4 = scriptEngine.eval("tv4");
                Invocable inv = (Invocable) scriptEngine;
                Object schemaToJavaScript
                        = inv.invokeMethod(JSON, "parse", jsonSchemaAsTxt);
                Object dataToJavaScript
                        = inv.invokeMethod(JSON, "parse", jsonData);
                Object validation = inv.invokeMethod(TV4,"validate", dataToJavaScript, schemaToJavaScript);
                return Boolean.valueOf(inv.invokeMethod(JSON, "stringify", validation).toString());
            } catch (ScriptException | IOException | URISyntaxException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            return false;
        }

    }
}
