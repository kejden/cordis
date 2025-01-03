import ServerBar from "./ServerBar/ServerBar.jsx";
import FriendManager from "./Friends/FriendManager.jsx";
import {useEffect, useState} from "react";
import FriendSideBar from "./User/FriendSideBar.jsx";

const DisplayPage = () => {
    const [chatOpen, setChatOpen] = useState(false);
    const [chatWindow, setChatWindow] = useState(null);

    useEffect(() => {
        if(chatOpen !== false) {
            console.log(chatWindow)
        }
    }, [chatOpen])


    return (
        <>
            <ServerBar/>
            <div className="flex h-screen">
                <FriendSideBar/>
                {!chatOpen && <FriendManager
                    setChatOpen={setChatOpen}
                    setChatWindow={setChatWindow}/>}
            </div>
        </>
    )
}

export default DisplayPage;