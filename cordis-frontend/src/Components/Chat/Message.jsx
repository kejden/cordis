import moment from "moment/moment.js";
import pfp from "../../assets/img/pfp.jpg";
import React from "react";

const Message = ({ message, showUserInfo }) => {
    const formattedDate = moment(message.sendAt).format("HH:mm, MMM DD");

    return (
        <div
            className={`p-3 rounded-lg ${
                message.sender === "Current User"
                    ? "bg-blue-500 self-end"
                    : "bg-gray-700 self-start"
            }`}
        >
            {showUserInfo && (
                <div className="flex items-center mb-2">
                    <img
                        src={pfp}
                        alt={`${message.sender}'s profile`}
                        className="w-8 h-8 rounded-full mr-2"
                    />
                    <div>
                        <span className="text-sm font-semibold">
                            {message.sender}
                        </span>
                        <br />
                        <span className="text-xs text-gray-300">
                            {formattedDate}
                        </span>
                    </div>
                </div>
            )}
            <p>{message.content}</p>
        </div>
    );
};

export default Message;
