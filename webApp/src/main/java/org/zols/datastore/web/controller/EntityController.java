package org.zols.datastore.web.controller;

import com.zols.datastore.DataStore;
import com.zols.datastore.domain.BaseObject;
import com.zols.datastore.domain.Entity;
import com.zols.datastore.util.DynamicBeanGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/entities")
public class EntityController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntityController.class);

    @Autowired
    private DataStore dataStore;

    @Autowired
    private DynamicBeanGenerator dynamicBeanGenerator;

    @RequestMapping(method = POST)
    @ResponseBody
    public Entity create(@RequestBody Entity entity) {
        LOGGER.info("Creating new entity {}", entity);
        return dataStore.create(entity, Entity.class);
    }

    @RequestMapping(value = "/{name}", method = GET)
    @ResponseBody
    public Map<String, Entity> read(@PathVariable(value = "name") String name) {
        LOGGER.info("Reading entity with id {}", name);
        Map<String, Entity> map = new HashMap<String, Entity>(1);
        map.put("entity", dataStore.read(name, Entity.class));
        return map;
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Entity entity) {
        LOGGER.info("Updating entity with id {} with {}", name, entity);
        if (name.equals(entity.getName())) {
            dataStore.update(entity, Entity.class);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) {
        LOGGER.info("Deleting entity with id {}", name);
        dataStore.delete(name, Entity.class);
    }

    @RequestMapping(method = GET)
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

    @RequestMapping(value = "/edit/{name}", method = GET)
    public String edit(@PathVariable(value = "name") String name, Model model) {
        model.addAttribute("entity", dataStore.read(name, Entity.class));
        return "datastore/entity";
    }

    @RequestMapping(value = "/add", method = GET)
    public String add(Model model) {
        model.addAttribute("entity", new Entity());
        return "datastore/entity";
    }

    @RequestMapping(value = "/listing", method = GET)
    public String listing() {
        return "datastore/listentities";
    }

    @RequestMapping(value = "/{entityName}", method = POST)
    @ResponseBody
    public BaseObject create(@PathVariable(value = "entityName") String entityName, @RequestBody HashMap<String, String> entityObjectMap) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        return dataStore.create(getBaseObject(clazz, entityName, entityObjectMap), clazz);
    }

    @RequestMapping(value = "/{entityName}/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "entityName") String entityName,@PathVariable(value = "name") String name,
            @RequestBody HashMap<String, String> entityObjectMap) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        BaseObject baseObject = getBaseObject(clazz, entityName, entityObjectMap);
        if (name.equals(baseObject.getName())) {
            dataStore.update(baseObject, clazz);
        }
    }
    
    @RequestMapping(value = "/{entityName}/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "entityName") String entityName,@PathVariable(value = "name") String name) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        dataStore.delete(name, clazz);
    }
    
    @RequestMapping(value = "/get/{entityName}",method = GET)
    @ResponseBody
    public Page<BaseObject> list(@PathVariable(value = "entityName") String entityName,
            Pageable page) {
        Class<? extends BaseObject> clazz = dynamicBeanGenerator.getBeanClass(entityName);
        return (Page<BaseObject>) dataStore.list(page, clazz);
    }

    private BaseObject getBaseObject(Class<? extends BaseObject> clazz, String entityName, HashMap<String, String> contactMap) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
        for (String entry : contactMap.keySet()) {
            beanWrapper.setPropertyValue(entry, contactMap.get(entry));
        }
        return (BaseObject) beanWrapper.getWrappedInstance();
    }

}
