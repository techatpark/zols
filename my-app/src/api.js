import axios from "axios";
import config from "./config";

const { BASE_URL } = config();
const api = axios.create({
  baseURL: BASE_URL
});
const APIModel = {
  get: route => api.get(route),
  post: (route, data) => api.post(route, data),
  put: (route, data) => api.put(route, data),
  remove: route => api.delete(route)
};

export default APIModel;
