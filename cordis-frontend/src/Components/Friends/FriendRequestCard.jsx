import React, {useEffect, useRef, useState} from "react";
import pfp from '../../assets/img/pfp.jpg'
import {BsChatFill} from "react-icons/bs";
import {SlOptionsVertical} from "react-icons/sl";
import Status from "../User/Status.jsx";

const FriendRequestCard = ({ friend, onAccept, onRefuse, setChatOpen, setChatWindow, handleBanFriend }) => {
    const [optionsOpen, setOptionsOpen] = useState(false);
    const optionsRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (optionsRef.current && !optionsRef.current.contains(event.target)) {
                setOptionsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    return (
        <>
            <div
                className="w-2/3 p-4 bg-gray-800 hover:bg-gray-600 text-white rounded flex justify-between items-center border-t-2 border-t-gray-600"
            >

                <div className="flex items-center space-x-4">
                    <div className="w-10 h-10">
                        <img
                            src={`http://localhost:8080/uploads/${friend.user.profileImage}`}
                            alt={`${friend.user.userName}'s avatar`}
                            className="w-10 h-10 rounded-full object-cover"
                        />
                    </div>
                    <div>
                        <p className="text-base font-medium">{friend.user.userName}</p>
                        <Status status={friend.user.status} />
                    </div>
                </div>
                <div className="flex space-x-3">
                    {onAccept && (
                        <button
                            onClick={() => onAccept(friend.id)}
                            className="px-3 py-1.5 bg-green-600 text-sm rounded hover:bg-green-700 transition"
                        >
                            Accept
                        </button>
                    )}
                    {onRefuse && (
                        <button
                            onClick={() => onRefuse(friend.id)}
                            className="px-3 py-1.5 bg-red-600 text-sm rounded hover:bg-red-700 transition"
                        >
                            Refuse
                        </button>
                    )}
                </div>
                {!onAccept && (<div className="flex items-center float-end space-x-2">
                    <div className="rounded-full  inline-block bg-gray-700 hover:bg-gray-900"
                         onClick={
                             () => {
                                 setChatOpen(true)
                                 setChatWindow(friend.id)
                             }
                         }
                    >
                        <BsChatFill className="text-xl m-2.5"/>
                    </div>
                    <div className="relative flex items-center space-x-2">
                        <div
                            className="rounded-full inline-block bg-gray-700 hover:bg-gray-900"
                            onClick={() => setOptionsOpen(!optionsOpen)}
                        >
                            <SlOptionsVertical className="text-xl m-2.5"/>
                        </div>

                        {optionsOpen && (
                            <div
                                className="absolute left-full top-0 ml-2 bg-gray-900 text-red-700 rounded shadow-lg p-2 w-40 z-10"
                            >
                                <button
                                    className="block w-full text-left px-4 py-2 hover:bg-gray-600 rounded"
                                    onClick={() => handleBanFriend(friend.id)}
                                >
                                    Ban Friend
                                </button>
                            </div>
                        )}
                    </div>
                </div>)}
            </div>
        </>
    );
};

export default FriendRequestCard;
