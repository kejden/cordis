import {
    LOGIN,
    REGISTER,
    UPDATE_USER,
    REQ_USER,
} from "./ActionType";

const initialValue = {
    signup: null,
    signin: null,
    reqUser: null,
    updateUser: null,
};

export const authReducer = (store = initialValue, { type, payload }) => {
    switch (type) {
        case REGISTER:
            return { ...store, signup: payload };
        case LOGIN:
            return { ...store, signin: payload };
        case REQ_USER:
            return { ...store, reqUser: payload };
        case UPDATE_USER:
            return { ...store, updateUser: payload };
        default:
            return store;
    }
};