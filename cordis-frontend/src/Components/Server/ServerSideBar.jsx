import React, { useEffect, useState } from 'react';
import UserCard from "../User/UserCard.jsx";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';
import toast from "react-hot-toast";

const ServerSideBar = ({ server, onChannelClick, serverName }) => {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [channelName, setChannelName] = useState('');
    const [channels, setChannels] = useState([]);
    const [userRole, setUserRole] = useState(null);

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

        const fetchUserRole = async (serverID) => {
            try {
                const response = await axios.get(`${BASE_API_URL}/api/server/${serverID}/role`, {
                    withCredentials: true,
                });
                setUserRole(response.data);
            } catch (error) {
                console.error("Error fetching user role:", error);
                toast.error("Error fetching user role");
            }
        };

        if (server) {
            fetchServerChannels(server);
            fetchUserRole(server);
        }
    }, [server]);

    const handleOpenDialog = () => {
        setIsDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setIsDialogOpen(false);
        setChannelName('');
    };

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

            handleCloseDialog();
        } catch (error) {
            console.error('Error creating channel:', error);
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

    const canEditOrDelete = userRole?.name === "OWNER" || userRole?.name === "MODERATOR";

    return (
        <>
            <div className="flex w-1/5">
                <div className="w-1/5"></div>
                <div className="flex flex-col w-4/5 bg-gray-900">
                    <div className="flex-grow">
                        <div className="flex flex-col p-7 items-center justify-center w-full">
                            <div className="flex items-center justify-between w-full">
                                <h2 className="text-lg font-semibold text-white">{serverName}</h2>
                                {canEditOrDelete && (
                                    <button
                                        onClick={handleOpenDialog}
                                        className="text-green-500 hover:text-green-400 transition-colors"
                                    >
                                        <FaPlus size={20} />
                                    </button>
                                )}
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

            {isDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="bg-gray-800 p-6 rounded-lg w-1/3">
                        <h2 className="text-lg font-semibold mb-4 text-white">Create New Channel</h2>
                        <input
                            type="text"
                            placeholder="Channel Name"
                            value={channelName}
                            onChange={(e) => setChannelName(e.target.value)}
                            className="w-full p-2 mb-4 bg-gray-700 text-white rounded"
                        />
                        <div className="flex justify-end">
                            <button
                                onClick={handleCloseDialog}
                                className="bg-gray-500 text-white px-4 py-2 rounded mr-2"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleCreateChannel}
                                className="bg-blue-500 text-white px-4 py-2 rounded"
                            >
                                Create
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
};

export default ServerSideBar;