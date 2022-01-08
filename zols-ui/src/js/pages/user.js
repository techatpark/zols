/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
		this.userForm = document.createElement("form");
		this.userForm.classList.add("row");
		this.userForm.classList.add("g-3");
		this.userForm.innerHTML = `<div class="col-md-6">
		<label for="inputEmail4" class="form-label">Email</label>
		<input type="email" class="form-control" id="inputEmail4">
	  </div>
	  <div class="col-md-6">
		<label for="inputPassword4" class="form-label">Password</label>
		<input type="password" class="form-control" id="inputPassword4">
	  </div>
	  <div class="col-12">
		<label for="inputAddress" class="form-label">Address</label>
		<input type="text" class="form-control" id="inputAddress" placeholder="1234 Main St">
	  </div>
	  <div class="col-12">
		<label for="inputAddress2" class="form-label">Address 2</label>
		<input type="text" class="form-control" id="inputAddress2" placeholder="Apartment, studio, or floor">
	  </div>
	  <div class="col-md-6">
		<label for="inputCity" class="form-label">City</label>
		<input type="text" class="form-control" id="inputCity">
	  </div>
	  <div class="col-md-4">
		<label for="inputState" class="form-label">State</label>
		<select id="inputState" class="form-select">
		  <option selected>Choose...</option>
		  <option>...</option>
		</select>
	  </div>
	  <div class="col-md-2">
		<label for="inputZip" class="form-label">Zip</label>
		<input type="text" class="form-control" id="inputZip">
	  </div>
	  <div class="col-12">
		<div class="form-check">
		  <input class="form-check-input" type="checkbox" id="gridCheck">
		  <label class="form-check-label" for="gridCheck">
			Check me out
		  </label>
		</div>
	  </div>
	  <div class="col-12">
		<button type="submit" class="btn btn-primary">Sign in</button>
	  </div>`;
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
			this.showUserForm();
			console.log("plus button clicked to add users");
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("Call to action for Edit");
			});
		});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("on-confirmation", () => {
				console.log("Call to action for Delete");
			});
		});
	}

	showUserForm() {
		this.oldChildNodes = [];
		while (this.container.firstChild) {
			this.oldChildNodes.push(
				this.container.removeChild(this.container.firstChild)
			);
		}
		this.container.appendChild(this.userForm);
	}
}
new UserScreen();
