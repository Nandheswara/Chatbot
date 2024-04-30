$(document).ready(function () {
    const chatInput = document.querySelector(".chat-input textarea");

    const suggestedMessageButtons = document.querySelectorAll(".suggested-message");

    document.getElementById('send-btn').addEventListener('click', function (event) {
        sendmsg(event);
    });

    // Add keypress event listener
    chatInput.addEventListener('keydown', function (event) {
        if (event.keyCode === 13) { // 13 is the key code for 'Enter'
            sendmsg(event);
        }
    });

    suggestedMessageButtons.forEach(function (messageButton) {
        messageButton.addEventListener('click', function () {
            chatInput.value = this.textContent;
            document.getElementById('send-btn').click();
        });
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
            },
            error: function (xhr, status, error) {
                alert('Your form was not sent successfully.');
                console.error(error);
            }
        });
    }
});