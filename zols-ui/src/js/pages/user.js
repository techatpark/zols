/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
	}
}
new UserScreen();

const addUser = document
	.querySelector("i.fa-plus")
	.addEventListener("click", () => {
		console.log("plus button clicked to add users");
	});

const edit = document.querySelectorAll("i.fa-pencil-alt").forEach((el) => {
	el.addEventListener("click", () => {
		console.log("edit all users");
	});
});
