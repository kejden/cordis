import React from 'react';

const SideBarIcon = ({ icon, text,  onClick }) => {
    return (
        <>
            <div className="sidebar-icon"
                 onClick={onClick}
            >
                {icon}
            </div>
            <span className="sidebar-tooltip">
                {text}
            </span>
        </>
    );
};

export default SideBarIcon; 