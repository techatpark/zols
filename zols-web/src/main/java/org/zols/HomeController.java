package org.zols;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * index.
     * @return forward:/index.html
     */
    @GetMapping
    public String index() {
        return "forward:/index.html";
    }

}
