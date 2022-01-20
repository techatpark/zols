/*eslint no-undef: 0*/
import { JSONPath } from "../../../node_modules/jsonpath-plus/dist/index-browser-esm.js";

class DataWarehouseScreen {
	constructor() {
		this.userForm = document.createElement("div");
		this.userForm.classList.add("row");
		this.userForm.classList.add("g-3");
		this.container = document.getElementById("content");
		this.setUp();
	}

	setUp() {
		console.log(JSONPath({ path: "a", json: { a: 1 } })[0]);
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			this.showUserForm();
		});

		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.addEventListener("click", () => {
				this.showUsers();
			});

		document.querySelector("i.fa-save").addEventListener("click", () => {
			this.showUsers();
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				this.showUserForm();
			});
		});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("on-confirmation", () => {
				console.log("Call to action for Delete");
			});
		});

		fetch("/api/schema", {
			headers: {
				Authorization: "Bearer " + JSON.parse(sessionStorage.auth).accessToken,
			},
		})
			.then((response) => response.json())
			.then((schemas) => {
				console.log(schemas);
				this.setSchemas(schemas);
			})
			.catch(() => {
				this.setSchemas();
			});
	}

	setSchemas(schemas) {
		if (schemas && schemas.length !== 0) {
			this.schemas = schemas;
			this.rootSchemas = schemas.filter((schema) => !schema["$ref"]);
			this.setSelectedSchema(this.rootSchemas[0]);
		} else {
			document.getElementById("content").innerHTML = `<main class="p-5 m-5">

			<p class="lead">
			There are no schema available.
			</p>
			<p class="lead">
			  <a href="#" class="btn btn-primary fw-bold">Create New</a>
			</p>
		  </main>`;
			document.querySelector(".btn-primary").addEventListener("click", () => {
				document.querySelector("ul.call-to-action").classList.remove("d-none");
				this.schemaEditor.createSchema();
			});
		}
	}

	setSelectedSchema(schema) {
		this.schema = schema;
		fetch("/api/data/" + schema["$id"] + "", {
			headers: {
				Authorization: "Bearer " + JSON.parse(sessionStorage.auth).accessToken,
			},
		})
			.then((response) => response.json())
			.then((dataPage) => {
				this.showDataPage(dataPage);
			})
			.catch((e) => {
				console.log("No Data", e);
			});

		const schemaList = document.getElementById("schemaList");
		document.getElementById("schemaMenuLink").innerHTML = schema.title;
		schemaList.innerHTML = "";
		this.schema = schema;
		for (var i = 0; i < this.rootSchemas.length; i++) {
			if (this.rootSchemas[i] != schema) {
				var li = document.createElement("li");
				var link = document.createElement("a");
				link.classList.add("dropdown-item");
				const cSchema = this.rootSchemas[i];
				link.innerHTML = cSchema.title;
				link.href = "javascript://";

				link.addEventListener("click", () => {
					this.setSelectedSchema(cSchema);
				});

				li.appendChild(link);
				schemaList.appendChild(li);
			}
		}
	}

	showUserForm() {
		document.querySelector("i.fa-warehouse").classList.add("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.remove("d-none");

		document
			.querySelector("i.fa-save")
			.parentElement.parentElement.classList.remove("d-none");
		document
			.querySelector("i.fa-plus")
			.parentElement.parentElement.classList.add("d-none");

		const schema = {
			$id: "product",
			$schema: "http://json-schema.org/draft-07/schema#",
			description: "A product from Acme's catalog",
			title: "Product",
			label: "name",
			type: "object",
			properties: {
				id: {
					description: "The unique identifier for a product",
					type: "integer",
					title: "Id",
				},
				name: {
					description: "Name of the product",
					type: "string",
					title: "Name",
				},
				price: {
					description: "The price for a product",
					type: "number",
					title: "Price",
				},
				availablity: {
					description: "The availablity for a product",
					type: "boolean",
					title: "is Available",
				},
			},
			required: ["id"],
			ids: ["id"],
		};

		// Also, you can define the form behavior on submission, e.g.:
		const submitCallback = (rootFormElement) => {
			// Show the resulting JSON instance in your page.
			document.getElementById("json-result").innerText = JSON.stringify(
				rootFormElement.getInstance(),
				null,
				2
			);
			// (For testing purposes, return false to prevent automatic redirect.)
			return false;
		};

		// Finally, get your form...
		const jsonSchemaForm = JsonSchemaForms.build(schema, submitCallback);

		this.userForm.appendChild(jsonSchemaForm);

		this.oldChildNodes = [];
		while (this.container.firstChild) {
			this.oldChildNodes.push(
				this.container.removeChild(this.container.firstChild)
			);
		}
		this.container.appendChild(this.userForm);
	}

	showDataPage(dataPage) {
		console.log(dataPage);
		document.querySelector("i.fa-warehouse").classList.remove("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.add("d-none");

		document
			.querySelector("i.fa-plus")
			.parentElement.parentElement.classList.remove("d-none");
		document
			.querySelector("i.fa-save")
			.parentElement.parentElement.classList.add("d-none");

		if (dataPage.first) {
			document
				.querySelector("i.fa-fast-backward")
				.parentElement.classList.add("disabled");
		} else {
			document
				.querySelector("i.fa-fast-backward")
				.parentElement.classList.remove("disabled");
		}

		if (dataPage.last) {
			document
				.querySelector("i.fa-fast-forward")
				.parentElement.classList.add("disabled");
		} else {
			document
				.querySelector("i.fa-fast-forward")
				.parentElement.classList.remove("disabled");
		}

		if (this.oldChildNodes) {
			// Navigate Back to Listing Screen
			this.container.removeChild(this.container.lastChild);
			this.oldChildNodes.forEach((child) => {
				this.container.appendChild(child);
			});
		}

		let html = `<tr>

		`;

		const idProperties = [];

		Object.keys(this.schema.properties).forEach((propertyName) => {
			if (this.schema["ids"].includes(propertyName)) {
				idProperties.push(this.schema.properties[propertyName]);
				this.schema.properties[propertyName].id = propertyName;
				html += `<th scope="col">ID</th>`;
			}
		});

		if (this.schema.label) {
			html += `<th scope="col">Label</th>
	`;
		}
		html += `
<th scope="col" style="width:100px"></th>
</tr>`;

		document.getElementById("table-header").innerHTML = html;

		html = ``;

		dataPage.content.forEach((data) => {
			html += `<tr>

		`;

			idProperties.forEach((property) => {
				html += `<td scope="col">${data[property.id]}</td>`;
			});
			if (this.schema.label) {
				html += `<td scope="col">${
					JSONPath({ path: this.schema.label, json: data })[0]
				}</td>`;
			}

			html += `
	<td><i data-bs-toggle="modal" data-bs-target="#exampleModal" class="fas fa-trash px-2"></i><i class="fas fa-pencil-alt"></i></td>
</tr>`;
		});

		document.getElementById("table-content").innerHTML = html;
	}
}
new DataWarehouseScreen();
