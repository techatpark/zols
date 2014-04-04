/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.setting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zols.datastore.DataStore;
import org.zols.setting.domain.Setting;

@Service
public class SettingManager {

    @Autowired
    private DataStore dataStore;

    public Setting add(Setting setting) {
        return dataStore.create(setting, Setting.class);
    }

    public void update(Setting setting) {
        dataStore.update(setting, Setting.class);
    }

    public void delete(String settingName) {
        dataStore.delete(settingName, Setting.class);
    }

    public Setting get(String settingName) {
        return dataStore.read(settingName, Setting.class);
    }

    public Page<Setting> list(Pageable page) {
        return dataStore.list(page, Setting.class);
    }

}
