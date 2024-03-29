/*eslint no-undef: 0*/
import Schema from "../components/Schema";

class SchemaScreen {
	constructor() {
		this.schemaEditor = new Schema(
			this,
			document.getElementById("schema-container")
		);

		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.addEventListener("click", () => {
				this.render();
			});

		document.querySelectorAll("i.fa-trash").forEach((el) => {
			el.addEventListener("on-confirmation", () => {
				fetch("/api/schema/" + this.schema["$id"], {
					method: "DELETE",
					headers: {
						Authorization:
							"Bearer " + JSON.parse(sessionStorage.auth).accessToken,
					},
				}).then(() => {
					let _self = this;
					this.schemas = this.schemas.filter(function (value) {
						return value !== _self.schema;
					});
					this.setSchemas(this.schemas);
				});
			});
		});

		document.querySelector("i.fa-code-branch").addEventListener("click", () => {
			this.schemaEditor.forkSchema(this.schema);
		});

		document.querySelector(".fa-plus").addEventListener("click", () => {
			this.schemaEditor.createSchema();
		});

		this.render();
	}

	render() {
		document.querySelector("i.fa-bezier-curve").classList.remove("d-none");
		document
			.querySelector("i.fa-arrow-alt-circle-left")
			.classList.add("d-none");

		fetch("/api/schema", {
			headers: {
				Authorization: "Bearer " + JSON.parse(sessionStorage.auth).accessToken,
			},
		})
			.then((response) => response.json())
			.then((schemas) => {
				this.setSchemas(schemas);
			})
			.catch(() => {
				this.setSchemas();
			});
	}

	setSchemas(schemas) {
		if (schemas && schemas.length !== 0) {
			document.querySelector("ul.call-to-action").classList.remove("d-none");
			this.schemas = schemas;
			this.rootSchemas = schemas.filter((schema) => !schema["$ref"]);
			this.setSelectedSchema(this.rootSchemas[0]);
		} else {
			document.querySelector("ul.call-to-action").classList.add("d-none");

			document.getElementById(
				"schema-container"
			).innerHTML = `<main class="p-5 m-5">

			<p class="lead">
			There are no schema available.
			</p>
			<p class="lead">
			  <a href="#" class="btn btn-primary fw-bold">Create New</a>
			</p>
		  </main>`;
			document.querySelector(".btn-primary").addEventListener("click", () => {
				document.querySelector("ul.call-to-action").classList.remove("d-none");
				this.schemaEditor.createSchema();
			});
		}
	}

	setSelectedSchema(schema) {
		document
			.getElementById("schemaList")
			.parentElement.classList.remove("d-none");

		document
			.querySelector(".fa-save")
			.parentElement.parentElement.classList.add("d-none");
		document
			.querySelector(".fa-code-branch")
			.parentElement.parentElement.classList.remove("d-none");

		const schemaList = document.getElementById("schemaList");
		document.getElementById("schemaMenuLink").innerHTML = schema.title;
		schemaList.innerHTML = "";
		this.schema = schema;
		for (var i = 0; i < this.rootSchemas.length; i++) {
			if (this.rootSchemas[i] != schema) {
				var li = document.createElement("li");
				var link = document.createElement("a");
				link.classList.add("dropdown-item");
				const cSchema = this.rootSchemas[i];
				link.innerHTML = cSchema.title;
				link.href = "javascript://";

				link.addEventListener("click", () => {
					this.setSelectedSchema(cSchema);
				});

				li.appendChild(link);
				schemaList.appendChild(li);
			}
		}
		this.drawChart();
	}

	drawChart() {
		document.getElementById("schema-container").innerHTML = "";
		const treeData = this.getTreeData(this.schema);
		const editor = this.schemaEditor;
		// Set the dimensions and margins of the diagram
		var margin = { top: 20, right: 90, bottom: 30, left: 90 },
			width = 960 - margin.left - margin.right,
			height = 500 - margin.top - margin.bottom;

		// append the svg object to the body of the page
		// appends a 'group' element to 'svg'
		// moves the 'group' element to the top left margin
		var svg = d3
			.select("#schema-container")
			.append("svg")
			.attr("width", width + margin.right + margin.left)
			.attr("height", height + margin.top + margin.bottom)
			.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		var i = 0,
			duration = 750,
			root;

		// declares a tree layout and assigns the size
		var treemap = d3.tree().size([height, width]);

		// Assigns parent, children, height, depth
		root = d3.hierarchy(treeData, function (d) {
			return d.children;
		});
		root.x0 = height / 2;
		root.y0 = 0;

		// Collapse after the second level
		if (root.children) {
			root.children.forEach(collapse);
		}

		update(root);

		// Collapse the node and all it's children
		function collapse(d) {
			if (d.children) {
				d._children = d.children;
				d._children.forEach(collapse);
				d.children = null;
			}
		}

		function update(source) {
			// Assigns the x and y position for the nodes
			var treeData = treemap(root);

			// Compute the new tree layout.
			var nodes = treeData.descendants(),
				links = treeData.descendants().slice(1);

			// Normalize for fixed-depth.
			nodes.forEach(function (d) {
				d.y = d.depth * 180;
			});

			// ****************** Nodes section ***************************

			// Update the nodes...
			var node = svg.selectAll("g.node").data(nodes, function (d) {
				return d.id || (d.id = ++i);
			});

			// Enter any new modes at the parent's previous position.
			var nodeEnter = node
				.enter()
				.append("g")
				.attr("class", "node")
				.attr("transform", function () {
					return "translate(" + source.y0 + "," + source.x0 + ")";
				})
				.on("click", click);

			// Add Circle for the nodes
			nodeEnter
				.append("circle")
				.attr("class", "node")
				.attr("r", 1e-6)
				.style("fill", function (d) {
					return d._children ? "lightsteelblue" : "#fff";
				});

			// Add labels for the nodes
			nodeEnter
				.append("text")
				.attr("dy", ".35em")
				.attr("x", function (d) {
					return d.children || d._children ? -13 : 13;
				})
				.attr("text-anchor", function (d) {
					return d.children || d._children ? "end" : "start";
				})
				.text(function (d) {
					return d.data.name;
				})
				.on("click", (d) => {
					editor.setSchema(d.data.id);
				});

			// UPDATE
			var nodeUpdate = nodeEnter.merge(node);

			// Transition to the proper position for the node
			nodeUpdate
				.transition()
				.duration(duration)
				.attr("transform", function (d) {
					return "translate(" + d.y + "," + d.x + ")";
				});

			// Update the node attributes and style
			nodeUpdate
				.select("circle.node")
				.attr("r", 10)
				.style("fill", function (d) {
					return d._children ? "lightsteelblue" : "#fff";
				})
				.attr("cursor", "pointer");

			// Remove any exiting nodes
			var nodeExit = node
				.exit()
				.transition()
				.duration(duration)
				.attr("transform", function () {
					return "translate(" + source.y + "," + source.x + ")";
				})
				.remove();

			// On exit reduce the node circles size to 0
			nodeExit.select("circle").attr("r", 1e-6);

			// On exit reduce the opacity of text labels
			nodeExit.select("text").style("fill-opacity", 1e-6);

			// ****************** links section ***************************

			// Update the links...
			var link = svg.selectAll("path.link").data(links, function (d) {
				return d.id;
			});

			// Enter any new links at the parent's previous position.
			var linkEnter = link
				.enter()
				.insert("path", "g")
				.attr("class", "link")
				.attr("d", function () {
					var o = { x: source.x0, y: source.y0 };
					return diagonal(o, o);
				});

			// UPDATE
			var linkUpdate = linkEnter.merge(link);

			// Transition back to the parent element position
			linkUpdate
				.transition()
				.duration(duration)
				.attr("d", function (d) {
					return diagonal(d, d.parent);
				});

			// Remove any exiting links
			link
				.exit()
				.transition()
				.duration(duration)
				.attr("d", function () {
					var o = { x: source.x, y: source.y };
					return diagonal(o, o);
				})
				.remove();

			// Store the old positions for transition.
			nodes.forEach(function (d) {
				d.x0 = d.x;
				d.y0 = d.y;
			});

			// Creates a curved (diagonal) path from parent to the child nodes
			function diagonal(s, d) {
				const path = `M ${s.y} ${s.x}
        C ${(s.y + d.y) / 2} ${s.x},
          ${(s.y + d.y) / 2} ${d.x},
          ${d.y} ${d.x}`;

				return path;
			}

			// Toggle children on click.
			function click(d) {
				if (d.children) {
					d._children = d.children;
					d.children = null;
				} else {
					d.children = d._children;
					d._children = null;
				}
				update(d);
			}
		}
	}

	getTreeData(_schema) {
		return {
			name: _schema.title,
			id: _schema["$id"],
			children: this.getChildren(_schema),
		};
	}

	getChildren(sschema) {
		return this.schemas
			.filter(function (schema) {
				return schema["$ref"] === sschema["$id"];
			})
			.map((schema) => {
				return this.getTreeData(schema);
			});
	}
}
new SchemaScreen();
