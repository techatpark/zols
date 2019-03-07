import React, { Component } from "react";
import { Link } from "react-router-dom";
import Api from "../../api";

export default class DataList extends Component {
  state = {
    schema: {}
  };

  onRemove = (e) => {
    e.preventDefault();

    Api.remove(e.currentTarget.getAttribute('data-ref'))
    .then(function (response) {
        window.location.reload();
    })
    .catch(function (error) {
      console.log(error);
    });
  };


  componentDidMount = async () => {
    
    const schemaId = this.props.match.params.schemaId;
    const { data } = await Api.get(`/schema/${schemaId}?enlarged`);
    const list = await Api.get(`/data/${schemaId}`);
    console.log("message", list.data);
    this.setState({ schema: Api.getPatchSchema(data), data: list.data });
  };
  render() {
    const schemaId = this.props.match.params.schemaId;

    return (
      <React.Fragment>
        <h3>List of {this.state.schema.title} </h3>
        <ul className="list-group">
          {this.state.data ? (
            this.state.data.content.map((d, i) => (
              <li className="list-group-item" key={i}>
                <Link
                  to={`/data/${d["$type"]}/${this.state.schema.ids[0]}/${
                    d[this.state.schema.ids[0]]
                  }`}
                >
                  {d[this.state.schema.labelField]}
                </Link>
                <span className="badge" aria-hidden="true" data-ref={`/data/${schemaId}/${this.state.schema.ids[0]}/${
                  d[this.state.schema.ids[0]]
                }`} onClick={this.onRemove}><i className="glyphicon glyphicon-remove"/> </span>
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
      </React.Fragment>
    );
  }
}
