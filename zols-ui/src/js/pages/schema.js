/*eslint no-undef: 0*/
import Schema from "../components/Schema";
import SchemaService from "../services/SchemaService";

/**
 * SchemaScreen - Main screen for managing schemas
 * Displays schemas in a card-based grid layout with CRUD operations
 */
class SchemaScreen {
	constructor() {
		this.schemaEditor = new Schema(
			this,
			document.getElementById("schema-container")
		);
		this.schemas = [];
		this.currentSchema = null;
		this.isLoading = false;
		this.isDeleting = false;
		this.eventListenersInitialized = false;
		this.isRendering = false;
		this.containerListenerAttached = false;

		this.initializeEventListeners();
		this.render();
	}

	/**
	 * Initialize all event listeners (only once)
	 */
	initializeEventListeners() {
		if (this.eventListenersInitialized) {
			return;
		}

		// Back button - return to list view
		const backButton = document.querySelector("i.fa-arrow-alt-circle-left");
		if (backButton) {
			backButton.addEventListener("click", () => {
				if (!this.isRendering) {
					this.render();
				}
			});
		}

		// Create new schema button
		const createButton = document.querySelector(".fa-plus");
		if (createButton) {
			createButton.addEventListener("click", () => {
				this.schemaEditor.createSchema();
			});
		}

		// Fork schema button (only visible in edit mode)
		const forkButton = document.querySelector("i.fa-code-branch");
		if (forkButton) {
			forkButton.addEventListener("click", () => {
				if (this.currentSchema) {
					this.schemaEditor.forkSchema(this.currentSchema);
				}
			});
		}

		// Header delete button (when editing a schema)
		const headerDeleteButton = document.querySelector("i.fa-trash");
		if (headerDeleteButton && !headerDeleteButton.dataset.listenerAttached) {
			headerDeleteButton.dataset.listenerAttached = "true";
			headerDeleteButton.addEventListener("click", (e) => {
				e.preventDefault();
				e.stopPropagation();
				if (this.currentSchema) {
					this.showDeleteSchemaModal(this.currentSchema);
				} else {
					window.warning("No schema selected for deletion.");
				}
			});
		}

		// Setup delete modal (for initialization)
		this.setupDeleteModal();

		this.eventListenersInitialized = true;
	}

	/**
	 * Show delete schema confirmation modal
	 * @param {Object} schema - Schema object to delete
	 */
	showDeleteSchemaModal(schema) {
		if (!schema || !schema["$id"]) {
			window.error("Invalid schema selected for deletion.");
			return;
		}

		// Store schema to delete
		this.schemaToDelete = schema;

		// Update modal content
		const modal = document.getElementById("exampleModal");
		if (modal) {
			const modalTitle = modal.querySelector("#exampleModalLabel");
			const modalBody = modal.querySelector(".modal-body");
			const confirmButton = modal.querySelector(".btn-primary");

			if (modalTitle) {
				modalTitle.textContent = "Delete Schema";
			}
			if (modalBody) {
				const schemaTitle = schema.title || schema["$id"];
				modalBody.innerHTML = `
					<p>Are you sure you want to delete the schema <strong>"${schemaTitle}"</strong>?</p>
					<p class="text-muted small mb-0">
						<code>${schema["$id"]}</code>
					</p>
					<p class="text-danger small mt-2 mb-0">
						<i class="fas fa-exclamation-triangle me-1"></i>
						This action cannot be undone. All data using this schema may be affected.
					</p>
				`;
			}

			// Setup confirm button handler - remove old and add new
			if (confirmButton) {
				// Remove existing listeners by cloning
				const newConfirmButton = confirmButton.cloneNode(true);
				confirmButton.parentNode.replaceChild(newConfirmButton, confirmButton);

				newConfirmButton.addEventListener("click", async () => {
					if (this.schemaToDelete) {
						await this.deleteSchema(this.schemaToDelete);
						this.schemaToDelete = null;
					}
					// eslint-disable-next-line no-undef
					const bsModalInstance = bootstrap.Modal.getInstance(modal);
					if (bsModalInstance) {
						bsModalInstance.hide();
					}
				});
			}

			// Show modal
			// eslint-disable-next-line no-undef
			const bsModal = new bootstrap.Modal(modal);
			bsModal.show();
		}
	}

	/**
	 * Setup delete confirmation modal (only once)
	 */
	setupDeleteModal() {
		if (this.modalSetupComplete) {
			return;
		}

		// Modal is now handled dynamically in showDeleteSchemaModal
		// This method is kept for backward compatibility
		this.modalSetupComplete = true;
	}

	/**
	 * Render the schema list view
	 */
	async render() {
		// Prevent multiple simultaneous renders
		if (this.isRendering) {
			return;
		}

		this.isRendering = true;
		this.showListView();
		this.showLoadingState();

		// Ensure container event listener is attached
		this.attachCardEventListeners();

		try {
			const schemas = await SchemaService.list();
			this.schemas = Array.isArray(schemas) ? schemas : [];
			if (this.schemas.length > 0) {
				this.renderSchemaCards();
			} else {
				this.renderEmptyState();
			}
		} catch (error) {
			console.error("Error loading schemas:", error);
			window.error("Failed to load schemas. Please try again.");
			this.renderEmptyState();
		} finally {
			this.hideLoadingState();
			this.isRendering = false;
		}
	}

	/**
	 * Show list view (hide editor view)
	 */
	showListView() {
		document.querySelector("i.fa-bezier-curve").classList.remove("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.add("d-none");
		document.querySelector("ul.call-to-action").classList.remove("d-none");
		this.currentSchema = null;
	}

	/**
	 * Show loading state
	 */
	showLoadingState() {
		this.isLoading = true;
		const container = document.getElementById("schema-container");
		container.innerHTML = `
			<div class="d-flex justify-content-center align-items-center" style="min-height: 400px;">
				<div class="spinner-border text-primary" role="status">
					<span class="visually-hidden">Loading...</span>
				</div>
			</div>
		`;
	}

	/**
	 * Hide loading state
	 */
	hideLoadingState() {
		this.isLoading = false;
	}

	/**
	 * Render schema cards in a grid layout
	 */
	renderSchemaCards() {
		const container = document.getElementById("schema-container");

		if (!this.schemas || this.schemas.length === 0) {
			this.renderEmptyState();
			return;
		}

		// Filter root schemas (those without $ref)
		const rootSchemas = this.schemas.filter(
			(schema) => !schema["$ref"] || !schema["$ref"].trim()
		);

		if (rootSchemas.length === 0) {
			this.renderEmptyState();
			return;
		}

		container.innerHTML = `
			<div class="row g-4 mt-2">
				${rootSchemas.map((schema) => this.createSchemaCard(schema)).join("")}
			</div>
		`;

		// Event listeners are already attached via event delegation
		// No need to call attachCardEventListeners() again
	}

	/**
	 * Create a schema card HTML
	 * @param {Object} schema - Schema object
	 * @returns {string} HTML string for the card
	 */
	createSchemaCard(schema) {
		const schemaId = schema["$id"] || "N/A";
		const title = schema.title || schemaId;
		const description = schema.description || "No description available";
		const type = schema.type || "object";

		return `
			<div class="col-md-4 col-lg-3" data-schema-id="${schemaId}">
				<div class="card h-100 shadow-sm">
					<div class="card-body">
						<h5 class="card-title text-truncate" title="${title}">${title}</h5>
						<p class="card-text text-muted small" style="min-height: 40px;">
							${description.substring(0, 100)}${description.length > 100 ? "..." : ""}
						</p>
						<div class="mb-2">
							<small class="text-muted">
								<strong>ID:</strong> <code class="small">${schemaId}</code>
							</small>
						</div>
						<div class="mb-2">
							<span class="badge bg-secondary">${type}</span>
						</div>
					</div>
					<div class="card-footer bg-transparent border-top-0">
						<div class="btn-group w-100" role="group">
							<button class="btn btn-sm btn-outline-primary view-schema" data-schema-id="${schemaId}" title="View/Edit">
								<i class="fas fa-eye"></i> View
							</button>
							<button class="btn btn-sm btn-outline-danger delete-schema" data-schema-id="${schemaId}" title="Delete">
								<i class="fas fa-trash"></i>
							</button>
						</div>
					</div>
				</div>
			</div>
		`;
	}

	/**
	 * Attach event listeners to schema cards using event delegation
	 */
	attachCardEventListeners() {
		// Use event delegation on the container to avoid re-attaching listeners
		const container = document.getElementById("schema-container");
		if (!container) {
			return;
		}

		// Only attach the listener once
		if (!this.containerListenerAttached) {
			container.addEventListener("click", (e) => {
				const target = e.target.closest(
					".view-schema, .delete-schema, .create-first-schema"
				);
				if (!target) {
					return;
				}

				if (target.classList.contains("view-schema")) {
					e.preventDefault();
					e.stopPropagation();
					const schemaId = target.getAttribute("data-schema-id");
					if (schemaId && !this.isRendering) {
						this.viewSchema(schemaId);
					}
				} else if (target.classList.contains("delete-schema")) {
					e.preventDefault();
					e.stopPropagation();
					const schemaId = target.getAttribute("data-schema-id");
					if (schemaId) {
						const schema = this.schemas.find((s) => s["$id"] === schemaId);
						if (schema) {
							this.showDeleteSchemaModal(schema);
						} else {
							window.error("Schema not found for deletion.");
						}
					}
				} else if (target.classList.contains("create-first-schema")) {
					e.preventDefault();
					this.schemaEditor.createSchema();
				}
			});
			this.containerListenerAttached = true;
		}
	}

	/**
	 * View/Edit a specific schema
	 * @param {string} schemaId - Schema ID to view
	 */
	async viewSchema(schemaId) {
		try {
			const schema = await SchemaService.get(schemaId);
			this.currentSchema = schema;
			this.schemaEditor.setSchema(schemaId);
		} catch (error) {
			console.error("Error loading schema:", error);
			window.error("Failed to load schema. Please try again.");
		}
	}

	/**
	 * Delete a schema
	 * @param {Object} schema - Schema object to delete
	 */
	async deleteSchema(schema) {
		if (!schema || !schema["$id"]) {
			window.error("Invalid schema selected for deletion.");
			return;
		}

		const schemaId = schema["$id"];
		const schemaTitle = schema.title || schemaId;

		// Prevent multiple simultaneous deletions
		if (this.isDeleting) {
			window.warning("A deletion is already in progress. Please wait.");
			return;
		}

		this.isDeleting = true;

		try {
			const deleted = await SchemaService.delete(schemaId);

			if (deleted) {
				window.success(`Schema "${schemaTitle}" deleted successfully.`);

				// Clear current schema if it was the one being deleted
				if (this.currentSchema && this.currentSchema["$id"] === schemaId) {
					this.currentSchema = null;
				}

				// Clear schemaToDelete
				this.schemaToDelete = null;

				// Refresh the list with fresh data from API
				await this.render();
			} else {
				window.warning(
					`Schema "${schemaTitle}" could not be deleted. It may not exist or may have already been deleted.`
				);
				// Refresh the list anyway to sync state with API
				await this.render();
			}
		} catch (error) {
			console.error("Error deleting schema:", error);
			const errorMessage = error.message || "Unknown error";

			// Provide more specific error messages
			if (errorMessage.includes("404") || errorMessage.includes("not found")) {
				window.warning(
					`Schema "${schemaTitle}" was not found. It may have already been deleted.`
				);
				// Refresh to sync state with API
				await this.render();
			} else if (
				errorMessage.includes("409") ||
				errorMessage.includes("conflict")
			) {
				window.error(
					`Cannot delete schema "${schemaTitle}". It may be referenced by other schemas or data.`
				);
			} else {
				window.error(
					`Failed to delete schema "${schemaTitle}": ${errorMessage}`
				);
			}
		} finally {
			this.isDeleting = false;
			this.schemaToDelete = null;
		}
	}

	/**
	 * Render empty state when no schemas exist
	 */
	renderEmptyState() {
		const container = document.getElementById("schema-container");
		if (!container) {
			return;
		}

		container.innerHTML = `
			<div class="text-center p-5 m-5">
				<i class="fas fa-database fa-4x text-muted mb-4"></i>
				<p class="lead text-muted">No schemas available</p>
				<p class="text-muted mb-4">Get started by creating your first schema</p>
				<button class="btn btn-primary btn-lg create-first-schema">
					<i class="fas fa-plus"></i> Create New Schema
				</button>
			</div>
		`;

		// Event listener is already attached via attachCardEventListeners
		// No need to add it again here
	}

	/**
	 * Set schemas (for backward compatibility with Schema component)
	 * @param {Array} schemas - Array of schema objects
	 */
	setSchemas(schemas) {
		this.schemas = schemas || [];
		if (schemas && schemas.length > 0) {
			this.renderSchemaCards();
		} else {
			this.renderEmptyState();
		}
	}
}

new SchemaScreen();
