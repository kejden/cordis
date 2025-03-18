import React, { useEffect, useRef } from "react";
import Message from "./Message.jsx";

const MessageList = ({ messages, handleEditMessage }) => {
    const containerRef = useRef(null);

    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
        }
    }, [messages]);

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
                messages.map((message, index) => (
                    <Message key={index} message={message} handleEditMessage={handleEditMessage} />
                ))
            ) : (
                <p className="text-center text-gray-400">No messages yet</p>
            )}
        </div>
    );
};

export default MessageList;
