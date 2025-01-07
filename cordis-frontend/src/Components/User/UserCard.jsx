import pfp from "../../assets/img/pfp.jpg";
import { BiCog } from "react-icons/bi";
import {useDispatch, useSelector} from "react-redux";
import {useState} from "react";
import {logoutAction} from "../../Redux/Auth/Action.js";
import {useNavigate} from "react-router-dom";

const UserCard = () => {
    const { auth } = useSelector((store) => store);
    const dispatch = useDispatch();
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const navigate = useNavigate();

    function userProfileEdit() {
        setIsDialogOpen(true);
    }

    function handleLogout() {
        dispatch(logoutAction()).then(() => {
            console.log(auth)
            navigate("/login");
        });
    }

    function closeDialog() {
        setIsDialogOpen(false);
    }

    return (
        <div className="flex items-center text-white">
            <div className="m-1 w-10 h-10">
                <img
                    src={pfp}
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
                <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-gray-800 text-white p-6 rounded-md shadow-lg w-80">
                        <h2 className="text-lg font-semibold mb-4">Profile Settings</h2>
                        <button
                            onClick={handleLogout}
                            className="w-full bg-red-600 hover:bg-red-700 text-white py-2 px-4 rounded mb-4"
                        >
                            Logout
                        </button>
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
