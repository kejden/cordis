import { CREATE_NEW_SERVER, GET_ALL_SERVERS } from "./ActionType.js";

const initialValue = {
    servers: [],
    newServer: null,
};

export const serverReducer = (store = initialValue, { type, payload }) => {
    if (type === GET_ALL_SERVERS) {
        return {
            ...store,
            servers: payload
        };
    } else if (type === CREATE_NEW_SERVER) {
        return { ...store, servers: [...store.servers, payload], newServer: payload };
    }
    return store;
};