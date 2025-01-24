import React, { useEffect, useState } from 'react';
import UserCard from "../User/UserCard.jsx";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import { FaPlus, FaEdit, FaTrash, FaEllipsisV } from 'react-icons/fa';
import toast from "react-hot-toast";

const ServerSideBar = ({ server, onChannelClick, serverName, role }) => {
    const canEditOrDelete = role === "OWNER" || role === "MODERATOR";
    const [isDashboardOpen, setIsDashboardOpen] = useState(false);
    const [channelName, setChannelName] = useState('');
    const [channels, setChannels] = useState([]);
    const [invitationKeys, setInvitationKeys] = useState([]);

    useEffect(() => {
        const fetchServerChannels = async (serverID) => {
            try {
                const response = await axios.get(`${BASE_API_URL}/api/server-channel/${serverID}`, {
                    withCredentials: true,
                });
                setChannels(response.data);
            } catch (error) {
                console.error('Error fetching server channels:', error);
            }
        };

        const fetchActiveInvitationKeys = async (serverID) => {
            try {
                const response = await axios.get(`${BASE_API_URL}/api/invitation-keys/active/${serverID}`, {
                    withCredentials: true,
                });
                setInvitationKeys(response.data || []);
            } catch (error) {
                console.error("Error fetching invitation keys:", error);
                toast.error("Error fetching invitation keys");
            }
        };

        if (server) {
            fetchServerChannels(server);
            fetchActiveInvitationKeys(server);
        }
    }, [server]);

    const handleCreateChannel = async () => {
        try {
            const response = await axios.post(
                `${BASE_API_URL}/api/server-channel`,
                {
                    name: channelName,
                    serverId: server,
                },
                {
                    withCredentials: true,
                }
            );

            setChannels([...channels, response.data]);
            setChannelName('');
            toast.success("Channel created successfully");
        } catch (error) {
            console.error('Error creating channel:', error);
            toast.error("Error creating channel");
        }
    };

    const handleEditChannel = async (channelId, updatedName) => {
        try {
            const response = await axios.put(
                `${BASE_API_URL}/api/server-channel/${channelId}`,
                { name: updatedName },
                { withCredentials: true }
            );
            toast.success("Channel updated successfully");
            setChannels(channels.map(channel =>
                channel.id === channelId ? { ...channel, name: updatedName } : channel
            ));
        } catch (error) {
            console.error("Error updating channel:", error);
            toast.error("Error updating channel");
        }
    };

    const handleDeleteChannel = async (channelId) => {
        try {
            await axios.delete(`${BASE_API_URL}/api/server-channel/${channelId}`, {
                withCredentials: true,
            });
            toast.success("Channel deleted successfully");
            setChannels(channels.filter(channel => channel.id !== channelId));
        } catch (error) {
            console.error("Error deleting channel:", error);
            toast.error("Error deleting channel");
        }
    };

    const handleGenerateInvitationKey = async () => {
        try {
            const response = await axios.post(
                `${BASE_API_URL}/api/invitation-keys/generate/${server}`,
                {},
                { withCredentials: true }
            );
            toast.success("Invitation key generated successfully");
            const keysResponse = await axios.get(`${BASE_API_URL}/api/invitation-keys/active/${server}`, {
                withCredentials: true,
            });
            setInvitationKeys(keysResponse.data || []);
        } catch (error) {
            console.error("Error generating invitation key:", error);
            toast.error("Error generating invitation key");
        }
    };

    const handleDeleteServer = async () => {
        try {
            await axios.delete(`${BASE_API_URL}/api/server/${server}`, {
                withCredentials: true,
            });
            toast.success("Server deleted successfully");
            window.location.reload();
        } catch (error) {
            console.error("Error deleting server:", error);
            toast.error("Error deleting server");
        }
    };

    const openDashboard = () => {
        setIsDashboardOpen(true);
    };

    const closeDashboard = () => {
        setIsDashboardOpen(false);
    };

    return (
        <>
            <div className="flex w-1/5">
                <div className="w-1/5"></div>
                <div className="flex flex-col w-4/5 bg-gray-900">
                    <div className="flex-grow">
                        <div className="flex flex-col p-7 items-center justify-center w-full">
                            <div className="flex items-center justify-between w-full">
                                <h2 className="text-lg font-semibold text-white">{serverName}</h2>
                                <button
                                    onClick={openDashboard}
                                    className="text-gray-400 hover:text-gray-300 transition-colors"
                                >
                                    <FaEllipsisV size={20} />
                                </button>
                            </div>
                        </div>
                        <div className="flex flex-col">
                            {channels.map((channel) => (
                                <div
                                    key={channel.id}
                                    className="group flex items-center justify-between text-white p-2 hover:bg-gray-800 cursor-pointer"
                                    onClick={() => onChannelClick(channel)}
                                >
                                    <span>{channel.name}</span>
                                    {canEditOrDelete && (
                                        <div className="flex space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    const newName = prompt("Enter new channel name:", channel.name);
                                                    if (newName) {
                                                        handleEditChannel(channel.id, newName);
                                                    }
                                                }}
                                                className="text-blue-400 hover:text-blue-300"
                                            >
                                                <FaEdit size={16} />
                                            </button>
                                            <button
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    if (window.confirm("Are you sure you want to delete this channel?")) {
                                                        handleDeleteChannel(channel.id);
                                                    }
                                                }}
                                                className="text-red-400 hover:text-red-300"
                                            >
                                                <FaTrash size={16} />
                                            </button>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="bg-gray-950 h-14">
                        <UserCard />
                    </div>
                </div>
            </div>

            {isDashboardOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-20">
                    <div className="bg-gray-800 p-6 rounded-md shadow-lg w-[600px]">
                        <h2 className="text-lg font-semibold mb-4">Server Dashboard</h2>
                        <div className="mb-4">
                            <input
                                type="text"
                                placeholder="Channel Name"
                                value={channelName}
                                onChange={(e) => setChannelName(e.target.value)}
                                className="w-full p-2 mb-4 bg-gray-700 text-white rounded"
                            />
                            {canEditOrDelete && (
                                <button
                                    onClick={handleCreateChannel}
                                    className="w-full bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Create Channel
                                </button>
                            )}
                        </div>
                        <ul className="max-h-[400px] overflow-y-auto">
                            {invitationKeys.map((key) => (
                                <li key={key.id} className="text-white p-4 bg-gray-700 rounded mb-2 flex justify-between items-center">
                                    <div>
                                        <span className="text-sm font-medium">{key.invitationKey}</span>
                                        <span className="text-xs text-gray-400 block mt-1">
                                            Expires: {new Date(key.expirationTime).toLocaleString()}
                                        </span>
                                    </div>
                                    <button
                                        onClick={() => navigator.clipboard.writeText(key.invitationKey)}
                                        className="text-gray-400 hover:text-gray-300"
                                    >
                                        Copy
                                    </button>
                                </li>
                            ))}
                        </ul>
                        <div className="mt-4">
                            {canEditOrDelete && (
                                <button
                                    onClick={handleGenerateInvitationKey}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Generate New Key
                                </button>
                            )}
                            {role === "OWNER" && (
                                <button
                                    onClick={() => {
                                        if (window.confirm("Are you sure you want to delete this server?")) {
                                            handleDeleteServer();
                                        }
                                    }}
                                    className="w-full bg-red-600 hover:bg-red-700 text-white py-2 px-4 rounded"
                                >
                                    Delete Server
                                </button>
                            )}
                        </div>
                        <button
                            onClick={closeDashboard}
                            className="w-full bg-gray-600 hover:bg-gray-700 text-white py-2 px-4 rounded mt-4"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </>
    );
};

export default ServerSideBar;