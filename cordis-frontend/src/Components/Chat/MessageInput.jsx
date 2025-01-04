import React, { useState } from "react";

const MessageInput = ({ handleCreateNewMessage }) => {
    const [messageContent, setMessageContent] = useState("");

    const handleSendMessage = () => {
        if (messageContent.trim()) {
            handleCreateNewMessage(messageContent);
            setMessageContent("");
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            handleSendMessage();
        }
    };

    return (
        <div className="p-4 bg-gray-900 border-t border-gray-700 flex items-center">
            <input
                type="text"
                placeholder="Type your message..."
                value={messageContent}
                onChange={(e) => setMessageContent(e.target.value)}
                onKeyDown={handleKeyDown}
                className="flex-1 px-4 py-2 rounded-lg bg-gray-700 text-white focus:outline-none"
            />
            <button
                onClick={handleSendMessage}
                className="ml-4 px-4 py-2 bg-blue-500 hover:bg-blue-600 rounded-lg text-white font-semibold"
            >
                Send
            </button>
        </div>
    );
};

export default MessageInput;