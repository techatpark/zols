import React, { Component } from "react";
import { Link } from "react-router-dom";
import Api from "../api";

export default class Data extends Component {
  state = {
    schema: {}
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    const { data } = await Api.get(`/schema/${schemaId}`);
    const list = await Api.get(`/data/${schemaId}`);
    this.setState({ schema: data, data: list.data.content });
  };
  render() {
    const schemaId = this.props.match.params.schemaId;

    return (
      <div>
        <div>List of data </div>
        {this.state.data ? (
          this.state.data.map((d, i) => (
            <li key={i}>
              <Link
                to={`${schemaId}/${this.state.schema.required[0]}/${
                  d[this.state.schema.required[0]]
                }`}
              >
                {d[this.state.schema.labelField]}
              </Link>
            </li>
          ))
        ) : (
          <p>
            <strong>Data Not Found</strong>
          </p>
        )}
      </div>
    );
  }
}
