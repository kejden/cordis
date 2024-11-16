import {useState} from "react";
import {useNavigate} from "react-router-dom";

const Register = () => {
    const [user, setUser] = useState({
        username: "",
        email: "",
        password: "",
        confirmpassword: ""
    });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if(!(user.confirmpassword === user.confirmpassword)) {
            console.error("hasla nie sa takie same")
        }
        console.log(`name: ${user.username}, email ${user.email}, password ${user.password}, confirmpassword: ${user.confirmpassword}`);
        const response = await fetch("http://localhost:8080/auth/sign-up", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                id: 0,
                email: user.email,
                password: user.password,
                name: user.username
            }),
        });

        // const data = await response.json();

        if (response.ok) {
            navigate("/login");
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
                        <h2 className="text-2xl font-bold text-center mb-4">Sign Up!</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">
                                    EMAIL
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
                                    USERNAME
                                </label>
                                <input
                                    type="text"
                                    name="username"
                                    value={user.username}
                                    onChange={onChange}
                                    placeholder="Enter your username"
                                    className="w-full px-4 py-2 bg-gray-700 text-white border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">
                                    PASSWORD
                                </label>
                                <input
                                    type="password"
                                    name="password"
                                    value={user.password}
                                    onChange={onChange}
                                    placeholder="Enter your password"
                                    className="w-full px-4 py-2 bg-gray-700 text-white border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-400 mb-1">
                                    CONFIRM PASSWORD
                                </label>
                                <input
                                    type="password"
                                    name="confirmpassword"
                                    value={user.confirmpassword}
                                    onChange={onChange}
                                    placeholder="Confirm your password"
                                    className="w-full px-4 py-2 bg-gray-700 text-white border border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <button
                                type="submit"
                                className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded transition"
                            >
                                Register
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Register;