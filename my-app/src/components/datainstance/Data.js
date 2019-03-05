import React, { Component } from "react";
import Form from "react-jsonschema-form";
import Api from "../../api";

export default class Data extends Component {

  onSubmit = ({formData}, e) => {
    if(this.state.locpath == "_addNew") {

      const created_data = Api.post(`/data/${this.state.schema["$id"]}`,formData)
      .then(function (response) {
        window.history.back();
      })
      .catch(function (error) {
        console.log(error);
      });
    }
    else {

      const updated_data = Api.put(`/data/${this.state.schema["$id"]}/${this.state.locpath}`,formData)
      .then(function (response) {
        window.history.back();
      })
      .catch(function (error) {
        console.log(error);
      });
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
    const locpath = this.props.location.pathname.split('/data/'+schemaId+'/')[1];
    const axis_schema = await Api.get(`/schema/${schemaId}?enlarged`);
    console.log(locpath);
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
      <React.Fragment>
        <Form schema={this.state.schema} uiSchema={this.state.schema.uiSchema} formData={this.state.data} onSubmit={this.onSubmit} FieldTemplate={Api.Tpl}>
        <div>
          <button className="btn btn-link" type="button" onClick={this.onCancel} >Cancel</button>
          <button className="btn btn-primary" type="submit">Submit</button>
        </div>
        </Form>
      </React.Fragment>
    );
  }
}
