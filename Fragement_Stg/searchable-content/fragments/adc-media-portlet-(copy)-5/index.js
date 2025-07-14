$(document).ready(function () {


var categoryId = 254016;
var pinned = {};
var favs = {};

//getFavs();
getArticles();


function getArticles() {

    $.ajax({
        url: `/o/adc-dxp-services/quick-links/search?search=&page=1&pageSize=100&categoryId=${categoryId}&startDate=$&endDate=&sort=displayDate:desc`,
        type: 'GET',
        headers: {
            'Authorization': 'Bearer ' + Liferay.authToken,
            "languageId": Liferay.ThemeDisplay.getLanguageId(),
            'groupId': Liferay.ThemeDisplay.getSiteGroupId()
        },
        success: function (articles) {
            pinned = articles;
            processResponse(articles);
        },
        error: function (error) {
            // Handle the error
        }
    });


}



function getFavs() {

    $.ajax({
        url: `/o/adc-dxp-services/quick-links/favorites`,
        type: 'GET',
        headers: {
            'p_auth': Liferay.authToken,
            'languageId': Liferay.ThemeDisplay.getLanguageId(),
            'groupId': Liferay.ThemeDisplay.getSiteGroupId()

        },
        success: function (articles) {
            favs = articles;
            processFavs(favs);
        },
        error: function (error) {
            // Handle the error
        }
    });


}



function processResponse(articles) {

    var count = 0;
    for (let article of articles.items) {
   
        if (article?.attributes?.Pinned && count < 6) {
            if (article?.attributes?.Pinned.value == 'y') {
                $('.fav-apps').append(`<div class="v-space flex-fill fav-col"><a class="btn btn-light applications-btn btn-block font-b" target="_blank" href="${article?.attributes?.quickLinksLink.value}">
        <img alt="" height="40" src="${article.previewImage}"> ${article.title}</a></div>`);
                count++;
                if (count == 6)
                    break;

            }
        }



    }


}


function processFavs(articles) {

    var count = 0;
    for (let article of articles) {
        if (count < 3) {
            if (article?.attributes?.Pinned) {
                if (article?.attributes?.Pinned.value != 'y') {
                    $('.fav-apps').append(`<div class="v-space flex-fill fav-col"><a class="btn btn-light applications-btn btn-block font-b" target="_blank" href="${article?.attributes?.quickLinksLink.value}">
        <img alt="" height="40" src="${article.previewImage}"> ${article.title}</a></div>`);
                    count++;
                }
            } else {
                $('.fav-apps').append(`<div class="v-space flex-fill fav-col"><a class="btn btn-light applications-btn btn-block font-b" target="_blank"  href="${article?.attributes?.quickLinksLink.value}">
    <img alt="" height="40" src="${article.previewImage}"> ${article.title}</a></div>`); count++;
            }

        }
    }

   // getArticles();


}





});