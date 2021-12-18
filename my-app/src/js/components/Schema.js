class Schema {
	constructor(_container) {
		this.container = _container;
	}

	setSchema(_schemaId) {
		this.container.innerHTML = _schemaId;
	}
}

export default Schema;
