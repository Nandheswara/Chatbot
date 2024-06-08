$(document).ready(function () {
    const chatInput = document.querySelector(".chat-input textarea");

    document.getElementById('send-btn').addEventListener('click', function (event) {
        sendmsg(event);
    });

    // Add keypress event listener
    chatInput.addEventListener('keydown', function (event) {
        if (event.keyCode === 13) { // 13 is the key code for 'Enter'
            sendmsg(event);
        }
    });

    // // Use event delegation for suggested-message buttons
    // document.querySelector('.chat.suggested-messages').addEventListener('click', function (event) {
    //     if (event.target.matches('.suggested-message')) {
    //         chatInput.value = event.target.textContent;
    //         document.getElementById('send-btn').click();
    //     }
    // });
    // Click event for dynamic added buttons
    $(document).on("click", ".chat.suggested-messages" , function() {
        if (event.target.matches('.suggested-message')) {
            chatInput.value = event.target.textContent;
            document.getElementById('send-btn').click();
        }
    });

    console.log("ready!");

    function sendmsg(event) {
        event.preventDefault();
        var form = document.getElementById('myForm');
        console.log(form);
        var formData = new FormData(form);
        $.ajax({
            url: '/bin/inputmessage',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                console.log(response)
                //showSuggestionMessage();
            },
            error: function (xhr, status, error) {
                alert('Your form was not sent successfully.');
                console.error(error);
            }
        });
    }

 
    $.ajax({
        url: "/bin/inputmessage",
        type: "GET",
        dataType: "json",
        success: function (data) {
            if (data != null) {
                console.log(data);
                // Iterate over the keys in the data object
                for (let key in data) {
                    // Create a new button with the text from the AJAX call
                    let newButton = $('<button class="suggested-message">' + data[key] + '</button>');
                    // Append the new button to the list
                    $('.chat.suggested-messages').append(newButton);
                }
            }
        },
        error: function () {
            console.log("Error getting the data");
        }
    });

});