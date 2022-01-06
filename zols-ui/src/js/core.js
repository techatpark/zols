class BasicScreen {
	constructor() {
		if (sessionStorage.auth) {
			document.querySelector(".logout").addEventListener("click", () => {
				delete sessionStorage.auth;
				window.location.href = "/";
			});
		} else {
			window.location.href = "/";
		}
		console.log("Core Screen");
	}
}
export default BasicScreen;
