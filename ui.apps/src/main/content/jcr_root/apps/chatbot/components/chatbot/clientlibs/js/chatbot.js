$(document).ready(function () {
    // Select elements once the page is loaded
    const chatbotToggler = document.querySelector(".chatbot-toggler");
    const closeBtn = document.querySelector(".close-btn");
    const chatbox = document.querySelector(".chatbox");
    const chatInput = document.querySelector(".chat-input textarea");
    const sendChatBtn = document.querySelector(".chat-input span");

    console.log("Chat Input: ", chatInput);
    console.log("Chat Toggler: ", chatbotToggler);

    let userMessage = null; // Variable to store user's message
    const API_KEY = "PASTE-YOUR-API-KEY"; // Paste your API key here
    const inputInitHeight = chatInput.scrollHeight;

    const createChatLi = (message, className) => {
        // Create a chat <li> element with passed message and className
        const chatLi = document.createElement("li");
        chatLi.classList.add("chat", `${className}`);
        let chatContent = className === "outgoing" ? `<p></p>` : `<span class="material-symbols-outlined">smart_toy</span><p></p>`;
        chatLi.innerHTML = chatContent;
        chatLi.querySelector("p").textContent = message;
        return chatLi; // return chat <li> element
    }

    //     const generateResponse = (chatElement) => {
    //     const API_URL = "https://api.openai.com/v1/chat/completions";
    //     const messageElement = chatElement.querySelector("p");

    //     // Define the properties and message for the API request
    //     const requestOptions = {
    //         method: "POST",
    //         headers: {
    //             "Content-Type": "application/json",
    //             "Authorization": `Bearer ${API_KEY}`
    //         },
    //         body: JSON.stringify({
    //             model: "gpt-3.5-turbo",
    //             messages: [{role: "user", content: userMessage}],
    //         })
    //     }

    //     // Send POST request to API, get response and set the reponse as paragraph text
    //     fetch(API_URL, requestOptions).then(res => res.json()).then(data => {
    //         messageElement.textContent = data.choices[0].message.content.trim();
    //     }).catch(() => {
    //         messageElement.classList.add("error");
    //         messageElement.textContent = "Oops! Something went wrong. Please try again.";
    //     }).finally(() => chatbox.scrollTo(0, chatbox.scrollHeight));
    // }

    const handleChat = () => {
    userMessage = chatInput.value.trim(); // Get user entered message and remove extra whitespace
    if (!userMessage) return;

    // Clear the input textarea and set its height to default
    chatInput.value = "";
    chatInput.style.height = `${inputInitHeight}px`;

    // Append the user's message to the chatbox
    chatbox.appendChild(createChatLi(userMessage, "outgoing"));
    chatbox.scrollTo(0, chatbox.scrollHeight);



    setTimeout(() => {
        // Display "Thinking..." message while waiting for the response
        const incomingChatLi = createChatLi("Thinking...", "incoming");
        chatbox.appendChild(incomingChatLi);
        chatbox.scrollTo(0, chatbox.scrollHeight);
        // generateResponse(incomingChatLi);
        $.ajax({
            url: "/bin/inputmessage",
            type: "GET",
            dataType: "json",
            success: function (data) {
                if (data != null) {
                    console.log(data);
                    const chatLi = document.createElement("li");
                    //chatLi.classList.add("chat", `${className}`);
                    // Iterate over the keys in the data object
                    let newButton;
                    for (let key in data) {
                        // Create a new button with the text from the AJAX call
                        let newButton = $('<button class="suggested-message">' + data[key] + '</button>');
                        // Append the new button to the list
                        //$('.chat.suggested-messages').append(newButton);
                       //$('.chatbox .incoming:last-child').append(newButton);
                       $('<li class="chat suggested-messages"> </li>').insertAfter('.chatbox .incoming:last-child');
                       $('.chatbox .suggested-messages:last-child').append(newButton);
                    }
                    chatbox.scrollTo(0, chatbox.scrollHeight);
                     const incomingChatLi = createChatLi(newButton, "incoming");
                        chatbox.appendChild(incomingChatLi);
                        chatbox.scrollTo(0, chatbox.scrollHeight);
                }
            },
            error: function () {
                console.log("Error getting the data");
            }
        });
    }, 600);
}


    chatInput.addEventListener("input", () => {
        // Adjust the height of the input textarea based on its content
        chatInput.style.height = `${inputInitHeight}px`;
        chatInput.style.height = `${chatInput.scrollHeight}px`;
    });

    chatInput.addEventListener("keydown", (e) => {
        // If Enter key is pressed without Shift key and the window 
        // width is greater than 800px, handle the chat
        if (e.key === "Enter" && !e.shiftKey && window.innerWidth > 800) {
            e.preventDefault();
            handleChat();
        }
    });

    sendChatBtn.addEventListener("click", handleChat);
    // To remove the class
    closeBtn.addEventListener("click", () => document.querySelector(".my-chatbot").classList.remove("show-chatbot"));

    // To toggle the class
    chatbotToggler.addEventListener("click", () => document.querySelector(".my-chatbot").classList.toggle("show-chatbot"));

});

