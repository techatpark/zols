/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

// TODO: Remove once moved to latest Spring Boot
@ConfigurationProperties("spring.thymeleaf")
public class ThymeleafProperties {

    public static final String DEFAULT_PREFIX = "classpath:/templates/";

    public static final String DEFAULT_SUFFIX = ".html";

    private boolean checkTemplateLocation = true;

    private String prefix = DEFAULT_PREFIX;

    private String suffix = DEFAULT_SUFFIX;

    private String mode = "HTML5";

    private String encoding = "UTF-8";

    private String contentType = "text/html";

    private boolean cache = true;

    private String[] viewNames;

    private String[] excludedViewNames;

    public boolean isCheckTemplateLocation() {
        return this.checkTemplateLocation;
    }

    public void setCheckTemplateLocation(boolean checkTemplateLocation) {
        this.checkTemplateLocation = checkTemplateLocation;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }

    public void setExcludedViewNames(String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

}
