const authHeaders = {
  Authorization:  'Basic VmlzaGFsLlNoYWg6SW5leHR1cmUxMjMh',
  languageId: Liferay.ThemeDisplay.getLanguageId(),
  groupId: Liferay.ThemeDisplay.getSiteGroupId()
};

getEvents();
getMedia();
getBotCast();
getNews();

function getEvents() {
  $.ajax({
    url: `/o/adc-dxp-services/events?page=1&pageSize=6&calendarId=-1&sort=startTime:desc`,
    type: "GET",
    headers: authHeaders,
    success: function (articles) {
      $("#events-counter").html(articles?.totalCount);
    },
    error: function (error) {
      console.error("Events Error", error);
    },
  });
}

function getMedia() {
  $.ajax({
    url: `/o/adc-dxp-services/medias/galleries?page=1&pageSize=1&categoryId=-1&sort=displayDate:desc`,
    type: "GET",
    headers: authHeaders,
    success: function (articles) {
      $("#media-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items?.[0]?.previewImage || "/o/ad-customs-theme/images/adc-HD-logo.jpeg"
        }" >`
      );
      $("#media-counter").html(articles?.totalCount);
    },
    error: function (error) {
      console.error("Media Error", error);
    },
  });
}

function getBotCast() {
  $.ajax({
    url: `/o/adc-dxp-services/announcements/web-content?page=1&pageSize=1&categoryId=-1&sort=displayDate:desc`,
    type: "GET",
    headers: authHeaders,
    success: function (articles) {
      $("#botcast-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items?.[0]?.previewImage || "/o/ad-customs-theme/images/placeholder-logo.svg"
        }" >`
      );
      $("#botcast-counter").html(articles?.totalCount);
    },
    error: function (error) {
      console.error("BotCast Error", error);
    },
  });
}

function getNews() {
  $.ajax({
    url: `/o/adc-dxp-services/news/search?page=1&pageSize=1&categoryId=-1&sort=displayDate:desc`,
    type: "GET",
    headers: authHeaders,
    success: function (articles) {
      $("#news-container").append(
        `<img class="img-absolute-object-fit-cover" src="${
          articles?.items?.[0]?.previewImage || "/o/ad-customs-theme/images/adc-HD-logo.jpeg"
        }" >`
      );
      $("#news-counter").html(articles?.totalCount);
    },
    error: function (error) {
      console.error("News Error", error);
    },
  });
}
