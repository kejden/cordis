import {BASE_API_URL} from "../../config/api.js";
import { CREATE_NEW_SERVER, GET_ALL_SERVERS } from "./ActionType.js";
import axios from "axios";

export const createNewServer = (data) => async (dispatch) => {
        try {
            const response = await axios.post(
                `${BASE_API_URL}/api/server`,
                data,
                { withCredentials: true }
            );

            if (response.status === 200) {
                dispatch({
                    type: CREATE_NEW_SERVER,
                    payload: response.data,
                });
            }
        } catch (error) {
            console.error("Error creating server: ", error);
        }
}

export const getAllServers = () => async (dispatch) => {
    try {
        const response = await axios.get(`${BASE_API_URL}/api/server`, {withCredentials: true});
        if (response.status === 200) {
            const serverData = response.data.map((item) => ({
                id: item.server.id,
                name: item.server.name,
                image: item.server.image,
                role: item.role.name,
            }));
            dispatch({type: GET_ALL_SERVERS, payload: serverData});
        }
    }catch(error) {
        console.error("Error getting servers: ", error);
    }
}