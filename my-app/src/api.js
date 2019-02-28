import axios from "axios";
import config from "./config";

const { BASE_URL } = config();
const api = axios.create({
  baseURL: BASE_URL
});

const fetchPlayers = () => api.get(`/data/player`);
const createAPlayer = player => api.post(`/data/player`, player);
const fetchAPlayer = (fieldName, value) =>
  api.get(`/data/player/${fieldName}/${value}`);
const updatePlayer = player =>
  api.put(`data/player/number/${player.number}`, player);
const fetchSchema = () => api.get(`/schema`);
export { fetchPlayers, createAPlayer, fetchAPlayer, updatePlayer, fetchSchema };
