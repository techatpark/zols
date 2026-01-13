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
			// Always include Accept-Language (default to 'en') so network requests reflect current language
			if (window.LANGUAGE) {
				header["Accept-Language"] = window.LANGUAGE;
			} else {
				header["Accept-Language"] = "en";
			}

			console.log(
				"ApplicationHeader Accept-Language:",
				header["Accept-Language"]
			);

			return header;
		};

		this.handleLanguage();
		this.applyAcceptLanguageHeaders();
	}

	handleLanguage() {
		const selectedLanguage = document.getElementById("selectedLanguage");
		const languageOptions = document.querySelectorAll(".language-option");

		// 1️⃣ Default language = English
		let savedLanguage = localStorage.getItem("selectedLanguage");
		if (!savedLanguage) {
			savedLanguage = "en";
			localStorage.setItem("selectedLanguage", "en");
		}

		// 2️⃣ Update UI label
		const selectedOption = document.querySelector(
			`[data-langcode="${savedLanguage}"]`
		);
		if (selectedOption) {
			selectedLanguage.textContent = selectedOption.textContent;
		}

		// 3️⃣ Store globally
		window.LANGUAGE = savedLanguage;

		// 4️⃣ Apply header on load
		this.applyAcceptLanguageHeaders();

		// 5️⃣ Handle dropdown change
		languageOptions.forEach((option) => {
			option.addEventListener("click", (e) => {
				e.preventDefault();

				const lang = option.dataset.langcode; // en / ta
				const label = option.textContent;

				// Update UI
				selectedLanguage.textContent = label;

				// Save selection
				localStorage.setItem("selectedLanguage", lang);
				window.LANGUAGE = lang;

				// Apply to API headers
				this.applyAcceptLanguageHeaders();

				console.log("Language changed to:", lang);
			});
		});
	}

	applyAcceptLanguageHeaders() {
		const langCode = localStorage.getItem("selectedLanguage") || "en";

		console.log("Applying Accept-Language:", langCode);

		// Apply to Axios defaults and add interceptor so any request uses up-to-date header
		if (window.axios) {
			window.axios.defaults.headers.common["Accept-Language"] = langCode;

			// Eject previous interceptor if set
			if (
				window._acceptLanguageInterceptorId != null &&
				window.axios.interceptors &&
				window.axios.interceptors.request
			) {
				try {
					window.axios.interceptors.request.eject(
						window._acceptLanguageInterceptorId
					);
				} catch (e) {
					// ignore
				}
			}

			if (window.axios.interceptors && window.axios.interceptors.request) {
				window._acceptLanguageInterceptorId =
					window.axios.interceptors.request.use((config) => {
						config.headers = config.headers || {};
						config.headers["Accept-Language"] = langCode;
						// Some axios instances use headers.common
						if (config.headers.common) {
							config.headers.common["Accept-Language"] = langCode;
						}
						return config;
					});
			}
		}

		// Apply to fetch — guard against double-wrapping by keeping _originalFetch
		if (!window._originalFetch) {
			window._originalFetch = window.fetch.bind(window);
		}

		const originalFetch = window._originalFetch;
		window.fetch = function (input, options = {}) {
			options = options || {};
			// Support Headers instance
			if (options.headers instanceof Headers) {
				options.headers.set("Accept-Language", langCode);
			} else {
				options.headers = Object.assign({}, options.headers || {}, {
					"Accept-Language": langCode,
				});
			}
			return originalFetch(input, options);
		};

		// Notify other modules that might manage their own axios instances
		try {
			window.dispatchEvent(
				new CustomEvent("language-changed", { detail: { langCode } })
			);
		} catch (e) {
			console.warn("Could not dispatch language-changed event", e);
		}

		try {
			const main = document.querySelector("main");
			if (!main) return;

			const url = window.location.pathname + window.location.search;

			fetch(url, { cache: "no-store" })
				.then((res) => {
					if (!res.ok) throw new Error("Failed to reload page fragment");
					return res.text();
				})
				.then((html) => {
					const doc = new DOMParser().parseFromString(html, "text/html");
					const newMain = doc.querySelector("main");

					if (newMain) {
						main.innerHTML = newMain.innerHTML;
						window.dispatchEvent(
							new CustomEvent("content-updated", { detail: { langCode } })
						);
					}
				})
				.catch((err) =>
					console.warn(
						"Error reloading page fragment after language change:",
						err
					)
				);
		} catch (e) {
			console.warn("Error attempting fragment refresh on language change", e);
		}
	}
}

new Core();
