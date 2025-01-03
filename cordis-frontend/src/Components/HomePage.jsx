import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {useSelector} from "react-redux";
import DisplayPage from "./DisplayPage.jsx";

const HomePage = () => {
    const { auth } = useSelector((store) => store);
    const navigate = useNavigate();

    useEffect(() => {
        if (auth.reqUser == null) {
            navigate("/login");
        }
    }, [auth.reqUser]);

    return (
        <>
            {auth.reqUser && <DisplayPage />}
        </>
    )
}

export default HomePage;