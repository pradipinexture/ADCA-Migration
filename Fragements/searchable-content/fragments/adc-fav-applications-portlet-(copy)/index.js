$(document).ready(function () {



  var objArr = [];
  var searchText = '';
  var pageNum = 1;
  var pageSize = 9;
  var categoryId = 2427913;
  var startDate = '';
  var endDate = '';
  var allArticles = {};
  getArticles();
 

      function getArticles() {

        $.ajax({
          url: `/o/headless-delivery/v1.0/content-structures/2428177/structured-contents?page=${pageNum}&pageSize=${pageSize}&search=${searchText}&filter=(taxonomyCategoryIds/any(t:t eq ${categoryId}))`,
            type: 'GET',
            headers: {
                   'x-csrf-token': Liferay.authToken,
                "Accept-Language": Liferay?.ThemeDisplay?.getLanguageId()?.replace("_", "-"),
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

  $(document).on('click', '.applications .next-btn', function (e) {
    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }
    event.preventDefault()


  });

  $(document).on('click', '.applications .prev-btn', function (e) {

    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }
    event.preventDefault()


  });

  $(".applications .search-input").on("keydown", function search(e) {
    if (e.keyCode == 13) {
      searchText = $(this).val();
      pageNum = 1;
      getArticles();
    }
  });


  function processResponse(articles) {
    $('.applications #gallery-mixed-content').html('');
    objArr = [];
    var count = 0;

    if (articles.items.length <= 0) { emptyResult(); }
    for (let article of articles.items) {
      
      var previewImageField = article.contentFields.find(field => field.name === "PreviewImage");
var quickLinksLinkField = article.contentFields.find(field => field.name === "quickLinksLink");

var previewImageUrl = previewImageField ? previewImageField.contentFieldValue.image.contentUrl : null;
var quickLinksLink = quickLinksLinkField ? quickLinksLinkField.contentFieldValue.data : null;


      objArr.push(`<div class="col-lg-4 col-md-6 v-space">
      <button articlePK=${article?.resourcePrimaryKey} thumbsUp=${article?.ratings?.thumbsUp} 
       voted=${article?.ratings?.voted} thumbsDown=${article?.ratings?.thumbsDown}" 
       class="btn fav-btn ${article.attributes?.Pinned?.value == 'y' ? 'd-none':'' }">
       <i class="${article.ratings?.thumbsUp ? 'icon-bookmark' : 'icon-bookmark-empty'}"></i></button><a class="btn btn-light applications-btn btn-block font-b" target="_blank" href="${quickLinksLink}">
       <img alt=""  height="40" src="${previewImageUrl}"> ${article.title}</a></div>`);

      $('.applications #gallery-mixed-content').append(objArr[count])

      count++;

    }
    $('.applications #pageNum').html(pageNum + ' / ' + articles.lastPage);
    $('.applications #totalRes').html(articles.totalCount);

  }


   $(document).on('click','.fav-btn',function(e){

    var thumbsUp = JSON.parse($(this).attr('thumbsUp'));
    var pk = $(this).attr('articlePK');
    
      
         thumbsUp = !thumbsUp;
         console.log(thumbsUp)
         var bool = thumbsUp == true ? 1.0 : 0.0;

   
      Liferay.Service('/ratingsentry/update-entry',
        {
          className: 'com.liferay.journal.model.JournalArticle',
          classPK: pk,
          score: bool
        }
      );
    
    
    
    

    getArticles();
   });


  function emptyResult() {

    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى '
    $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
  }



});