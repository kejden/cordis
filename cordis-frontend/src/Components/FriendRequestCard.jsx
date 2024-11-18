import React, {useEffect, useState} from "react";
import pfp from '../assets/img/pfp.jpg'
import {BsChatFill} from "react-icons/bs";
import {SlOptionsVertical} from "react-icons/sl";

const FriendRequestCard = ({ friend, onAccept, onRefuse }) => {
    // const [showOptions, setShowOptions] = useState(false);
    // const dropdownRef = useRef(null); // Reference for dropdown container
    //
    // // Close dropdown if clicking outside
    // useEffect(() => {
    //     const handleClickOutside = (event) => {
    //         if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
    //             setShowOptions(false);
    //         }
    //     };
    //     document.addEventListener("mousedown", handleClickOutside);
    //
    //     return () => {
    //         document.removeEventListener("mousedown", handleClickOutside);
    //     };
    // }, []);

    return (
        <>
            {/*<hr className="border-t-2 border-t-gray-600"/>*/}
            <div
                key={friend.userId}
                className="w-2/3 p-4 bg-gray-800 hover:bg-gray-600 text-white rounded flex justify-between items-center border-t-2 border-t-gray-600"
            >

                <div className="flex items-center space-x-4">
                    {/* Avatar */}
                    <div className="w-10 h-10">
                        <img
                            src={pfp}
                            alt={`${friend.userName}'s avatar`}
                            className="w-10 h-10 rounded-full object-cover"
                        />
                    </div>
                    {/* User details */}
                    <div>
                        <p className="text-base font-medium">{friend.userName}</p>
                        <p className="text-sm text-gray-400">{friend.email}</p>
                    </div>
                </div>
                <div className="flex space-x-3">
                    {onAccept && (
                        <button
                            onClick={() => onAccept(friend.id)}
                            className="px-3 py-1.5 bg-green-600 text-sm rounded hover:bg-green-700 transition"
                        >
                            Akceptuj
                        </button>
                    )}
                    {onRefuse && (
                        <button
                            onClick={() => onRefuse(friend.id)}
                            className="px-3 py-1.5 bg-red-600 text-sm rounded hover:bg-red-700 transition"
                        >
                            Odrzuć
                        </button>
                    )}
                </div>
                {!onAccept && (<div className="flex items-center float-end space-x-2">
                    <div className="rounded-full  inline-block bg-gray-700 hover:bg-gray-900">
                        <BsChatFill className="text-xl m-2.5" />
                    </div>

                    <div className="rounded-full inline-block bg-gray-700 hover:bg-gray-900"
                         // onClick={() => setShowOptions((prev) => !prev)}
                    >
                        <SlOptionsVertical className="text-xl m-2.5"/>
                    </div>

                </div>) }

                {/*{showOptions && (*/}
                {/*    <div className="absolute right-0 mt-2 w-40 bg-gray-800 rounded shadow-lg z-10">*/}
                {/*        <button*/}
                {/*            className="block w-full px-4 py-2 text-left text-sm text-white hover:bg-red-500 hover:text-white rounded"*/}
                {/*            onClick={() => alert("User Blocked!")} // Replace with actual block logic*/}
                {/*        >*/}
                {/*            Block User*/}
                {/*        </button>*/}
                {/*    </div>*/}
                {/*)}*/}

            </div>
        </>
    );
};

export default FriendRequestCard;
