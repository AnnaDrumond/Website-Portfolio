import { combineReducers } from "redux";
import dataCards from "./reducerCards";
import dataIdiom from "./reducerLanguage";

export default combineReducers({ dataCards, dataIdiom });
