import React, { Component } from "react";
import Form from "react-jsonschema-form";
import Api from "../../api";

export default class Data extends Component {

  
  state = {
    schema: {},
    data: {}
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    const locpath = this.props.location.pathname.split('/data/asset/')[1];
    const axis_schema = await Api.get(`/schema/${schemaId}`);
    console.log(locpath);
    const axis_data = await Api.get(`/data/${schemaId}/${locpath}`);
    this.setState({ schema: axis_schema.data, data:  axis_data.data});
  };
  render() {
    return (
      <div>
        <Form schema={this.state.schema} formData={this.state.data} />
      </div>
    );
  }
}
