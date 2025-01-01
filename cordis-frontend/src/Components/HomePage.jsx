import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useSelector} from "react-redux";
import ServerBar from "./ServerBar/ServerBar.jsx";
import FriendManager from "./Friends/FriendManager.jsx";

const HomePage = () => {
    const { auth } = useSelector((store) => store);
    const navigate = useNavigate();

    useEffect(() => {
        if (!auth.reqUser) {
            navigate("/login");
        }
    }, [auth.reqUser]);

    return (
        <>
                <ServerBar />
                <FriendManager/>
        </>
    )
}

export default HomePage;