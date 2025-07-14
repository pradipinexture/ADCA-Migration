$(document).ready(function () {


    var objArr = [];
    var searchText = '';
    var pageNum = 1;
    var pageSize = 6;
    var categoryId = -1;

    var allArticles = {};
    var readMore =
        Liferay.ThemeDisplay.getLanguageId() == "en_US"
            ? "Read More"
            : "اقرأ المزيد";
    getArticles();
    getCategories();




    function getCategories() {

        $.ajax({
            url: `/o/adc-dxp-services/related/categoriesList?type=knowledge-sharing`,
            type: 'GET',
            headers: {
                   'x-csrf-token': Liferay.authToken,
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
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

        let endPoint = `/o/headless-delivery/v1.0/content-structures/2430772/structured-contents?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&sort=dateCreated:desc&p_auth=${Liferay.authToken}`;
        if (categoryId != -1)
            endPoint = `/o/headless-delivery/v1.0/content-structures/2430772/structured-contents?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&filter=(taxonomyCategoryIds/any(t:t eq ${categoryId}))&sort=dateCreated:desc&p_auth=${Liferay.authToken}`;

        $.ajax({
            url: endPoint,
            type: 'GET',
            headers: {
                 "Accept-Language": (Liferay.ThemeDisplay.getLanguageId()).replace('_', '-'),
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


    getCreators();
    function getCreators() {


        $.ajax({
            url: `/o/headless-delivery/v1.0/content-structures/2430772/structured-contents?page=1&pageSize=100&sort=dateCreated:desc&fields=creator`,
            type: 'GET',
            headers: {
                   'x-csrf-token': Liferay.authToken,
                "Accept-Language": (Liferay.ThemeDisplay.getLanguageId()).replace('_', '-'),
                'groupId': Liferay.ThemeDisplay.getSiteGroupId()
            },
            success: function (result) {
                getTopCreators(result);
            },
            error: function (error) {
                // Handle the error
            }
        });


    }

    function getTopCreators(users) {
        var topCreatorsArr = [];
        var usersArr = users?.items
        for (let user of usersArr) {


            let id = user?.creator?.id;
            let entriesOfUser = usersArr.filter(x => x.creator.id == id)
            if (entriesOfUser?.length > 0) {
                topCreatorsArr.push({ "user_name": user?.creator?.name, "user_id": user?.creator?.id, "entries": entriesOfUser.length })
            }
            usersArr = usersArr.filter(x => x.creator.id != id)


        }

         var count=1;

       
        
         topCreatorsArr.sort(function(a, b) {
            return b.entries - a.entries; // Sort in descending order based on "entries" property
        });
        
     
       
     
        for (let item of topCreatorsArr.slice(0, 5)) {

            var creator = `<div class="col-md-12 v-space">
            <div class="card-shadowed user-info text-center position-relative overflow-hidden">
              <span class="rank">${count}</span>
             
      
              <h5>${item.user_name}</h5>
              <span>${Liferay.ThemeDisplay.getLanguageId()  == 'en_US' ?'Total Participations' :'إجمالي المشاركات'}: ${item.entries}</span>
      
            </div>
          </div>`;

          $('.top-emps').append(creator);
            count++;

        }

    }


    $(document).on('click', '.knowledge-sharing .next-btn', function (e) {
        if (pageNum < allArticles.lastPage) {
            pageNum++;
            getArticles();
        }

    });

    $(document).on('click', '.knowledge-sharing .prev-btn', function (e) {

        if (pageNum > 1) {
            pageNum--;
            getArticles();
        }

    });

    $(".knowledge-sharing .search-input").on("keydown", function search(e) {
        if (e.keyCode == 13) {
            searchText = $(this).val();
            pageNum = 1;
            getArticles();
        }
    });


    function processResponse(articles) {
        $('.knowledge-sharing #gallery-mixed-content').html('');
        objArr = [];
        var count = 0;

        if (articles.items.length <= 0) { emptyResult(); }
        for (let article of articles.items) {


            let poster = article.contentFields.find(x => x.name == 'KS_poster')?.contentFieldValue?.image?.contentUrl;
            let docs = article.contentFields.filter(x => x.name == 'KS_attachments');

            let category = article?.taxonomyCategoryBriefs[0]?.taxonomyCategoryName;
            let docsCount = 0;
            for (let item of docs) {

                docsCount = item?.contentFieldValue?.document ? (docsCount + 1) : docsCount;
            }
            let attachmentsList = `<span class="article-attachments"><small class="attachment-badge">${docsCount}</small><i class="icon-paper-clip"></i></span>`;


            objArr.push(`<div class="col-md-6 v-space " style="height:300px">
      <div class="bg-white card h-100 overflow-hidden">
        <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
          <img class="img-object-contain" src="${poster ? poster : '/o/ad-customs-theme/images/placeholder-logo.svg'}">
          <div class=" flex-column z-index-10 text-white">
            <h5 class="two-lines"> ${article.title} </h5>
            <p class="text-truncate"> ${category ? category : ''} </p>
          
          </div><small class="text-white z-index-10"> <i class="icon-calendar mr-2"></i> ${stringfyDate(article?.datePublished
            )}</small>
          <div class="more-div z-index-10 internal-card-body h-auto">
            <p class="card-text text-white v-align-gap justify-content-end mt-3"> ${readMore} <a
                class="btn btn-outline-light rounded-circle"
                href="/group/portal/knowledge-sharing/detail?id=${article.id}"><i
                  class="icon-arrow-right"></i></a></p>
          </div>
        </div>
      </div>
    </div`);

            $('.knowledge-sharing #gallery-mixed-content').append(objArr[count])

            count++;

        }
        $('.knowledge-sharing #pageNum').html(pageNum + ' / ' + articles.lastPage);
        $('.knowledge-sharing #totalRes').html(articles.totalCount);

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

        $('.advance-search .form-control').val('');
        getArticles();
    })


    function stringfyDate(date) {
        var date = new Date(date);
        var year = date.getFullYear();
        var month = date.getMonth() + 1; // January is 0, so add 1
        var day = date.getDate();

        return day + ' - ' + month + ' - ' + year;
    }

});


$(".btn-add-article").on('click', function () {

    $('.knowledge-sharing').toggleClass('d-none')


});