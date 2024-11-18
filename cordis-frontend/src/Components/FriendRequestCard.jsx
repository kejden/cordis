import React, {useEffect, useState} from "react";
import pfp from '../assets/img/pfp.jpg'
import {BsChatFill} from "react-icons/bs";
import {SlOptionsVertical} from "react-icons/sl";

const FriendRequestCard = ({ friend, onAccept, onRefuse }) => {
    return (
        <>
            <div
                key={friend.userId}
                className="w-2/3 p-4 bg-gray-800 hover:bg-gray-600 text-white rounded flex justify-between items-center border-t-2 border-t-gray-600"
            >

                <div className="flex items-center space-x-4">
                    {/* Avatar */}
                    <div className="w-10 h-10">
                        <img
                            src={pfp}
                            alt={`${friend.userName}'s avatar`}
                            className="w-10 h-10 rounded-full object-cover"
                        />
                    </div>
                    {/* User details */}
                    <div>
                        <p className="text-base font-medium">{friend.userName}</p>
                    </div>
                </div>
                <div className="flex space-x-3">
                    {/*Buttons*/}
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
                        // todo onClick open chat
                    >
                        <BsChatFill className="text-xl m-2.5" />
                    </div>

                    <div className="rounded-full inline-block bg-gray-700 hover:bg-gray-900"
                         // todo onClick ban
                    >
                        <SlOptionsVertical className="text-xl m-2.5"/>
                    </div>

                </div>) }
            </div>
        </>
    );
};

export default FriendRequestCard;
