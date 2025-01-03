import React, { useState, useEffect } from "react";

const ChatWindow = ({handleCreateNewMessage, messages}) => {
    const [messageContent, setMessageContent] = React.useState("");

    const handleSendMessage = () => {
        if (messageContent.trim()) {
            handleCreateNewMessage(messageContent);
            setMessageContent("");
        }
    };

    return (
        <div className="flex flex-col flex-1 h-full bg-gray-800 text-white">
            <div className="p-4 bg-gray-900 border-b border-gray-700">
                <h2 className="text-lg font-semibold">Chat Window</h2>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-2">
                {messages.length > 0 ? (
                    messages.map((message, index) => (
                        <div
                            key={index}
                            className={`p-3 rounded-lg ${
                                message.isSentByUser
                                    ? "bg-blue-500 self-end"
                                    : "bg-gray-700 self-start"
                            }`}
                        >
                            {message.content}
                        </div>
                    ))
                ) : (
                    <p className="text-center text-gray-400">No messages yet</p>
                )}
            </div>

            <div className="p-4 bg-gray-900 border-t border-gray-700 flex items-center">
                <input
                    type="text"
                    placeholder="Type your message..."
                    value={messageContent}
                    onChange={(e) => setMessageContent(e.target.value)}
                    className="flex-1 px-4 py-2 rounded-lg bg-gray-700 text-white focus:outline-none"
                />
                <button
                    onClick={handleSendMessage}
                    className="ml-4 px-4 py-2 bg-blue-500 hover:bg-blue-600 rounded-lg text-white font-semibold"
                >
                    Send
                </button>
            </div>
        </div>
    );

};

export default ChatWindow;