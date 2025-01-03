import React from 'react';
import UserCard from "./UserCard.jsx";

const FruebdSideBar = () => {
    return (
        <>
            <div className="flex w-1/5">
                <div className="w-1/5">
                </div>
                <div className="flex flex-col w-4/5 bg-gray-900">
                    <div className="flex-grow">

                    </div>
                    <div className="bg-gray-950 h-14">
                        <UserCard/>
                    </div>
                </div>
            </div>
        </>
    );
};

export default FruebdSideBar;
