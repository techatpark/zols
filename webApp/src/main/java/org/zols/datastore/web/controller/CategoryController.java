package org.zols.datastore.web.controller;

import com.zols.datastore.util.LinkManager;
import com.zols.linkmanager.domain.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CategoryController.class);

    @Autowired
    private LinkManager linkManager;

    @RequestMapping(value = "/api/categories", method = POST)
    @ResponseBody
    public Category create(@RequestBody Category category) {
        LOGGER.info("Creating new entity {}", category);
        return linkManager.add(category);
    }

    @RequestMapping(value = "/categories/add", method = GET)
    public String add(Model model) {
        model.addAttribute("category", new Category());
        return "datastore/category";
    }

    @RequestMapping(value = "/api/categories", method = GET)
    @ResponseBody
    public Page<Category> list(
            Pageable page) {
        LOGGER.info("Listing categories");
        return linkManager.categoryList(page);
    }

    @RequestMapping(value = "/categories", method = GET)
    public String listing() {
        return "datastore/listcategories";
    }

}
