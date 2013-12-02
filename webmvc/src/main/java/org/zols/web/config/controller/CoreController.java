/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.config.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/core")
public class CoreController {
    
    @RequestMapping(value = "/templates/{name}", method = GET)
    public String templates(@PathVariable(value = "name") String name) {         
        return name;
    }
}
