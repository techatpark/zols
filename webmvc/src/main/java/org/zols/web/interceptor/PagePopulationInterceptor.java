/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.interceptor;

import org.zols.linkmanager.LinkManager;
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {            
            modelAndView.addObject("links", linkManager.getApplicationLinks());
            modelAndView.addObject("viewName",modelAndView.getViewName());
            modelAndView.setViewName("index");
        }
    }
}
