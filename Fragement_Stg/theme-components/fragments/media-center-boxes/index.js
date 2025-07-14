var obj = { news: "", events: "", botcast: "", media: "" };

$(document).ready(function () {
  getEvents();
  getMedia();
  getBotCast();
  getNews();
});

// Utility: support both old and new data formats for preview images
function resolvePreviewImage(item, fallback) {
  if (!item) {
    return fallback;
  }

  // 1. Old format
  if (item.previewImage) {
    return item.previewImage;
  }

  // 2. New format in attributes
  if (item.attributes?.PreviewImage?.value) {
    try {
      const parsed = JSON.parse(item.attributes.PreviewImage.value);
      if (parsed?.url) {
        return parsed.url;
      }
    } catch (e) {
      console.error("Error parsing PreviewImage:", e);
    }
  }

  // 3. Fallback
  return fallback;
}

function getEvents() {
  $.ajax({
    url: `/o/adc-dxp-services/events?search=&page=1&pageSize=6&calendarId=4069810&startDate=&endDate=&sort=startTime:desc`,
    type: "GET",
    headers: {
      'x-csrf-token': Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
      $("#events-counter").html(articles?.totalCount ?? 0);
    },
    error: function (error) {
      console.error("Error fetching events:", error);
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
      const item = articles?.items?.[0];
      const fallback = "/o/ad-customs-theme/images/adc-HD-logo.jpeg";
      const imgUrl = resolvePreviewImage(item, fallback);

      $("#media-container").append(
        `<img class="img-absolute-object-fit-cover" src="${imgUrl}">`
      );
      $("#media-counter").html(articles?.totalCount ?? 0);
    },
    error: function (error) {
      console.error("Error fetching media:", error);
    },
  });
}

function getBotCast() {
  $.ajax({
    url: `/o/adc-dxp-services/announcements/web-content?search=&page=1&pageSize=1&categoryId=-1&startDate=&endDate=&sort=displayDate:desc`,
    type: "GET",
    headers: {
      'x-csrf-token': Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
      const item = articles?.items?.[0];
      const fallback = "/o/ad-customs-theme/images/placeholder-logo.svg";
      const imgUrl = resolvePreviewImage(item, fallback);

      $("#botcast-container").append(
        `<img class="img-absolute-object-fit-cover" src="${imgUrl}">`
      );
      $("#botcast-counter").html(articles?.totalCount ?? 0);
    },
    error: function (error) {
      console.error("Error fetching botcast:", error);
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
      const item = articles?.items?.[0];
      const fallback = "/o/ad-customs-theme/images/adc-HD-logo.jpeg";
      const imgUrl = resolvePreviewImage(item, fallback);

      $("#news-container").append(
        `<img class="img-absolute-object-fit-cover" src="${imgUrl}">`
      );
      $("#news-counter").html(articles?.totalCount ?? 0);
    },
    error: function (error) {
      console.error("Error fetching news:", error);
    },
  });
}