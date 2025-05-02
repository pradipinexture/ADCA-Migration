$(document).ready(function () {



  var mediaJson = [];
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
   
  
  
  


  function getArticles() {

    $.ajax({
      url: `/o/adc-dxp-services/management-messages?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=-1&startDate=&endDate=&sort=displayDate:desc&id=null`,
      type: 'GET',
      headers: {
         'Authorization': 'Basic VmlzaGFsLlNoYWg6SW5leHR1cmUxMjMh',
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

  $(document).on('click', '.messages .next-btn', function (e) {

    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }

  });


  $(document).on('click', '.messages .prev-btn', function (e) {

    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }

  });

  $(".messages .search-input").on("keydown", function search(e) {
    if (e.keyCode == 13) {
      searchText = $(this).val();
      pageNum = 1;
      getArticles();
    }
  });


  function processResponse(articles) {
    $('.messages #gallery-mixed-content').html('');
    mediaJson = [];
    var count = 0;

    if (articles.items.length <= 0) { emptyResult(); }
    for (let article of articles.items) {

      mediaJson.push(`<div class="col-lg-4 col-md-6 col-sm-12 v-space">
      <a 
      href="/group/portal/director-message/detail?id=${article.articleId}"
      class="card h-100 d-flex">
        <div class="card-image">
          <img src="/o/ad-customs-theme/images/news/news-item.svg" alt="">
        </div>
        <div class="card-body">
          <p class="card-text two-lines">
            ${article.title}
          </p>
        </div>
        <div class="card-footer d-flex align-items-center justify-content-between">
          <small class="date m-0"><i class="icon-calendar mr-2"></i>${stringfyDate(article.displayDate)}</small>
          <p class="card-text v-align-gap justify-content-end">
           ${readMore}
            <button 
              class="btn btn-secondary-outline rounded-sm-btn"><i class="icon-arrow-right"></i></button>
          </p>
        </div>
      </a>
    </div>`);

      $('.messages #gallery-mixed-content').append(mediaJson[count])

      count++;

    }
    $('#pageNum').html(pageNum + ' / ' + articles.lastPage);
    $('#totalRes').html(articles.totalCount);

  }


  function emptyResult() {

    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى'
    $('.messages #gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
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