/*eslint no-undef: 0*/
class UserScreen {
	constructor() {
		console.log("I am code behind for users");
	}
}
new UserScreen();

const plusBtn = document.getElementsByClassName("fas fa-plus")[0];
plusBtn.addEventListener("click", function () {
	console.log("clicked plus button to add users");
});
