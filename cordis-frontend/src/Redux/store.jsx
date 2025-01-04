import { combineReducers, legacy_createStore, applyMiddleware } from "redux";
import { thunk } from "redux-thunk";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // Default is localStorage for web
import { authReducer } from "./Auth/Reducer";
import { messageReducer } from "./Message/Reducer";

// Combine multiple reducers into a single rootReducer
const rootReducer = combineReducers({
    auth: authReducer, // Authentication related state
    // chat: chatReducer, // Chat related state
    message: messageReducer, // Message related state
});

// Configuration for redux-persist
const persistConfig = {
    key: "root", // Key for storing the root level state
    storage, // Define storage type
    whitelist: ["auth"], // State slices to persist (e.g., auth, chat)
};

// Create a persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Create the Redux store with the persisted reducer
export const store = legacy_createStore(
    persistedReducer,
    applyMiddleware(thunk)
);

export const persistor = persistStore(store);
