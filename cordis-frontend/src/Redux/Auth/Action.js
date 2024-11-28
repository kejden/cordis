import {BASE_API_URL} from "../../config/api.js";

export const register = (data) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/auth/signup`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });
        const resData = await res.json();

        // Store the JWT token in local storage if available
        if (resData.jwt) localStorage.setItem("token", resData.jwt);

        console.log("register", resData);
        dispatch({ type: REGISTER, payload: resData });
    } catch (error) {
        console.log("catch error", error);
    }
};

// Action creator for user login
export const login = (data) => async (dispatch) => {
    try {
        const res = await fetch(`${BASE_API_URL}/auth/signin`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });
        const resData = await res.json();

        // Store the JWT token in local storage if available
        if (resData.jwt) localStorage.setItem("token", resData.jwt);

        console.log("login", resData);
        dispatch({ type: LOGIN, payload: resData });
    } catch (error) {
        console.log("catch error", error);
    }
};