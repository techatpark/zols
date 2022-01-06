import BasicScreen from "../core";

/*eslint no-undef: 0*/
class WorkspaceScreen extends BasicScreen {
	constructor() {
		super();
		console.log("I am code behind for workspace");
		this.setUp();
	}

	setUp() {
		// Bind Call to actions

		document.querySelector("i.fa-plus").addEventListener("click", () => {
			console.log("plus button clicked to add users");
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("I am call to action method for edit user");
			});
		});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("I am call to action method for delete user");
			});
		});
	}
}
new WorkspaceScreen();
