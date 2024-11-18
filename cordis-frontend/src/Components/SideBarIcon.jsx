import React from 'react';

const SideBarIcon = ({ icon, text = 'tooltip 💡' }) => {
    return (
        <>
            <div className="sidebar-icon">
                {icon}
            </div>
            <span class="sidebar-tooltip">
                {text}
            </span>
        </>
    );
};

export default SideBarIcon; 