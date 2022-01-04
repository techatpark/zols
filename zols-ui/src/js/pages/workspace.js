/*eslint no-undef: 0*/
class WorkspaceScreen {
	constructor() {
		console.log("I am code behind for workspace");
		this.setUp();
	}
	setUp() {
		document.querySelector("i.fa-plus").addEventListener("click", () => {
			console.log("plus button clicked to add users");
		});
	}
}
new WorkspaceScreen();
