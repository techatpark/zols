import React, { Component } from "react";
import axios from "axios";
import config from "./config";

const { BASE_URL } = config();
let api = axios.create({
  baseURL: BASE_URL,
  headers:{}
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

setLocale: (locale) => {
  localStorage.setItem("locale", locale);
},

  getPatchSchema: schema => {

    const patched_schema = Object.assign({}, schema);
    let cs = schema;


    let e_order = [];
    while(cs != undefined && cs["$ref"] != undefined) {
      cs = schema.definitions[cs["$ref"].split("#/definitions/")[1]];
      let keys = Object.keys(cs.properties);
      if(cs["ids"] !== undefined) {
        patched_schema["ids"] = cs["ids"];
      }
      if(cs["labelField"] !== undefined) {
        patched_schema["labelField"] = cs["labelField"];
      }
      e_order = e_order.concat(Object.keys(cs.properties));
      Object.assign(patched_schema.properties, cs.properties);
    }

    let ui_order = [];
    ui_order = ui_order.concat(patched_schema["ids"]);
    console.log("e_order",e_order);
    console.log("ui_order",ui_order);

    for (let e of e_order) {
        if(!ui_order.includes(e)) {
          ui_order.push(e);
        }
    }

    ui_order.push("*");

    const uiSchema = {
      "ui:order": ui_order
    };
      patched_schema["uiSchema"] = uiSchema;

    return patched_schema;
  },
  get: route => api.get(route,{
    params: {
      lang: localStorage.getItem("locale")
    }
  }),
  post: (route, data) => api.post(route, data),
  put: (route, data) => api.put(route, data),
  remove: route => api.delete(route)
};

export default APIModel;
