/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema;

import java.io.InputStream;
import org.everit.json.schema.loader.SchemaClient;

/**
 *
 * This loads the schema from resources folder
 */
public class TestSchemaClient implements SchemaClient{

    @Override
    public InputStream get(String url) {
        return getClass().getResourceAsStream("/schema/"+url+".json");
    }
    
}
