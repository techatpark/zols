import SchemaService from "../services/SchemaService";

/**
 * Schema - Component for editing schema definitions
 * Handles both creating new schemas and editing existing ones
 */
class Schema {
	constructor(_caller, _container) {
		this.container = _container;
		this.caller = _caller;
		this.schema = null;
		this.selectedObject = null;
		this.selectedProperty = null;
		this.isNewSchema = false;
		this.isSaving = false;
		this.propertyCounter = 0;
		this.currentPropertyKey = null; // Track current property key for renaming
		this.propertyChangeTimeout = null; // For debouncing updates
		this.propertiesListListenerAttached = false; // Track if listener is attached
		this.propertyToDelete = null; // Track property to delete
		this.modalSetupComplete = false; // Track if modal is set up

		this.schemaManager = document.createElement("div");
		this.schemaManager.classList.add("row", "g-4");

		this.scemaNavigator = document.createElement("div");
		this.scemaNavigator.classList.add("col-3");
		this.scemaNavigator.innerHTML = `
		<aside class="bd-aside sticky-xl-top align-self-start mb-3 mb-xl-5 px-2">
          <nav class="small" id="toc">
            <ul class="list-unstyled">
              <li class="my-2">
				<a id="generalLink" class="d-inline-flex align-items-center rounded active" href="#">
					<i class="fas fa-cog me-2"></i> General
				</a>
			  </li>
              <li class="my-2">
                <button class="btn d-inline-flex align-items-center collapsed w-100 text-start" 
				data-bs-toggle="collapse" aria-expanded="false" data-bs-target="#contents-collapse" aria-controls="contents-collapse">
				<i class="fas fa-list me-2"></i> Properties
				<span id="propertyCount" class="badge bg-secondary ms-auto">0</span>
				</button>
                <ul class="list-unstyled ps-3 collapse" id="contents-collapse">
                </ul>
              </li>
            </ul>
          </nav>
        </aside>
		`;

		this.scemaEditor = document.createElement("div");
		this.scemaEditor.classList.add("col-9");
		this.scemaEditor.innerHTML = `
		<div class="card shadow-sm">
			<div class="card-header bg-primary text-white">
				<h5 class="mb-0" id="editorTitle">
					<i class="fas fa-edit me-2"></i>Schema Editor
				</h5>
			</div>
			<div class="card-body">
		<form class="needs-validation" id="editForm" novalidate>
					<!-- Schema General Information -->
					<div id="schemaGeneralSection">
						<h6 class="text-muted mb-3">
							<i class="fas fa-info-circle me-2"></i>General Information
						</h6>
						
		<div class="row mb-3">
							<label for="nameTxt" class="col-sm-3 col-form-label">
								Schema ID <span class="text-danger">*</span>
							</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="nameTxt" 
									placeholder="e.g., product, user, order" 
									autocomplete="off" required
									pattern="[a-zA-Z][a-zA-Z0-9_]*"
									title="Must start with a letter and contain only letters, numbers, and underscores">
								<div class="form-text">Unique identifier for this schema (letters, numbers, underscores only)</div>
								<div class="invalid-feedback">Please provide a valid Schema ID.</div>
        </div>
						</div>

						<div class="row mb-3">
							<label for="titleTxt" class="col-sm-3 col-form-label">
								Title <span class="text-danger">*</span>
							</label>
							<div class="col-sm-9">
								<input class="form-control" id="titleTxt" 
									placeholder="e.g., Product Schema" 
									autocomplete="off" required>
								<div class="invalid-feedback">Please provide a Title.</div>
			</div>
		</div>

		<div class="row mb-3">
							<label for="descriptionTxt" class="col-sm-3 col-form-label">
								Description
							</label>
							<div class="col-sm-9">
								<textarea class="form-control" id="descriptionTxt" 
									rows="3" placeholder="Describe what this schema represents..."
									autocomplete="off"></textarea>
								<div class="form-text">Optional description of the schema</div>
        </div>
			</div>
		</div>

					<!-- Property Editor Section (hidden when editing schema general) -->
					<div id="propertyEditorSection" class="d-none">
						<h6 class="text-muted mb-3">
							<i class="fas fa-tag me-2"></i>Property Details
						</h6>
						
						<div class="row mb-3">
							<label for="propertyNameTxt" class="col-sm-3 col-form-label">
								Property Name <span class="text-danger">*</span>
							</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="propertyNameTxt" 
									placeholder="e.g., name, price, email" 
									autocomplete="off" required
									pattern="[a-zA-Z][a-zA-Z0-9_]*"
									title="Must start with a letter and contain only letters, numbers, and underscores">
								<div class="form-text">Field name (must be unique, letters/numbers/underscores only)</div>
								<div class="invalid-feedback">Please provide a valid Property Name.</div>
								<div id="propertyNameError" class="text-danger small mt-1 d-none"></div>
							</div>
						</div>

						<div class="row mb-3">
							<label for="typeSelect" class="col-sm-3 col-form-label">
								Type <span class="text-danger">*</span>
							</label>
							<div class="col-sm-9">
								<select class="form-select" id="typeSelect" required>
									<option value="string">Text (string)</option>
  <option value="integer">Integer</option>
									<option value="number">Number (float)</option>
  <option value="boolean">Boolean</option>
									<option value="array">Array</option>
									<option value="object">Object</option>
</select>
								<div class="invalid-feedback">Please choose a Type.</div>
        </div>
	</div>

  <div class="row mb-3">
							<label for="propertyTitleTxt" class="col-sm-3 col-form-label">
								Property Title <span class="text-danger">*</span>
							</label>
							<div class="col-sm-9">
								<input class="form-control" id="propertyTitleTxt" 
									placeholder="e.g., Product Name" 
									autocomplete="off" required>
								<div class="invalid-feedback">Please provide a Property Title.</div>
  </div>
	</div>

  <div class="row mb-3">
							<label for="propertyDescriptionTxt" class="col-sm-3 col-form-label">
								Description
							</label>
							<div class="col-sm-9">
								<textarea class="form-control" id="propertyDescriptionTxt" 
									rows="2" placeholder="Describe this property..."
									autocomplete="off"></textarea>
        </div>
    </div>
  </div>

					<!-- Properties List Section -->
					<div id="propertiesListSection" class="mt-4">
						<div class="d-flex justify-content-between align-items-center mb-3">
							<h6 class="text-muted mb-0">
								<i class="fas fa-list me-2"></i>Properties
							</h6>
							<button type="button" class="btn btn-sm btn-success" id="addPropertyBtn">
								<i class="fas fa-plus me-1"></i> Add Property
							</button>
        </div>
						
						<div id="propertiesList" class="list-group">
							<!-- Properties will be dynamically added here -->
						</div>
						
						<div id="noPropertiesMessage" class="alert alert-info">
							<i class="fas fa-info-circle me-2"></i>
							No properties added yet. Click "Add Property" to get started.
    </div>
  </div>
  
					<!-- Required Fields Section -->
					<div id="requiredFieldsSection" class="mt-4">
						<h6 class="text-muted mb-3">
							<i class="fas fa-asterisk me-2"></i>Required Fields
						</h6>
						<div id="requiredChoices" class="row g-2">
							<!-- Required checkboxes will be dynamically added here -->
      </div>
						<div class="form-text mt-2">
							Select which properties are required when creating instances of this schema
      </div>
    </div>

  <button id="submitBtn" class="btn btn-primary d-none" type="submit">Submit form</button>
</form>
			</div>
		</div>
		`;

		this.schemaManager.appendChild(this.scemaNavigator);
		this.schemaManager.appendChild(this.scemaEditor);

		const form = this.scemaEditor.querySelector("#editForm");
		form.addEventListener("submit", (event) => {
			event.preventDefault();
			event.stopPropagation();
			form.classList.add("was-validated");
		});

		this.form = form;

		// Event listeners
		this.scemaNavigator
			.querySelector("#generalLink")
			.addEventListener("click", (e) => {
				e.preventDefault();
				if (this.schema) {
					this.saveCurrentEditorValues();
					this.showGeneralSection();
					this.setEditor(this.schema);
				}
			});

		// Add property button
		const addPropertyBtn = this.scemaEditor.querySelector("#addPropertyBtn");
		if (addPropertyBtn) {
			addPropertyBtn.addEventListener("click", () => {
				this.addNewProperty();
			});
		}

		// Real-time property field updates
		this.setupPropertyFieldListeners();

		// Setup delete confirmation modal
		this.setupDeleteModal();

		// Save button handler
		const saveButton = document.querySelector(".fa-save");
		if (saveButton) {
			saveButton.addEventListener("click", () => {
				this.saveSchema();
			});
		}
	}

	/**
	 * Setup delete confirmation modal for properties
	 */
	setupDeleteModal() {
		if (this.modalSetupComplete) {
			return;
		}

		// Modal is now handled dynamically in showDeletePropertyModal
		// This method is kept for backward compatibility
		this.modalSetupComplete = true;
	}

	/**
	 * Setup real-time listeners for property fields
	 */
	setupPropertyFieldListeners() {
		// Property name validation and uniqueness check
		const propertyNameField = document.getElementById("propertyNameTxt");
		if (propertyNameField) {
			propertyNameField.addEventListener("input", (e) => {
				this.validatePropertyName(e.target.value);
			});
			propertyNameField.addEventListener("blur", () => {
				this.saveCurrentEditorValues();
				this.updatePropertyList();
			});
		}

		// Property title updates
		const propertyTitleField = document.getElementById("propertyTitleTxt");
		if (propertyTitleField) {
			propertyTitleField.addEventListener("input", () => {
				this.debounceUpdate(() => {
					this.saveCurrentEditorValues();
					this.updatePropertyList();
				});
			});
		}

		// Property type updates
		const typeSelect = document.getElementById("typeSelect");
		if (typeSelect) {
			typeSelect.addEventListener("change", () => {
				this.saveCurrentEditorValues();
				this.updatePropertyList();
			});
		}

		// Property description updates
		const propertyDescField = document.getElementById("propertyDescriptionTxt");
		if (propertyDescField) {
			propertyDescField.addEventListener("input", () => {
				this.debounceUpdate(() => {
					this.saveCurrentEditorValues();
					this.updatePropertyList();
				});
			});
		}
	}

	/**
	 * Debounce function calls
	 */
	debounceUpdate(callback, delay = 300) {
		if (this.propertyChangeTimeout) {
			clearTimeout(this.propertyChangeTimeout);
		}
		this.propertyChangeTimeout = setTimeout(callback, delay);
	}

	/**
	 * Validate property name
	 */
	validatePropertyName(name) {
		const errorDiv = document.getElementById("propertyNameError");
		const nameField = document.getElementById("propertyNameTxt");

		if (!name || !name.trim()) {
			if (errorDiv) {
				errorDiv.textContent = "Property name is required";
				errorDiv.classList.remove("d-none");
			}
			if (nameField) {
				nameField.setCustomValidity("Property name is required");
			}
			return false;
		}

		// Check pattern
		const pattern = /^[a-zA-Z][a-zA-Z0-9_]*$/;
		if (!pattern.test(name)) {
			if (errorDiv) {
				errorDiv.textContent =
					"Must start with a letter and contain only letters, numbers, and underscores";
				errorDiv.classList.remove("d-none");
			}
			if (nameField) {
				nameField.setCustomValidity("Invalid property name format");
			}
			return false;
		}

		// Check uniqueness (excluding current property)
		if (this.schema && this.schema.properties) {
			const existingKey = Object.keys(this.schema.properties).find(
				(key) => key === name && key !== this.currentPropertyKey
			);
			if (existingKey) {
				if (errorDiv) {
					errorDiv.textContent = `Property "${name}" already exists`;
					errorDiv.classList.remove("d-none");
				}
				if (nameField) {
					nameField.setCustomValidity("Property name must be unique");
				}
				return false;
			}
		}

		// Valid
		if (errorDiv) {
			errorDiv.classList.add("d-none");
		}
		if (nameField) {
			nameField.setCustomValidity("");
		}
		return true;
	}

	/**
	 * Save current editor values without validation
	 */
	saveCurrentEditorValues() {
		if (!this.schema) {
			return;
		}

		if (this.selectedObject === this.schema) {
			// Editing schema itself
			const nameField = document.getElementById("nameTxt");
			const titleField = document.getElementById("titleTxt");
			const descField = document.getElementById("descriptionTxt");

			if (nameField) this.schema["$id"] = nameField.value.trim();
			if (titleField) this.schema.title = titleField.value.trim();
			if (descField) this.schema.description = descField.value.trim();
		} else if (this.selectedObject && this.schema.properties) {
			// Editing a property
			const propertyNameField = document.getElementById("propertyNameTxt");
			const propertyTitleField = document.getElementById("propertyTitleTxt");
			const typeSelect = document.getElementById("typeSelect");
			const propertyDescField = document.getElementById(
				"propertyDescriptionTxt"
			);

			if (!propertyNameField || !propertyTitleField || !typeSelect) {
				return;
			}

			const newPropertyName = propertyNameField.value.trim();
			const oldKey =
				this.currentPropertyKey ||
				Object.keys(this.schema.properties).find(
					(key) => this.schema.properties[key] === this.selectedObject
				);

			// Validate property name
			if (!this.validatePropertyName(newPropertyName)) {
				return;
			}

			// Update property values
			if (this.selectedObject) {
				this.selectedObject.title = propertyTitleField.value.trim();
				this.selectedObject.type = typeSelect.value;
				this.selectedObject.description = propertyDescField
					? propertyDescField.value.trim()
					: "";
			}

			// Handle property renaming
			if (oldKey && newPropertyName && newPropertyName !== oldKey) {
				// Check if new name already exists
				if (
					this.schema.properties[newPropertyName] &&
					this.schema.properties[newPropertyName] !== this.selectedObject
				) {
					window.error(`Property "${newPropertyName}" already exists.`);
					propertyNameField.value = oldKey;
					return;
				}

				// Rename property
				Object.defineProperty(
					this.schema.properties,
					newPropertyName,
					Object.getOwnPropertyDescriptor(this.schema.properties, oldKey)
				);
				delete this.schema.properties[oldKey];
				this.currentPropertyKey = newPropertyName;
				this.selectedObject = this.schema.properties[newPropertyName];

				// Update required array if property was required
				if (this.schema.required) {
					const index = this.schema.required.indexOf(oldKey);
					if (index !== -1) {
						this.schema.required[index] = newPropertyName;
					}
				}
			} else if (oldKey) {
				this.currentPropertyKey = oldKey;
			}
		}
	}

	/**
	 * Update property list display (reactive update)
	 */
	updatePropertyList() {
		this.renderPropertiesList();
		this.updateRequiredFieldsSection();
		this.updateNavigator();
	}

	/**
	 * Update navigator with current properties
	 */
	updateNavigator() {
		const contentsCollapse = document.getElementById("contents-collapse");
		if (!contentsCollapse || !this.schema || !this.schema.properties) {
			return;
		}

		contentsCollapse.innerHTML = "";
		Object.keys(this.schema.properties).forEach((property) => {
			if (property && property.trim()) {
				this.addPropertyToNavigator(property);
			}
		});
	}

	/**
	 * Show general section, hide property editor
	 */
	showGeneralSection() {
		document.getElementById("schemaGeneralSection").classList.remove("d-none");
		document.getElementById("propertyEditorSection").classList.add("d-none");
		document.getElementById("generalLink").classList.add("active");
	}

	/**
	 * Show property editor section, hide general
	 */
	showPropertyEditorSection() {
		document.getElementById("schemaGeneralSection").classList.add("d-none");
		document.getElementById("propertyEditorSection").classList.remove("d-none");
		document.getElementById("generalLink").classList.remove("active");
	}

	/**
	 * Validate schema form
	 * @returns {boolean} True if schema is valid
	 */
	isValidSchema() {
		const form = this.form;
		let isValid = form.checkValidity();

		if (isValid && this.schema && this.schema.properties) {
			Object.keys(this.schema.properties).forEach((property) => {
				if (property === "" || !property.trim()) {
					window.error(
						"Invalid property name. Property names cannot be empty."
					);
					isValid = false;
				}
			});
		}

		if (!isValid) {
			form.classList.add("was-validated");
		}

		return isValid;
	}

	/**
	 * Save schema (create or update)
	 */
	async saveSchema() {
		// Save current editor values first
		this.saveCurrentEditorValues();

		if (!this.isValidSchema()) {
			return;
		}

		if (!this.schema) {
			window.error("No schema data to save.");
			return;
		}

		// Validate required fields
		if (!this.schema["$id"] || !this.schema["$id"].trim()) {
			window.error("Schema ID is required.");
			document.getElementById("nameTxt").focus();
			return;
		}

		if (!this.schema.title || !this.schema.title.trim()) {
			window.error("Schema title is required.");
			document.getElementById("titleTxt").focus();
			return;
		}

		// Update required fields from checkboxes
		this.updateRequiredFields();

		// Ensure schema has required structure
		if (!this.schema["$schema"]) {
			this.schema["$schema"] = "http://json-schema.org/draft-07/schema#";
		}
		if (!this.schema.type) {
			this.schema.type = "object";
		}

		this.isSaving = true;
		this.setSaveButtonState(true);

		try {
			let savedSchema;
			const schemaId = this.schema["$id"];

			if (this.isNewSchema || !this.originalSchemaId) {
				savedSchema = await SchemaService.create(this.schema);
				window.success(
					`Schema "${savedSchema.title || schemaId}" created successfully.`
				);
			} else {
				await SchemaService.update(this.originalSchemaId, this.schema);
				window.success(
					`Schema "${this.schema.title || schemaId}" updated successfully.`
				);
				savedSchema = this.schema;
			}

			this.isNewSchema = false;
			this.originalSchemaId = savedSchema["$id"];

			this.goBack();
		} catch (error) {
			console.error("Error saving schema:", error);
			window.error(
				`Failed to save schema: ${error.message || "Unknown error"}`
			);
		} finally {
			this.isSaving = false;
			this.setSaveButtonState(false);
		}
	}

	/**
	 * Update required fields from checkboxes
	 */
	updateRequiredFields() {
		const checkboxes = document.querySelectorAll(
			"#requiredChoices input[type='checkbox']"
		);
		const required = [];
		checkboxes.forEach((checkbox) => {
			if (checkbox.checked) {
				const propertyName = checkbox.value;
				if (propertyName && this.schema.properties[propertyName]) {
					required.push(propertyName);
				}
			}
		});
		this.schema.required = required.length > 0 ? required : undefined;
	}

	/**
	 * Set save button loading state
	 * @param {boolean} isLoading - Whether button is in loading state
	 */
	setSaveButtonState(isLoading) {
		const saveButton = document.querySelector(".fa-save");
		if (saveButton) {
			const buttonParent = saveButton.parentElement;
			if (isLoading) {
				buttonParent.disabled = true;
				saveButton.classList.add("fa-spinner", "fa-spin");
			} else {
				buttonParent.disabled = false;
				saveButton.classList.remove("fa-spinner", "fa-spin");
			}
		}
	}

	/**
	 * Add a new property to the schema
	 */
	addNewProperty() {
		if (!this.schema) {
			this.schema = {
				$id: "",
				title: "",
				description: "",
				$schema: "http://json-schema.org/draft-07/schema#",
				type: "object",
				properties: {},
			};
		}

		if (!this.schema.properties) {
			this.schema.properties = {};
		}

		const tempId = `property_${Date.now()}_${this.propertyCounter++}`;
		const newProperty = {
			title: "New Property",
			type: "string",
			description: "",
		};

		this.schema.properties[tempId] = newProperty;
		this.currentPropertyKey = tempId;
		this.renderPropertiesList();
		this.setEditor(newProperty);
		this.showPropertyEditorSection();

		// Update property name field
		const nameField = document.getElementById("propertyNameTxt");
		if (nameField) {
			nameField.value = tempId;
			setTimeout(() => nameField.focus(), 100);
		}
	}

	/**
	 * Create a new schema
	 */
	createSchema() {
		this.schema = {
			$id: "",
			title: "",
			description: "",
			$schema: "http://json-schema.org/draft-07/schema#",
			type: "object",
			properties: {},
		};
		this.isNewSchema = true;
		this.originalSchemaId = null;
		this.propertyCounter = 0;
		this.currentPropertyKey = null;
		this.setSchema();
	}

	/**
	 * Fork (copy) an existing schema
	 * @param {Object} _schema - Schema to fork
	 */
	forkSchema(_schema) {
		if (!_schema) {
			window.error("No schema selected to fork.");
			return;
		}

		this.schema = {
			$ref: _schema["$id"],
			$id: "",
			title: "",
			description: "",
			$schema: "http://json-schema.org/draft-07/schema#",
			type: "object",
			properties: {},
		};
		this.isNewSchema = true;
		this.originalSchemaId = null;
		this.propertyCounter = 0;
		this.currentPropertyKey = null;
		this.setSchema();
	}

	/**
	 * Set schema for editing
	 * @param {string} _schemaId - Schema ID to load (optional)
	 */
	async setSchema(_schemaId) {
		document.querySelector("i.fa-bezier-curve").classList.add("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.remove("d-none");
		this.container.parentElement
			.querySelector(".dropdown")
			.classList.add("d-none");

		this.oldChildNodes = [];
		while (this.container.firstChild) {
			this.oldChildNodes.push(
				this.container.removeChild(this.container.firstChild)
			);
		}

		document
			.querySelector(".fa-code-branch")
			.parentElement.parentElement.classList.add("d-none");
		document
			.querySelector(".fa-save")
			.parentElement.parentElement.classList.remove("d-none");

		this.container.appendChild(this.schemaManager);
		document.getElementById("nameTxt").focus();

		// Reset listener flags since DOM is recreated
		this.propertiesListListenerAttached = false;
		this.modalSetupComplete = false;

		// Re-setup listeners after DOM is added
		this.setupPropertyFieldListeners();
		this.setupDeleteModal();

		if (_schemaId) {
			try {
				this.schema = await SchemaService.get(_schemaId);
				this.originalSchemaId = _schemaId;
				this.isNewSchema = false;
				this.currentPropertyKey = null;
				this.setEditor(this.schema);
				this.renderPropertiesList();
				this.prepareNavigator();
			} catch (error) {
				console.error("Error loading schema:", error);
				window.error("Failed to load schema. Please try again.");
				this.goBack();
			}
		} else {
			this.setEditor(this.schema);
			this.renderPropertiesList();
			this.prepareNavigator();
		}
	}

	/**
	 * Go back to list view
	 */
	goBack() {
		if (this.container.contains(this.schemaManager)) {
			this.container.removeChild(this.schemaManager);
		}
		this.oldChildNodes.forEach((child) => {
			this.container.appendChild(child);
		});

		this.schema = null;
		this.selectedObject = null;
		this.isNewSchema = false;
		this.originalSchemaId = null;
		this.propertyCounter = 0;
		this.currentPropertyKey = null;
		this.propertiesListListenerAttached = false;
		this.propertyToDelete = null;
		this.modalSetupComplete = false;

		if (this.propertyChangeTimeout) {
			clearTimeout(this.propertyChangeTimeout);
		}

		if (this.caller && this.caller.render) {
			this.caller.render();
		}
	}

	/**
	 * Get current editor values and update schema (legacy method, now uses saveCurrentEditorValues)
	 */
	getEditorValue() {
		this.saveCurrentEditorValues();
	}

	/**
	 * Set editor to display specific object (schema or property)
	 * @param {Object} _input - Object to edit
	 */
	setEditor(_input) {
		if (!_input || !this.schema) {
			return;
		}

		// Save previous values before switching
		this.saveCurrentEditorValues();

		if (_input === this.schema) {
			// Editing schema itself
			this.showGeneralSection();
			const nameField = document.getElementById("nameTxt");
			const titleField = document.getElementById("titleTxt");
			const descField = document.getElementById("descriptionTxt");

			if (nameField) nameField.value = _input["$id"] || "";
			if (titleField) titleField.value = _input.title || "";
			if (descField) descField.value = _input.description || "";
			this.selectedObject = this.schema;
			this.currentPropertyKey = null;
		} else {
			// Editing a property
			this.showPropertyEditorSection();
			const propertyName = Object.keys(this.schema.properties).find(
				(key) => this.schema.properties[key] === _input
			);

			if (propertyName) {
				this.currentPropertyKey = propertyName;
				const nameField = document.getElementById("propertyNameTxt");
				const titleField = document.getElementById("propertyTitleTxt");
				const typeSelect = document.getElementById("typeSelect");
				const descField = document.getElementById("propertyDescriptionTxt");
				const errorDiv = document.getElementById("propertyNameError");

				if (nameField) nameField.value = propertyName;
				if (titleField) titleField.value = _input.title || "";
				if (typeSelect) typeSelect.value = _input.type || "string";
				if (descField) descField.value = _input.description || "";
				if (errorDiv) errorDiv.classList.add("d-none");

				this.selectedObject = _input;
			}
		}
	}

	/**
	 * Render properties list
	 */
	renderPropertiesList() {
		const propertiesList = document.getElementById("propertiesList");
		const noPropertiesMessage = document.getElementById("noPropertiesMessage");
		const propertyCount = document.getElementById("propertyCount");

		if (!this.schema || !this.schema.properties) {
			this.schema = this.schema || {
				$id: "",
				title: "",
				$schema: "http://json-schema.org/draft-07/schema#",
				type: "object",
				properties: {},
			};
		}

		const properties = this.schema.properties || {};
		const propertyKeys = Object.keys(properties).filter(
			(key) => key && key.trim()
		);

		if (propertyCount) {
			propertyCount.textContent = propertyKeys.length;
		}

		if (propertyKeys.length === 0) {
			if (propertiesList) propertiesList.innerHTML = "";
			if (noPropertiesMessage) noPropertiesMessage.classList.remove("d-none");
			this.updateRequiredFieldsSection();
			return;
		}

		if (noPropertiesMessage) noPropertiesMessage.classList.add("d-none");
		if (propertiesList) {
			propertiesList.innerHTML = propertyKeys
				.map((propName) => {
					const prop = properties[propName];
					const title = prop.title || propName;
					const type = prop.type || "string";
					const typeBadge = this.getTypeBadge(type);
					const isSelected = this.currentPropertyKey === propName;

					return `
					<div class="list-group-item" 
						data-property-name="${propName}">
						<div class="d-flex w-100 justify-content-between align-items-center">
							<div class="flex-grow-1">
								<h6 class="mb-1">
									${title}
									${typeBadge}
									${isSelected ? '<i class="fas fa-edit ms-2 text-primary"></i>' : ""}
								</h6>
								<small class="text-muted">
									<code>${propName}</code>
									${
										prop.description
											? ` • ${prop.description.substring(0, 50)}${
													prop.description.length > 50 ? "..." : ""
											  }`
											: ""
									}
								</small>
							</div>
							<div class="btn-group btn-group-sm ms-2" role="group">
								<button type="button" class="btn ${
									isSelected ? "btn-primary" : "btn-outline-primary"
								} edit-property-btn" 
									data-property-name="${propName}" title="Edit property">
									<i class="fas fa-edit"></i>
								</button>
								<button type="button" class="btn btn-outline-danger delete-property-btn" 
									data-property-name="${propName}" title="Delete property">
									<i class="fas fa-trash"></i>
								</button>
							</div>
						</div>
					</div>
				`;
				})
				.join("");

			// Attach event listeners using event delegation (only once)
			if (!this.propertiesListListenerAttached) {
				propertiesList.addEventListener("click", (e) => {
					const editBtn = e.target.closest(".edit-property-btn");
					const deleteBtn = e.target.closest(".delete-property-btn");
					const listItem = e.target.closest(".list-group-item");

					// Handle delete button click
					if (deleteBtn) {
						e.stopPropagation();
						e.preventDefault();
						const propName = deleteBtn.getAttribute("data-property-name");
						if (propName) {
							this.showDeletePropertyModal(propName);
						}
					}
					// Handle edit button or list item click
					else if (editBtn || listItem) {
						e.stopPropagation();
						e.preventDefault();
						const propName = (editBtn || listItem).getAttribute(
							"data-property-name"
						);
						if (propName) {
							const prop = this.schema.properties[propName];
							if (prop) {
								this.setEditor(prop);
							}
						}
					}
				});
				this.propertiesListListenerAttached = true;
			}
		}

		this.updateRequiredFieldsSection();
	}

	/**
	 * Get type badge HTML
	 * @param {string} type - Property type
	 * @returns {string} Badge HTML
	 */
	getTypeBadge(type) {
		const badges = {
			string: '<span class="badge bg-info ms-2">text</span>',
			integer: '<span class="badge bg-primary ms-2">integer</span>',
			number: '<span class="badge bg-success ms-2">number</span>',
			boolean: '<span class="badge bg-warning ms-2">boolean</span>',
			array: '<span class="badge bg-secondary ms-2">array</span>',
			object: '<span class="badge bg-dark ms-2">object</span>',
		};
		return (
			badges[type] ||
			'<span class="badge bg-secondary ms-2">' + type + "</span>"
		);
	}

	/**
	 * Show delete property confirmation modal
	 * @param {string} propertyName - Property name to delete
	 */
	showDeletePropertyModal(propertyName) {
		if (!propertyName || !this.schema || !this.schema.properties) {
			return;
		}

		const prop = this.schema.properties[propertyName];
		const propertyTitle = prop ? prop.title || propertyName : propertyName;

		// Store property to delete
		this.propertyToDelete = propertyName;

		// Update modal content
		const modal = document.getElementById("exampleModal");
		if (modal) {
			const modalTitle = modal.querySelector("#exampleModalLabel");
			const modalBody = modal.querySelector(".modal-body");
			const confirmButton = modal.querySelector(".btn-primary");

			if (modalTitle) {
				modalTitle.textContent = "Delete Property";
			}
			if (modalBody) {
				modalBody.innerHTML = `
					<p>Are you sure you want to delete the property <strong>"${propertyTitle}"</strong>?</p>
					<p class="text-muted small mb-0">
						<code>${propertyName}</code>
					</p>
					<p class="text-danger small mt-2 mb-0">
						<i class="fas fa-exclamation-triangle me-1"></i>
						This action cannot be undone.
					</p>
				`;
			}

			// Setup confirm button handler
			if (confirmButton) {
				// Remove existing listeners by cloning
				const newConfirmButton = confirmButton.cloneNode(true);
				confirmButton.parentNode.replaceChild(newConfirmButton, confirmButton);

				newConfirmButton.addEventListener("click", () => {
					if (this.propertyToDelete) {
						this.deleteProperty(this.propertyToDelete);
						this.propertyToDelete = null;
					}
					// eslint-disable-next-line no-undef
					bootstrap.Modal.getInstance(modal).hide();
				});
			}

			// Show modal
			// eslint-disable-next-line no-undef
			const bsModal = new bootstrap.Modal(modal);
			bsModal.show();
		}
	}

	/**
	 * Delete a property
	 * @param {string} propertyName - Property name to delete
	 */
	async deleteProperty(propertyName) {
		if (!propertyName || !propertyName.trim()) {
			window.error("Invalid property name for deletion.");
			return;
		}

		if (!this.schema) {
			window.error("No schema available. Cannot delete property.");
			return;
		}

		if (!this.schema.properties) {
			this.schema.properties = {};
		}

		if (!this.schema.properties[propertyName]) {
			window.warning(
				`Property "${propertyName}" does not exist or has already been deleted.`
			);
			// Refresh the list to sync state
			this.renderPropertiesList();
			return;
		}

		// Save current editor values before deletion (only if we're editing a property)
		if (this.selectedObject && this.selectedObject !== this.schema) {
			this.saveCurrentEditorValues();
		}

		// Store property title for success message
		const propertyTitle =
			this.schema.properties[propertyName].title || propertyName;

		// Delete the property
		delete this.schema.properties[propertyName];

		// Remove from required array if present
		if (this.schema.required && Array.isArray(this.schema.required)) {
			const index = this.schema.required.indexOf(propertyName);
			if (index !== -1) {
				this.schema.required.splice(index, 1);
			}
			if (this.schema.required.length === 0) {
				delete this.schema.required;
			}
		}

		// If we were editing this property, switch to schema general
		if (this.currentPropertyKey === propertyName) {
			this.currentPropertyKey = null;
			this.selectedObject = null;
			// Switch to general section
			this.showGeneralSection();
			this.setEditor(this.schema);
		}

		// Update UI immediately
		this.renderPropertiesList();
		this.updateRequiredFieldsSection();
		this.updateNavigator();

		window.success(`Property "${propertyTitle}" deleted successfully.`);

		// If this is an existing schema (not new), save and reload from API
		if (!this.isNewSchema && this.originalSchemaId) {
			// Auto-save the schema after property deletion, then reload
			await this.autoSaveAndReloadSchema();
		}
	}

	/**
	 * Auto-save schema after property deletion and reload from API
	 */
	async autoSaveAndReloadSchema() {
		if (!this.schema || !this.originalSchemaId) {
			return;
		}

		// Save current editor values
		this.saveCurrentEditorValues();

		// Update required fields
		this.updateRequiredFields();

		// Ensure schema has required structure
		if (!this.schema["$schema"]) {
			this.schema["$schema"] = "http://json-schema.org/draft-07/schema#";
		}
		if (!this.schema.type) {
			this.schema.type = "object";
		}

		try {
			// Save the schema
			await SchemaService.update(this.originalSchemaId, this.schema);

			// Reload the schema from API to get fresh data
			const reloadedSchema = await SchemaService.get(this.originalSchemaId);
			if (reloadedSchema) {
				this.schema = reloadedSchema;

				// Update UI with fresh data
				this.renderPropertiesList();
				this.updateRequiredFieldsSection();
				this.updateNavigator();

				// If we were on general section, refresh it
				if (this.selectedObject === this.schema) {
					this.setEditor(this.schema);
				}
			}
		} catch (error) {
			console.error(
				"Error auto-saving/reloading schema after property deletion:",
				error
			);
			window.error("Failed to save changes. Please save manually.");
		}
	}

	/**
	 * Update required fields section
	 */
	updateRequiredFieldsSection() {
		const requiredChoices = document.getElementById("requiredChoices");
		if (!requiredChoices || !this.schema || !this.schema.properties) {
			return;
		}

		const properties = this.schema.properties;
		const propertyKeys = Object.keys(properties).filter(
			(key) => key && key.trim()
		);

		if (propertyKeys.length === 0) {
			requiredChoices.innerHTML =
				'<div class="col-12"><small class="text-muted">Add properties first to mark them as required</small></div>';
			return;
		}

		requiredChoices.innerHTML = propertyKeys
			.map((propName) => {
				const prop = properties[propName];
				const title = prop.title || propName;
				const isRequired =
					this.schema.required && this.schema.required.includes(propName);
				const checkboxId = `required-${propName.replace(/[^a-zA-Z0-9]/g, "-")}`;

				return `
				<div class="col-md-6 col-lg-4">
					<div class="form-check">
						<input class="form-check-input" type="checkbox" 
							name="requiredFields" id="${checkboxId}" 
							value="${propName}" ${isRequired ? "checked" : ""}>
						<label class="form-check-label" for="${checkboxId}">
					${title}
					</label>
					</div>
				</div>
			`;
			})
			.join("");
	}

	/**
	 * Prepare navigator with schema properties
	 */
	prepareNavigator() {
		this.updateNavigator();
	}

	/**
	 * Add a property to the navigator
	 * @param {string} property - Property name
	 */
	addPropertyToNavigator(property) {
		if (
			!this.schema ||
			!this.schema.properties ||
			!this.schema.properties[property]
		) {
			return;
		}

		const prop = this.schema.properties[property];
		const title = prop.title || property;
		const contentsCollapse = document.getElementById("contents-collapse");

		if (contentsCollapse) {
			const li = document.createElement("li");
			const anchor = document.createElement("a");
			anchor.classList.add("d-inline-flex", "align-items-center", "rounded");
			anchor.innerHTML = title;
			anchor.href = "#";

			anchor.addEventListener("click", (e) => {
				e.preventDefault();
				this.setEditor(prop);
			});

			li.appendChild(anchor);
			contentsCollapse.appendChild(li);
		}
	}
}

export default Schema;
