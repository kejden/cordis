import './App.css'
import { Routes, Route } from "react-router-dom";
import Login from "./Components/Login.jsx";
import Register from "./Components/Register.jsx";
import Home from "./Components/Home.jsx";
import FriendManager from "./Components/FriendManager.jsx";


function App() {
    return (
        <>
            <Routes>
                <Route path="/h" component={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/friends" element={<FriendManager />} />
            </Routes>
        </>
    );
}

export default App
