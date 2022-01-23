package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import static org.zols.datastore.web.util.SpringConverter.getPage;

@RestController
@RequestMapping(value = "/api/data/{schemaId}")
class DataAPIController {

    /**
     * Logger.
     */
    private static final Logger LOGGER = getLogger(DataAPIController.class);

    /**
     * The DataService.
     */
    private final DataService dataService;

    /**
     * Build Controller.
     * @param aDataService
     */
    DataAPIController(final DataService aDataService) {
        this.dataService = aDataService;
    }

    /**
     * creates the schema.
     *
     * @param schemaName the jsonSchema
     * @param jsonData   the jsonData
     * @param loc        the loc
     * @return schema
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @PathVariable(value = "schemaId") final String schemaName,
            @RequestBody final Map<String, Object> jsonData, final Locale loc)
            throws DataStoreException {
        LOGGER.info("Creating new instance of {}", schemaName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataService.create(schemaName, jsonData, loc));
    }

    /**
     * updates the schema.
     *
     * @param schemaName the jsonSchema
     * @param idname     the idname
     * @param id         the id
     * @param loc        the loc
     * @return schema
     */
    @GetMapping(value = "/{idname}/{id}")
    public ResponseEntity<Map<String, Object>> read(
            @PathVariable(value = "schemaId") final String schemaName,
            @PathVariable(value = "idname") final String idname,
            @PathVariable(value = "id") final String id, final Locale loc)
            throws DataStoreException {
        LOGGER.info("Getting Data {} value {}", idname, id);
        Optional<Map<String, Object>> optional =
                dataService.read(schemaName, loc, new SimpleEntry(idname, id));
        return ResponseEntity.of(optional);
    }

    /**
     * updates the schema.
     *
     * @param schemaName the jsonSchema
     * @param idname     the idname
     * @param id         the id
     * @param jsonData   the jsonData
     * @param loc        the loc
     * @return empty.
     */
    @PutMapping(value = "/{idname}/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity update(@PathVariable(
                              value = "schemaId") final String schemaName,
                       @PathVariable(value = "idname") final String idname,
                       @PathVariable(value = "id") final String id,
                       @RequestBody final Map<String, Object> jsonData,
                       final Locale loc)
            throws DataStoreException {
        dataService.update(schemaName, jsonData, loc,
                new SimpleEntry(idname, id));
        return ResponseEntity.noContent().build();
    }

    /**
     * updates the schema.
     *
     * @param schemaName the jsonSchema
     * @param idname     the idname
     * @param id         the id
     * @return empty.
     */
    @DeleteMapping(value = "/{idname}/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity delete(@PathVariable(
                               value = "schemaId") final String schemaName,
                       @PathVariable(value = "idname") final String idname,
                       @PathVariable(value = "id") final String id)
            throws DataStoreException {
        LOGGER.info("Deleting jsonSchemas with id {} value {}", idname, id);
        dataService.delete(schemaName, new SimpleEntry(idname, id));
        return ResponseEntity.noContent().build();
    }

    /**
     * updates the schema.
     *
     * @param schemaName  the jsonSchema
     * @param loc         the loc
     * @param pageable    the pageable
     * @param queryString the queryString
     * @return schema
     */
    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> list(
            @PathVariable(value = "schemaId") final String schemaName,
            @RequestParam(
                    value = "q", required = false) final String queryString,
            final Pageable pageable, final Locale loc)
            throws DataStoreException {
        LOGGER.info("Getting Data for {}", schemaName);
        Page page = getPage(dataService.list(schemaName, queryString,
                        pageable.getPageNumber(), pageable.getPageSize(), loc),
                pageable);
        return ResponseEntity.of(Optional.ofNullable(page));
    }

}
