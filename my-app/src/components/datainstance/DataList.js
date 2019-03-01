import React, { Component } from "react";
import { Link } from "react-router-dom";
import Api from "../../api";

export default class DataList extends Component {
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
        <h3>List of {this.state.schema.title} </h3>
        <ul className="list-group">
          {this.state.data ? (
            this.state.data.map((d, i) => (
              <li className="list-group-item" key={i}>
                <Link
                  to={`/data/${schemaId}/${this.state.schema.ids[0]}/${
                    d[this.state.schema.ids[0]]
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
        </ul>
        <div>
          <button className="btn btn-default">
            <Link to={`/data/${schemaId}/_addNew`}>Add {this.state.schema.title}</Link>
          </button>
        </div>
      </div>
    );
  }
}
