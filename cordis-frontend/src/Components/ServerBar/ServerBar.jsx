import React from 'react';
import SideBarIcon from "./SideBarIcon.jsx";
import {CgDice1, CgDice2, CgDice3, CgDice4, CgDice5, CgDice6} from "react-icons/cg";

const ServerBar = () => {
    return(
        <>
            <div
                className="fixed top-0 left-0 h-screen w-16 m-0 flex flex-col bg-gray-950 text-white shadow-lg">
                <SideBarIcon icon={<CgDice1/>}/>
                <SideBarIcon icon={<CgDice2/>}/>
                <SideBarIcon icon={<CgDice3/>}/>
                <SideBarIcon icon={<CgDice4/>}/>
                <SideBarIcon icon={<CgDice5/>}/>
                <SideBarIcon icon={<CgDice6/>}/>
                <SideBarIcon icon={<CgDice1/>}/>
                <SideBarIcon icon={<CgDice2/>}/>
                <SideBarIcon icon={<CgDice3/>}/>
                <SideBarIcon icon={<CgDice6/>}/>
            </div>
        </>
    );
};

export default ServerBar;