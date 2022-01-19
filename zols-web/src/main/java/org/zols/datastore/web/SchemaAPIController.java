package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zols.datastore.DataStoreException;
import org.zols.datastore.service.SchemaService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping(value = "/api/schema")
public class SchemaAPIController {

    /**
     * logger.
     */
    private static final Logger LOGGER = getLogger(SchemaAPIController.class);

    /**
     * schemaService.
     */
    @Autowired
    private SchemaService schemaService;

    /**
     * Create response entity.
     *
     * @param schema the schema
     * @return the response entity
     * @throws DataStoreException
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody final Map<String, Object> schema)
            throws DataStoreException {
        LOGGER.info("Creating new schema {}", schema);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schemaService.create(schema));
    }

    /**
     * Read response entity.
     *
     * @param id the id
     * @param enlarged the enlarged
     * @return the response entity
     * @throws DataStoreException
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> read(
            @PathVariable(value = "id") final String id,
            @RequestParam(value = "enlarged", required = false) final
            String enlarged) throws DataStoreException {
        LOGGER.info("Getting schema ", id);
        return ResponseEntity.of(
                Optional.of((enlarged == null) ? schemaService.read(id)
                        : schemaService.readEnlargedSchema(id)));
    }

    /**
     * update response entity.
     *
     * @param id the id
     * @param schema the schema
     * @return the response entity
     * @throws DataStoreException
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity update(@PathVariable(value = "id") final String id,
                                 @RequestBody final Map<String, Object> schema)
            throws DataStoreException {
        LOGGER.info("Updating schema with id {} with {}", id, schema);
        boolean updated = schemaService.update(id, schema);
        return updated ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();

    }

    /**
     * delete response entity.
     *
     * @param id the id
     * @return the response entity
     * @throws DataStoreException
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") final String id)
            throws DataStoreException {
        LOGGER.info("Deleting schema with id {}", id);
        boolean deleted = schemaService.delete(id);
        return deleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * List response entity.
     *
     * @return the response entity
     * @throws DataStoreException
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list()
            throws DataStoreException {
        LOGGER.info("Getting JsonSchemas ");
        return ResponseEntity.ok(schemaService.list());
    }
}
