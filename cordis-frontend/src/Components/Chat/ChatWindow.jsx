import React from "react";
import MessageInput from "./MessageInput.jsx";
import MessageList from "./MessageList.jsx";
import { IoClose } from "react-icons/io5";

const ChatWindow = ({ handleCreateNewMessage, handleEditMessage, messages, onClose }) => {
    return (
        <div className="flex flex-col flex-1 h-full bg-gray-800 text-white">
            <div className="p-4 bg-gray-900 border-b border-gray-700 flex items-center justify-between">
                <h2 className="text-lg font-semibold">Chat Window</h2>
                <div
                    className="cursor-pointer text-gray-400 hover:text-red-700"
                    onClick={onClose}
                >
                    <IoClose size={24} />
                </div>
            </div>

            <MessageList messages={messages} handleEditMessage={handleEditMessage} />
            <MessageInput handleCreateNewMessage={handleCreateNewMessage} />
        </div>
    );
};

export default ChatWindow;