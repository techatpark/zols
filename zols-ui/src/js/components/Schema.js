class Schema {
	constructor(_caller, _container) {
		this.container = _container;
		this.caller = _caller;

		this.schemaManager = document.createElement("div");
		this.schemaManager.classList.add("row");

		this.scemaNavigator = document.createElement("div");
		this.scemaNavigator.classList.add("col-3");
		this.scemaNavigator.innerHTML = `
		<aside class="bd-aside sticky-xl-top align-self-start mb-3 mb-xl-5 px-2">
          <nav class="small" id="toc">
            <ul class="list-unstyled">
			  <li class="my-2"><a id="generalLink" class="d-inline-flex align-items-center rounded">General</a></li>
              <li class="my-2">
                <button class="btn d-inline-flex align-items-center collapsed" 
				data-bs-toggle="collapse" aria-expanded="false" data-bs-target="#contents-collapse" aria-controls="contents-collapse">
				Fields
				</button>
                <ul class="list-unstyled ps-3" id="contents-collapse">
                  <li><a class="d-inline-flex align-items-center rounded" href="#typography">Typography</a></li>
                  <li><a class="d-inline-flex align-items-center rounded" href="#images">Images</a></li>
                  <li><a class="d-inline-flex align-items-center rounded" href="#tables">Tables</a></li>
                  <li><a class="d-inline-flex align-items-center rounded" href="#figures">Figures</a></li>
                </ul>
              </li>
            </ul>
          </nav>
        </aside>
		`;

		this.scemaEditor = document.createElement("div");
		this.scemaEditor.classList.add("col-9");
		this.scemaEditor.innerHTML = `
		<form class="needs-validation" id="editForm" novalidate>
		<div class="row mb-3">
			<label for="nameTxt" class="col-sm-2 col-form-label">Name</label>
			<div class="col-sm-5">
			<input type="text" class="form-control" id="nameTxt" autocomplete="off" required>
			<div class="invalid-feedback">
          Please choose a Name.
        </div>
			</div>
		</div>

  <div data-for-property="true" class="row mb-3">
  <label for="typeSelect" class="col-sm-2 col-form-label">Type</label>
  <div class="col-sm-5">
  <select class="form-select" id="typeSelect" aria-label="Default select example" required>
  <option value="string">Text</option>
  <option value="integer">Integer</option>
  <option value="number">Float</option>
  <option value="boolean">Boolean</option>
</select>
<div class="invalid-feedback">
          Please choose a Type.
        </div>
  </div>
	</div>

  <div class="row mb-3">
    <label for="titleTxt" class="col-sm-2 col-form-label">Title</label>
    <div class="col-sm-5">
      <input  class="form-control" id="titleTxt" autocomplete="off" required>
	  <div class="invalid-feedback">
          Please choose a Title.
        </div>
    </div>
  </div>

  <div class="row mb-3">
    <label for="descriptionTxt" class="col-sm-2 col-form-label">Description</label>
    <div class="col-sm-8">
	<textarea class="form-control" id="descriptionTxt" autocomplete="off" required></textarea>
	<div class="invalid-feedback">
          Please provide a Description.
        </div>
    </div>
  </div>
  
  <fieldset data-for-schema="true" class="row mb-3">
    <legend class="col-form-label col-sm-2 pt-0">Required</legend>
    <div id="requiredChoices" class="col-sm-10">
      <div class="form-check">
        <input class="form-check-input" type="checkbox" name="gridRadios" id="gridRadios1" value="option1" checked>
        <label class="form-check-label" for="gridRadios1">
          First radio
        </label>
      </div>
      <div class="form-check">
        <input class="form-check-input" type="checkbox" name="gridRadios" id="gridRadios2" value="option2">
        <label class="form-check-label" for="gridRadios2">
          Second radio
        </label>
      </div>
    </div>
  </fieldset>

  <button id="submitBtn" class="btn btn-primary d-none" type="submit">Submit form</button>
</form>
		`;

		this.schemaManager.appendChild(this.scemaNavigator);
		this.schemaManager.appendChild(this.scemaEditor);

		const _self = this;
		const form = this.scemaEditor.querySelector("#editForm");
		form.addEventListener("submit", function (event) {
			event.preventDefault();
			event.stopPropagation();
			form.classList.add("was-validated");

			if (_self.selectedObject && document.getElementById("titleTxt")) {
				_self.selectedObject.title = document.getElementById("titleTxt").value;
				_self.selectedObject.description =
					document.getElementById("descriptionTxt").value;

				if (document.getElementById("typeSelect").offsetParent !== null) {
					_self.selectedObject.type =
						document.getElementById("typeSelect").value;
				}

				if (_self.selectedObject !== _self.schema) {
					if (_self.schema.properties) {
						Object.keys(_self.schema.properties).forEach((propName) => {
							if (_self.schema.properties[propName] === _self.selectedObject) {
								let new_key = document.getElementById("nameTxt").value;
								let old_key = propName;

								if (new_key !== old_key) {
									Object.defineProperty(
										_self.schema.properties,
										new_key, // modify old key
										// fetch description from object
										Object.getOwnPropertyDescriptor(
											_self.schema.properties,
											old_key
										)
									);
									delete _self.schema.properties[old_key];
								}
							}
						});
					}
				} else {
					_self.selectedObject["$id"] =
						document.getElementById("nameTxt").value;
				}
			}
		});

		this.form = form;

		this.scemaNavigator
			.querySelector("#generalLink")
			.addEventListener("click", () => {
				this.setEditor(this.schema);
			});
		document.querySelector(".fa-save").addEventListener("click", () => {
			if (this.isValidSchema()) {
				fetch("/api/schema/", {
					method: "POST",
					body: JSON.stringify(this.schema),
					headers: {
						"content-type": "application/json",
						Authorization:
							"Bearer " + JSON.parse(sessionStorage.auth).accessToken,
					},
				})
					.then((response) => response.json())
					.then((schema) => {
						console.log(schema);
					})
					.catch((err) => {
						console.error(err);
					});

				this.goBack();

				document
					.querySelector(".fa-save")
					.parentElement.parentElement.classList.add("d-none");
				document
					.querySelector(".fa-code-branch")
					.parentElement.parentElement.classList.remove("d-none");
			}
		});
	}

	isValidSchema() {
		let isValid = document
			.getElementById("submitBtn")
			.parentElement.checkValidity();
		document.getElementById("submitBtn").click();
		if (isValid && this.schema.properties) {
			Object.keys(this.schema.properties).forEach((property) => {
				if (property === "") {
					document.getElementById("nameTxt").focus();
					// window.error("invalid property name");
					isValid = false;
				}
			});
		}

		return isValid;
	}

	createSchema() {
		if (this.container.firstChild === this.schemaManager) {
			if (this.isValidSchema()) {
				let property = {
					title: "New Property",
				};
				if (!this.schema.properties) {
					this.schema.properties = {
						"": property,
					};
				} else {
					this.schema.properties[""] = property;
				}
				this.setSchema();
				this.setEditor(property);
			}
		} else {
			this.schema = {
				$id: "",
				title: "",
				$schema: "http://json-schema.org/draft-07/schema#",
				type: "object",
			};
			this.setSchema();
		}
	}

	forkSchema(_schema) {
		this.schema = {
			$ref: _schema["$id"],
			$id: "",
			title: "",
		};
		this.setSchema();
	}

	setSchema(_schemaId) {
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

		if (_schemaId) {
			fetch("/api/schema/" + _schemaId, {
				headers: {
					"content-type": "application/json",
					Authorization:
						"Bearer " + JSON.parse(sessionStorage.auth).accessToken,
				},
			})
				.then((response) => response.json())
				.then((schema) => {
					this.schema = schema;
					this.setEditor(this.schema);
					this.prepareNavigator();
				})
				.catch((err) => {
					console.error(err);
				});
		} else {
			this.setEditor(this.schema);
			this.prepareNavigator();
		}
	}

	saveSchema() {}

	goBack() {
		// Navigate Back to Listing Screen
		this.container.removeChild(this.schemaManager);
		this.oldChildNodes.forEach((child) => {
			this.container.appendChild(child);
		});

		this.caller.render();
	}

	setEditor(_input) {
		if (!this.selectedObject || this.isValidSchema()) {
			document.getElementById("titleTxt").value = _input.title
				? _input.title
				: "";
			document.getElementById("descriptionTxt").value = _input.description
				? _input.description
				: "";

			this.selectedObject = _input;

			if (_input === this.schema) {
				document
					.querySelectorAll('[data-for-schema="true"]')
					.forEach((elelemnt) => {
						elelemnt.classList.remove("d-none");
					});
				document
					.querySelectorAll('[data-for-property="true"]')
					.forEach((elelemnt) => {
						elelemnt.classList.add("d-none");
					});
				document.getElementById("nameTxt").value = _input["$id"];
			} else {
				document
					.querySelectorAll('[data-for-schema="true"]')
					.forEach((elelemnt) => {
						elelemnt.classList.add("d-none");
					});
				document
					.querySelectorAll('[data-for-property="true"]')
					.forEach((elelemnt) => {
						elelemnt.classList.remove("d-none");
					});

				this.selectedProperty = _input;

				var key = Object.keys(this.schema.properties).filter(
					(propName) => this.schema.properties[propName] === _input
				);
				document.getElementById("nameTxt").value = key;

				document.getElementById("typeSelect").value = _input.type;
			}
		}
	}

	prepareNavigator() {
		document.getElementById("contents-collapse").innerHTML = "";

		document.getElementById("requiredChoices").innerHTML = "";

		if (this.schema.properties) {
			Object.keys(this.schema.properties).forEach((property) => {
				this.addProperty(property);
			});
		}
	}

	addProperty(property) {
		const li = document.createElement("li");
		const anchor = document.createElement("a");
		anchor.classList.add("d-inline-flex");
		anchor.classList.add("align-items-center");
		anchor.classList.add("rounded");

		let title = this.schema.properties[property].title;
		if (!title) {
			title = property;
		}

		anchor.innerHTML = title;

		const selectedProperty = this.schema.properties[property];
		this.selectedObject = selectedProperty;

		anchor.addEventListener("click", () => {
			this.setEditor(selectedProperty);
		});
		li.appendChild(anchor);

		document.getElementById("contents-collapse").appendChild(li);

		const requiredChoice = document.createElement("div");
		requiredChoice.classList.add("form-check");
		requiredChoice.innerHTML = `
				<input class="form-check-input" type="checkbox" name="gridRadios" id="gridRadios1" value="option1" ${
					this.schema.required && this.schema.required.includes(property)
						? "checked"
						: ""
				}>
					<label class="form-check-label" for="gridRadios1">
					${title}
					</label>
				`;
		document.getElementById("requiredChoices").appendChild(requiredChoice);
	}
}

export default Schema;
