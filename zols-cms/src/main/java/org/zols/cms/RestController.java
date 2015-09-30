/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.cms;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private Client client;

    @RequestMapping("/elastic")
    public String elastisTest() {
        return client.admin().cluster().prepareClusterStats().execute().actionGet().toString();
    }

    @RequestMapping("/greeting")
    public Map greeting() {
        return System.getenv();
    }
}
