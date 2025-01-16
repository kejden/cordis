import React, {useEffect, useState} from 'react';
import SideBarIcon from "./SideBarIcon.jsx";
import {CgDice1, CgDice2, CgDice3, CgDice4, CgDice5} from "react-icons/cg";
import {FaPlus} from "react-icons/fa";
import axios from "axios";
import {BASE_API_URL} from "../../config/api.js";
import { createNewServer } from "../../Redux/Server/Action.js";
import toast from "react-hot-toast";
import {useDispatch} from "react-redux";
import ServerBarIcon from "./ServerBarIcon.jsx";

const ServerBar = ({servers}) => {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [serverName, setServerName] = useState("");
    const [serverImage, setServerImage] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const dispatch = useDispatch();

    const openDialog = () => setIsDialogOpen(true);
    const closeDialog = () => {
        setIsDialogOpen(false);
        setServerName("");
        setServerImage("");
        setSelectedFile(null);
    };

    const handleFileUpload = async () => {
        if (!selectedFile) return;
        const formData = new FormData();
        formData.append("file", selectedFile);

        try {
            const response = await axios.post(`${BASE_API_URL}/api/file/upload`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                withCredentials: true,
            });
            setServerImage(response.data);
        } catch (error) {
            toast.error("Error uploading file");
            console.error("Error uploading file: " + error)

        }
    };

    const handleCreateServer = async () => {
        if (!serverName || !serverImage) {
            toast.error("Server name and image are required.");
            return;
        }

        try {
            await dispatch(createNewServer({ name: serverName, image: serverImage }));
            toast.success("Server created successfully.");
            closeDialog();
        } catch (error) {
            console.error("Error in handleCreateServer: ", error);
        }
    };

    return (
        <>
            <div
                className="fixed top-0 left-0 h-screen w-16 m-0 flex flex-col bg-gray-950 text-white shadow-lg justify-between"
            >
                <div>
                    <SideBarIcon icon={<CgDice1/>}/>

                    {servers && servers.length > 0 ? (
                        servers.map((server) => (
                            <ServerBarIcon
                                key={server.id}
                                icon={server.image}
                                text={server.name}
                            />
                        ))
                    ) : (
                        <p>No servers available</p>
                    )}
                </div>
                <div>
                    <SideBarIcon icon={<FaPlus/>} onClick={openDialog}/>
                </div>
            </div>

            {isDialogOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-1/3">
                        <h2 className="text-xl font-bold mb-4">Create Server</h2>
                        <label className="block mb-2">
                            <span className="text-gray-700">Server Name</span>
                            <input
                                type="text"
                                value={serverName}
                                onChange={(e) => setServerName(e.target.value)}
                                className="w-full p-2 border rounded mt-1"
                            />
                        </label>
                        <label className="block mb-4">
                            <span className="text-gray-700">Upload Server Image</span>
                            <input
                                type="file"
                                onChange={(e) => setSelectedFile(e.target.files[0])}
                                className="w-full p-2 border rounded mt-1"
                            />
                            <button
                                onClick={handleFileUpload}
                                className="mt-2 px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                            >
                                Upload
                            </button>
                        </label>
                        <div className="flex justify-end">
                            <button
                                onClick={closeDialog}
                                className="px-4 py-2 bg-gray-400 text-white rounded mr-2 hover:bg-gray-500"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleCreateServer}
                                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                                disabled={!serverImage}
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

export default ServerBar;