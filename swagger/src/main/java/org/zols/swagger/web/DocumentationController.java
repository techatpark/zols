/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.swagger.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DocumentationController {

    @RequestMapping(value = "/api", method = GET)
    @ApiIgnore
    public String business() {
        return "org/zols/swagger/relative";
    }

    @RequestMapping(value = "/businessapi", method = GET)
    @ApiIgnore
    public String relative() {
        return "org/zols/swagger/business";
    }
}
