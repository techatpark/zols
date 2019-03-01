import React, { Component } from "react";
import { Form } from "react-jsonschema-form";
import Api from "../../api";

export default class Data extends Component {

  
  state = {
    schema: {}
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    const { data } = await Api.get(`/schema/${schemaId}`);
    this.setState({ schema: data });
  };
  render() {
    

    return (
      <div>
        <h3>Data Form {this.state.schema.title} </h3>
        <Form
      schema={this.state.schema}
      
      />
      
        
      </div>
    );
  }
}
