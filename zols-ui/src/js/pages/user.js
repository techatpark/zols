/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
		this.setup();
	}

	setup() {
		// Bind Call to actions

		document.querySelector("i.fa-plus").addEventListener("click", () => {
			console.log("I am call to action method for add user");
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((element) => {
			element.addEventListener("click", () => {
				console.log("I am call to action method for edit user");
			});
		});
	}
}
new UserScreen();
