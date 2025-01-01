import './App.css'
import {Routes, Route, Router} from "react-router-dom";
import Login from "./Components/Auth/Login.jsx";
import Register from "./Components/Auth/Register.jsx";
import React from "react";
import {Toaster} from "react-hot-toast";
import HomePage from "./Components/HomePage.jsx";


function App() {
    return (
        <>
            <Toaster position="top-center" reverseOrder={false} />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                {/*<Route path="/friends" element={<FriendManager />} />*/}
            </Routes>
        </>
    );
}

export default App
