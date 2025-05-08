$(document).ready(function () {
    var mediaJson = [];
    var searchText = '';
    var pageNum = 1;
    var pageSize = 6;
    var allArticles = {};
    getArticles();

    function getArticles() {
        $('#gallery-mixed-content').html(`<div class="col">
            <div class="bg-white p-5 rounded-10 d-flex align-items-center justify-content-center ">
                <div class="loader"></div>
            </div>
        </div>`);

        $.ajax({
            url: `/o/adc-dxp-services/users?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&sort=firstName:asc`,
            type: 'GET',
            headers: {
                'Accept': 'application/json',
                   'x-csrf-token': Liferay.authToken, // Basic Auth with Base64-encoded credentials
                'Cookie': `GUEST_LANGUAGE_ID=${Liferay.ThemeDisplay.getLanguageId()}; JSESSIONID=${document.cookie.match(/JSESSIONID=([^;]+)/)?.[1] || ''}`
            },
            success: function (articles) {
                allArticles = articles;
                processResponse(articles);
                $('.loader').css('display', 'none');
            },
            error: function (error) {
                // Handle the error
                emptyResult();
            }
        });
    }

    $(document).on('click', '.emp-directory .next-btn', function (e) {
        if (pageNum < allArticles.lastPage) {
            pageNum++;
            getArticles();
        }
    });

    $(document).on('click', '.emp-directory .prev-btn', function (e) {
        if (pageNum > 1) {
            pageNum--;
            getArticles();
        }
    });

    $(".emp-directory .search-input").on("keydown", function search(e) {
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
        var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en' : 'ar';

        if (articles.items.length <= 0) {
            emptyResult();
        }
        for (let article of articles.items) {
            mediaJson.push(`<div class="col-md-6 v-space h-100">
                <div class="bg-white h-100 p-4 d-flex flex-column justify-content-between rounded-10 card-shadowed">
                    <div class="d-flex align-items-center pb-3">
                        <img src="/o/ad-customs-theme/images/orgChart/placeholder.png" width="40" height="40" style="border-radius: 50%;">
                        <b class="mb-0 ml-3 text-truncate">${article.name} 
                            <small class="text-muted mb-0 text-truncate"> ${locale == 'en' ? article.description : article.jobTitle}</small>
                        </b>
                    </div>
                    <ul class="px-0 ml-2 mb-0 list-unstyled border-top pt-3 small">
                        <li class="contact-email-btn">
                            <i class="icon-envelope-alt mr-3"></i>
                            <a class="m-auto text-dark" href="mailto:${article.email}">${article.email}</a>
                        </li>
                        <li class="contact-phone-btn">
                            <i class="icon-phone mr-3"></i>
                            ${article.mobileNumber && article.mobileNumber != '-' ? `
                                <a class="m-auto text-dark" href="tel:${article.mobileNumber}">${article.mobileNumber}</a>` : ''}
                            ${article.mobileNumber && article.mobileNumber != '-' && article.telephoneNumber && article.telephoneNumber != '-' ? `
                                <a class="m-auto text-dark ml-2" href="tel:${article.telephoneNumber}">
                                    <b class="mx-1">|</b> ${article.telephoneNumber}
                                </a>` : (article.telephoneNumber && article.telephoneNumber != '-' ? `
                                <a class="m-auto text-dark ml-2" href="tel:${article.telephoneNumber}">
                                    ${article.telephoneNumber}
                                </a>` : '')}
                            ${(!article.mobileNumber || article.mobileNumber === '-') && (!article.telephoneNumber || article.telephoneNumber === '-') ? '-' : ''}
                        </li>
                        <li class="contact-phone-btn">
                            <i class="icon-map-marker mr-3"></i>
                            <a class="m-auto text-dark">${article?.office ? article?.office : '-'}</a>
                        </li>
                    </ul>
                </div>
            </div>`);

            $('.emp-directory #gallery-mixed-content').append(mediaJson[count]);
            count++;
        }
        $('#pageNum').html(pageNum + ' / ' + articles.lastPage);
        $('#totalRes').html(articles.totalCount);
    }

    function emptyResult() {
        var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى ';
        $('.emp-directory #gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`);
    }
});
