$(document).ready(function () {

    if (localStorage.getItem('feedbackNotification')) {
        $('.satisfaction-modal-btn').removeClass('pulsating-circle');
    } else {
        $('.satisfaction-modal-btn').addClass('pulsating-circle');
    }

    $('.satisfaction-modal-btn').on('click', function () {
        $('#satisfaction-modal').toggleClass('d-none');
    });

    $('.close-modal').on('click', function () {
        $('#satisfaction-modal').toggleClass('d-none');
    });



    // Show message field when "Sad" is selected
    $("input[name='satisfactionRating']").change(function () {
        if ($(this).val() === "Sad") {
            $("#messageField").slideDown(); // Show the message field
        } else {
            $("#messageField").slideUp(); // Hide the message field
        }
    });


    $("#submitBtn").click(function () {
        const formValues = {
            satisfactionRating: $("input[name='satisfactionRating']:checked").val(),
            message: $("#messageInput").val(),

        };



        postForm(formValues)
    });

});


function postForm(formValues) {
  // UAT form id  1884559
  // Prd form id  2427536
    // URL for the API endpoint
    const apiUrl = "/o/headless-form/v1.0/forms/2427536/form-records";

    // JSON data for the request body
    const requestData = {
        "creator": {
            "additionalName": "string",
            "familyName": "string",
            "givenName": "string",
            "id": 0,
            "image": "string",
            "name": "string",
            "profileURL": "string"
        },
        "draft": false,
        "formFieldValues": [
            {
                "name": "SatisfactionRating",
                "value": formValues.satisfactionRating
            },
            {
                "name": "Message",
                "value": formValues.message
            }, {
                "name": 'PageURL',
                "value": window.location.href

            }
        ],
        "formId": 2427536
    };

    // Headers for the request
    const headers = new Headers();
    headers.append("Authorization", "Bearer " + Liferay.authToken);
    headers.append("languageId", Liferay.ThemeDisplay.getLanguageId());
    headers.append("groupId", Liferay.ThemeDisplay.getSiteGroupId());
    headers.append("Content-Type", "application/json"); // Add this line to specify the content type

    // Create the options object for the fetch request
    const requestOptions = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(requestData)
    };

    // Make the fetch request
    fetch(apiUrl, requestOptions)
        .then(response => response.json())
        .then(data => {
            console.log("Form record created successfully:", data);

            $('#satisfaction-modal .modal-footer').remove();
            $('#feedbackForm').toggleClass('d-none');
            $('#submitted-message').toggleClass('d-none');
           localStorage.setItem('feedbackNotification','true');
           $('.satisfaction-modal-btn').removeClass('pulsating-circle');


        })
        .catch(error => {
            console.error("Error creating form record:", error);
        });

}