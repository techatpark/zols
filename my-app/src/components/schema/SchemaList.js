import React, {Component} from 'react';
import * as d3 from "d3";
import { Link } from "react-router-dom";
import Api from "../../api";
export default class SchemaList extends Component {
state = {
    schemas: []
  };
  drawChart() {
    const data = [12, 5, 6, 6, 9, 10];

    
        const w = 700;
        const h = 500;
    
    const svg = d3.select("body")
    .append("svg")
    .attr("width", w)
    .attr("height", h)
    .style("margin-left", 100);
                  
    svg.selectAll("rect")
      .data(data)
      .enter()
      .append("rect")
      .attr("x", (d, i) => i * 70)
      .attr("y", (d, i) => h - 10 * d)
      .attr("width", 65)
      .attr("height", (d, i) => d * 10)
      .attr("fill", "green")
  }
  componentDidMount = async () => {
    const { data } = await Api.get(`/schema`);
    this.setState({ schemas: data });
    this.drawChart();
  };
  render() {
    const { schemas } = this.state;
    return (
      <ul>
        {schemas.map((s, i) => (
          <li key={i}>
                  <Link to={`/data/${s.$id}`}>{s.title}</Link>
          </li>
        ))}
      </ul>
    );
  }
}