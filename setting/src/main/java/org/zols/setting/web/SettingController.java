/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.setting.web;

import com.mangofactory.swagger.annotations.ApiIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zols.setting.SettingManager;
import org.zols.setting.domain.Setting;

@Controller
public class SettingController {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SettingController.class);

    @Autowired
    SettingManager settingsManager;

    @RequestMapping(value = "/api/settings", method = POST)
    @ApiIgnore
    @ResponseBody
    public Setting create(@RequestBody Setting setting) {
        LOGGER.info("Creating new setting {}", setting);
        return settingsManager.add(setting);
    }

    @RequestMapping(value = "/api/settings/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Setting setting) {
        LOGGER.info("Updating setting with id {} with {}", name, setting);
        if (name.equals(setting.getName())) {
            settingsManager.update(setting);
        }
    }

    @RequestMapping(value = "/api/settings/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting setting with id {}", name);
        settingsManager.delete(name);
    }

    @RequestMapping(value = "/settings/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("setting", settingsManager.get(name));
        return "org/zols/settingmanager/setting";
    }

    @RequestMapping(value = "/settings/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("setting", new Setting());
        return "org/zols/settingmanager/setting";
    }

    @RequestMapping(value = "/api/settings", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<Setting> list(
            Pageable page) {
        LOGGER.info("Listing settings");
        return settingsManager.list(page);
    }

    @RequestMapping(value = "/settings", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/settingmanager/listsettings";
    }
}
