$(document).ready(function () {

  var objArr = [];
  var searchText = '';
  var pageNum = 1;
  var pageSize = 6;
  var categoryId = -1;
  var startDate = '';
  var endDate = '';
  var allArticles = {};

  var isEn = Liferay.ThemeDisplay.getLanguageId() == "en_US";
  var readMore = isEn ? "Read More" : "اقرأ المزيد";
  var expiredLbl = isEn ? "Expired" : "منتهي";

  getArticles();
  getCategories();

  function getCategories() {
    $.ajax({
      url: `/o/adc-dxp-services/related/categoriesList?type=Announcement`,
      type: 'GET',
      headers: {
        'x-csrf-token': Liferay.authToken,
        "languageId": Liferay.ThemeDisplay.getLanguageId(),
        'groupId': Liferay.ThemeDisplay.getSiteGroupId()
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
      url: `/o/adc-dxp-services/announcements/web-content?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
      type: 'GET',
      headers: {
        'x-csrf-token': Liferay.authToken,
        languageId: Liferay.ThemeDisplay.getLanguageId(),
        groupId: Liferay.ThemeDisplay.getSiteGroupId(),
      },
      success: function (articles) {
        allArticles = articles;
        processResponse(articles);
      },
      error: function (error) {
        console.error("Error fetching announcements:", error);
        emptyResult();
      }
    });
  }

  $(document).on('click', '.botcast .next-btn', function () {
    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }
  });

  $(document).on('click', '.botcast .prev-btn', function () {
    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }
  });

  $(".botcast .search-input").on("keydown", function (e) {
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

    if (articles.items.length <= 0) {
      emptyResult();
      return;
    }

    for (let article of articles.items) {
      const imgUrl = resolvePreviewImage(article);

      // Check if expired
      let expiredBadge = '';
      if (article?.endDate && !isNaN(Date.parse(article.endDate))) {
        const endDateObj = new Date(article.endDate);
        const now = new Date();
        if (endDateObj < now) {
          expiredBadge = `<span class="expired-badge"><i class="icon-warning-sign mr-1"></i>${expiredLbl}</span>`;
        }
      }

      objArr.push(`
        <div class="col-md-4 v-space" style="height:400px">
          <a href="/group/portal/adc-botcast/detail?id=${article.articleId}"
            class="bg-white card h-100 overflow-hidden">
            <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
              <img class="img-object-fit" src="${imgUrl}">
              ${expiredBadge}
              <div class="flex-column z-index-10 text-white">
                <h5 class="two-lines">${article.title}</h5>
                <span class="badge badge-secondary d-none">${article?.category?.name ?? ''}</span>
              </div>
              <small class="text-white z-index-10">
                <i class="icon-calendar mr-2"></i> ${stringfyDate(article.displayDate)}
              </small>
              <div class="more-div z-index-10 internal-card-body h-auto">
                <p class="card-text text-white v-align-gap justify-content-end mt-3">
                  ${readMore} 
                  <button class="btn btn-outline-light rounded-circle">
                    <i class="icon-arrow-right"></i>
                  </button>
                </p>
              </div>
            </div>
          </a>
        </div>
      `);

      $('.botcast #gallery-mixed-content').append(objArr[count]);
      count++;
    }

    $('.botcast #pageNum').html(pageNum + ' / ' + articles.lastPage);
    $('.botcast #totalRes').html(articles.totalCount);
  }

  function emptyResult() {
    var emptyMsg = isEn ? 'There is nothing here yet' : 'لا يوجد محتوى';
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
    pageSize = 6;
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
    let locale = isEn ? 'en-AE' : 'ar';
    return date.toLocaleDateString(locale, {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }

  function resolvePreviewImage(article) {
    if (article?.previewImage) {
      return article.previewImage;
    }

    if (article?.attributes?.PreviewImage?.value) {
      try {
        const parsed = JSON.parse(article.attributes.PreviewImage.value);
        if (parsed?.url) {
          return parsed.url;
        }
      } catch (e) {
        console.error("Error parsing PreviewImage:", e);
      }
    }

    return "/o/ad-customs-theme/images/placeholder-logo.svg";
  }

});
