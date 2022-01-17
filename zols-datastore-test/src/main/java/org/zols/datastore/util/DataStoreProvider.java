/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.util;

import org.reflections.Reflections;
import org.zols.datastore.DataStore;
import org.zols.datastore.persistence.BrowsableDataStorePersistence;
import org.zols.datastore.persistence.DataStorePersistence;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sathish
 */
public interface DataStoreProvider {
    static DataStore getDataStore() {
        Reflections reflections = new Reflections("org.zols.datastore");

        Set<Class<? extends DataStorePersistence>> subTypes =
                reflections.getSubTypesOf(DataStorePersistence.class);
        subTypes.remove(BrowsableDataStorePersistence.class);
        if (subTypes.size() == 1) {
            try {
                return new DataStore(subTypes.iterator().next().newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(DataStoreProvider.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    DataStore buildDataStore();
}
