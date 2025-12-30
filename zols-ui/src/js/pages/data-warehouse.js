/*eslint no-undef: 0*/
import { JSONPath } from "../../../node_modules/jsonpath-plus/dist/index-browser-esm.js";

class DataWarehouseScreen {
	constructor() {
		// Set an option globally
		JSONEditor.defaults.options.theme = "bootstrap5";

		this.dataForm = document.createElement("div");
		this.dataForm.classList.add("row");
		this.dataForm.classList.add("g-3");
		this.container = document.getElementById("content");

		this.setUp();
	}

	setUp() {
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			this.showDataForm();
		});

		document
			.querySelector("i.fa-fast-forward")
			.addEventListener("click", () => {
				this.showNextPage();
			});

		document
			.querySelector("i.fa-fast-backward")
			.addEventListener("click", () => {
				this.showPreviousPage();
			});

		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.addEventListener("click", () => {
				this.showDataPage();
			});

		// Get the query string part of the current URL
		const queryString = window.location.search;

		// Create a URLSearchParams object
		const urlParams = new URLSearchParams(queryString);

		// Get a specific parameter by name
		this.id = urlParams.get("id");

		document.querySelector("i.fa-save").addEventListener("click", () => {
			const value = this.editor.getValue();

			if (this.isAdd) {
				fetch("/api/data/" + this.id, {
					method: "POST",
					headers: window.ApplicationHeader(),
					body: JSON.stringify(value),
				})
					.then(() => {
						this.showDataPage();
					})
					.catch(() => {
						console.error("No Schema found for " + this.id);
					});
			} else {
				let enpoint = "/api/data/" + this.id;
				this.schema.ids.forEach((id) => {
					enpoint += "/" + id + "/" + value[id];
				});

				fetch(enpoint, {
					method: "PUT",
					headers: window.ApplicationHeader(),
					body: JSON.stringify(value),
				})
					.then(() => {
						this.showDataPage();
					})
					.catch(() => {
						console.error("No Schema found for " + this.id);
					});
			}

			// this.showDataPage();
		});

		console.log(window.ApplicationHeader());

		fetch("/api/schema/" + this.id, {
			headers: window.ApplicationHeader(),
		})
			.then((response) => response.json())
			.then((schemas) => {
				this.setSelectedSchema(schemas);
			})
			.catch(() => {
				console.error("No Schema found for " + this.id);
			});
	}

	setSelectedSchema(schema) {
		this.schema = schema;

		this.showDataPage();

		const schemaList = document.getElementById("schemaList");
		document.getElementById("schemaMenuLink").innerHTML = schema.title;
		schemaList.innerHTML = "";

		if (this.rootSchemas.length === 1) {
			document
				.getElementById("schemaMenuLink")
				.classList.remove("dropdown-toggle");
			document.getElementById("schemaList").classList.remove("dropdown-menu");
		} else {
			document
				.getElementById("schemaMenuLink")
				.classList.add("dropdown-toggle");
			document.getElementById("schemaList").classList.add("dropdown-menu");
			for (var i = 0; i < this.rootSchemas.length; i++) {
				if (this.rootSchemas[i] !== schema) {
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
	}

	showDataForm(value) {
		if (value) {
			this.isAdd = false;
		} else {
			this.isAdd = true;
		}
		console.log("Show Data Form " + value);

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

		this.oldChildNodes = [];
		while (this.container.firstChild) {
			this.oldChildNodes.push(
				this.container.removeChild(this.container.firstChild)
			);
		}

		fetch("/api/schema/" + this.schema["$id"] + "?enlarged", {
			headers: window.ApplicationHeader(),
		})
			.then((response) => response.json())
			.then((enlargedSchema) => {
				this.dataForm.innerHTML = "";
				this.editor = new JSONEditor(this.dataForm, {
					schema: enlargedSchema,
					disable_collapse: true,
					disable_edit_json: true,
					disable_properties: true,
					no_additional_properties: true,
				});

				if (value) {
					this.editor.on("ready", () => {
						this.editor.setValue(value);
					});
				}
			})
			.catch((e) => {
				console.log(e);
			});

		this.container.appendChild(this.dataForm);
	}

	showNextPage() {
		this.showDataPage(this.pageNumber + 1);
	}

	showPreviousPage() {
		this.showDataPage(this.pageNumber - 1);
	}

	showDataPage(pageNumber) {
		let requestVariable = "";

		if (pageNumber) {
			this.pageNumber = pageNumber;
			requestVariable = "?size=3&page=" + pageNumber;
		} else {
			this.pageNumber = 0;
			requestVariable = "?size=3";
		}

		console.log(window.ApplicationHeader());

		fetch("/api/data/" + this.schema["$id"] + requestVariable, {
			headers: window.ApplicationHeader(),
		})
			.then((response) => response.json())
			.then((dataPage) => {
				this.dataPage = dataPage;
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

				const startingfrom = dataPage.number * dataPage.size + 1;
				document.getElementById("page-details").innerHTML = `${startingfrom}-${
					startingfrom + dataPage.numberOfElements - 1
				} of ${dataPage.totalElements}`;

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
				document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
					el.addEventListener("click", () => {
						const tr = el.parentElement.parentElement;
						const tbody = tr.parentElement;
						const selectedIndex = Array.prototype.indexOf.call(
							tbody.children,
							tr
						);
						this.showDataForm(this.dataPage.content[selectedIndex]);
					});
				});
				document.querySelectorAll("i.fa-trash").forEach((el) => {
					el.addEventListener("on-confirmation", () => {
						const tr = el.parentElement.parentElement;
						const tbody = tr.parentElement;
						const selectedIndex = Array.prototype.indexOf.call(
							tbody.children,
							tr
						);

						fetch(
							"/api/data" +
								this.getDataEndpoint(this.dataPage.content[selectedIndex]),
							{
								method: "DELETE",
								headers: window.ApplicationHeader(),
							}
						)
							.then(() => {
								this.showDataPage();
							})
							.catch((e) => {
								console.log("Delete failed ", e);
							});
					});
				});
			})
			.catch((e) => {
				console.log(e);
				document.getElementById("content").innerHTML = `<main class="p-5 m-5">

			<p class="lead">
			There are no ${this.schema.title} available.
			</p>
			<p class="lead">
			  <a href="#" class="btn btn-primary fw-bold">Create New</a>
			</p>
		  </main>`;
				document.querySelector(".btn-primary").addEventListener("click", () => {
					document
						.querySelector("ul.call-to-action")
						.classList.remove("d-none");
					this.showDataForm();
				});
			});
	}

	getDataEndpoint(data) {
		let ep = "/" + this.schema["$id"];

		Object.keys(this.schema.properties).forEach((propertyName) => {
			if (this.schema["ids"].includes(propertyName)) {
				ep += "/" + propertyName + `/` + data[propertyName];
			}
		});
		return ep;
	}
}
new DataWarehouseScreen();
