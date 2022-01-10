/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
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
	}

	showUserForm() {
		document.querySelector("i.fa-users").classList.add("d-none");
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

	showUsers() {
		document.querySelector("i.fa-users").classList.remove("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.add("d-none");

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
new UserScreen();
