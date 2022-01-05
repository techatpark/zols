/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this jsonSchema file, choose Tools | JsonSchemas
 * and open the jsonSchema in the editor.
 */
package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.zols.datastore.service.DataService;
import org.zols.datastore.DataStoreException;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.zols.datastore.web.util.SpringConverter.getPage;

/**
 *
 * @author sathish_ku
 */
@RestController
@RequestMapping(value = "/api/data/{schemaId}")
public class DataControler {

    private static final Logger LOGGER = getLogger(DataControler.class);

    @Autowired
    private DataService dataService;

    @RequestMapping(method = POST)
    public Map<String, Object> create(@PathVariable(value = "schemaId") String schemaName,
            @RequestBody Map<String, Object> jsonData, Locale loc) throws DataStoreException {
        LOGGER.info("Creating new instance of {}", schemaName);
        return dataService.create(schemaName, jsonData, loc);
    }

    @RequestMapping(value = "/{idname}/{id}", method = GET)
    public Map<String, Object> read(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "idname") String idname, @PathVariable(value = "id") String id, Locale loc) throws DataStoreException {
        LOGGER.info("Getting Data {} value {}", idname, id);
        Optional<Map<String, Object>> optional = dataService.read(schemaName, loc, new SimpleEntry(idname, id));
        return optional.orElse(null);
    }

    @RequestMapping(value = "/{idname}/{id}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "idname") String idname,
            @PathVariable(value = "id") String id,
            @RequestBody Map<String, Object> jsonData, Locale loc) throws DataStoreException {
        dataService.update(schemaName, jsonData, loc, new SimpleEntry(idname, id));

    }

    @RequestMapping(value = "/{idname}/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "schemaId") String schemaName,
            @PathVariable(value = "idname") String idname,
            @PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {} value {}", idname, id);
        dataService.delete(schemaName, new SimpleEntry(idname, id));
    }

    @RequestMapping(method = GET)
    public Page<Map<String, Object>> list(@PathVariable(value = "schemaId") String schemaName,
            @RequestParam(value = "q", required = false) String queryString,
            Pageable pageable, Locale loc) throws DataStoreException {
        LOGGER.info("Getting Data for {}", schemaName);
        return getPage(dataService.list(schemaName, queryString, pageable.getPageNumber(), pageable.getPageSize(), loc), pageable);
    }

}
