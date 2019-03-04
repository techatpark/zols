import React, { Component } from "react";
import Form from "react-jsonschema-form";
import JSONInput from 'react-json-editor-ajrm';
import Api from "../../api";

export default class Schema extends Component {

  state = {
    schema: {},
    schema_enlarged: {},
    patched_schema:{},
    isAdd: false
  };

  componentDidMount = async () => {
    const schemaId = this.props.match.params.schemaId;
    if(schemaId === "_addNew") {
      const schema = {
      "$schema": "http://json-schema.org/draft-07/schema#",
      "type": "object",
      "properties": {

      }
      };
      const patched_schema = Api.getPatchSchema(schema);
      this.setState({ isAdd:true,schema:schema,schema_enlarged: patched_schema, patched_schema: patched_schema});
    }else {
      const axis_schema = await Api.get(`/schema/${schemaId}?enlarged`);
      var schema_enlarged = axis_schema.data;
      const patched_schema = Api.getPatchSchema(schema_enlarged);
      const {data} = await Api.get(`/schema/${schemaId}`);
      this.setState({ isAdd:false, schema:data,schema_enlarged: schema_enlarged,patched_schema: patched_schema});
    }
  };

  onSubmit = (e) => {
    e.preventDefault();
    const schemaId = this.props.match.params.schemaId;
    if(this.state.isAdd === true) {
      this.state.schema.properties = this.state.patched_schema.properties;

      if(this.state.patched_schema.properties.hasOwnProperty("id")) {
        this.state.schema.ids = ["id"];
      }

      Api.post(`/schema`,this.state.schema)
      .then(function (response) {
        window.location = '/';

      })
      .catch(function (error) {
        console.log(error);
      });
    }
    else {
      const updated_data = Api.put(`/schema/${schemaId}`,this.state.schema)
      .then(function (response) {
        console.log(response);
        window.location = '/';
      })
      .catch(function (error) {
        console.log(error);
      });
    }
  };

  onChange = (jsondata) => {
    if(jsondata.error === false) {


      this.state.schema.properties = jsondata.jsObject;
      this.state.schema_enlarged.properties = jsondata.jsObject;
      this.setState({ patched_schema: Api.getPatchSchema(this.state.schema_enlarged)});
    }
  };

  onCancel = (e) => {
    e.preventDefault()
    window.location = '/';
  };



  handleChange = (event) => {
    this.state.schema[event.target.name] = event.target.value;
    let schema = this.state.schema;
    this.setState({ schema: {}});
    this.setState({ schema: schema});
  }

  render() {
    const isAdd = this.state.isAdd;
    let idText;
    if (isAdd) {
        idText = <div className="col-md-4">
          <label>
            Id:
            <input type="text" name="$id" value={this.state.schema["$id"]} onChange={this.handleChange}/>
          </label>
        </div>
      }
    return (
      <div className="container">
          <div className="row">
              {idText}
              <div className="col-md-4">
                <label>
                  Title:
                  <input type="text" name="title" value={this.state.schema.title} onChange={this.handleChange} />
                </label>
              </div>
              <div className="col-md-4">
                <label>
                  Description:
                  <input type="text" name="description" value={this.state.schema.description} onChange={this.handleChange} />
                </label>
              </div>
          </div>
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
          </div>
          <div className="row">
              <div className="col-md-12">
                <button className="btn btn-link" type="button" onClick={this.onCancel} >Cancel</button>
                <button className="btn btn-primary" type="submit" onClick={this.onSubmit}>Submit</button>
              </div>
          </div>
      </div>

    );
  }
}
