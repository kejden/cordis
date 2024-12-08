import {BASE_API_URL} from "../../config/api.js";
import axios from "axios";
import {LOGIN, LOGOUT, REGISTER} from "./ActionType.js";

export const register = (data) => async (dispatch) => {
    try {
        const response = await axios.post(`${BASE_API_URL}/auth/sign-up`, data, {
            headers: {'Content-Type': 'application/json'},
        });
        const resData = await response.json();

        console.log("register", resData);
        dispatch({ type: REGISTER, payload: resData });
    } catch (error) {
        console.log("catch error", error);
    }
};

export const login = (data) => async (dispatch) => {
    try {
        const response = await axios.post(`${BASE_API_URL}/auth/sign-in`, data, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true })
        const resData = await response.json();

        console.log("login", resData);
        dispatch({ type: LOGIN, payload: resData });
    } catch (error) {
        console.log("catch error", error);
    }
};

// export const logoutAction = () => async (dispatch) => {
//     const response = await axios.post(`${BASE_API_URL}/auth/logout`,  {
//         headers: { 'Content-Type': 'application/json' },
//         withCredentials: true })
//     if(response.status.ok){
//         localStorage.removeItem("email");
//         localStorage.removeItem("id");
//         localStorage.removeItem("userName");
//         dispatch({ type: LOGOUT, payload: null });
//         // dispatch({ type: REQ_USER, payload: null });
//     }
// };