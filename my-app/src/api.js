import React, { Component } from "react";
import axios from "axios";
import config from "./config";

const { BASE_URL } = config();
const api = axios.create({
  baseURL: BASE_URL
});
const APIModel = {
  Tpl: (props) => {
  const {id, classNames, label, help, required, description, rawErrors=[], children} = props;
  return (
    <div className={classNames}>
      {description}
      {children}
      {rawErrors.map(error => <div style={{color: "blue"}}><h1>{error}</h1></div>)}
      {help}
    </div>
  );
},
  getPatchSchema: schema => {

    const patched_schema = Object.assign({}, schema);
    let cs = schema;
    while(cs != undefined && cs["$ref"] != undefined) {
      cs = schema.definitions[cs["$ref"].split("#/definitions/")[1]];
      console.log(cs);
      Object.assign(patched_schema.properties, cs.properties);
    }
    return patched_schema;
  },
  get: route => api.get(route),
  post: (route, data) => api.post(route, data),
  put: (route, data) => api.put(route, data),
  remove: route => api.delete(route)
};

export default APIModel;
