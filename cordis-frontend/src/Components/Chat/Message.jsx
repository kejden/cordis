import React, { useState } from "react";
import moment from "moment/moment.js";
import { useSelector } from "react-redux";
import { FaSave, FaEdit, FaTrash } from "react-icons/fa";

const Message = ({ message, handleEditMessage, handleDeleteMessage }) => {
    const { auth } = useSelector((store) => store);
    const [isEditing, setIsEditing] = useState(false);
    const [newContent, setNewContent] = useState(message.content);

    const formattedDate = moment(message.timestamp).format("HH:mm, MMM DD");

    const handleSaveEdit = () => {
        handleEditMessage(message.id, newContent);
        setIsEditing(false);
    };

    const isOwnMessage = message.sender.userName === auth.signin.userName;

    return (
        <div
            className={`
                group relative p-3 rounded-lg max-w-full
                ${isOwnMessage ? "bg-blue-500 self-end" : "bg-gray-700 self-start"}
            `}
        >
            <div className="flex items-center mb-2">
                <img
                    src={`http://localhost:8080/uploads/${message.sender.profileImage}`}
                    alt={`${message.sender.userName}'s profile`}
                    className="w-8 h-8 rounded-full mr-2"
                />
                <div>
                    <span className="text-sm font-semibold">
                        {message.sender.userName}
                    </span>
                    <br />
                    <span className="text-xs text-gray-300">
                        {formattedDate}
                    </span>
                </div>
            </div>

            {isEditing ? (
                <>
                    <input
                        type="text"
                        value={newContent}
                        onChange={(e) => setNewContent(e.target.value)}
                        className="w-full p-2 rounded bg-gray-600 text-white"
                    />
                    <div className="flex gap-2 mt-2">
                        <button
                            onClick={handleSaveEdit}
                            className="p-2 bg-green-500 rounded hover:bg-green-600"
                            title="Save"
                        >
                            <FaSave className="text-white" />
                        </button>
                        <button
                            onClick={() => setIsEditing(false)}
                            className="p-2 bg-red-500 rounded hover:bg-red-600"
                            title="Cancel"
                        >
                            <FaTrash className="text-white" />
                        </button>
                    </div>
                </>
            ) : (
                <p>{message.content}</p>
            )}

            {isOwnMessage && !isEditing && (
                <div
                    className="
                        absolute top-2 right-2 flex gap-2
                        opacity-0 group-hover:opacity-100
                        transition-opacity
                    "
                >
                    <button
                        onClick={() => setIsEditing(true)}
                        className="p-2 bg-yellow-500 rounded hover:bg-yellow-600"
                        title="Edit"
                    >
                        <FaEdit className="text-white" />
                    </button>
                    <button
                        onClick={() => handleDeleteMessage(message.id)}
                        className="p-2 bg-red-500 rounded hover:bg-red-600"
                        title="Delete"
                    >
                        <FaTrash className="text-white" />
                    </button>
                </div>
            )}
        </div>
    );
};

export default Message;