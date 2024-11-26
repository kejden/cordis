import axios from "axios";

class UserService {

    static BASE_URL = "http://localhost:8080"


    static async login(userData) {

        const response = await axios.post(`${UserService.BASE_URL}/auth/sign-in`, userData, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true })
        return response.data;
    }

    static async register(userData) {
        try {
            const response = await axios.post(`${UserService.BASE_URL}/auth/sign-up`, userData, {
                headers: {'Content-Type': 'application/json'},
            });

            // Return the created resource or success message
            return response.data;
        } catch (error) {
            // Handle errors (e.g., validation or server issues)
            console.error("Registration error:", error);
            throw error.response?.data || error.message;
        }
    }
}

export default UserService;