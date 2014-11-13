/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoreController {

    @RequestMapping("/")
    public String index() {        
        return "index";
    }
    
    @RequestMapping("/login")
    public String login() {        
        return "login";
    }
    
    @RequestMapping("/controlpanel")
    public String controlpanel() {        
        return "controlpanel";
    }
    
}
