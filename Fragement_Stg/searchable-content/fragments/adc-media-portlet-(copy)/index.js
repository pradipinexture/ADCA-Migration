$(document).ready(function () {

  var objArr = [];
  var searchText = "";
  var pageNum = 1;
  var pageSize = 6;
  var categoryId = -1;
  var startDate = "";
  var endDate = "";
  var allArticles = {};
  var achievements = false;

  var readMore = Liferay.ThemeDisplay.getLanguageId() == "en_US"
    ? "Read More"
    : "اقرأ المزيد";

  getArticles();

  function getArticles() {
    $.ajax({
      url: `/o/adc-dxp-services/news/search?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&achievement=${achievements}&sort=displayDate:desc`,
      type: "GET",
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
        console.error("Error fetching articles", error);
      },
    });
  }

  $(document).on("click", ".news .next-btn", function () {
    if (pageNum < allArticles.lastPage) {
      pageNum++;
      getArticles();
    }
  });

  $(document).on("click", ".news .prev-btn", function () {
    if (pageNum > 1) {
      pageNum--;
      getArticles();
    }
  });

  $(".news .search-input").on("keydown", function (e) {
    if (e.keyCode == 13) {
      searchText = $(this).val();
      pageNum = 1;
      getArticles();
    }
  });

  $("#categoriesSelect").change(function () {
    categoryId = $(this).val();
    pageNum = 1;
    getArticles();
  });

  $("#clearBtn").on("click", function () {
    searchText = "";
    pageNum = 1;
    pageSize = 6;
    categoryId = -1;
    startDate = "";
    endDate = "";
    $(".advance-search .form-control").val("");
    getArticles();
  });

  $(".news #startDate").on("change", function () {
    startDate = $(this).val();
    getArticles();
  });

  $(".news #endDate").on("change", function () {
    endDate = $(this).val();
    getArticles();
  });

  $(".news #achievements").on("change", function () {
    achievements = true;
    pageNum = 1;
    getArticles();
  });

  $(".news #allNews").on("change", function () {
    achievements = false;
    pageNum = 1;
    getArticles();
  });

  function processResponse(articles) {
    $(".news #gallery-mixed-content").html("");
    objArr = [];
    var count = 0;
    $(".news #pageNum").html(pageNum + " / " + articles.lastPage);
    $(".news #totalRes").html(articles.totalCount);
    if (articles.items.length <= 0) {
      emptyResult();
      return;
    }

    for (let article of articles.items) {
      const imageUrl = resolvePreviewImage(article);

      objArr.push(`
        <div class="col-md-4 v-space" style="height:400px">
          <a href="/group/portal/news/detail?id=${article.articleId}" class="bg-white card h-100 overflow-hidden">
            <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
              <img class="img-object-fit" src="${imageUrl}">
              <div class="flex-column z-index-10 text-white">
                <h5 class="two-lines">${article.title}</h5>
                <span class="badge badge-secondary d-none">${article?.category?.name ?? ''}</span>
              </div>
              <small class="text-white z-index-10">
                <i class="icon-calendar mr-2"></i> ${stringfyDate(article.displayDate)}
              </small>
              <div class="more-div z-index-10 internal-card-body h-auto">
                <p class="card-text text-white v-align-gap justify-content-end mt-3">
                  ${readMore} <button class="btn btn-outline-light rounded-circle">
                    <i class="icon-arrow-right"></i>
                  </button>
                </p>
              </div>
            </div>
          </a>
        </div>
      `);

      $(".news #gallery-mixed-content").append(objArr[count]);
      count++;
    }
  }

  function emptyResult() {
    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == "en_US"
      ? "There is nothing here yet"
      : "لا يوجد محتوى";
    $(".news #gallery-mixed-content").html(
      `<div class="col p-5 text-center text-muted">${emptyMsg}</div>`
    );
  }

  function stringfyDate(dateStr) {
    let date = new Date(dateStr);
    let locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
    return date.toLocaleDateString(locale, {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }

  function resolvePreviewImage(article) {
    // First priority: Old format
    if (article?.previewImage) {
      return article.previewImage;
    }

    // Second: New format in attributes
    if (article?.attributes?.PreviewImage?.value) {
      try {
        const parsed = JSON.parse(article.attributes.PreviewImage.value);
        if (parsed && parsed.url) {
          return parsed.url;
        }
      } catch (e) {
        console.error("Error parsing PreviewImage:", e);
      }
    }

    // Fallback
    return "/o/ad-customs-theme/images/placeholder-logo.svg";
  }

});
