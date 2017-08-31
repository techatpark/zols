/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

/**
 *
 * @author sathish
 */
public class JsonSchemaTest {

    @Test
    public void testSomeMethod() {
        try (InputStream inputStream = getClass().getResourceAsStream("/schema/person.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema,new TestSchemaClient());
            JSONObject rawData = new JSONObject(new JSONTokener(getClass().getResourceAsStream("/jsondata/person.json")));
            schema.validate(rawData); // throws a ValidationException if this object is invalid
        } catch (IOException ex) {
            Logger.getLogger(JsonSchema.class.getName()).log(Level.SEVERE, null, ex);
        } catch(ValidationException exception) {
            System.out.println(exception);
        }
    }

}
