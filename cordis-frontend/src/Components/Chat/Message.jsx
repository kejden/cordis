import React, { useState } from "react";
import moment from "moment/moment.js";
import { useSelector } from "react-redux";

const Message = ({ message, handleEditMessage }) => {
    const { auth } = useSelector((store) => store);
    const [isEditing, setIsEditing] = useState(false);
    const [newContent, setNewContent] = useState(message.content);

    const formattedDate = moment(message.timestamp).format("HH:mm, MMM DD");

    const handleSaveEdit = () => {
        handleEditMessage(message.id, newContent);
        setIsEditing(false);
    };

    return (
        <div
            className={`p-3 rounded-lg ${
                message.sender.userName === auth.signin.userName
                    ? "bg-blue-500 self-end"
                    : "bg-gray-700 self-start"
            }`}
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
                <div>
                    <input
                        type="text"
                        value={newContent}
                        onChange={(e) => setNewContent(e.target.value)}
                        className="w-full p-2 rounded bg-gray-600 text-white"
                    />
                    <button onClick={handleSaveEdit} className="mt-2 px-4 py-2 bg-green-500 rounded">
                        Save
                    </button>
                </div>
            ) : (
                <p>{message.content}</p>
            )}
            {message.sender.userName === auth.signin.userName && !isEditing && (
                <button onClick={() => setIsEditing(true)} className="mt-2 px-4 py-2 bg-yellow-500 rounded">
                    Edit
                </button>
            )}
        </div>
    );
};

export default Message;



