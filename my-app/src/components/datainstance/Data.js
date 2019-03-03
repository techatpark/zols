import React, { Component } from "react";
import Form from "react-jsonschema-form";
import Api from "../../api";

export default class Data extends Component {

  onSubmit = ({formData}, e) => {
    if(this.state != "_addNew") {
      const created_data = Api.post(`/data/${this.state.schema["$id"]}`,formData)
      .then(function (response) {
        window.history.back();
      })
      .catch(function (error) {
        console.log(error);
      });;
      //console.log(created_data);
    }
  };

  onCancel = (e) => {
    e.preventDefault()
    window.history.back();
  };

  state = {
    schema: {},
    data: {}
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    const locpath = this.props.location.pathname.split('/data/')[1].split('/')[1];
    const axis_schema = await Api.get(`/schema/${schemaId}?enlarged`);

    const patched_schema = Api.getPatchSchema(axis_schema.data);

    if(locpath != "_addNew") {
      const axis_data = await Api.get(`/data/${schemaId}/${locpath}`);
      this.setState({ schema: patched_schema, locpath:locpath ,data:  axis_data.data});
    }else {
      this.setState({ schema: patched_schema, locpath:locpath});
    }
  };



  render() {
    return (
      <div>
        <Form schema={this.state.schema} formData={this.state.data} onSubmit={this.onSubmit} >
        <div>
          <button className="btn btn-link" type="button" onClick={this.onCancel} >Cancel</button>
          <button className="btn btn-primary" type="submit">Submit</button>
        </div>
        </Form>
      </div>
    );
  }
}
