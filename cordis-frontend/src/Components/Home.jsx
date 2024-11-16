import {useEffect} from "react";

const Home = () => {
    const makeAuthenticatedRequest = async () => {
        const token = localStorage.getItem("accessToken");

        if (!token) {
            console.error("No access token available");
            return;
        }

        const response = await fetch("http://localhost:8080/api/", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const protectedData = await response.json();
            console.log("Protected data:", protectedData);
        } else {
            console.error("Failed to fetch protected data", response.statusText);
        }
    };

    useEffect(() => {
        makeAuthenticatedRequest();
    }, []);
}

export default Home;