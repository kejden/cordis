import {
    LOGIN,
    REGISTER,
    UPDATE_USER,
} from "./ActionType";

// Initial state for the authentication store
const initialValue = {
    signup: null, // Holds data related to user registration
    signin: null, // Holds data related to user login
    updateUser: null, // Holds data related to user updates
};

// Reducer function for handling authentication-related actions
export const authReducer = (store = initialValue, { type, payload }) => {
    // Check the action type and update the store accordingly
    if (type === REGISTER) {
        return { ...store, signup: payload };
    } else if (type === LOGIN) {
        return { ...store, signin: payload };
    } else if (type === UPDATE_USER) {
        return { ...store, updateUser: payload };
    }
    // If the action type is not recognized, return the current store unchanged
    return store;
};