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
	}
}
new Core();
