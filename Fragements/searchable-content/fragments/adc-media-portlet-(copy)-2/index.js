$(document).ready(function () {


var objArr = [];
var searchText = '';
var pageNum = 1;
var pageSize = 6;
var categoryId = -1;
var startDate = '';
var endDate = '';
var allArticles = {};
  var readMore =
  Liferay.ThemeDisplay.getLanguageId() == "en_US"
    ? "Read More"
    : "اقرأ المزيد";
getArticles();
getCategories();




function getCategories() {

  $.ajax({
    url: `/o/adc-dxp-services/related/categoriesList?type=Announcement`,
    type: 'GET',
    headers: {
      'Authorization': 'Bearer ' + Liferay.authToken,
      "languageId": Liferay.ThemeDisplay.getLanguageId(),
      'groupId': Liferay.ThemeDisplay.getSiteGroupId()
    },
    success: function (categories) {
      $('#categoriesSelect').append(`<option value=-1>-</option>`)
      for (let cat of categories) {
        $('#categoriesSelect').append(`<option value="${cat.value}">${cat.label}</option>`)

      }
    },
    error: function (error) {
      // Handle the error
    }
  });

}

function getArticles() {

  $.ajax({
    url: `/o/adc-dxp-services/announcements/web-contents?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
    type: 'GET',
    headers: {
       Authorization: "Bearer " + Liferay.authToken,
      languageId: Liferay.ThemeDisplay.getLanguageId(),
      groupId: Liferay.ThemeDisplay.getSiteGroupId(),
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

$(document).on('click', '.botcast .next-btn', function (e) {
  if (pageNum < allArticles.lastPage) {
    pageNum++;
    getArticles();
  }

});

$(document).on('click', '.botcast .prev-btn', function (e) {

  if (pageNum > 1) {
    pageNum--;
    getArticles();
  }

});

$(".botcast .search-input").on("keydown", function search(e) {
  if (e.keyCode == 13) {
    searchText = $(this).val();
    pageNum = 1;
    getArticles();
  }
});


function processResponse(articles) {
  $('.botcast #gallery-mixed-content').html('');
  objArr = [];
  var count = 0;

  if (articles.items.length <= 0) { emptyResult(); }
  for (let article of articles.items) {

    objArr.push(`<div class="col-md-4 v-space " style="height:400px">
    <a 
    href="/group/portal/adc-botcast/detail?id=${article.articleId}"
    class="bg-white card h-100 overflow-hidden">
      <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
        <img class="img-object-fit" src="${
          article?.previewImage
            ? article.previewImage
            : "/o/ad-customs-theme/images/placeholder-logo.svg"
        }">
        <div class=" flex-column z-index-10 text-white">
          <h5 class="two-lines"> ${article.title} </h5>
          <span class="badge badge-secondary d-none"> ${
            article?.category?.name
          } </span>
        </div><small class="text-white z-index-10"> <i class="icon-calendar mr-2"></i> ${stringfyDate(
          article.displayDate
        )}</small>
        <div class="more-div z-index-10 internal-card-body h-auto">
          <p class="card-text text-white v-align-gap justify-content-end mt-3"> ${readMore} <button
              class="btn btn-outline-light rounded-circle" ><i class="icon-arrow-right"></i></button></p>
        </div>
      </div>
    </a>
  </div`);

    $('.botcast #gallery-mixed-content').append(objArr[count])

    count++;

  }
  $('.botcast #pageNum').html(pageNum + ' / ' + articles.lastPage);
  $('.botcast #totalRes').html(articles.totalCount);

}


function emptyResult() {

  var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى'
  $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
}


$('#categoriesSelect').change(function () {
  categoryId = $(this).val();
  pageNum = 1;
  getArticles();
});



$('#clearBtn').on('click', function () {
  searchText = '';
  pageNum = 1;
  pageSize = 6;
  categoryId = -1;
  startDate = '';
  endDate = '';
  $('.advance-search .form-control').val('');
  getArticles();
})
$('#startDate').on('change', function () { startDate = $(this).val(); getArticles(); })
$('#endDate').on('change', function () { endDate = $(this).val(); getArticles(); })


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