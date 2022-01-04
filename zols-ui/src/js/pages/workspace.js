/*eslint no-undef: 0*/
class WorkspaceScreen {
	constructor() {
		console.log("I am code behind for workspace");
	}
}
new WorkspaceScreen();

const addUser = document
	.querySelector("i.fa-plus")
	.addEventListener("click", () => {
		console.log("plus button clicked to add users");
	});
