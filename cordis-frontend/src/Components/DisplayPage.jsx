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
    const { auth, message = {}} = useSelector((store) => store);
    const [chatOpen, setChatOpen] = useState(false);
    const [chatWindow, setChatWindow] = useState(null);
    const [stompClient, setStompClient] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [messages, setMessages] = useState([]);
    const [content, setContent] = useState("");
    const dispatch = useDispatch();


    useEffect(() => {
        if(chatOpen !== false) {
            console.log(chatWindow)
        }
    }, [chatOpen])

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
        const receivedMessage = JSON.parse(payload.body);
        setMessages((prevMessages) => [...prevMessages, receivedMessage]);
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
            setMessages(message.messages);
        }
    }, [message?.messages]);

    useEffect(() => {
        if (chatWindow  && message?.newMessage) {
            dispatch(getAllMessages({ chatId: chatWindow }));
        }
    }, [chatWindow, message.newMessage]);

    const handleCreateNewMessage = (content) => {
        // console.log(auth);
        // console.log("chatId:"+ chatWindow +" content: " + content + " userId: " + auth.signin.id);
        dispatch(
            createMessage({ chatId: chatWindow, content: content, userId: auth.signin.id })
        );
        setContent("");
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
                                messages={messages} />}
            </div>
        </>
    )
}

export default DisplayPage;