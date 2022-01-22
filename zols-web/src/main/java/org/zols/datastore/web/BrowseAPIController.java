package org.zols.datastore.web;


import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
class BrowseAPIController {

    /**
     * logger.
     */
    private static final Logger LOGGER = getLogger(BrowseAPIController.class);

    /**
     * BrowseService.
     */
    private final BrowseService browseService;

    /**
     * Build Controller.
     * @param abrowseService
     */
    BrowseAPIController(final BrowseService abrowseService) {
        this.browseService = abrowseService;
    }

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
    public ResponseEntity<Page<Map<String, Object>>> searchBySchema(
            @PathVariable("schemaName") final String schemaName,
            @RequestParam(required = false, value = "q") final String keyword,
            final Pageable pageable, final HttpServletRequest request)
            throws DataStoreException {
        return ResponseEntity.ok(getPage(browseService.searchSchema(schemaName, keyword,
                getQuery(request), pageable.getPageNumber(),
                pageable.getPageSize()), pageable));
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
    public ResponseEntity<SpringAggregatedResults> browseBySchema(
            @PathVariable("schemaName") final String schemaName,
            @RequestParam(required = false, value = "q") final String keyword,
            final Pageable pageable, final HttpServletRequest request,
            final Locale locale)
            throws DataStoreException {
        return ResponseEntity.ok(getAggregatedResults(
                browseService.browseSchema(schemaName, keyword,
                        getQuery(request), locale, pageable.getPageNumber(),
                        pageable.getPageSize()), pageable));
    }

}
