/**
 * SchemaService - Service layer for schema API operations
 * Handles all communication with the /api/schema endpoint
 */
class SchemaService {
	/**
	 * Get authentication headers
	 * @returns {Object} Headers object with Authorization token
	 */
	static getAuthHeaders() {
		const auth = sessionStorage.auth ? JSON.parse(sessionStorage.auth) : null;
		const headers = {
			"Content-Type": "application/json",
		};
		if (auth && auth.accessToken) {
			headers.Authorization = `Bearer ${auth.accessToken}`;
		}
		return headers;
	}

	/**
	 * Handle API response and parse JSON
	 * @param {Response} response - Fetch response object
	 * @returns {Promise} Parsed JSON data
	 * @throws {Error} If response is not ok
	 */
	static async handleResponse(response) {
		// Get response text once
		const contentType = response.headers.get("content-type");
		const text = await response.text();

		if (!response.ok) {
			let errorMessage = `Error: ${response.status} ${response.statusText}`;
			if (text) {
				try {
					const errorJson = JSON.parse(text);
					errorMessage = errorJson.message || errorMessage;
				} catch (e) {
					errorMessage = text;
				}
			}
			throw new Error(errorMessage);
		}

		// If no content, return empty array
		if (!text || text.trim() === "") {
			return [];
		}

		// Try to parse JSON
		try {
			return JSON.parse(text);
		} catch (e) {
			// If it's supposed to be JSON but parsing failed
			if (contentType && contentType.includes("application/json")) {
				throw new Error("Invalid JSON response from server");
			}
			// If not JSON, return the text
			return text;
		}
	}

	/**
	 * List all schemas
	 * @returns {Promise<Array>} Array of schema objects
	 */
	static async list() {
		try {
			const response = await fetch("/api/schema", {
				method: "GET",
				headers: SchemaService.getAuthHeaders(),
			});
			return await SchemaService.handleResponse(response);
		} catch (error) {
			console.error("Error fetching schemas:", error);
			throw error;
		}
	}

	/**
	 * Get a specific schema by ID
	 * @param {string} id - Schema ID
	 * @param {boolean} enlarged - Whether to get enlarged schema
	 * @returns {Promise<Object>} Schema object
	 */
	static async get(id, enlarged = false) {
		try {
			const url = enlarged
				? `/api/schema/${id}?enlarged=true`
				: `/api/schema/${id}`;
			const response = await fetch(url, {
				method: "GET",
				headers: SchemaService.getAuthHeaders(),
			});
			return await SchemaService.handleResponse(response);
		} catch (error) {
			console.error(`Error fetching schema ${id}:`, error);
			throw error;
		}
	}

	/**
	 * Create a new schema
	 * @param {Object} schema - Schema object to create
	 * @returns {Promise<Object>} Created schema object
	 */
	static async create(schema) {
		try {
			const response = await fetch("/api/schema", {
				method: "POST",
				headers: SchemaService.getAuthHeaders(),
				body: JSON.stringify(schema),
			});
			return await SchemaService.handleResponse(response);
		} catch (error) {
			console.error("Error creating schema:", error);
			throw error;
		}
	}

	/**
	 * Update an existing schema
	 * @param {string} id - Schema ID to update
	 * @param {Object} schema - Updated schema object
	 * @returns {Promise<boolean>} True if update was successful
	 */
	static async update(id, schema) {
		try {
			const response = await fetch(`/api/schema/${id}`, {
				method: "PUT",
				headers: SchemaService.getAuthHeaders(),
				body: JSON.stringify(schema),
			});
			if (!response.ok) {
				const errorText = await response.text();
				let errorMessage = `Error: ${response.status} ${response.statusText}`;
				try {
					const errorJson = JSON.parse(errorText);
					errorMessage = errorJson.message || errorMessage;
				} catch (e) {
					if (errorText) {
						errorMessage = errorText;
					}
				}
				throw new Error(errorMessage);
			}
			return response.status === 202; // Accepted status
		} catch (error) {
			console.error(`Error updating schema ${id}:`, error);
			throw error;
		}
	}

	/**
	 * Delete a schema
	 * @param {string} id - Schema ID to delete
	 * @returns {Promise<boolean>} True if deletion was successful
	 */
	static async delete(id) {
		try {
			const response = await fetch(`/api/schema/${id}`, {
				method: "DELETE",
				headers: SchemaService.getAuthHeaders(),
			});
			if (!response.ok) {
				const errorText = await response.text();
				let errorMessage = `Error: ${response.status} ${response.statusText}`;
				try {
					const errorJson = JSON.parse(errorText);
					errorMessage = errorJson.message || errorMessage;
				} catch (e) {
					if (errorText) {
						errorMessage = errorText;
					}
				}
				throw new Error(errorMessage);
			}
			return response.status === 200; // OK status
		} catch (error) {
			console.error(`Error deleting schema ${id}:`, error);
			throw error;
		}
	}
}

export default SchemaService;
