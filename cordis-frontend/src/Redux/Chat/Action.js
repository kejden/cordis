import { UPDATE_LATEST_CHATS } from "./ActionType";

export const updateLatestChats = (latestChats) => ({
    type: UPDATE_LATEST_CHATS,
    payload: latestChats,
});