/*eslint no-undef: 0*/
class DataWarehouseScreen {
	constructor() {
		this.userForm = document.createElement("form");
		this.userForm.classList.add("row");
		this.userForm.classList.add("g-3");
		this.userForm.innerHTML = `<div class="mb-3">
		<label for="exampleInputEmail1" class="form-label">Email address</label>
		<input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp">
		<div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>
	  </div>
	  <div class="mb-3">
		<label for="exampleInputPassword1" class="form-label">Password</label>
		<input class="form-control" id="exampleInputPassword1">
	  </div>
	  <div class="mb-3">
	  <label for="exampleInputRole" class="form-label">Roles</label>
	  	<div class="form-check">
  			<input class="form-check-input" type="checkbox" value="" id="flexCheckDefault">
  			<label class="form-check-label" for="flexCheckDefault">
    			Admin
  			</label>
		</div>
		<div class="form-check">
			<input class="form-check-input" type="checkbox" value="" id="flexCheckDefault">
			<label class="form-check-label" for="flexCheckDefault">
		  		Moderator
			</label>
  		</div>
		<div class="form-check">
			<input class="form-check-input" type="checkbox" value="" id="flexCheckChecked" checked>
			<label class="form-check-label" for="flexCheckChecked">
		  		User
			</label>
		</div>
	  </div>
	 `;
		this.container = document.getElementById("content");
		this.setUp();
	}

	setUp() {
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
		fetch("/api/data/" + schema["$id"], {
			headers: {
				Authorization: "Bearer " + JSON.parse(sessionStorage.auth).accessToken,
			},
		})
			.then((response) => response.json())
			.then((dataPage) => {
				this.showDataPage(dataPage);
			})
			.catch(() => {
				console.log("No Data");
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
				html += `<th scope="col">${this.schema.properties[propertyName].title}</th>`;
			}
		});

		html += `<th scope="col">${
			this.schema.properties[this.schema.label].title
		}</th>
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

			html += `<td scope="col">${data[this.schema.label]}</td>
			<td><i data-bs-toggle="modal" data-bs-target="#exampleModal" class="fas fa-trash px-2"></i><i class="fas fa-pencil-alt"></i></td>
	</tr>`;
		});

		document.getElementById("table-content").innerHTML = html;
	}
}
new DataWarehouseScreen();
