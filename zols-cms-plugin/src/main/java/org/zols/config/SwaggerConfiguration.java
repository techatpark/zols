/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.config;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableSwagger
@Profile(value = "Dev")
public class SwaggerConfiguration {
    
}
