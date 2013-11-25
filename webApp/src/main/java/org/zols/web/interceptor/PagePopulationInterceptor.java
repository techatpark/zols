/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.interceptor;

import com.zols.linkmanager.LinkManager;
import com.zols.linkmanager.domain.Link;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author sathish_ku
 */
public class PagePopulationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LinkManager linkManager;

    private Map<String, List<Link>> links;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {            
            modelAndView.addObject("title", "I am the title");
            modelAndView.addObject("viewName",modelAndView.getViewName());
            modelAndView.setViewName("home");
        }
    }
}
