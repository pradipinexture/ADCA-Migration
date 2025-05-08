$(document).ready(function () {
    var objArr = [];
    var searchText = '';
    var pageNum = 1;
    var pageSize = 9;
    var categoryId = -1;
    var startDate = '';
    var endDate = '';
    var allArticles = {};
    getArticles();
    getCategories();

    function getCategories() {
        $.ajax({
            url: `/o/adc-dxp-services/related/categoriesList?type=media`,
            type: 'GET',
            headers: {
                   'x-csrf-token': Liferay.authToken, // Basic Auth from cURL
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
                'groupId': '2412549' // Hardcoded from cURL
            },
            success: function (categories) {
                $('#categoriesSelect').append(`<option value=-1>-</option>`);
                for (let cat of categories) {
                    $('#categoriesSelect').append(`<option value="${cat.value}">${cat.label}</option>`);
                }
            },
            error: function (error) {
                console.error("Error fetching categories:", error);
            }
        });
    }

    function getArticles() {
        $.ajax({
            url: `/o/adc-dxp-services/medias/galleries?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
            type: 'GET',
            headers: {
                 'x-csrf-token': Liferay.authToken, // Basic Auth from cURL
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
                'groupId': '2412549', // Hardcoded from cURL
                'Cookie': `GUEST_LANGUAGE_ID=${Liferay.ThemeDisplay.getLanguageId()}; JSESSIONID=${document.cookie.match(/JSESSIONID=([^;]+)/)?.[1] || ''}`
            },
            success: function (articles) {
                allArticles = articles;
                processResponse(articles);
            },
            error: function (error) {
                console.error("Error fetching galleries:", error);
                emptyResult();
            }
        });
    }

    $(document).on('click', '.media-gallery .next-btn', function (e) {
        if (pageNum < allArticles.lastPage) {
            pageNum++;
            getArticles();
        }
    });

    $(document).on('click', '.media-gallery .prev-btn', function (e) {
        if (pageNum > 1) {
            pageNum--;
            getArticles();
        }
    });

    $(".media-gallery .search-input").on("keydown", function search(e) {
        if (e.keyCode == 13) {
            searchText = $(this).val();
            pageNum = 1;
            getArticles();
        }
    });

    function processResponse(articles) {
        $('.media-gallery #gallery-mixed-content').html('');
        objArr = [];
        var count = 0;

        if (articles.items.length <= 0) {
            emptyResult();
        }
        for (let article of articles.items) {
            if (article?.attributes?.Type?.value) {
                if (article.attributes.Type.value == 'G') {
                    objArr.push(`<a class="col-md-4 v-space v-gallery-item" style="height:250px" data-sub-html="${article.title}" 
                        href="${article?.previewImage}">
                        <div class="position-relative h-100 w-100 rounded-10 card-shadowed overflow-hidden d-flex justify-content-end text-white p-3 flex-column" style="background:linear-gradient(to top ,#3d4742,transparent),url('${article.previewImage}') center; background-size:cover;">
                            <h5 class="two-lines">${article.title}</h5>
                            <small><i class="icon-calendar mr-2"></i>${stringfyDate(article.displayDate)}</small>
                        </div></a>`);
                } else if (article.attributes.Type.value == 'V') {
                    objArr.push(`<a data-sub-html="${article.title}" data-html="#video-${article.articleId}"
                        class="col-md-4 v-space v-gallery-item" style="height:250px">
                        <div style="display:none;" id="video-${article.articleId}">
                            <video class="lg-video-object lg-html5" controls preload="none">
                                <source src="${article.videoSRC}" type="video/mp4">
                                Your browser does not support HTML5 video.
                            </video>
                        </div>
                        <div class="position-relative h-100 w-100 rounded-10 card-shadowed overflow-hidden d-flex justify-content-end text-white p-3 flex-column"
                            style="background:linear-gradient(to top ,#3d4742,transparent),url('${article.previewImage ? article.previewImage : '/o/ad-customs-theme/images/placeholder-logo.svg'}') center; background-size:cover;">
                            <i class="icon-play play-icon"></i>
                            <h5 class="two-lines">${article.title}</h5>
                            <small><i class="icon-calendar mr-2"></i>${stringfyDate(article.displayDate)}</small>
                        </div>
                    </a>`);
                }
            }

            $('.media-gallery #gallery-mixed-content').append(objArr[count]);
            count++;
        }
        $('.media-gallery #pageNum').html(pageNum + ' / ' + articles.lastPage);
        $('.media-gallery #totalRes').html(articles.totalCount);

        lightGallery(document.getElementById('gallery-mixed-content'));
    }

    function emptyResult() {
        var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى';
        $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`);
    }

    $('#categoriesSelect').change(function () {
        categoryId = $(this).val();
        pageNum = 1;
        getArticles();
    });

    $('#clearBtn').on('click', function () {
        searchText = '';
        pageNum = 1;
        pageSize = 9;
        categoryId = -1;
        startDate = '';
        endDate = '';
        $('.advance-search .form-control').val('');
        getArticles();
    });

    $('#startDate').on('change', function () {
        startDate = $(this).val();
        getArticles();
    });

    $('#endDate').on('change', function () {
        endDate = $(this).val();
        getArticles();
    });

    function stringfyDate(dateStr) {
        let date = new Date(dateStr);
        let locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
        let formattedDate = date.toLocaleDateString(locale, {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });
        return formattedDate;
    }
});