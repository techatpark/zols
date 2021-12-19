class Schema {
	constructor(_container) {
		this.container = _container;

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
		<form>
  <div class="row mb-3">
    <label for="nameTxt" class="col-sm-2 col-form-label">Name</label>
    <div class="col-sm-10">
      <input class="form-control" id="nameTxt">
    </div>
  </div>

  <div data-for-property="true" class="row mb-3">
  <label for="typeSelect" class="col-sm-2 col-form-label">Type</label>
  <div class="col-sm-10">
  <select class="form-select" id="typeSelect" aria-label="Default select example">
  <option value="string">Text</option>
  <option value="integer">Integer</option>
  <option value="number">Float</option>
  <option value="boolean">Boolean</option>
  <option value="array">Array</option>
</select>
  </div>
	</div>

  <div class="row mb-3">
    <label for="titleTxt" class="col-sm-2 col-form-label">Title</label>
    <div class="col-sm-10">
      <input  class="form-control" id="titleTxt">
    </div>
  </div>

  <div class="row mb-3">
    <label for="descriptionTxt" class="col-sm-2 col-form-label">Description</label>
    <div class="col-sm-10">
	<textarea class="form-control" id="descriptionTxt"></textarea>
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

  
</form>
		`;

		this.schemaManager.appendChild(this.scemaNavigator);
		this.schemaManager.appendChild(this.scemaEditor);

		this.scemaNavigator
			.querySelector("#generalLink")
			.addEventListener("click", () => {
				this.setEditor(this.schema);
			});
	}

	setSchema(_schemaId) {
		fetch("/api/schema/" + _schemaId)
			.then((response) => response.json())
			.then((schema) => {
				this.schema = schema;
				this.setEditor(this.schema);
				this.prepareNavigator();
			})
			.catch((err) => {
				console.error(err);
			});

		this.container.innerHTML = "";
		this.container.appendChild(this.schemaManager);
	}

	setEditor(_input) {
		document.getElementById("titleTxt").value = _input.title
			? _input.title
			: "";
		document.getElementById("descriptionTxt").value = _input.description
			? _input.description
			: "";

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

	prepareNavigator() {
		document.getElementById("contents-collapse").innerHTML = "";

		document.getElementById("requiredChoices").innerHTML = "";

		Object.keys(this.schema.properties).forEach((property) => {
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

			anchor.addEventListener("click", () => {
				this.setEditor(this.schema.properties[property]);
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
		});
	}
}

export default Schema;
