package org.sample.app;

import com.mangofactory.swagger.plugin.EnableSwagger;
import java.util.Date;
import java.util.Map;
import static org.springframework.boot.SpringApplication.run;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zols.plugin.EnableCMS;

@ComponentScan
@EnableAutoConfiguration
@EnableCMS
@EnableSwagger
public class Application {
    
    @Controller
    protected static class HomeController {

        @RequestMapping("/")
        public String home(Map<String, Object> model) {
            model.put("message", "Hello World");
            model.put("title", "Hello Home");
            model.put("date", new Date());
            return "index";
        }

    }
    
    public static void main(String[] args) {
        run(Application.class, args);
    }
}
