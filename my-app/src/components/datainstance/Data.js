import React, { Component } from "react";
import Form from "react-jsonschema-form";
import Api from "../../api";

export default class Data extends Component {

  onSubmit = ({formData}, e) => console.log("Data submitted: ",  formData);

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
      this.setState({ schema: patched_schema, data:  axis_data.data});
    }else {
      this.setState({ schema: patched_schema});
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
