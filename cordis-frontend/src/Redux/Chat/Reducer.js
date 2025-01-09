import { UPDATE_LATEST_CHATS } from "./ActionType";

const initialValue = {
    latestChats: [],
};

export const chatReducer = (state = initialValue, { type, payload }) => {
    switch (type) {
        case UPDATE_LATEST_CHATS:
            return { ...state, latestChats: payload };
        default:
            return state;
    }
};