import moment from "moment/moment.js";
import pfp from "../../assets/img/pfp.jpg";
import React from "react";
import {useSelector} from "react-redux";

const Message = ({message, showUserInfo}) => {
    const { auth } = useSelector((store) => store);

    const formattedDate = moment(message.timestamp).format("HH:mm, MMM DD");

    return (
        <div
            className={`p-3 rounded-lg ${
                message.sender.userName == auth.signin.userName
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
                    <br/>
                    <span className="text-xs text-gray-300">
                                {formattedDate}
                    </span>
                </div>
            </div>
        <p>{message.content}</p>
        </div>
    );
};

export default Message;



