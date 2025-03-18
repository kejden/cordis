import { CREATE_NEW_MESSAGE, GET_ALL_MESSAGE } from "./ActionType";

const initialValue = {
    messages: {},
    newMessage: null,
};

export const messageReducer = (store = initialValue, { type, payload }) => {
    if (type === CREATE_NEW_MESSAGE) {
        return { ...store, newMessage: payload };
    } else if (type === GET_ALL_MESSAGE) {
        return { ...store, messages: payload };
    } else if (type === "EDIT_MESSAGE") {
        if(Array.isArray(store.messages)){
            const updatedMessages = store.messages.map((m) =>
                m.id === payload.id ? { ...m, ...payload } : m
              );
            return { ...store, messages: updatedMessages };
        }
    }
    return store;
};