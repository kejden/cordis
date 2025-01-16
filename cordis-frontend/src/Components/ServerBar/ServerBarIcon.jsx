import React from 'react';

const ServerBarIcon = ({ icon, text,  onClick }) => {
    return (
        <>
            <div className="sidebar-icon"
                 onClick={onClick}
            >
                <img
                    src={`http://localhost:8080/uploads/${icon}`}
                    alt={`${text}'s server icon`}
                />
            </div>
            <span className="sidebar-tooltip">
                {text}
            </span>
        </>
    );
};

export default ServerBarIcon;