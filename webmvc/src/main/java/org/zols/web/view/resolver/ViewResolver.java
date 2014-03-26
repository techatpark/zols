/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.view.resolver;

import java.util.Locale;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 *
 * @author sathish_ku
 */
public class ViewResolver extends ThymeleafViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {    
        View view = super.resolveViewName(viewName, locale) ;
//        boolean b = isValidTemplate(viewName, locale);
//        b = b;
        return view; //To change body of generated methods, choose Tools | Templates.
    }

//    private boolean isValidTemplate(String viewName, Locale locale) {
//        boolean isValidTemplate = true ; 
//        Context context = new Context(locale);
//        TemplateProcessingParameters processingParameters
//                = new TemplateProcessingParameters(this.getTemplateEngine().getConfiguration(), viewName, context);
//        try {            
//            Template template = this.getTemplateEngine().getTemplateStorage().getTemplate(processingParameters);
//        } catch (Exception e) {            
//            isValidTemplate = false ;
//        }
//        
//        return isValidTemplate;
//    }

}
