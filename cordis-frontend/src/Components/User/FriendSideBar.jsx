import React, { useEffect } from "react";
import UserCard from "./UserCard.jsx";

const FriendSideBar = ({ latestChats, setChatOpen, setChatWindow, setIsGroup }) => {

    return (
        <>
            <div className="flex w-1/5">
                <div className="w-1/5"></div>
                <div className="flex flex-col w-4/5 bg-gray-900">
                    <div className="flex-grow">
                        <div className="flex flex-col p-8 items-center justify-center w-full">
                            <h2 className="text-lg font-semibold mb-2 text-white">Latest chats</h2>
                        </div>
                        <div className="flex flex-col">
                            {latestChats.map((chat) => (
                                <div
                                    key={chat.id}
                                    className="flex items-center p-2 cursor-pointer hover:bg-gray-800"
                                    onClick={() => {
                                        setChatOpen(true);
                                        setChatWindow(chat.id);
                                    }}
                                >
                                    <img
                                        src={`http://localhost:8080/uploads/${chat.user.profileImage}`}
                                        alt={`${chat.user.userName}'s profile`}
                                        className="w-10 h-10 rounded-full mr-3"
                                    />
                                    <span className="text-white">{chat.user.userName}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="bg-gray-950 h-14">
                        <UserCard />
                    </div>
                </div>
            </div>
        </>
    );
};

export default FriendSideBar;
