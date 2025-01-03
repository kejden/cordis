import {BASE_API_URL} from "../../config/api.js";
import { CREATE_NEW_MESSAGE, GET_ALL_MESSAGE } from "./ActionType.js";
import axios from "axios";

export const createMessage = (data) => async (dispatch) => {
    try {
        const res = await axios.post(
            `${BASE_API_URL}/api/messages/create`, data, {withCredentials: true,}
        );

        console.log("create message ", res.data);

        dispatch({ type: CREATE_NEW_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while creating message: ", error.response || error);
    }
};

export const getAllMessages = (reqData) => async (dispatch) => {
    console.log("Came inside get all messages");
    try {
        const res = await axios.get(
            `${BASE_API_URL}/api/messages/${reqData.chatId}`, {withCredentials: true,}
        );

        console.log("get all messages from action method", res.data);

        dispatch({ type: GET_ALL_MESSAGE, payload: res.data });
    } catch (error) {
        console.error("Error while fetching messages: ", error.response || error);
    }
};