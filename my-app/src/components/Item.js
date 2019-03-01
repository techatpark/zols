import React, { Component } from "react";
import Form from "react-jsonschema-form";
import Api from "../api";
import { Alert } from "reactstrap";
export default class AddNew extends Component {
  state = {
    schema: {},
    formData: {}
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    const { data } = await Api.get(`/schema/${schemaId}`);
    this.setState({ schema: data });
  };
  onChange = ({ formData }) => this.setState({ formData });
  onSave = async ({ formData }) => {
    const schemaId = this.props.match.params.schemaId;
    try {
      await Api.post(`data/${schemaId}`, formData);
      this.setState({ isSuccess: true });
    } catch (e) {
      this.setState({ isError: true });
    }
    setTimeout(() => {
      this.setState({ isSuccess: false, isError: false });
    }, 2000);
  };
  render() {
    const schemaId = this.props.match.params.schemaId;
    const { formData, schema, isError, isSuccess } = this.state;
    return (
      <div>
        {(isError || isSuccess) && (
          <Alert color={isError ? "danger" : "primary"}>
            {isSuccess ? "Save successfully" : "failed"}
          </Alert>
        )}
        <h3>Create a {schemaId} </h3>
        {schema && (
          <Form
            schema={schema}
            formData={formData}
            onChange={this.onChange}
            onSubmit={this.onSave}
            liveValidate={true}
          >
            <button className="btn btn-primary" type="submit">
              Save
            </button>
          </Form>
        )}
      </div>
    );
  }
}
