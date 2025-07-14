var calendarBookingId = 0;
    getDetails();
    function getURLParam(param) {
        return new URLSearchParams(window.location.search).get(param);
    }

    function getDetails() {

        let id = getURLParam('id');

        $.ajax({
            url: `/o/adc-dxp-services/events/booking/${id}`,
            type: 'GET',
            headers: {
                'x-csrf-token': Liferay.authToken,
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
                'groupId': Liferay.ThemeDisplay.getSiteGroupId()
            },
            success: function (article) {
                calendarBookingId = article.calendarBookingId;
                $('#calendar-title').text(article.title);
                $('#calendar-name').text(article.calendarName);
                $('#calendarDescription').html(article.description);
                $('.from-to-event').append(`<small><i class="icon-calendar mr-2"></i>` + stringfyDate(article.startTime, 4) + ` <i class="icon-arrow-right mx-2"></i>` + stringfyDate(article.endTime, 4) + `</small>`);
                $('.event-acceptance-card .month').text(stringfyDate(article.startTime, 2))
                $('.event-acceptance-card .day').text(stringfyDate(article.startTime, 1))
                handleUsersList(article)
            },
            error: function (error) {
                // Handle the error
            }
        });



    }


    $('.invitation-action').on("click", function (e) {
        var invitationStatus = $(this).attr('data-status');
        var btn = $(this);
        $.ajax({
            url: `/o/adc-dxp-services/events/` + calendarBookingId + `/` + invitationStatus,
            type: 'PATCH',
            headers: {
                'x-csrf-token': Liferay.authToken,
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
                'groupId': Liferay.ThemeDisplay.getSiteGroupId()
            },
            success: function () {
                $('.status-btn').removeClass('active-btn');
                btn.addClass('active-btn');
               console.log('success',$(this))

            },
            error: function (error) {
                if(error.responseJSON.title == 'User is not invited to this calendar booking') {
                    Liferay.Util.openToast({
                      message: error.responseJSON.title,
                      type: "danger"
                    });
                }
            }
        });

    })


    function stringfyDate(date, type) {

        // Convert the string to a Date object
        var date = new Date(Date.parse(date));
        // Use the .toLocaleString() method to format the date and time
        var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
        let options = {};
        switch (type) {
            case 1:
                options = { day: '2-digit', }
                break;

            case 2:
                options = { month: 'short', }
                break;

            case 3:
                options = { year: 'numeric', }
                break;

            case 4:
                options = {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                }

                break;


        }
        var formattedDate = date.toLocaleString(locale, options);

        return formattedDate;
    }

    function handleUsersList(eventDetails) {
        var invitationStatus = -1;

        if (eventDetails.calendarBoobingUserVO.length > 0) {

           invitationStatus = eventDetails.calendarBoobingUserVO.filter(x => x?.user?.userId == Liferay.ThemeDisplay.getUserId()).shift()?.status;

        }

        switch (invitationStatus) {
            case 0:
          $('.accept-btn').addClass('active-btn');
                break;
            case 9:
            $('.maybe-btn').addClass('active-btn');
                break;
            case 4:
            $('.reject-btn').addClass('active-btn');
                break;
        }

    }