import {BASE_API_URL} from "../../config/api.js";
import axios from "axios";
import {LOGIN, LOGOUT, REGISTER, REQ_USER} from "./ActionType.js";
import storage from "redux-persist/lib/storage";
import {persistor, store} from "../store.jsx";
import {persistStore} from "redux-persist";

export const register = (data) => async (dispatch) => {
    try {
        const response = await axios.post(`${BASE_API_URL}/auth/sign-up`, data, {
            headers: {'Content-Type': 'application/json'},
        });
        const resData = await response.json();

        console.log("register", resData);
        dispatch({ type: REGISTER, payload: resData });
    } catch (error) {
        console.error(`Error: `, error);
        throw new Error(error.message);
    }
};

export const login = (data) => async (dispatch) => {
    try {
        const response = await axios.post(`${BASE_API_URL}/auth/sign-in`, data, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true })
        const resData = await response.data;
        dispatch({ type: LOGIN, payload: resData });
        dispatch({ type: REQ_USER, payload: resData });
    } catch (error) {
        console.error(`Error: `, error);
        throw new Error(error.message);
    }
};

export const logoutAction = () => async (dispatch) => {
    const response = await axios.post(`${BASE_API_URL}/auth/logout`,
        {}, {withCredentials: true })
    dispatch({ type: LOGIN, payload: null });
    dispatch({ type: REQ_USER, payload: null });
};