import React from "react";
import Message from "./Message.jsx";


const MessageList = ({ messages }) => {
    return (
        <div className="flex-1 overflow-y-auto p-4 space-y-2">
            {messages.length > 0 ? (
                messages.map((message, index) => {
                    return (
                        <Message
                            key={index}
                            message={message}
                            showUserInfo={true}
                        />
                    );
                })
            ) : (
                <p className="text-center text-gray-400">No messages yet</p>
            )}
        </div>
    );
};

export default MessageList;
