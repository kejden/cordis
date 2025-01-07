import React, { useEffect, useState } from "react";
import FriendRequestCard from "./FriendRequestCard.jsx";
import { FaUserFriends } from "react-icons/fa";
import {BASE_API_URL} from "../../config/api.js";
import axios from "axios";
import toast from "react-hot-toast";



const FriendManager = ({setChatOpen, setChatWindow}) => {
    const [acceptedFriends, setAcceptedFriends] = useState([]);
    const [awaitingFriends, setAwaitingFriends] = useState([]);
    const [pendingFriends, setPendingFriends] = useState([]);
    const [newFriendUsername, setNewFriendUsername] = useState("");
    const [error, setError] = useState("");
    const [activeSection, setActiveSection] = useState("friends");

    useEffect(() => {
        fetchAcceptedFriends();
        fetchAwaitingFriends();
        fetchPendingFriends();
    }, []);


    const fetchAcceptedFriends = async () => {
        try {
            const response = await axios.get(`${BASE_API_URL}/api/friend/responses`, {
                withCredentials: true,
            });

            const data = await response.data;
            setAcceptedFriends(data);
        } catch (error) {
            console.error("Error fetching accepted friends:", error);
        }
    };

    const fetchAwaitingFriends = async () => {
        try {
            const response = await axios.get(`${BASE_API_URL}/api/friend/awaiting`, {
                withCredentials: true,
            });

            const data = await response.data;
            setAwaitingFriends(data);
        } catch (error) {
            console.error("Error fetching awaiting friends:", error);
        }
    };

    const fetchPendingFriends = async () => {
        try {
            const response = await axios.get(`${BASE_API_URL}/api/friend/pending`, {
                withCredentials: true,
            });

            const data = await response.data;
            setPendingFriends(data);
        } catch (error) {
            console.error("Error fetching pending friends:", error);
        }
    };

    const acceptFriend = async (id) => {
        try {
            const response = await axios.post(`${BASE_API_URL}/api/friend/accept/${id}`, {}, {
                withCredentials: true,
            });
            if (response.ok) {
                setAwaitingFriends((prev) => prev.filter((friend) => friend.id !== id));
                fetchAcceptedFriends();
            }
        } catch (error) {
            console.error("Error accepting friend:", error);
        }
    };

    const refuseFriend = async (id) => {
        try {
            const response = await axios.delete(`${BASE_API_URL}/api/friend/refuse/${id}`, {
                withCredentials: true,
            });
            if (response.ok) {
                setAwaitingFriends((prev) => prev.filter((friend) => friend.id !== id));
            }
        } catch (error) {
            console.error("Error refusing friend:", error);
        }
    };

    const handleAddFriend = async () => {
        if (!newFriendUsername) {
            setError("Please enter a valid username.");
            return;
        }

        try {
            const response = await axios.post(
                `${BASE_API_URL}/api/friend/request`,
                { userName: newFriendUsername },
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true,
                }
            );


            if (response.ok) {
                setNewFriendUsername("");
                setError("");
                fetchPendingFriends();
            } else {
                setError("Failed to send friend request.");
            }
        } catch (error) {
            console.error("Error adding friend:", error);
            setError("An error occurred while sending the friend request.");
        }
    };

    const handleBanFriend = async (friendId) => {
        try {
            console.log()
            const response = await axios.post(`${BASE_API_URL}/api/friend/ban/${friendId}`,{},{
                withCredentials: true,
            });
            if (response.status === 200) {
                toast.success("Friend banned successfully!");
                setAcceptedFriends((prevFriends) =>
                    prevFriends.filter((friend) => friend.id !== friendId)
                );
            } else {
                toast.error("Failed to ban friend.");
            }
        } catch (error) {
            console.log(error);
            toast.error("An error occurred while banning the friend.");
        }
    };

    return (
        <>
            <div className="w-4/5  p-6 bg-gray-800 text-white float-end">
                <div className="flex flex-row space-x-4 mb-6">
                    <h1 className="text-xl font-bold flex items-center space-x-2 mb-0">
                        <FaUserFriends className="text-3xl"/>
                        <span>Friends</span>
                    </h1>
                    <button
                        onClick={() => setActiveSection("friends")}
                        className={`px-4 py-2 rounded text-white ${
                            activeSection === "friends" ? "bg-gray-700" : "bg-gray-800 hover:bg-gray-700"
                        }`}
                    >
                        Accepted Friends
                    </button>
                    <button
                        onClick={() => setActiveSection("awaiting")}
                        className={`px-4 py-2 rounded text-white ${
                            activeSection === "awaiting" ? "bg-gray-700" : "bg-gray-800 hover:bg-gray-700"
                        }`}
                    >
                        Awaiting Requests
                    </button>
                    <button
                        onClick={() => setActiveSection("pending")}
                        className={`px-4 py-2 rounded text-white ${
                            activeSection === "pending" ? "bg-gray-700" : "bg-gray-800 hover:bg-gray-700"
                        }`}
                    >
                        Pending Requests
                    </button>
                    <button
                        onClick={() => setActiveSection("add")}
                        className={`px-4 py-2 rounded ${activeSection === "add" ? "bg-gray-700 text-green-500" : "bg-green-500 hover:bg-green-400 text-white"}`}
                    >
                        Add Friend
                    </button>
                </div>

                {activeSection === "friends" && (
                    <div>
                        <h2 className="text-lg font-semibold mb-2">Accepted Friends</h2>
                        {acceptedFriends.length > 0 ? (
                            acceptedFriends.map((friend) => (
                                <FriendRequestCard
                                    key={friend.id}
                                    friend={friend}
                                    onAccept={null}
                                    onRefuse={null}
                                    setChatOpen={setChatOpen}
                                    setChatWindow={setChatWindow}
                                    handleBanFriend={(friendId) => handleBanFriend(friendId)}
                                />
                            ))
                        ) : (
                            <p>No accepted friends.</p>
                        )}
                    </div>
                )}

                {activeSection === "awaiting" && (
                    <div>
                        <h2 className="text-lg font-semibold mb-2">Awaiting Friend Requests</h2>
                        {awaitingFriends.length > 0 ? (
                            awaitingFriends.map((friend) => (
                                <FriendRequestCard
                                    key={friend.id}
                                    friend={friend}
                                    onAccept={acceptFriend}
                                    onRefuse={refuseFriend}
                                    handleBanFriend={(friendId) => handleBanFriend(friendId)}
                                />
                            ))
                        ) : (
                            <p>No awaiting requests.</p>
                        )}
                    </div>
                )}

                {activeSection === "pending" && (
                    <div>
                        <h2 className="text-lg font-semibold mb-2">Pending Friend Requests</h2>
                        {pendingFriends.length > 0 ? (
                            pendingFriends.map((friend) => (
                                <FriendRequestCard
                                    key={friend.id}
                                    friend={friend}
                                    onAccept={null}
                                    onRefuse={null}
                                    handleBanFriend={(friendId) => handleBanFriend(friendId)}
                                />
                            ))
                        ) : (
                            <p>No pending requests.</p>
                        )}
                    </div>
                )}

                {activeSection === "add" && (
                    <div>
                        <h2 className="text-lg font-semibold mb-2">Add New Friend</h2>
                        <input
                            type="text"
                            placeholder="Enter username to add"
                            value={newFriendUsername}
                            onChange={(e) => setNewFriendUsername(e.target.value)}
                            className="px-4 py-2 border border-gray-300 rounded-lg w-64"
                        />
                        <button
                            onClick={handleAddFriend}
                            className="ml-2 px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                        >
                            Add Friend
                        </button>
                        {error && <p className="mt-2 text-red-500">{error}</p>}
                    </div>
                )}
            </div>
        </>
    );
};

export default FriendManager;
