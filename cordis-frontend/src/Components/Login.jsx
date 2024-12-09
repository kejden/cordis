import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import {login} from "../Redux/Auth/Action.js";
import toast from "react-hot-toast";


const Login = () => {
    const [user, setUser] = useState({
        email: "",
        password: ""
    });
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const handleSubmit = async (e) => {
        e.preventDefault();
        dispatch(login(user))
            .then(() => {
                navigate("/friends");
                toast.success("You have logged in successfully.");
            })
            .catch(() => {
                toast.error("Failed to login. Please try again.");
            })
    };

    const onChange = (event) =>{
        const {name, value} = event.target;
        setUser({...user, [name]: value});
    }


    return (
        <>
            <div className="loginBackGround">
                <div className="lex items-center justify-center">
                    <div className="w-full max-w-sm bg-gray-800 text-white p-6 rounded-lg shadow-md">
                        <h2 className="text-2xl font-bold text-center mb-4">Welcome back!</h2>
                        <p className="text-center text-gray-400 mb-6">We're so excited to see you again!</p>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">
                                    EMAIL<span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="text"
                                    placeholder="Enter your email"
                                    name="email"
                                    value={user.email}
                                    onChange={onChange}
                                    className="w-full px-4 py-2 bg-gray-700 text-white border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">
                                    PASSWORD <span className="text-red-500">*</span>
                                </label>
                                <input
                                    type="password"
                                    placeholder="Enter your password"
                                    name="password"
                                    value={user.password}
                                    onChange={onChange}
                                    className="w-full px-4 py-2 bg-gray-700 text-white border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>

                            <div className="text-right">
                                <a href="#" className="text-sm text-blue-400 hover:underline">
                                    Forgot your password?
                                </a>
                            </div>

                            <button
                                type="submit"
                                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded transition"
                            >
                                Log In
                            </button>
                        </form>

                        <p className="text-center text-gray-400 mt-4">
                            Need an account?{" "}
                            <a href="/register" className="text-blue-400 hover:underline">
                                Register
                            </a>
                        </p>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Login;