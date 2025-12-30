/*eslint no-undef: 0*/
class Core {
	constructor() {
		if (sessionStorage.auth) {
			document.querySelector(".logout").addEventListener("click", () => {
				delete sessionStorage.auth;
				window.location.href = "/";
			});
		} else {
			window.location.href = "/";
		}

		// Confirmation Modal pop up logic
		const myModalEl = document.getElementById("exampleModal");
		const confirmBtn = myModalEl.querySelector(".btn-primary");
		let cRelatedTarget = null;

		// When modal opens
		myModalEl.addEventListener("shown.bs.modal", (event) => {
			cRelatedTarget = event.relatedTarget || null;
		});

		// Confirm click handler (attach ONCE)
		confirmBtn.addEventListener("click", () => {
			const textarea = myModalEl.querySelector("textarea");

			const confirmationEvent = new CustomEvent("on-confirmation", {
				bubbles: false,
				detail: {
					text: () => (textarea ? textarea.value : ""),
				},
			});

			const target =
				cRelatedTarget && typeof cRelatedTarget.dispatchEvent === "function"
					? cRelatedTarget
					: myModalEl;

			try {
				target.dispatchEvent(confirmationEvent);
			} catch (e) {
				console.error("Error dispatching confirmation event:", e);
			}

			// Clear textarea AFTER delete
			if (textarea) textarea.value = "";

			bootstrap.Modal.getInstance(myModalEl).hide();
		});

		// Reset state when modal closes
		myModalEl.addEventListener("hidden.bs.modal", () => {
			cRelatedTarget = null;
		});

		const showStatus = (type, statusMessage) => {
			var delay = 2000;

			var toastConainerElement = document.getElementById("toast-container");
			toastConainerElement.innerHTML = `<div class="toast align-items-center  border-0" role="alert" aria-live="assertive" aria-atomic="true">
		  <div class="d-flex">
			<div class="toast-body text-${type}">
			  ${statusMessage}
			</div>
			<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
		  </div>
		  </div>`;
			// eslint-disable-next-line no-undef
			var toast = new bootstrap.Toast(toastConainerElement.firstElementChild, {
				delay: delay,
				animation: true,
			});
			toast.show();

			// setTimeout(() => toastElement.remove(), delay + 3000); // let a certain margin to allow the "hiding toast animation"
		};

		window.success = (statusMessage) => {
			showStatus("success", statusMessage);
		};

		window.error = (statusMessage) => {
			showStatus("danger", statusMessage);
		};

		window.warning = (statusMesaage) => {
			showStatus("warning", statusMesaage);
		};

		window.info = (statusMesaage) => {
			showStatus("info", statusMesaage);
		};

		window.ApplicationHeader = () => {
			const header = {
				"content-type": "application/json",
			};
			if (sessionStorage.auth) {
				header["Authorization"] =
					"Bearer " + JSON.parse(sessionStorage.auth).accessToken;
			}
			if (window.LANGUAGE != null && window.LANGUAGE !== "en") {
				header["Accept-Language"] = window.LANGUAGE;
			}

			console.log(window.LANGUAGE);

			return header;
		};

		this.handleLanguage();
		this.applyAcceptLanguageHeaders();
	}

	handleLanguage() {
		const selectedLanguage = document.getElementById("selectedLanguage");
		const languageOptions = document.querySelectorAll(".language-option");

		const savedLanguage = localStorage.getItem("selectedLanguage");
		if (savedLanguage) {
			selectedLanguage.textContent = document.querySelector(
				"[data-langcode='" + savedLanguage + "']"
			).textContent;
		}

		window.LANGUAGE = "en" === savedLanguage ? null : savedLanguage;

		languageOptions.forEach((option) => {
			option.addEventListener("click", (e) => {
				e.preventDefault();
				const lang = option.dataset.langcode;
				selectedLanguage.textContent = document.querySelector(
					"[data-langcode='" + lang + "']"
				).textContent;

				localStorage.setItem("selectedLanguage", lang);

				this.applyAcceptLanguageHeaders();
			});
		});
	}

	applyAcceptLanguageHeaders() {
		const savedLanguage = localStorage.getItem("selectedLanguage");

		const langCode = savedLanguage === "Tamil" ? "ta" : "en";

		if (window.axios) {
			window.axios.defaults.headers.common["Accept-Language"] = langCode;
		}

		const originalFetch = window.fetch;
		window.fetch = function (url, options = {}) {
			options.headers = options.headers || {};
			options.headers["Accept-Language"] = langCode;
			return originalFetch(url, options);
		};
	}
}
new Core();
