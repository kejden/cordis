import React, { useEffect, useState } from "react";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import toast from "react-hot-toast";
import Status from "../User/Status.jsx";

const ServerUsers = ({ serverId }) => {
    const [users, setUsers] = useState([]);

    useEffect(() => {
        if (serverId) {
            fetchServerUsers(serverId);
        }
    }, [serverId]);

    const fetchServerUsers = async (serverId) => {
        try {
            const response = await axios.get(`${BASE_API_URL}/api/server/${serverId}/users`, {
                withCredentials: true,
            });
            // Store the full response in the state
            setUsers(response.data || []);
        } catch (error) {
            console.error("Error fetching server users:", error);
            toast.error("Error fetching server users");
        }
    };

    return (
        <div className="w-64 bg-gray-900 h-screen text-white p-4">
            <h2 className="font-bold text-lg mb-4">Server Users</h2>
            <ul>
                {users.map((userRole) => (
                    <li key={userRole.user.id} className="mb-2">
                        <div className="flex items-center">
                            <img
                                src={`http://localhost:8080/uploads/${userRole.user.profileImage}`}
                                alt={userRole.user.userName}
                                className="w-8 h-8 rounded-full mr-2"
                            />
                            <div>
                                <span className="text-sm">{userRole.user.userName}</span>
                                <span className="text-xs text-gray-400 ml-2">({userRole.role.name})</span>
                                <div className="text-xs text-gray-400 mt-1">
                                    <Status status={userRole.user.status}/>
                                </div>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};


export default ServerUsers;