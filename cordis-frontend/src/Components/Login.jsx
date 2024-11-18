import { useState } from "react";
import { useNavigate } from "react-router-dom";


const Login = () => {
    const [user, setUser] = useState({
        email: "",
        password: ""
    });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        const response = await fetch("http://localhost:8080/auth/sign-in", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                email: user.email,
                password: user.password
            }),
        });

        const data = await response.json();

        if (response.ok) {
            // console.log(data);
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("userName", data.userName);
            localStorage.setItem("email", data.email);
            navigate("/");
            // alert("Login successful!");
        } else {
            console.log(response.error);
        }
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