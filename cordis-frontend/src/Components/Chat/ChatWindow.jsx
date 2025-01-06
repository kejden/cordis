import React from "react";
import MessageInput from "./MessageInput.jsx";
import MessageList from "./MessageList.jsx";

const ChatWindow = ({handleCreateNewMessage, messages}) => {

    return (
        <div className="flex flex-col flex-1 h-full bg-gray-800 text-white">
            <div className="p-4 bg-gray-900 border-b border-gray-700">
                <h2 className="text-lg font-semibold">Chat Window</h2>
            </div>
            <MessageList messages={messages} />
            <MessageInput handleCreateNewMessage={handleCreateNewMessage} />
        </div>
    );

};

export default ChatWindow;