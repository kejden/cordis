import React, { useEffect, useState } from 'react';
import UserCard from "../User/UserCard.jsx";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import { FaPlus } from 'react-icons/fa';

const ServerSideBar = ({ server, onChannelClick }) => {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [channelName, setChannelName] = useState('');
    const [channels, setChannels] = useState([]);

    useEffect(() => {
        const fetchServerChannels = async (serverID) => {
            try {
                const response = await axios.get(`${BASE_API_URL}/api/server-channel/${serverID}`, {
                    withCredentials: true,
                });
                console.log('Server Channels:', response.data);
                setChannels(response.data);
            } catch (error) {
                console.error('Error fetching server channels:', error);
            }
        };
        fetchServerChannels(server);
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
            console.log('Channel created:', response.data);

            setChannels([...channels, response.data]);

            handleCloseDialog();
        } catch (error) {
            console.error('Error creating channel:', error);
        }
    };

    return (
        <>
            <div className="flex w-1/5">
                <div className="w-1/5"></div>
                <div className="flex flex-col w-4/5 bg-gray-900">
                    <div className="flex-grow">
                        <div className="flex flex-col p-7 items-center justify-center w-full">
                            <div className="flex items-center justify-between w-full">
                                <h2 className="text-lg font-semibold text-white">Server Channels</h2>
                                <button
                                    onClick={handleOpenDialog}
                                    className="text-green-500 hover:text-green-400 transition-colors"
                                >
                                    <FaPlus size={20} />
                                </button>
                            </div>
                        </div>
                        <div className="flex flex-col">
                            {channels.map((channel) => (
                                <div
                                    key={channel.id}
                                    className="text-white p-2 hover:bg-gray-800 cursor-pointer"
                                    onClick={() => onChannelClick(channel)}
                                >
                                    {channel.name}
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