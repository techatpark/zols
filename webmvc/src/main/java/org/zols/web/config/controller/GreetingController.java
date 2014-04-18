package org.zols.web.config.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.zols.web.domain.Greeting;
import org.zols.web.domain.HelloMessage;

@Controller
public class GreetingController {

    @MessageMapping("/helloGreetings")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(3000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }

    @RequestMapping(value = "/hello", method = GET)
    @ApiIgnore
    public String controlPanel() {
        return "hello";
    }
}
