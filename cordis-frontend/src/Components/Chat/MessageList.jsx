import React, { useEffect, useRef } from "react";
import Message from "./Message.jsx";

const MessageList = ({ messages, handleEditMessage, handleDeleteMessage }) => {
    const containerRef = useRef(null);

    useEffect(() => {
        if (containerRef.current) {
            setTimeout(() => {
                containerRef.current.scrollTop = containerRef.current.scrollHeight;
            }, 100);
        }
    }, [messages]);

    console.log("Messages in MessageList:", messages);

    return (
        <div
            ref={containerRef}
            className="flex-1 overflow-y-auto p-4 space-y-2"
            style={{
                scrollbarWidth: "none",
                msOverflowStyle: "none",
            }}
        >
            <style>{`
                div::-webkit-scrollbar {
                    display: none;
                }
            `}</style>
            {messages.length > 0 ? (
                messages.map((message) => (
                    <Message
                        key={message.id}
                        message={message}
                        handleEditMessage={handleEditMessage}
                        handleDeleteMessage={handleDeleteMessage}
                    />
                ))
            ) : (
                <p className="text-center text-gray-400">No messages yet</p>
            )}
        </div>
    );
};

export default MessageList;