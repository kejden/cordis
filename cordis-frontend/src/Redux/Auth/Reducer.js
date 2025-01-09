import {
    LOGIN,
    REGISTER,
    UPDATE_USER,
    REQ_USER,
    UPDATE_PROFILE_IMAGE,
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
            return {
                ...store,
                reqUser: {
                    ...store.reqUser,
                    ...payload,
                },
                updateUser: payload,
            };
        case UPDATE_PROFILE_IMAGE:
            return {
                ...store,
                reqUser: {
                    ...store.reqUser,
                    profileImage: payload.profileImage,
                },
            };
        default:
            return store;
    }
};