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
import org.zols.datastore.model.Schema;

@Controller
@RequestMapping(value = "/schemas")
public class SchemaController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SchemaController.class);

	@Autowired
	private DataStore dataStore;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Schema create(@RequestBody Schema schema) {
		LOGGER.info("Creating new schema {}", schema);
		return dataStore.create(schema, Schema.class);
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseBody
	public Schema read(@PathVariable(value = "name") String name) {
		LOGGER.info("Reading schema with id {}", name);
		Schema schema = dataStore.read(name, Schema.class);
		Validate.isTrue(schema != null, "Unable to find schema with id: "
				+ name);
		return schema;
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void update(@PathVariable(value = "name") String name,
			@RequestBody Schema schema) {
		LOGGER.info("Updating schema with id {} with {}", name, schema);
		Validate.isTrue(name.equals(schema.getName()),
				"name doesn't match URL name: " + schema.getName());
		dataStore.update(schema, Schema.class);
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(value = "name") String name) {
		LOGGER.info("Deleting schema with id {}", name);
		dataStore.delete(name, Schema.class);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<Schema> list(
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "per_page", required = false, defaultValue = "10") Integer per_page) {
		LOGGER.info("Listing schemas");
		return dataStore.list(page, per_page, Schema.class);
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
