import React, { Component } from "react";
import Form from "react-jsonschema-form";
import JSONInput from 'react-json-editor-ajrm';
import Api from "../../api";

export default class Schema extends Component {

  state = {
    schema: {},
    patched_schema:{},
    schema_txt:""
  };

  onSubmit = (e) => {
    e.preventDefault();
    const schemaId = this.props.match.params.schemaId;
    if(schemaId === "_addNew") {
    }
    else {
      this.state.schema.properties = this.state.patched_schema.properties;
      console.log(this.state.schema);
      const updated_data = Api.put(`/schema/${schemaId}`,this.state.schema)
      .then(function (response) {
        console.log(response);
        window.history.back();
      })
      .catch(function (error) {
        console.log(error);
      });

    }

  };

  onChange = (jsondata) => {
    if(jsondata.error === false) {
      const patched_schema = Object.assign({}, this.state.patched_schema);
      Object.assign(patched_schema.properties, jsondata.jsObject);
      console.log(patched_schema);
      this.setState({ patched_schema: {}});
      this.setState({ patched_schema: patched_schema});
    }
  };

  onCancel = (e) => {
    e.preventDefault()
    window.history.back();
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    if(schemaId !== "_addNew") {
      const axis_schema = await Api.get(`/schema/${schemaId}?enlarged`);
      const patched_schema = Api.getPatchSchema(axis_schema.data);
      const {data} = await Api.get(`/schema/${schemaId}`);
      this.setState({ schema:data,patched_schema: patched_schema, schema_txt: JSON.stringify(data.properties)});
    }
  };

  render() {
    return (
      <div className="row">
          <div className="col-md-6">
          <JSONInput
              id          = 'a_unique_id'
              placeholder = {this.state.schema.properties}
              height      = '390px'
              onChange    = {this.onChange}
          />
          </div>
          <div className="col-md-6">
            <Form schema={this.state.patched_schema} onSubmit={this.onSubmit} >
            <div></div>
            </Form>
          </div>
          <div className="col-md-12">
            <button className="btn btn-link" type="button" onClick={this.onCancel} >Cancel</button>
            <button className="btn btn-primary" type="submit" onClick={this.onSubmit}>Submit</button>
          </div>
      </div>
    );
  }
}
