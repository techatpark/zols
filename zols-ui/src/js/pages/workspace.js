import BasicScreen from "../core";

/*eslint no-undef: 0*/
class WorkspaceScreen extends BasicScreen {
	constructor() {
		super();
		console.log("I am code behind for users");
		this.workspaceForm = document.createElement("form");
		this.workspaceForm.innerHTML = `<form>
        <div class="form-group row mb-3 mt-3">
          <label for="inputName3" class="col-sm-1 col-form-label">Name</label>
          <div class="col-sm-5">
            <input type="text" class="form-control" id="inputName3" placeholder="Enter Name" required="" autofocus="">
          </div>
        </div>
        <div class="form-group row mb-3">
          <label for="inputDescription3" class="col-sm-1 col-form-label">Description</label>
          <div class="col-sm-5">
            <input type="text" class="form-control" id="inputDescription3" placeholder="Description">
          </div>
        </div>
        <button type="submit" class="btn btn-primary">Save</button>
      </form>`;
		this.container = document.getElementById("content");
		this.setUp();
	}

	setUp() {
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			document
				.querySelector("i.fa-save")
				.parentElement.parentElement.classList.remove("d-none");
			document
				.querySelector("i.fa-plus")
				.parentElement.parentElement.classList.add("d-none");
			this.showWorkspaceForm();
			console.log("plus button clicked to add workspace");
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("Call to action for Edit");
			});
		});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("Call to action for Delete");
			});
		});
	}

	showWorkspaceForm() {
		this.oldChildNodes = [];
		while (this.container.firstChild) {
			this.oldChildNodes.push(
				this.container.removeChild(this.container.firstChild)
			);
		}
		this.container.appendChild(this.workspaceForm);
	}
}
new WorkspaceScreen();
