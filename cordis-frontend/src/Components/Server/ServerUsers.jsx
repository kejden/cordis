import React, { useEffect, useState } from "react";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import toast from "react-hot-toast";
import Status from "../User/Status.jsx";
import { FaEllipsisV } from "react-icons/fa";

const ServerUsers = ({ serverId, role }) => {
    const [users, setUsers] = useState([]);
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [newRole, setNewRole] = useState("USER");
    const canEditOrDelete = role === "OWNER" || role === "MODERATOR";

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
            setUsers(response.data || []);
        } catch (error) {
            console.error("Error fetching server users:", error);
            toast.error("Error fetching server users");
        }
    };

    const handleUpdateRole = async (serverId, memberId, newRole) => {
        try {
            const response = await axios.put(
                `${BASE_API_URL}/api/member-roles`,
                {serverId: serverId,memberId: memberId, role: newRole },
                { withCredentials: true }
            );
            toast.success("Role updated successfully");
            fetchServerUsers(serverId);
        } catch (error) {
            console.error("Error updating role:", error);
            toast.error("Error updating role");
        }
    };

    const handleDeleteUser = async (serverId, userId) => {
        try {
            await axios.delete(`${BASE_API_URL}/api/member-roles/${serverId}/${userId}`, {
                withCredentials: true,
            });
            toast.success("User removed successfully");
            fetchServerUsers(serverId);
        } catch (error) {
            console.error("Error deleting user:", error);
            toast.error("Error deleting user");
        }
    };

    const openDialog = (userRole) => {
        setSelectedUser(userRole);
        setNewRole(userRole.role.name);
        setIsDialogOpen(true);
    };

    const closeDialog = () => {
        setIsDialogOpen(false);
        setSelectedUser(null);
        setNewRole("USER");
    };

    const canEditUserRole = (targetUserRole) => {
        if (role === "OWNER") {
            return true;
        } else if (role === "MODERATOR") {
            return targetUserRole !== "OWNER";
        }
        return false;
    };

    return (
        <div className="w-64 bg-gray-900 h-screen text-white p-4">
            <h2 className="font-bold text-lg mb-4">Server Users</h2>
            <ul>
                {users.map((userRole) => (
                    <li key={userRole.user.id} className="mb-2 group">
                        <div className="flex items-center">
                            <img
                                src={`http://localhost:8080/uploads/${userRole.user.profileImage}`}
                                alt={userRole.user.userName}
                                className="w-8 h-8 rounded-full mr-2"
                            />
                            <div className="flex-grow">
                                <span className="text-sm">{userRole.user.userName}</span>
                                <span className="text-xs text-gray-400 ml-2">({userRole.role.name})</span>
                                <div className="text-xs text-gray-400 mt-1">
                                    <Status status={userRole.user.status} />
                                </div>
                            </div>
                            {canEditOrDelete && canEditUserRole(userRole.role.name) && (
                                <button
                                    onClick={() => openDialog(userRole)}
                                    className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-gray-300 transition-opacity"
                                >
                                    <FaEllipsisV size={16} />
                                </button>
                            )}
                        </div>
                    </li>
                ))}
            </ul>

            {isDialogOpen && selectedUser && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="bg-gray-800 p-6 rounded-lg w-1/3">
                        <h2 className="text-lg font-semibold mb-4">Edit User</h2>
                        <div className="mb-4">
                            <label className="block text-sm font-medium mb-2">Change Role</label>
                            <select
                                value={newRole}
                                onChange={(e) => setNewRole(e.target.value)}
                                className="bg-gray-700 text-white text-sm p-2 rounded w-full"
                            >
                                <option value="OWNER">OWNER</option>
                                <option value="MODERATOR">MODERATOR</option>
                                <option value="USER">USER</option>
                            </select>
                        </div>
                        <div className="flex justify-end space-x-2">
                            <button
                                onClick={() => {
                                    handleUpdateRole(serverId, selectedUser.user.id, newRole);
                                    closeDialog();
                                }}
                                className="bg-blue-500 text-white px-4 py-2 rounded"
                            >
                                Save
                            </button>
                            <button
                                onClick={() => {
                                    if (window.confirm("Are you sure you want to remove this user?")) {
                                        handleDeleteUser(serverId, selectedUser.user.id);
                                        closeDialog();
                                    }
                                }}
                                className="bg-red-500 text-white px-4 py-2 rounded"
                            >
                                Delete
                            </button>
                            <button
                                onClick={closeDialog}
                                className="bg-gray-500 text-white px-4 py-2 rounded"
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ServerUsers;