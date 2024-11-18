import React, { useEffect, useState } from "react";
import FriendRequestCard from "./FriendRequestCard"; // Import komponentu karty zaproszenia znajomego
import { FaUserFriends } from "react-icons/fa";


const FriendManager = () => {
    const [acceptedFriends, setAcceptedFriends] = useState([]);
    const [awaitingFriends, setAwaitingFriends] = useState([]);
    const [pendingFriends, setPendingFriends] = useState([]);
    const [newFriendUsername, setNewFriendUsername] = useState("");
    const [error, setError] = useState("");
    const [activeSection, setActiveSection] = useState("friends"); // Dodajemy stan dla aktywnej sekcji

    useEffect(() => {
        fetchAcceptedFriends();
        fetchAwaitingFriends();
        fetchPendingFriends();
    }, []);

    const getAccessToken = () => {
        return localStorage.getItem("accessToken");
    };

    const fetchAcceptedFriends = async () => {
        try {
            const token = getAccessToken();
            const response = await fetch("http://localhost:8080/api/friend/responses", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                },
                credentials: "include",
            });

            if (!response.ok) {
                console.error("Failed to fetch accepted friends:", response.status);
                return;
            }

            const data = await response.json();
            console.log(data)
            setAcceptedFriends(data);
        } catch (error) {
            console.error("Error fetching accepted friends:", error);
        }
    };

    const fetchAwaitingFriends = async () => {
        try {
            const token = getAccessToken();
            const response = await fetch("http://localhost:8080/api/friend/awaiting", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                },
                credentials: "include",
            });

            if (!response.ok) {
                console.error("Failed to fetch awaiting friends:", response.status);
                return;
            }

            const data = await response.json();
            console.log(data)
            setAwaitingFriends(data);
        } catch (error) {
            console.error("Error fetching awaiting friends:", error);
        }
    };

    const fetchPendingFriends = async () => {
        try {
            const token = getAccessToken();
            const response = await fetch("http://localhost:8080/api/friend/pending", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                },
                credentials: "include",
            });

            if (!response.ok) {
                console.error("Failed to fetch pending friends:", response.status);
                return;
            }

            const data = await response.json();
            console.log(data)
            setPendingFriends(data);
        } catch (error) {
            console.error("Error fetching pending friends:", error);
        }
    };

    const acceptFriend = async (id) => {
        try {
            const token = getAccessToken();
            const response = await fetch(`http://localhost:8080/api/friend/accept/${id}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                },
                credentials: "include",
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
            const token = getAccessToken();
            const response = await fetch(`http://localhost:8080/api/friend/refuse/${id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`,
                },
                credentials: "include",
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
            const token = getAccessToken();
            const response = await fetch("http://localhost:8080/api/friend/request", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify({ userName: newFriendUsername }),
            });

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

    return (
        <div className="w-4/5 h-screen p-6 bg-gray-800 text-white float-end">
            {/* Button Controls */}
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
    );
};

export default FriendManager;
