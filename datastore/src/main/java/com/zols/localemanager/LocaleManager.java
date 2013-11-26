/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zols.localemanager;

import com.zols.datastore.DataStore;
import com.zols.localemanager.domain.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Navin
 */
@Service
public class LocaleManager {

    @Autowired
    private DataStore dataStore;

    public Locale add(Locale locale) {
        return dataStore.create(locale, Locale.class);
    }

    public void update(Locale locale) {
        dataStore.update(locale, Locale.class);
    }

    public void deleteLocale(String localeName) {
        dataStore.delete(localeName, Locale.class);
    }

    public Locale getLocale(String localeName) {
        return dataStore.read(localeName, Locale.class);
    }

    public Page<Locale> localeList(Pageable page) {
        return dataStore.list(page, Locale.class);
    }

}
