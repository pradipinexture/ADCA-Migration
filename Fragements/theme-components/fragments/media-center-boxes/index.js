var obj = { news: "", events: "", botcast: "", media: "" };

getEvents();
getMedia();
getBotCast();
getNews();

function getEvents() {
  $.ajax({
    url: `/o/adc-dxp-services/events?search=&page=1&pageSize=6&calendarId=-1&startDate=&endDate=&sort=startTime:desc`,
    type: "GET",
    headers: {
        'x-csrf-token': Liferay.authToken,
        languageId: Liferay.ThemeDisplay.getLanguageId(), 
        groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
      $("#events-counter").html(articles?.totalCount);
    },
    error: function (error) {
      // Handle the error
    },
  });
}

function getMedia() {
  $.ajax({
    url: `/o/adc-dxp-services/medias/galleries?search=&page=1&pageSize=1&categoryId=-1&startDate=&endDate&sort=displayDate:desc`,
    type: "GET",
    headers: {
         'x-csrf-token': Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
       $("#media-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items[0]?.previewImage
            ? articles?.items[0]?.previewImage
            : "/o/ad-customs-theme/images/adc-HD-logo.jpeg"
        }" >`
      );
      $("#media-counter").html(articles?.totalCount);
    },
    error: function (error) {
      // Handle the error
    },
  });
}

function getBotCast() {
  $.ajax({
    url: `/o/adc-dxp-services/announcements/web-contents?search=&page=1&pageSize=1&categoryId=-1&startDate=&endDate=&sort=displayDate:desc`,
    type: "GET",
    headers: {
         'x-csrf-token': Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
       $("#botcast-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items[0]?.previewImage
            ? articles?.items[0]?.previewImage
            : "/o/ad-customs-theme/images/placeholder-logo.svg"
        }" >`
      );
      $("#botcast-counter").html(articles?.totalCount);
    },
    error: function (error) {
      // Handle the error
    },
  });
}

function getNews() {
  $.ajax({
    url: `/o/adc-dxp-services/news/search?search=&page=1&pageSize=1&categoryId=-1&startDate=&endDate=&achievement=&sort=displayDate:desc`,
    type: "GET",
    headers: {
         'x-csrf-token': Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
      
      $("#news-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items[0]?.previewImage
            ? articles?.items[0]?.previewImage
            : "/o/ad-customs-theme/images/adc-HD-logo.jpeg"
        }" >`
      );
      $("#news-counter").html(articles?.totalCount);
    },
    error: function (error) {
      // Handle the error
    },
  });
}