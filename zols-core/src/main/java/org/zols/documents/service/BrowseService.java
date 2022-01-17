package org.zols.documents.service;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zols.datastore.DataStore;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.query.AggregatedResults;
import org.zols.datastore.query.MapQuery;
import org.zols.datastore.query.Page;
import org.zols.datastore.service.DataService;

import java.util.Locale;
import java.util.Map;


public class BrowseService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(BrowseService.class);

    /**
     * DataService.
     */
    private final DataService dataService;

    /**
     * DataStore.
     */
    private final DataStore dataStore;

    /**
     * this is the constructor.
     * @param aDataService a dataService
     * @param anDataStore an dataStore
     */
    public BrowseService(final DataService aDataService,
                         final DataStore anDataStore) {
        this.dataService = aDataService;
        this.dataStore = anDataStore;
    }

    /**
     * search Schema.
     * @param keyword  the keyword
     * @param pageNumber  the pageNumber
     * @param pageSize pageSize
     * @param query query
     * @param schemaId schemaId
     * @return list
     */
    public final Page<Map<String, Object>> searchSchema(final String schemaId,
                                                  final String keyword,
                                             final Condition<MapQuery> query,
                                                  final Integer pageNumber,
                                                  final Integer pageSize)
            throws DataStoreException {
        return dataService.list(schemaId, query, pageNumber, pageSize, null);
    }

    /**
     * search Schema.
     * @param keyword  the keyword
     * @param pageNumber  the pageNumber
     * @param pageSize pageSize
     * @param query query
     * @param schemaId schemaId
     * @param locale locale
     * @return list
     */
    public final AggregatedResults browseSchema(final String schemaId,
                                          final String keyword,
                                          final Condition<MapQuery> query,
                                          final Locale locale,
                                          final Integer pageNumber,
                                          final Integer pageSize)
            throws DataStoreException {
        return dataStore.browse(schemaId, keyword, query, locale, pageNumber,
                pageSize);
    }

}
