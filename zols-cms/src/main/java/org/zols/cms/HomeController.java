package org.zols.cms;

import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zols.datatore.exception.DataStoreException;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() throws IOException, DataStoreException {
        return "index";
    }

}
