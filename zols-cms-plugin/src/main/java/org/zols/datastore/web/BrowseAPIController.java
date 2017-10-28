/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.web;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datastore.query.AggregatedResults;
import static org.zols.datastore.web.util.HttpUtil.getQuery;
import org.zols.datatore.exception.DataStoreException;
import org.zols.documents.service.BrowseService;
import org.zols.documents.service.SpringAggregatedResults;

@RestController
@RequestMapping(value = "/api")
public class BrowseAPIController {

    private static final Logger LOGGER = getLogger(BrowseAPIController.class);

    @Autowired
    private BrowseService browseService;

    @RequestMapping(value = "/search/{schemaName}")
    public Page<Map<String, Object>> searchBySchema(@PathVariable("schemaName") String schemaName,
            Pageable pageable,HttpServletRequest request) throws DataStoreException {
        return browseService.searchSchema(schemaName, null, getQuery(request),pageable);
    }

    @RequestMapping(value = "/browse/{schemaName}")
    public SpringAggregatedResults browseBySchema(@PathVariable("schemaName") String schemaName,
            Pageable pageable,HttpServletRequest request) throws DataStoreException {
        return browseService.browseSchema(schemaName, null, getQuery(request),pageable);
    }

}
