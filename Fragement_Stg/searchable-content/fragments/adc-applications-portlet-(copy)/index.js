$(document).ready(function () {


  var objArr = [];
  var searchText = '';
  var pageNum = 1;
  var pageSize = 9;
  var categoryId = 62179;
  var startDate = '';
  var endDate = '';
  var allArticles = {};
  getArticles();
   
  
 
  function getArticles() {

    $.ajax({
      url: `/o/adc-dxp-services/quick-links/search?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
      type: 'GET',
      headers: {
        'Authorization': 'Bearer ' + Liferay.authToken,
        "languageId": Liferay.ThemeDisplay.getLanguageId(),
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

  $(document).on('click', '.links .next-btn', function (e) {
    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }

  });

  $(document).on('click', '.links .prev-btn', function (e) {

    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }

  });

  $(".links .search-input").on("keydown", function search(e) {
    if (e.keyCode == 13) {
      searchText = $(this).val();
      pageNum = 1;
      getArticles();
    }
  });


  function processResponse(articles) {
    $('.links #gallery-mixed-content').html('');
    objArr = [];
    var count = 0;

    if (articles.items.length <= 0) { emptyResult(); }
    for (let article of articles.items) {

  objArr.push(`<div class="col-lg-4 col-md-6 v-space"><a class="btn btn-light applications-btn btn-block font-b" href=${article?.attributes?.quickLinksLink?.value}">
         <img alt="" height="40" src="${article.previewImage}"> ${article.title}</a></div>`)

      $('.links #gallery-mixed-content').append(objArr[count])

      count++;

    }
    $('.links #pageNum').html(pageNum + ' / ' + articles.lastPage);
    $('.links #totalRes').html(articles.totalCount);

  }


  function emptyResult() {

    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى '
    $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
  }

  

});