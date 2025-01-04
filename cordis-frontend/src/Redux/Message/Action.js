import {BASE_API_URL} from "../../config/api.js";
import { CREATE_NEW_MESSAGE, GET_ALL_MESSAGE } from "./ActionType.js";
import axios from "axios";

export const createMessage = (data) => async (dispatch) => {
    try {
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
        const res = await axios.get(
            `${BASE_API_URL}/api/messages/${reqData.chatId}`, {withCredentials: true,}
        );
        dispatch({ type: GET_ALL_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while fetching messages: ", error.response || error);
    }
};