package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.service.DataService;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.zols.datastore.web.util.SpringConverter.getPage;

@RestController
@RequestMapping(value = "/api/data/{schemaId}")
public class DataAPIController {

    private static final Logger LOGGER = getLogger(DataAPIController.class);

    @Autowired
    private DataService dataService;

    @RequestMapping(method = POST)
    public Map<String, Object> create(
            @PathVariable(value = "schemaId") final String schemaName,
            @RequestBody final Map<String, Object> jsonData, final Locale loc)
            throws DataStoreException {
        LOGGER.info("Creating new instance of {}", schemaName);
        return dataService.create(schemaName, jsonData, loc);
    }

    @RequestMapping(value = "/{idname}/{id}", method = GET)
    public Map<String, Object> read(
            @PathVariable(value = "schemaId") final String schemaName,
            @PathVariable(value = "idname") final String idname,
            @PathVariable(value = "id") final String id, final Locale loc)
            throws DataStoreException {
        LOGGER.info("Getting Data {} value {}", idname, id);
        Optional<Map<String, Object>> optional =
                dataService.read(schemaName, loc, new SimpleEntry(idname, id));
        return optional.orElse(null);
    }

    @RequestMapping(value = "/{idname}/{id}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "schemaId") final String schemaName,
                       @PathVariable(value = "idname") final String idname,
                       @PathVariable(value = "id") final String id,
                       @RequestBody final Map<String, Object> jsonData, final Locale loc)
            throws DataStoreException {
        dataService.update(schemaName, jsonData, loc,
                new SimpleEntry(idname, id));

    }

    @RequestMapping(value = "/{idname}/{id}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "schemaId") final String schemaName,
                       @PathVariable(value = "idname") final String idname,
                       @PathVariable(value = "id") final String id)
            throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {} value {}", idname, id);
        dataService.delete(schemaName, new SimpleEntry(idname, id));
    }

    @RequestMapping(method = GET)
    public Page<Map<String, Object>> list(
            @PathVariable(value = "schemaId") final String schemaName,
            @RequestParam(value = "q", required = false) final String queryString,
            final Pageable pageable, final Locale loc) throws DataStoreException {
        LOGGER.info("Getting Data for {}", schemaName);
        return getPage(dataService.list(schemaName, queryString,
                        pageable.getPageNumber(), pageable.getPageSize(), loc),
                pageable);
    }

}
