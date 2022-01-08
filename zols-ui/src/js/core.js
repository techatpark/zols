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
		var myModalEl = document.getElementById("exampleModal");
		let cRelatedTarget = null;
		myModalEl.addEventListener("shown.bs.modal", function (event) {
			cRelatedTarget = event.relatedTarget;
			myModalEl
				.querySelector(".btn-primary")
				.addEventListener("click", (event) => {
					if (!event.calledFlag) {
						event.calledFlag = true;
						const confirmationEvent = new Event("on-confirmation");
						cRelatedTarget.dispatchEvent(confirmationEvent, {
							bubbles: false,
							detail: { text: () => "textarea.value" },
						});
						// eslint-disable-next-line no-undef
						bootstrap.Modal.getInstance(myModalEl).hide();
					}
				});
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
