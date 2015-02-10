/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.links.web;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zols.datatore.exception.DataStoreException;
import org.zols.links.domain.Category;
import org.zols.links.domain.Link;
import org.zols.links.service.CategoryService;

@RestController
@RequestMapping(value="/api/link_categories")
public class CategoryController {

    private static final Logger LOGGER = getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;    

    @RequestMapping(method = POST)    
    public Category create(@RequestBody Category category) throws DataStoreException {
        LOGGER.info("Creating new categories {}", category);
        return categoryService.create(category);
    }

    @RequestMapping(value = "/{name}", method = GET)    
    public Category read(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting category ", name);
        return categoryService.read(name);
    }

    @RequestMapping(value = "/{name}", method = PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@PathVariable(value = "name") String name,
            @RequestBody Category category) throws DataStoreException {        
        if (name.equals(category.getName())) {
            LOGGER.info("Updating categories with id {} with {}", name, category);
            categoryService.update(category);
        }
    }

    @RequestMapping(value = "/{name}", method = DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Deleting categories with id {}", name);
        categoryService.delete(name);
    }
    
    @RequestMapping(method = GET)    
    public List<Category> list() throws DataStoreException {
        LOGGER.info("Getting categories ");
        return categoryService.list();
    }
    
    @RequestMapping(value = "/{name}/first_level_links", method = GET)    
    public List<Link> listFirstLevelLinks(@PathVariable(value = "name") String name) throws DataStoreException {
        LOGGER.info("Getting first level links of category {} ",name);
        return categoryService.getFirstLevelLinks(name);
    }
}
