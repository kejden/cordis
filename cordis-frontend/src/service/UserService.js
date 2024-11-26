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
        console.log(userData);
        const response = await axios.post(`${UserService.BASE_URL}/auth/sign-in`, userData, {
            headers: { 'Content-Type': 'application/json' }
        })
        return response.data;
    }
}

export default UserService;