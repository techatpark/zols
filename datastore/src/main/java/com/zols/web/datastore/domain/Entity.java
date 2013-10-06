package com.zols.web.datastore.domain;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * 
 * This describes the entity of the entity added to zols datastore.
 * @author Sathish Kumar Thiyagarajan
 * 
 */
public class Entity {

	@Id
	private String name;

	private String label;

	private String description;

	private List<Attribute> attributes;

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
