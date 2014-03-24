/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.languagemanager;

import org.zols.datastore.DataStore;
import org.zols.languagemanager.domain.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Navin.
 */
@Service
public class LanguageManager {

    @Autowired
    private DataStore dataStore;

    public Language add(Language language) {
        return dataStore.create(language, Language.class);
    }

    public void update(Language language) {
        dataStore.update(language, Language.class);
    }

    public void delete(String languageName) {
        dataStore.delete(languageName, Language.class);
    }

    public Language get(String languageName) {
        return dataStore.read(languageName, Language.class);
    }

    public Page<Language> list(Pageable page) {
        return dataStore.list(page, Language.class);
    }

}
