import React from 'react';

const Status = ({ status }) => {
    const isOnline = status === 'ONLINE';

    const styles = {
        container: {
            display: 'flex',
            alignItems: 'center',
        },
        circle: {
            width: '10px',
            height: '10px',
            borderRadius: '50%',
            backgroundColor: isOnline ? 'green' : 'red',
            marginRight: '8px',
        },
        text: {
            fontSize: '14px',
            color: '#fff',
        },
    };

    return (
        <div style={styles.container}>
            <div style={styles.circle}></div>
            <span style={styles.text}>{isOnline ? 'Online' : 'Offline'}</span>
        </div>
    );
};

export default Status;
