import React, { Component } from "react";
import Api from "../../api";
import { Link } from "react-router-dom";
import * as d3 from "d3";

export default class SchemaList extends Component {
    state = {
        schemas: []
      };
      componentDidMount = async () => {
        const { data } = await Api.get(`/schema`);
        this.setState({ schemas: data });
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