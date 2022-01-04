/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
		this.setUp();
	}

	setUp() {
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			console.log("plus button clicked to add users");
		});

		document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
			el.addEventListener("click", () => {
				console.log("edit all users");
			});
		});
	}
}
new UserScreen();
