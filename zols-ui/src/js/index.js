class IndexScreen {
	constructor() {
		if (sessionStorage.auth) {
			window.location.href = "pages/home.html";
		}

		document.querySelector("#userName").focus();
		this.registerEvents();
	}

	registerEvents() {
		document
			.querySelector("form")
			.addEventListener("submit", (e) => this.login(e));
	}

	login(event) {
		event.preventDefault();
		let authRequest = {
			username: document.querySelector("#userName").value,
			password: document.querySelector("#password").value,
		};
		fetch("/api/auth/signin", {
			method: "POST",
			headers: {
				"content-type": "application/json",
			},
			body: JSON.stringify(authRequest),
		})
			.then((response) => {
				if (!response.ok) {
					throw Error(response.statusText);
				}
				return response.json();
			})
			.then((data) => {
				sessionStorage.auth = JSON.stringify(data);
				window.location.href = "pages/home.html";
			})
			.catch((err) => {
				document.querySelector(".invisible").classList.remove("invisible");
				console.error(err);
			});
	}
}
new IndexScreen();
