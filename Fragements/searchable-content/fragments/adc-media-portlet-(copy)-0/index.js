$(document).ready(function () {



var mediaJson = [];
var searchText = '';
var pageNum = 1;
var pageSize = 9;
var categoryId = -1;
var startDate = '';
var endDate = '';
var allArticles = {};
var isEn = Liferay.ThemeDisplay.getLanguageId() == "en_US" ? true : false;
var readMore = isEn
? "Read More"
    : "اقرأ المزيد";

  var  expiredLbl = isEn ? "Expired" : "منتهي";
getArticles();



function getArticles() {

  $.ajax({
    url: `/o/adc-dxp-services/promotions/search?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
    type: 'GET',
    headers: {
         'x-csrf-token': Liferay.authToken,
      "languageId": Liferay.ThemeDisplay.getLanguageId(),
       'groupId': Liferay.ThemeDisplay.getSiteGroupId()
    },
    success: function (articles) {
      allArticles = articles;
      processResponse(articles);
    },
    error: function (error) {
      // Handle the error
    }
  });


}

$(document).on('click', '.promotions .next-btn', function (e) {

  if (pageNum < allArticles.lastPage) {
    pageNum++;
    getArticles();
  }

});


$(document).on('click', '.promotions .prev-btn', function (e) {

  if (pageNum > 1) {
    pageNum--;
    getArticles();
  }

});

$(".promotions .search-input").on("keydown", function search(e) {
  if (e.keyCode == 13) {
    searchText = $(this).val();
    pageNum = 1;
    getArticles();
  }
});


function processResponse(articles) {
  $('#gallery-mixed-content').html('');
  mediaJson = [];
  var count = 0;

  if (articles.items.length <= 0) { emptyResult(); }
  for (let article of articles.items) {

    let endDate = new Date(article.endDate);
    // Get the current date and time
    let currentDate = new Date();
    // Compare the dates
    let isExpired = endDate < currentDate;

    mediaJson.push(`<div class="col-md-4 v-space " style="height:300px">
  <a href="/group/portal/promotions/detail?id=${article.articleId}"
  class="bg-white card h-100 overflow-hidden">
    <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
      <img class="img-object-fit" src="${article.previewImage ? article.previewImage : '/o/ad-customs-theme/images/placeholder-logo.svg'}">
      ${!isExpired && article?.attributes?.DiscountDetails?.value  ? '<span class="discount-badge">'+ article.attributes.DiscountDetails.value+' </span>' : ''} 
      ${isExpired ? '<span class="expired-badge"><i class="icon-warning-sign mr-1"></i>'+expiredLbl+' </span>'  : ''}
      <div class=" flex-column z-index-10 text-white">
        <h5 class="two-lines"> ${article.title} </h5>
        <p class="badge badge-secondary mb-2"> ${article?.category?.name} </p>
      </div><small class="text-white z-index-10"> <i class="icon-calendar mr-2"></i> ${stringfyDate(article.displayDate
    )} <i class="icon-arrow-right mx-2"></i> ${stringfyDate(article.endDate)}</small>
      <div class="more-div z-index-10 internal-card-body h-auto">
        <p class="card-text text-white v-align-gap justify-content-end mt-3"> ${readMore} <button
            class="btn btn-outline-light rounded-circle"><i
              class="icon-arrow-right"></i></button></p>
      </div>
    </div>
  </a>
</div`);

    $('#gallery-mixed-content').append(mediaJson[count])

    count++;

  }
  $('#pageNum').html(pageNum + ' / ' + articles.lastPage);
  $('#totalRes').html(articles.totalCount);

}


function emptyResult() {

  var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى'
  $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
}



function stringfyDate(dateStr) {

let date = new Date(dateStr);
let locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE':'ar';
let formattedDate = date.toLocaleDateString(locale, {
day: '2-digit',
month: 'long',
year: 'numeric'
});

  return formattedDate
}

});