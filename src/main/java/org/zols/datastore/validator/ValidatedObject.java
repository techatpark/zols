/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.datastore.validator;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.Map;

/**
 *
 * @author sathish_ku
 */
public class ValidatedObject {
    
    private final JsonSchema jsonSchema ;
    private final Map<String, Object> dataObject;

    public ValidatedObject(JsonSchema jsonSchema, Map<String, Object> dataObject) {
        this.jsonSchema = jsonSchema;
        this.dataObject = dataObject;
    }

    public JsonSchema getJsonSchema() {
        return jsonSchema;
    }

    public Map<String, Object> getDataObject() {
        return dataObject;
    }    
    
}
