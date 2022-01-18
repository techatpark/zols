/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.web.util.SpringAggregatedResults;
import org.zols.documents.service.BrowseService;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.zols.datastore.web.util.HttpUtil.getQuery;
import static org.zols.datastore.web.util.SpringConverter.getAggregatedResults;
import static org.zols.datastore.web.util.SpringConverter.getPage;

@RestController
@RequestMapping(value = "/api")
public class BrowseAPIController {

    /**
     * logger.
     */
    private static final Logger LOGGER = getLogger(BrowseAPIController.class);

    /**
     * BrowseService.
     */
    @Autowired
    private BrowseService browseService;

    /**
     * updates the schema.
     *
     * @param schemaName the jsonSchema
     * @param keyword    the keyword
     * @param pageable   the pageable
     * @param request    the request
     * @return schema
     */
    @RequestMapping(value = "/search/{schemaName}")
    public Page<Map<String, Object>> searchBySchema(
            @PathVariable("schemaName") final String schemaName,
            @RequestParam(required = false, value = "q") final String keyword,
            final Pageable pageable, final HttpServletRequest request)
            throws DataStoreException {
        return getPage(browseService.searchSchema(schemaName, keyword,
                getQuery(request), pageable.getPageNumber(),
                pageable.getPageSize()), pageable);
    }

    /**
     * updates the schema.
     *
     * @param schemaName the jsonSchema
     * @param keyword    the keyword
     * @param pageable   the pageable
     * @param request    the request
     * @param locale     the locale
     * @return schema
     */
    @RequestMapping(value = "/browse/{schemaName}")
    public SpringAggregatedResults browseBySchema(
            @PathVariable("schemaName") final String schemaName,
            @RequestParam(required = false, value = "q") final String keyword,
            final Pageable pageable, final HttpServletRequest request,
            final Locale locale)
            throws DataStoreException {
        return getAggregatedResults(
                browseService.browseSchema(schemaName, keyword,
                        getQuery(request), locale, pageable.getPageNumber(),
                        pageable.getPageSize()), pageable);
    }

}
