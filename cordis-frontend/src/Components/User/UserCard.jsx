import pfp from "../../assets/img/pfp.jpg";
import { BiCog } from "react-icons/bi";
import {useDispatch, useSelector} from "react-redux";
// import {logoutAction} from "../Redux/Auth/Action.js";

const UserCard = () => {
    const { auth } = useSelector((store) => store);
    // const dispatch = useDispatch();

    function userProfileEdit() {

        console.log("userProfileEdit");
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
        </div>
    );
};

export default UserCard;
