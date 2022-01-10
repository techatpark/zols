
package org.zols.datastore.web;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zols.datastore.service.SchemaService;
import org.zols.datastore.DataStoreException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping(value = "/api/schema")
public class SchemaAPIController {

    private static final Logger LOGGER = getLogger(SchemaAPIController.class);

    @Autowired
    private SchemaService schemaService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> schema) throws DataStoreException {
        LOGGER.info("Creating new schema {}", schema);
        return ResponseEntity.status(HttpStatus.CREATED).body(schemaService.create(schema));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> read(@PathVariable(value = "id") String id, @RequestParam(value = "enlarged",required = false) String enlarged) throws DataStoreException {
        LOGGER.info("Getting schema ", id);
        return ResponseEntity.of(Optional.of((enlarged == null ) ? schemaService.read(id)
                : schemaService.readEnlargedSchema(id)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity update(@PathVariable(value = "id") String id,
            @RequestBody Map<String, Object> schema) throws DataStoreException {
        LOGGER.info("Updating schema with id {} with {}", id, schema);
        boolean updated = schemaService.update(id,schema);
        return updated ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") String id) throws DataStoreException {
        LOGGER.info("Deleting schema with id {}", id);
        boolean deleted = schemaService.delete(id);
        return deleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() throws DataStoreException {
        LOGGER.info("Getting JsonSchemas ");
        return ResponseEntity.ok(schemaService.list());
    }
}
