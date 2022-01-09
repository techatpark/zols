/*eslint no-undef: 0*/
class WorkspaceScreen {
	constructor() {
		console.log("I am code behind for users");
		this.workspaceForm = document.createElement("form");
		this.workspaceForm.innerHTML = `<div class="mb-3">
		<label for="workspaceFormControlInput1" class="form-label">Name</label>
		<input type="text" class="form-control" id="workspaceFormControlInput1" placeholder="">
	  </div>
	  <div class="mb-3">
		<label for="workspaceFormControlInput1" class="form-label">Description</label>
		<textarea class="form-control" id="workspaceFormControlInput1" rows="3"></textarea>
	  </div>`;
		this.container = document.getElementById("content");
		this.setUp();
	}

	setUp() {
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			this.showWorkspaceForm();
		});

		document.querySelector("i.fa-save").addEventListener("click", () => {
			this.showWorkspaces();
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				this.showWorkspaceForm();
			});
		});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("on-confirmation", () => {
				console.log("Call to action for Delete");
			});
		});
	}

	showWorkspaceForm() {
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
		this.container.appendChild(this.workspaceForm);
	}

	showWorkspaces() {
		document
			.querySelector("i.fa-plus")
			.parentElement.parentElement.classList.remove("d-none");
		document
			.querySelector("i.fa-save")
			.parentElement.parentElement.classList.add("d-none");
		// Navigate Back to Listing Screen
		this.container.removeChild(this.container.lastChild);
		this.oldChildNodes.forEach((child) => {
			this.container.appendChild(child);
		});
	}
}
new WorkspaceScreen();
