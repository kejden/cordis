import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import ServerBar from "./ServerBar/ServerBar.jsx";
import FriendManager from "./Friends/FriendManager.jsx";
import FriendSideBar from "./User/FriendSideBar.jsx";
import ChatWindow from "./Chat/ChatWindow.jsx";
import SockJs from "sockjs-client/dist/sockjs";
import { over } from "stompjs";
import axios from "axios";
import toast from "react-hot-toast";
import { BASE_API_URL } from "../config/api.js";
import { createMessage, getAllMessages } from "../Redux/Message/Action.js";
import { updateLatestChats } from "../Redux/Chat/Action.js";
import {getAllServers} from "../Redux/Server/Action.js";
import ServerSideBar from "./Server/ServerSideBar.jsx";
import ServerUsers from "./Server/ServerUsers.jsx";

const DisplayPage = () => {
    const { auth, chat, message, server} = useSelector((store) => store);
    const dispatch = useDispatch();

    const [chatOpen, setChatOpen] = useState(false);
    const [serverOpen, setServerOpen] = useState(false);
    const [openedServer, setOpenedServer] = useState(null);
    const [chatWindow, setChatWindow] = useState(null);
    const [stompClient, setStompClient] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [messages, setMessages] = useState([]);
    const [content, setContent] = useState("");
    const [serverName, setServerName] = useState("");
    const [localLatestChats, setLocalLatestChats] = useState([]);
    const [userRole, setUserRole] = useState(null);


    useEffect(() => {
        setLocalLatestChats(chat.latestChats || []);
    }, [chat.latestChats]);

    useEffect(() => {
        if (chatWindow) {
            dispatch(getAllMessages({ chatId: chatWindow, isServerChannel: isGroup }));
        }
    }, [chatWindow]);

    useEffect(() => {
        connect();
        fetchLatestChats();
        fetchServers();
    }, []);

    useEffect(() => {
        if (message?.messages) {
            setMessages(Array.isArray(message.messages) ? message.messages : []);
        }
    }, [message?.messages]);

    useEffect(() => {
        if (chatWindow  && message?.newMessage) {
            dispatch(getAllMessages({ chatId: chatWindow, isServerChannel: isGroup }));
        }
    }, [chatWindow, message.newMessage]);

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

    const connect = () => {
        const sock = new SockJs("http://localhost:8080/ws");
        const temp = over(sock);
        setStompClient(temp);

        temp.connect([], onConnect, onError);
    };

    const onError = (error) => {
        console.log("Error connecting:", error);
    };

    const onConnect = () => {
        setIsConnected(true);

        if (stompClient && chatWindow) {
            const subscription = isGroup
                ? stompClient.subscribe(`/group/${chatWindow}`, onMessageReceive)
                : stompClient.subscribe(`/user/${auth.signin.id}/messages`, onMessageReceive); // /user/${chatWindow}

            return () => {
                subscription.unsubscribe();
            };
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


            const chatId = receivedMessage.chatId || chatWindow;

            if (chatId) {
                updateChatOrder(chatId);
            } else {
                toast.error("Chat ID not found in received message:", receivedMessage);
            }
        } catch (error) {
            console.error("Error parsing message payload:", error);
        }
    };

    const handleCreateNewMessage = (content) => {
        dispatch(
            createMessage({ chatId: chatWindow, content: content, userId: auth.signin.id, group: isGroup }),
        );
        setContent("");
        updateChatOrder(chatWindow);
    };

    const fetchLatestChats = async () => {
        try {
            const response = await axios.get(`${BASE_API_URL}/api/friend/latestChats`, {
                withCredentials: true,
            });
            dispatch(updateLatestChats(response.data || []));
        } catch (error) {
            console.error("Error fetching latest chats:", error);
            toast.error("Error fetching latest chats");
        }
    };

    const updateChatOrder = (chatId) => {
        const chatIndex = localLatestChats.findIndex((chat) => chat.id === chatId);

        if (chatIndex > 0) {
            const updatedChats = [...localLatestChats];
            const [movedChat] = updatedChats.splice(chatIndex, 1);
            updatedChats.unshift(movedChat);
            setLocalLatestChats(updatedChats);
            dispatch(updateLatestChats(updatedChats));
        } else if (chatIndex === -1) {
            console.warn("Chat ID not found in latestChats.");
        }
    };

    const fetchServers = async () => {
        try {
            await dispatch(getAllServers());
        } catch (error) {
            toast.error("Error fetching servers.");
            console.error("Error fetching servers:", error);
        }
    };

    const openServer = (serverId) => {
        const selectedServer = server.servers.find((server) => server.id === serverId);

        const fetchUserRole = async (serverID) => {
            try {
                const response = await axios.get(`${BASE_API_URL}/api/server/${serverID}/role`, {
                    withCredentials: true,
                });
                setUserRole(response.data.name);
            } catch (error) {
                console.error("Error fetching user role:", error);
                toast.error("Error fetching user role");
            }
        };

        setServerOpen(true);
        setIsGroup(true);
        setOpenedServer(serverId);
        setChatOpen(false);
        setServerName(selectedServer.name)
        fetchUserRole(serverId);

    };

    const closeServer = () => {
        setServerOpen(false);
        setIsGroup(false);
        setOpenedServer(null);
        setChatOpen(false);
        setServerName("")
        setUserRole(null)
    };

    const handleChannelClick = (channel) => {
        setChatWindow(channel.id);
        setChatOpen(true);
    };

    return (
        <>
            <ServerBar servers={server.servers} openServer={openServer} closeServer={closeServer} />
            <div className="flex h-screen bg-gray-800">
                {!serverOpen ? (<FriendSideBar
                    latestChats={localLatestChats}
                    setChatOpen={setChatOpen}
                    setChatWindow={setChatWindow}
                />) : (
                    <ServerSideBar
                        server={openedServer}
                        onChannelClick={handleChannelClick}
                        serverName={serverName}
                        role={userRole}
                    />
                )}
                {!serverOpen && !chatOpen && (
                    <FriendManager
                        setChatOpen={setChatOpen}
                        setChatWindow={setChatWindow}
                    />
                )}
                {chatOpen && (
                    <ChatWindow
                        handleCreateNewMessage={(content) => handleCreateNewMessage(content)}
                        messages={messages}
                        onClose={() => setChatOpen(false)}
                    />
                )}
                <div className="ml-auto">
                    {serverOpen && (
                        <ServerUsers
                            serverId={openedServer}
                            role={userRole}
                        />
                    )}
                </div>

            </div>
        </>
    );
};

export default DisplayPage;
