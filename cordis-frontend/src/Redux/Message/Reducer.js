import { CREATE_NEW_MESSAGE, GET_ALL_MESSAGE, EDIT_MESSAGE, DELETE_MESSAGE } from "./ActionType";

const initialValue = {
    messages: [],
    newMessage: null,
};

export const messageReducer = (store = initialValue, { type, payload }) => {
    switch (type) {
        case CREATE_NEW_MESSAGE:
            return { ...store, newMessage: payload };

        case GET_ALL_MESSAGE:
            return { ...store, messages: Array.isArray(payload) ? payload : [] };

        case EDIT_MESSAGE:
            if (Array.isArray(store.messages)) {
                const updatedMessages = store.messages.map((m) =>
                    m.id === payload.id ? { ...m, ...payload } : m
                );
                return { ...store, messages: updatedMessages };
            }
            return store;

        case DELETE_MESSAGE:
            if (Array.isArray(store.messages)) {
                return {
                    ...store,
                    messages: store.messages.filter((msg) => msg.id !== payload),
                };
            }
            console.table(store.messages);
            return store;
        default:
            return store;
    }
};