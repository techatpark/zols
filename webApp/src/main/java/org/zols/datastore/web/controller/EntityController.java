package org.zols.datastore.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.util.Validate;
import org.zols.datastore.DataStore;
import org.zols.datastore.model.Entity;

@Controller
@RequestMapping(value = "/entity")
public class EntityController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EntityController.class);

	@Autowired
	private DataStore dataStore;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Entity create(@RequestBody Entity entity) {
		LOGGER.info("Creating new entity {}", entity);
		return dataStore.create(entity,Entity.class);
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseBody
	public Entity read(@PathVariable(value = "name") String name) {
		LOGGER.info("Reading entity with id {}", name);
		Entity entity = dataStore.read(name,Entity.class);
		Validate.isTrue(entity != null, "Unable to find entity with id: "
				+ name);
		return entity;
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void update(@PathVariable(value = "name") String name,
			@RequestBody Entity entity) {
		LOGGER.info("Updating entity with id {} with {}", name, entity);
		Validate.isTrue(name.equals(entity.getId()),
				"name doesn't match URL name: " + entity.getId());
		dataStore.update(entity,Entity.class);
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(value = "name") String name) {
		LOGGER.info("Deleting entity with id {}", name);
		dataStore.delete(name,Entity.class);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<Entity> list(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "per_page", required = false, defaultValue = "10") Integer per_page) {
		LOGGER.info("Listing entitys");
		return dataStore.list(page, per_page,Entity.class);
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

}
