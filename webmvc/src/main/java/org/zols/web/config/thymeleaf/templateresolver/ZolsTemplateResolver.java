/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.web.config.thymeleaf.templateresolver;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 *
 * @author sathish_ku
 */
public class ZolsTemplateResolver extends TemplateResolver {

    private final List<TemplateResolver> templateResolvers;

    public ZolsTemplateResolver() {
        templateResolvers = new ArrayList<TemplateResolver>();
    }

    public void addTemplateResolver(TemplateResolver templateResolver) {
        templateResolver.initialize();
        templateResolvers.add(templateResolver);
    }

    @Override
    public TemplateResolution resolveTemplate(TemplateProcessingParameters templateProcessingParameters) {
        TemplateResolution templateResolution = null ;
        for (TemplateResolver templateResolver : templateResolvers) {
            if(templateResolution == null) {
                templateResolution = templateResolver.resolveTemplate(templateProcessingParameters);
            }            
        }
        return templateResolution; //To change body of generated methods, choose Tools | Templates.
    }
}
