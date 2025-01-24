import pfp from "../../assets/img/pfp.jpg";
import { BiCog } from "react-icons/bi";
import { useDispatch, useSelector } from "react-redux";
import { useEffect, useState } from "react";
import { logoutAction } from "../../Redux/Auth/Action.js";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { BASE_API_URL } from "../../config/api.js";
import toast from "react-hot-toast";
import { UPDATE_PROFILE_IMAGE, UPDATE_USER } from "../../Redux/Auth/ActionType.js";
import {getAllServers} from "../../Redux/Server/Action.js";

const UserCard = () => {
    const { auth } = useSelector((store) => store);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [nickname, setNickname] = useState(auth.reqUser.userName);
    const [profileImage, setProfileImage] = useState(null);
    const [activeTab, setActiveTab] = useState("profile");
    const [inviteCode, setInviteCode] = useState("");

    function userProfileEdit() {
        setIsDialogOpen(true);
    }

    function closeDialog() {
        setIsDialogOpen(false);
        setActiveTab("profile");
        setInviteCode("");
    }

    function handleLogout() {
        dispatch(logoutAction()).then(() => {
            navigate("/login");
        });
    }

    async function handleNicknameUpdate() {
        try {
            const response = await axios.post(`${BASE_API_URL}/profile/edit`, {
                username: nickname,
            }, { withCredentials: true });
            const data = await response.data;

            dispatch({ type: UPDATE_USER, payload: data });

            toast.success("Nickname updated successfully");
            setIsDialogOpen(false);
        } catch (error) {
            toast.error("Error updating nickname");
            console.error("Failed to update nickname:", error);
        }
    }

    async function handleProfileImageUpdate() {
        const formData = new FormData();
        formData.append("image", profileImage);

        try {
            const response = await axios.put(`${BASE_API_URL}/profile/image`, formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                withCredentials: true,
            });
            const data = await response.data;
            const strippedData = data.replace(/^.*\/uploads\//, '');

            dispatch({ type: UPDATE_PROFILE_IMAGE, payload: { profileImage: strippedData } });

            toast.success("Profile image updated successfully");
            setIsDialogOpen(false);
        } catch (error) {
            toast.error("Error updating profile image");
            console.error("Failed to update profile image:", error);
        }
    }

    async function handleJoinServer() {
        try {
            const response = await axios.post(
                `${BASE_API_URL}/api/server/join`,
                inviteCode,
                {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "text/plain",
                    },
                }
            );
            toast.success("Successfully joined the server");
            dispatch(getAllServers());
            closeDialog();
        } catch (error) {
            toast.error("Error joining server");
            console.error("Failed to join server:", error);
        }
    }

    return (
        <div className="flex items-center text-white">
            <div className="m-1 w-10 h-10">
                <img
                    src={`http://localhost:8080/uploads/${auth.reqUser.profileImage}`}
                    alt={`${auth.reqUser.userName}'s avatar`}
                    className="w-10 h-10 rounded-full object-cover"
                />
            </div>
            <div className="flex-grow p-4">
                <p className="text-base font-medium">{auth.reqUser.userName}</p>
            </div>
            <div
                className="m-3 p-1 text-2xl hover:rounded-full hover:bg-gray-600 hover:animate-spin cursor-pointer"
                onClick={userProfileEdit}
            >
                <BiCog />
            </div>

            {isDialogOpen && (
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20">
                    <div className="bg-gray-800 text-white p-6 rounded-md shadow-lg w-80">
                        {/* Tabs */}
                        <div className="flex mb-4 border-b border-gray-700">
                            <button
                                onClick={() => setActiveTab("profile")}
                                className={`flex-1 py-2 text-center ${
                                    activeTab === "profile"
                                        ? "border-b-2 border-blue-500"
                                        : "text-gray-400 hover:text-white"
                                }`}
                            >
                                Profile
                            </button>
                            <button
                                onClick={() => setActiveTab("joinServer")}
                                className={`flex-1 py-2 text-center ${
                                    activeTab === "joinServer"
                                        ? "border-b-2 border-blue-500"
                                        : "text-gray-400 hover:text-white"
                                }`}
                            >
                                Join Server
                            </button>
                        </div>

                        {activeTab === "profile" && (
                            <>
                                <label className="block mb-2">
                                    <span className="text-sm font-medium">Nickname</span>
                                    <input
                                        type="text"
                                        value={nickname}
                                        onChange={(e) => setNickname(e.target.value)}
                                        className="w-full mt-1 p-2 bg-gray-700 text-white rounded"
                                    />
                                </label>
                                <button
                                    onClick={handleNicknameUpdate}
                                    className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Update Nickname
                                </button>

                                <label className="block mb-2">
                                    <span className="text-sm font-medium">Profile Image</span>
                                    <input
                                        type="file"
                                        onChange={(e) => setProfileImage(e.target.files[0])}
                                        className="w-full mt-1 p-2 bg-gray-700 text-white rounded"
                                    />
                                </label>
                                <button
                                    onClick={handleProfileImageUpdate}
                                    className="w-full bg-green-600 hover:bg-green-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Update Profile Image
                                </button>

                                <button
                                    onClick={handleLogout}
                                    className="w-full bg-red-600 hover:bg-red-700 text-white py-2 px-4 rounded mb-4"
                                >
                                    Logout
                                </button>
                            </>
                        )}

                        {activeTab === "joinServer" && (
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
        </div>
    );
};

export default UserCard;