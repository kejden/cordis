import React from 'react';

const SideBarIcon = ({ icon, text = 'tooltip 💡' }) => {
    return (
        <>
            <div className="sidebar-icon">
                {icon}
            </div>
            <span className="sidebar-tooltip">
                {text}
            </span>
        </>
    );
};

export default SideBarIcon; 