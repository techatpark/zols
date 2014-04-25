/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.interceptor;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.zols.linkmanager.LinkManager;

/**
 *
 * @author sathish_ku
 */
public class PagePopulationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LinkManager linkManager;

    private String appVersion;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            if(appVersion == null) {
                appVersion = getVersion(request);
            }
            modelAndView.addObject("version", appVersion);
            modelAndView.addObject("links", linkManager.getApplicationLinks());
            modelAndView.addObject("viewName", modelAndView.getViewName());
            modelAndView.setViewName("index");
        }
    }

    private String getVersion(HttpServletRequest request) {
        String version = null;
        Set<String> libPath = request.getServletContext().getResourcePaths("/WEB-INF/lib");
        for (String libFile : libPath) {
            if (libFile.contains("webmvc")) {
                version = libFile.replace("/WEB-INF/lib/webmvc-", "").replaceAll(".jar", "");
            }
        }
        return version;
    }
}
