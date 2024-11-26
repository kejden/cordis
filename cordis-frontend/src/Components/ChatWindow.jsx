import React, { useState, useEffect } from "react";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

const ChatWindow = ({ friend, onClose }) => {
    const senderName = localStorage.getItem("userName");
    const receiverName = friend.userName;
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const [stompClient, setStompClient] = useState(null);

    const onConnected = () => {
        client.subscribe(`/user/${friend.userName}/queue/messages`, onMessageReceived);
        client.subscribe(`/user/${friend.userName}/queue/messages`, onMessageReceived);

    }

    const onError = (e) => {

    }

    const onMessageReceived = () => {

    }

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws");
        const client = Stomp.over(socket);

        client.connect({}, onConnected, onError);


        // client.connect({}, () => {
        //     const subscriptionPath = `/topic/chat/${friend.id}`;
        //     client.subscribe(subscriptionPath, (message) => {
        //         const receivedMessage = JSON.parse(message.body);
        //         setMessages((prevMessages) => [...prevMessages, receivedMessage]);
        //     });
        // });
        //
        // setStompClient(client);
        //
        // return () => {
        //     if (client) {
        //         client.disconnect();
        //     }
        // };
    }, [friend.id]);

    const handleSendMessage = () => {
        if (newMessage.trim() !== "" && stompClient) {
            const messagePayload = {
                sender: senderName,
                receiver: friend.userName,
                content: newMessage,
                channelId: friend.id,
            };

            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(messagePayload));
            setNewMessage("");
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-gray-800 text-white rounded-lg w-1/3 p-4 shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-medium">{`Chat with ${receiverName}`}</h2>
                    <button
                        className="text-red-500 hover:text-red-700"
                        onClick={onClose}
                    >
                        Close
                    </button>
                </div>
                <div className="h-64 bg-gray-900 rounded p-4 overflow-y-auto">
                    {messages.map((msg, index) => (
                        <div key={index} className="mb-2">
                            <p className="text-sm">
                                <span className="font-bold">{msg.sender}:</span>{" "}
                                {msg.content}
                            </p>
                        </div>
                    ))}
                </div>
                <div className="mt-4 flex">
                    <input
                        type="text"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        placeholder="Type your message..."
                        className="flex-grow px-4 py-2 rounded-l bg-gray-700 text-white border border-gray-600 focus:outline-none focus:ring focus:ring-blue-500"
                    />
                    <button
                        onClick={handleSendMessage}
                        className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-r"
                    >
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ChatWindow;