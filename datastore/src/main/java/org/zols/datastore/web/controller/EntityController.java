package org.zols.datastore.web.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zols.datastore.DataStore;
import org.zols.datastore.domain.Entity;
import org.zols.datastore.exception.DataStoreException;

@Controller
@Api(value = "Entities")
public class EntityController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntityController.class);

    @Autowired
    private DataStore dataStore;

    @ApiOperation(value = "Creates an Entity", response = Entity.class, notes = "Returns the created entity")
    @RequestMapping(value = "/api/entities", method = POST,consumes = APPLICATION_JSON_VALUE , produces = APPLICATION_JSON_VALUE)    
    @ResponseBody
    public Entity create(@RequestBody Entity entity) {
        LOGGER.info("Creating new entity {}", entity);
        return dataStore.create(entity, Entity.class);
    }

    @ApiOperation(value = "Read", response =Map.class ,notes = "Return entity")
    @RequestMapping(value = "/api/entities/{name}", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Entity> read(@PathVariable(value = "name") String name) {
        LOGGER.info("Reading entity with id {}", name);
        Map<String, Entity> map = new HashMap<String, Entity>(1);
        map.put("entity", dataStore.read(name, Entity.class));
        return map;
    }

    @ApiOperation(value = "Update", notes = "Updates an entity")
    @RequestMapping(value = "/api/entities/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Entity entity) {
        LOGGER.info("Updating entity with id {} with {}", name, entity);
        if (name.equals(entity.getName())) {
            dataStore.update(entity, Entity.class);
        } else {
            throw new DataStoreException("Invalid Record");
        }
    }

    @ApiOperation(value = "Delete", notes = "Deletes an entity")
    @RequestMapping(value = "/api/entities/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting entity with id {}", name);
        dataStore.delete(name, Entity.class);
    }

    @ApiOperation(value = "List", response = Page.class,notes = "List all entity")
    @RequestMapping(value = "/api/entities", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Entity> list(
            Pageable page) {
        LOGGER.info("Listing entities");
        return dataStore.list(page, Entity.class);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleClientErrors(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleServerErrors(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @RequestMapping(value = "/entities/{name}", method = GET)
    @ApiIgnore
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("entity", dataStore.read(name, Entity.class));
        return "org/zols/datastore/entity";
    }

    @RequestMapping(value = "/entities/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("entity", new Entity());
        return "org/zols/datastore/entity";
    }

    @RequestMapping(value = "/entities", method = GET)
    @ApiIgnore
    public String listing() {
        return "org/zols/datastore/listentities";
    }

      
}
