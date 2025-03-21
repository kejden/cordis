import React, { useState } from 'react';
import SideBarIcon from "./SideBarIcon.jsx";
import { FaPlus } from "react-icons/fa";
import { IoHomeSharp } from "react-icons/io5";
import axios from "axios";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { BASE_API_URL } from "../../config/api.js";
import { createNewServer, getAllServers } from "../../Redux/Server/Action.js";
import ServerBarIcon from "./ServerBarIcon.jsx";

const ServerBar = ({ servers, openServer, closeServer }) => {
    const dispatch = useDispatch();

    const [isDialogOpen, setIsDialogOpen] = useState(false);

    const [activeTab, setActiveTab] = useState("create");

    const [serverName, setServerName] = useState("");
    const [serverImage, setServerImage] = useState(null);
    const [uploadedImageUrl, setUploadedImageUrl] = useState("");

    const [inviteCode, setInviteCode] = useState("");

    const openDialog = () => {
        setIsDialogOpen(true);
    };

    const closeDialog = () => {
        setIsDialogOpen(false);
        setActiveTab("create");
        setServerName("");
        setServerImage(null);
        setUploadedImageUrl("");
        setInviteCode("");
    };

    const handleOpenServer = (serverId) => {
        openServer(serverId);
    };

    const handleFileUpload = async () => {
        if (!serverImage) return;
        const formData = new FormData();
        formData.append("file", serverImage);

        try {
            const response = await axios.post(`${BASE_API_URL}/api/file/upload`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                withCredentials: true,
            });
            setUploadedImageUrl(response.data);
            toast.success("Image uploaded successfully!");
        } catch (error) {
            toast.error("Error uploading file");
            console.error("Error uploading file: ", error);
        }
    };

    const handleCreateServer = async () => {
        if (!serverName || !uploadedImageUrl) {
            toast.error("Please provide both a server name and an image.");
            return;
        }
        try {
            await dispatch(createNewServer({ name: serverName, image: uploadedImageUrl }));
            toast.success("Server created successfully.");
            dispatch(getAllServers());
            closeDialog();
        } catch (error) {
            toast.error("Error creating server");
            console.error("Error in handleCreateServer: ", error);
        }
    };

    const handleJoinServer = async () => {
        if (!inviteCode) return;
        try {
            await axios.post(
                `${BASE_API_URL}/api/server/join`,
                inviteCode,
                {
                    withCredentials: true,
                    headers: { "Content-Type": "text/plain" },
                }
            );
            toast.success("Successfully joined the server");
            dispatch(getAllServers());
            closeDialog();
        } catch (error) {
            toast.error("Error joining server");
            console.error("Failed to join server:", error);
        }
    };

    return (
        <>
            <div className="fixed top-0 left-0 h-screen w-16 m-0 flex flex-col bg-gray-950 text-white shadow-lg justify-between">
                <div>
                    <SideBarIcon icon={<IoHomeSharp />} onClick={closeServer} />
                    <div className="border-b border-gray-600 my-2" />
                    {servers && servers.length > 0 ? (
                        servers.map((server) => (
                            <ServerBarIcon
                                key={server.id}
                                icon={server.image}
                                text={server.name}
                                onClick={() => handleOpenServer(server.id)}
                            />
                        ))
                    ) : (
                        <p>No servers available</p>
                    )}
                </div>
                <div>
                    <SideBarIcon icon={<FaPlus />} onClick={openDialog} />
                </div>
            </div>

            {isDialogOpen && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-gray-800 text-white p-6 rounded-md shadow-lg w-80">
                        {/* Tabs */}
                        <div className="flex mb-4 border-b border-gray-700">
                            <button
                                onClick={() => setActiveTab("create")}
                                className={`flex-1 py-2 text-center ${
                                    activeTab === "create"
                                        ? "border-b-2 border-blue-500"
                                        : "text-gray-400 hover:text-white"
                                }`}
                            >
                                Create
                            </button>
                            <button
                                onClick={() => setActiveTab("join")}
                                className={`flex-1 py-2 text-center ${
                                    activeTab === "join"
                                        ? "border-b-2 border-blue-500"
                                        : "text-gray-400 hover:text-white"
                                }`}
                            >
                                Join
                            </button>
                        </div>

                        {activeTab === "create" && (
                            <>
                                <label className="block mb-2">
                                    <span className="text-sm font-medium">Server Name</span>
                                    <input
                                        type="text"
                                        value={serverName}
                                        onChange={(e) => setServerName(e.target.value)}
                                        className="w-full mt-1 p-2 bg-gray-700 text-white rounded"
                                    />
                                </label>

                                <label className="block mb-2">
                                    <span className="text-sm font-medium">Server Image</span>
                                    <input
                                        type="file"
                                        onChange={(e) => setServerImage(e.target.files[0])}
                                        className="w-full mt-1 p-2 bg-gray-700 text-white rounded"
                                    />
                                </label>

                                <button
                                    onClick={handleFileUpload}
                                    className="w-full bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded mb-4"
                                    disabled={!serverImage}
                                >
                                    Upload Image
                                </button>

                                <button
                                    onClick={handleCreateServer}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded mb-4"
                                    disabled={!uploadedImageUrl}
                                >
                                    Create Server
                                </button>
                            </>
                        )}

                        {activeTab === "join" && (
                            <>
                                <label className="block mb-2">
                                    <span className="text-sm font-medium">Invite Code</span>
                                    <input
                                        type="text"
                                        value={inviteCode}
                                        onChange={(e) => setInviteCode(e.target.value)}
                                        className="w-full mt-1 p-2 bg-gray-700 text-white rounded"
                                    />
                                </label>
                                <button
                                    onClick={handleJoinServer}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Join Server
                                </button>
                            </>
                        )}

                        <button
                            onClick={closeDialog}
                            className="w-full bg-gray-600 hover:bg-gray-700 text-white py-2 px-4 rounded"
                        >
                            Close
                        </button>
                    </div>
                </div>
            )}
        </>
    );
};

export default ServerBar;