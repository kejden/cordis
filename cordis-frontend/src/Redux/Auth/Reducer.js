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
    if (type === REGISTER) {
        return { ...store, signup: payload };
    } else if (type === LOGIN) {
        return { ...store, signin: payload };
    } else if (type === REQ_USER) {
        return { ...store, reqUser: payload };
    } else if (type === UPDATE_USER) {
        return { ...store, updateUser: payload };
    }
    return store;
};