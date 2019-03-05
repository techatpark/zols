import React, {Component} from 'react';
import * as d3 from "d3";
import { Link } from "react-router-dom";
import Api from "../../api";
export default class SchemaList extends Component {

  state = {
      schemas: []
  };
  drawChart(treeData) {
    d3.selectAll("#svg > *").remove();
    console.log("dddd", treeData);

    // ************** Generate the tree diagram	 *****************
    var margin = {top: 20, right: 120, bottom: 20, left: 120},
      width = 960 - margin.right - margin.left,
      height = 500 - margin.top - margin.bottom;

    var i = 0,
      duration = 750,
      root;

    var tree = d3.layout.tree()
      .size([height, width]);

    var diagonal = d3.svg.diagonal()
      .projection(function(d) { return [d.y, d.x]; });

    var svg = d3.select("#svg").append("svg")
      .attr("width", width + margin.right + margin.left)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    root = treeData[0];
    root.x0 = height / 2;
    root.y0 = 0;

    update(root);

    d3.select(window.self.frameElement).style("height", "500px");

    function update(source) {

      // Compute the new tree layout.
      var nodes = tree.nodes(root).reverse(),
        links = tree.links(nodes);

      // Normalize for fixed-depth.
      nodes.forEach(function(d) { d.y = d.depth * 180; });

      // Update the nodes…
      var node = svg.selectAll("g.node")
        .data(nodes, function(d) { return d.id || (d.id = ++i); });

      // Enter any new nodes at the parent's previous position.
      var nodeEnter = node.enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; });


      nodeEnter.append("circle")
        .attr("r", 1e-6)
        .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; })
        .on("click", click)
        .on("dblclick",dnclickOnText);

      nodeEnter.append("text")
        .attr("x", function(d) { return d.children || d._children ? -13 : 13; })
        .attr("dy", ".35em")
        .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
        .text(function(d) { return d.name; })
        .style("fill-opacity", 1e-6)
        .on("click", clickOnText);

      // Transition nodes to their new position.
      var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

      nodeUpdate.select("circle")
        .attr("r", 10)
        .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

      nodeUpdate.select("text")
        .style("fill-opacity", 1);

      // Transition exiting nodes to the parent's new position.
      var nodeExit = node.exit().transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
        .remove();

      nodeExit.select("circle")
        .attr("r", 1e-6);

      nodeExit.select("text")
        .style("fill-opacity", 1e-6);

      // Update the links…
      var link = svg.selectAll("path.link")
        .data(links, function(d) { return d.target.id; });

      // Enter any new links at the parent's previous position.
      link.enter().insert("path", "g")
        .attr("class", "link")
        .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
        });

      // Transition links to their new position.
      link.transition()
        .duration(duration)
        .attr("d", diagonal);

      // Transition exiting nodes to the parent's new position.
      link.exit().transition()
        .duration(duration)
        .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
        })
        .remove();

      // Stash the old positions for transition.
      nodes.forEach(function(d) {
      d.x0 = d.x;
      d.y0 = d.y;
      });
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

    function dnclickOnText(d) {
      window.location = '/schema/'+d.id + "/_addNew";
    }

    // Edit on click.
    function clickOnText(d) {
      window.location = '/schema/'+d.id;
    }
  }

  getTreeChildren = (sschema) => {
    return this.state.schemas.filter(function(schema) {
      return schema["$ref"] === sschema["$id"];
    }).map(schema => {
      let treeObj = {};
      treeObj.id = schema["$id"];
      treeObj.name = schema.title;
      treeObj.parent = null;
      treeObj.children = this.getTreeChildren(schema);
      return treeObj;
    });

  }

  getTreeData = () => {

    var e = document.getElementById("schemas_select");
    var sschema = e.options[e.selectedIndex].value;

    var newArray = this.state.schemas.filter(function(schema) {
      return schema["$id"] === sschema;
    }).map(schema => {
      let treeObj = {};
      treeObj.id = schema["$id"];
      treeObj.name = schema.title;
      treeObj.parent = null;
      treeObj.children = this.getTreeChildren(schema);
      return treeObj;
    });


    return newArray;
  };

  onSchemaSelection = () => this.drawChart(this.getTreeData());

  componentDidMount = async () => {
    const { data } = await Api.get(`/schema`);
    var sel = document.getElementById('schemas_select');
    if(data.length === 0) {
      sel.style.display = "none";
    }else {
      for(var i = 0; i < data.length; i++) {
          if(data[i]["$ref"] === undefined) {
            var opt = document.createElement('option');
            opt.innerHTML = data[i].title;
            opt.value = data[i]["$id"];
            sel.appendChild(opt);
          }
      }
    }

    this.setState({ schemas: data });
    if(data.length !== 0) {
      this.drawChart(this.getTreeData());
    }

  };
  render() {
    return <React.Fragment>
        <div className="row">
          <div className="col-md-4">
          <select className="form-control" id="schemas_select" onChange={this.onSchemaSelection}>

          </select>
          </div>
          <div className="col-md-8 text-right">
          <Link to={`/schema/_addNew`}>
            Create
          </Link>
          </div>
        </div>
        <div className="row">
        <div className="col-md-12">
        <div id="svg"></div>
        </div>
        </div>
      </React.Fragment>
    ;
  }
}
