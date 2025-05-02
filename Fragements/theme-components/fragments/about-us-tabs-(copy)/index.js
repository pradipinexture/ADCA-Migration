$(document).ready(function () {


  

  var locale = Liferay.ThemeDisplay.getLanguageId();
  var mediaJson = [];
  var searchText = '';
  var pageNum = 1;
  var pageSize = 6;
  var allArticles = {};
  var sectionsLabel = locale == 'en_US' ? 'Sections' : 'الأقسام';
  var locationLabel = locale == 'en_US' ? 'Location' :'الموقع';
  var contactsLabel = locale == 'en_US' ? 'Contacts':'أرقام التواصل';
  var linkLabel = locale == 'en_US' ?  'Link' : 'الرابط';
  getArticles();



  function getArticles() {

    $.ajax({
      url: `/o/adc-dxp-services/contacts?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=-1&sort=title:asc&excludeCategoryId=Main%20Building`,
      type: 'GET',
      headers: {
                        'Authorization': 'Basic VmlzaGFsLlNoYWg6SW5leHR1cmUxMjMh', // Basic Auth,
        "languageId": locale,
        "groupId": Liferay.ThemeDisplay.getSiteGroupId()
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

  $(document).on('click', '.contacts .next-btn', function (e) {

    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }

  });

 
  $(document).on('click', '.contacts .prev-btn', function (e) {

    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }

  });

  $(".contacts .search-input").on("keydown", function search(e) {
    if (e.keyCode == 13) {
      searchText = $(this).val();
      pageNum = 1;
      getArticles();
    }
  });


  function processResponse(articles) {
    $('#contacts-section').html('');
    mediaJson = [];
    var count = 0;

    if (articles.items.length <= 0) { emptyResult(); }
    for (let contact of articles.items) {

      mediaJson.push(`<div class="col-md-12 p-0 about-us"  >
      <div id="first-stage-${count}">
        <div class="card-shadowed mb-3 bg-white rounded-10 overflow-hidden p-4">
          <div class="d-flex " id="circle-icon">
            <a data-toggle="collapse" href="#contact-${count}"
              class="d-flex align-self-center mr-3 h-100 justify-content-between w-100" role="button"
              aria-expanded="false" aria-controls="contact-${count}">
              <b class="my-auto">${contact?.title}</b>
              <i class="icon-chevron-down d-block my-auto text-secondary"></i>
            </a>
          </div>
    
          <div class="collapse " id="contact-${count}">
            <div class="card-body ">
              <div class="col-12 pr-3 pl-3 pt-2 pb-2 about ">
                <div class="row ">
                  <div class="col">
    
                    <h5><i class="icon-sitemap mr-2"></i> ${sectionsLabel}</h5>
                    <ul class="list-unstyled sections-ul">
                    </ul>
    
    
                  </div>
    
                  <div class="col">
                    <h5><i class="icon-phone mr-2"></i>${contactsLabel}</h5>
                    <ul class="list-unstyled contacts-ul"> </ul>
                  </div>
    
                  <div class="col">
    
                    <h5><i class="icon-map-marker mr-2"></i>${locationLabel}</h5>
     
                    <p><i aria-hidden="true" class="fa fa-map-marker"></i> <a
                        href="${contact.attributes.LocationMapLink.value}"><i
                          class="icon-chevron-right mr-2 text-secondary"></i> ${linkLabel}</a>
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
     </div>`);

     $('#contacts-section').append(mediaJson[count]);


      if (contact?.sections) {
        for (let section of contact.sections) {
          $('.sections-ul').eq(count).append(`<li class="py-2 border-bottom-dashed">
            <i class="icon-chevron-right mr-2 text-secondary"></i>${section}</li>`);}
      }

       if (contact.numbers[0]['']) {
        for (let number of contact.numbers[0]['']) {
          $('.contacts-ul').eq(count).append(`<li  class="py-2 border-bottom-dashed">
            <i class="icon-chevron-right mr-2 text-secondary"></i> <a href="tel:${number}"
              class="mb-0">${number}</a>
            </li>`);

        }

      }

       count++;
      $('#pageNum').html(pageNum + ' / ' + articles.lastPage);
      $('#totalRes').html(articles.totalCount);

    }
  }

    function emptyResult() {

      var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'No matching results found' : 'لم يتم العثور على نتائج مطابقة'
      $('#contacts-section').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
    }


$(document).on('click','[data-toggle="collapse"]', function(){
     
        $(this).find('i').toggleClass('icon-chevron-up');
          $(this).find('i').toggleClass('icon-chevron-down');
   })
 


  });