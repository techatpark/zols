package org.zols.datastore.web.controller;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.zols.datastore.DataStore;
import com.zols.datastore.domain.BaseObject;
import com.zols.datastore.domain.Entity;
import com.zols.datastore.util.DynamicBeanGenerator;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller

public class EntityController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntityController.class);

    @Autowired
    private DataStore dataStore;

    @Autowired
    private DynamicBeanGenerator dynamicBeanGenerator;

    @RequestMapping(value = "/api/entities", method = POST)
    @ApiIgnore
    @ResponseBody
    public Entity create(@RequestBody Entity entity) {
        LOGGER.info("Creating new entity {}", entity);
        return dataStore.create(entity, Entity.class);
    }

    @RequestMapping(value = "/api/entities/{name}", method = GET)
    @ApiIgnore
    @ResponseBody
    public Map<String, Entity> read(@PathVariable(value = "name") String name) {
        LOGGER.info("Reading entity with id {}", name);
        Map<String, Entity> map = new HashMap<String, Entity>(1);
        map.put("entity", dataStore.read(name, Entity.class));
        return map;
    }

    @RequestMapping(value = "/api/entities/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Entity entity) {
        LOGGER.info("Updating entity with id {} with {}", name, entity);
        if (name.equals(entity.getName())) {
            dataStore.update(entity, Entity.class);
        }
    }

    @RequestMapping(value = "/api/entities/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting entity with id {}", name);
        dataStore.delete(name, Entity.class);
    }

    @RequestMapping(value = "/api/entities", method = GET)
    @ApiIgnore
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
        return "com/zols/datastore/entity";
    }

    @RequestMapping(value = "/entities/add", method = GET)
    @ApiIgnore
    public String add(Model model) {
        model.addAttribute("entity", new Entity());
        return "com/zols/datastore/entity";
    }

    @RequestMapping(value = "/entities", method = GET)
    @ApiIgnore
    public String listing() {
        return "com/zols/datastore/listentities";
    }

    //dataList mapping
    @RequestMapping(value = "/data/{entityName}", method = GET)
    @ApiIgnore
    public String listdata(@PathVariable(value = "entityName") String entityName, Model model) {
        model.addAttribute("entity", dataStore.read(entityName, Entity.class));
        return "com/zols/datastore/listdata";
    }

    @RequestMapping(value = "/data/{entityName}/add", method = GET)
    @ApiIgnore
    public String addData(@PathVariable(value = "entityName") String entityName, Model model) {
        model.addAttribute("entityName", entityName);
        return "com/zols/datastore/data";
    }

    @RequestMapping(value = "/data/{entityName}/{dataName}", method = GET)
    @ApiIgnore
    public String editData(@PathVariable(value = "entityName") String entityName,
            @PathVariable(value = "dataName") String dataName,
            Model model) {
        model.addAttribute("entityName", entityName);
        model.addAttribute("dataName", dataName);
        return "com/zols/datastore/data";
    }
    
    @RequestMapping(value = "api/data/{entityName}/{dataName}", method = GET)
    @ApiIgnore
    @ResponseBody
    public BaseObject readData(@PathVariable(value = "entityName") String entityName,
            @PathVariable(value = "dataName") String dataName,
            Model model) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        return dataStore.read(dataName, clazz);
    }

    @RequestMapping(value = "/api/data/{entityName}", method = POST)
    @ApiIgnore
    @ResponseBody
    public BaseObject create(@PathVariable(value = "entityName") String entityName, @RequestBody HashMap<String, String> entityObjectMap) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        return dataStore.create(dataStore.getBaseObject(clazz, entityName, entityObjectMap), clazz);
    }

    @RequestMapping(value = "/api/data/{entityName}/{name}", method = PUT)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "entityName") String entityName, @PathVariable(value = "name") String name,
            @RequestBody HashMap<String, String> entityObjectMap) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        BaseObject baseObject = dataStore.getBaseObject(clazz, entityName, entityObjectMap);
        if (name.equals(baseObject.getName())) {
            dataStore.update(baseObject, clazz);
        }
    }

    @RequestMapping(value = "/api/data/{entityName}/{name}", method = DELETE)
    @ApiIgnore
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "entityName") String entityName, @PathVariable(value = "name") String name) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        dataStore.delete(name, clazz);
    }

    @RequestMapping(value = "/api/data/{entityName}", method = GET)
    @ApiIgnore
    @ResponseBody
    public Page<BaseObject> list(@PathVariable(value = "entityName") String entityName,
            Pageable page) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        return (Page<BaseObject>) dataStore.list(page, clazz);
    }

    

}
