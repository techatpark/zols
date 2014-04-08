/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.feedback.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zols.feedback.FeedbackManager;
import org.zols.feedback.domain.FeedBack;

@Controller
public class FeedBackController {
    
    @Autowired
    private FeedbackManager feedbackManager ;
    
    @RequestMapping(value = "/api/feedback", method = POST)
    @ApiIgnore
    @ResponseBody
    public FeedBack create(@RequestBody FeedBack feedback) throws IOException {        
        return feedbackManager.add(feedback);
    }
    
    @RequestMapping(value = "/feedback", method = GET)
    @ApiIgnore
    public String listing( Model model) throws IOException {
        model.addAttribute("feedbacks", feedbackManager.getFeedBacks());
        return "org/zols/feedback/listfeedbacks";
    }
    
    @RequestMapping(value = "/feedback/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("feedback", new FeedBack());
        return "org/zols/feedback/feedback";
    }
}
