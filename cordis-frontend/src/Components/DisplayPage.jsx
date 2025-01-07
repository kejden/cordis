import ServerBar from "./ServerBar/ServerBar.jsx";
import FriendManager from "./Friends/FriendManager.jsx";
import {useEffect, useState} from "react";
import FriendSideBar from "./User/FriendSideBar.jsx";
import SockJs from "sockjs-client/dist/sockjs";
import { over } from "stompjs";
import {useDispatch, useSelector} from "react-redux";
import {createMessage, getAllMessages} from "../Redux/Message/Action.js";
import ChatWindow from "./Chat/ChatWindow.jsx";

const DisplayPage = () => {
    const { auth, message} = useSelector((store) => store);
    const [chatOpen, setChatOpen] = useState(false);
    const [chatWindow, setChatWindow] = useState(null);
    const [stompClient, setStompClient] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [messages, setMessages] = useState([]);
    const [content, setContent] = useState("");
    const dispatch = useDispatch();

    useEffect(() => {
        if (chatWindow) {
            dispatch(getAllMessages({ chatId: chatWindow }));
        }
    }, [chatWindow]);

    const connect = () => {
        const sock = new SockJs("http://localhost:8080/ws");
        const temp = over(sock);
        setStompClient(temp);

        temp.connect([], onConnect, onError);
    };

    useEffect(() => {
        connect();
    }, []);

    const onError = (error) => {
        console.log("on error ", error);
    };

    const onConnect = () => {
        setIsConnected(true);

        if (stompClient && chatWindow) {
            if (isGroup) {
                stompClient.subscribe(`/group/${chatWindow}`, onMessageReceive);
            } else {
                stompClient.subscribe(`/user/${chatWindow}`, onMessageReceive);
            }
        }
    };

    const onMessageReceive = (payload) => {
        try {
            const receivedMessage = JSON.parse(payload.body);
            setMessages((prevMessages) => {
                if (!Array.isArray(prevMessages)) {
                    console.error("prevMessages is not an array:", prevMessages);
                    return [receivedMessage];
                }
                return [...prevMessages, receivedMessage];
            });
        } catch (error) {
            console.error("Error parsing message payload:", error);
        }
    };

    useEffect(() => {
        if (isConnected && stompClient && chatWindow) {
            const subscription = isGroup
                ? stompClient.subscribe(`/group/${chatWindow}`, onMessageReceive)
                : stompClient.subscribe(`/user/${chatWindow}`, onMessageReceive);

            return () => {
                subscription.unsubscribe();
            };
        }
    }, [isConnected, stompClient, chatWindow]);

    useEffect(() => {
        if (message?.messages) {
            setMessages(Array.isArray(message.messages) ? message.messages : []);
        }
    }, [message?.messages]);

    useEffect(() => {
        if (chatWindow  && message?.newMessage) {
            dispatch(getAllMessages({ chatId: chatWindow }));
        }
    }, [chatWindow, message.newMessage]);

    const handleCreateNewMessage = (content) => {
        dispatch(
            createMessage({ chatId: chatWindow, content: content, userId: auth.signin.id })
        );
        setContent("");
        console.log(message.newMessage);
    };

    return (
        <>
            <ServerBar/>
            <div className="flex h-screen">
                <FriendSideBar/>
                {!chatOpen && <FriendManager
                    setChatOpen={setChatOpen}
                    setChatWindow={setChatWindow}
                    setIsGroup={ () => setIsGroup(false)}/>}
                {chatOpen && <ChatWindow
                                handleCreateNewMessage={ (content) => handleCreateNewMessage(content)}
                                messages={messages}
                                onClose={() => setChatOpen(false)}
                            />}
            </div>
        </>
    )
}

export default DisplayPage;