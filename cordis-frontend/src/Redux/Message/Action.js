import {BASE_API_URL} from "../../config/api.js";
import {CREATE_NEW_MESSAGE, EDIT_MESSAGE, GET_ALL_MESSAGE} from "./ActionType.js";
import axios from "axios";

export const createMessage = (data) => async (dispatch) => {
    try {
        console.log(data)
        const res = await axios.post(
            `${BASE_API_URL}/api/messages/create`, data, {withCredentials: true,}
        );
        dispatch({ type: CREATE_NEW_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while creating message: ", error.response || error);
    }
};

export const getAllMessages = (reqData) => async (dispatch) => {
    try {
        let endpoint;
        if (reqData.isServerChannel) {
            endpoint = `${BASE_API_URL}/api/server-messages/${reqData.chatId}`;
        } else {
            endpoint = `${BASE_API_URL}/api/messages/${reqData.chatId}`;
        }

        const res = await axios.get(endpoint, { withCredentials: true });
        dispatch({ type: GET_ALL_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while fetching messages: ", error.response || error);
    }
};

export const editMessage = (messageId, data) => async (dispatch) => {
    try {
        let endpoint;
        if (data.isServerChannel) {
            endpoint = `${BASE_API_URL}/api/server-messages/edit/${messageId}`;
        } else {
            endpoint = `${BASE_API_URL}/api/messages/edit/${messageId}`;
        }
        const res = await axios.put(endpoint, data,{ withCredentials: true });
        dispatch({ type: EDIT_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while fetching messages: ", error.response || error);
    }

};