/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoreController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/controlpanel")
    @Secured("ROLE_ADMIN")
    public String controlpanel() {
        return "controlpanel";
    }

    @RequestMapping("/links")
    public String links() {
        return "links";
    }

    @RequestMapping("/datastore")
    public String datastore() {
        return "datastore";
    }

    @RequestMapping("/templates")
    public String templates() {
        return "templates";
    }

    @RequestMapping("/pages/{name}")
    public String pages(@PathVariable(value = "name") String name) {
        return name;
    }
    
    @RequestMapping("/create_page/{linkName}")
    public String createPage(@PathVariable(value = "linkName") String linkName) {
        return "create_page";
    }

}
