package org.zols.datastore.model;

import org.springframework.data.annotation.Id;

public class Entity {
	
	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
