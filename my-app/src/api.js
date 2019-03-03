import axios from "axios";
import config from "./config";

const { BASE_URL } = config();
const api = axios.create({
  baseURL: BASE_URL
});
const APIModel = {
  getPatchSchema: schema => {
    const patched_schema = Object.assign({}, schema);
    var current_schema = patched_schema;
    while(current_schema["$ref"] !== undefined) {
      current_schema = patched_schema.definitions[current_schema["$ref"].split('#/definitions/')[1]];
      Object.assign(patched_schema.properties, current_schema.properties);
      console.log(current_schema.title);
    }
    return patched_schema;
  },
  get: route => api.get(route),
  post: (route, data) => api.post(route, data),
  put: (route, data) => api.put(route, data),
  remove: route => api.delete(route)
};

export default APIModel;
