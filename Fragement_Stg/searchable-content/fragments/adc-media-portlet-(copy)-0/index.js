$(document).ready(function () {
  const pageSize = 9;
  let searchText = '';
  let pageNum = 1;
  let categoryId = -1;
  let startDate = '';
  let endDate = '';
  let isEn = Liferay.ThemeDisplay.getLanguageId() === "en_US";
  let readMore = isEn ? "Read More" : "اقرأ المزيد";
  let expiredLbl = isEn ? "Expired" : "منتهي";

  let validCards = [];
  let totalValidCount = 0;
  let currentDisplayPage = 1;

  getArticles();

  $(document).on('click', '.promotions .next-btn', function () {
    currentDisplayPage++;
    var [current, total] = document.getElementById('pageNum').textContent.split('/').map(s => s.trim());
    if (current === total) return;
    loadDisplayPage();
  });

  $(document).on('click', '.promotions .prev-btn', function () {
    if (currentDisplayPage > 1) {
      currentDisplayPage--;
      loadDisplayPage();
    }
  });

  $(".promotions .search-input").on("keydown", function (e) {
    if (e.keyCode === 13) {
      searchText = $(this).val();
      pageNum = 1;
      currentDisplayPage = 1;
      validCards = [];
      totalValidCount = 0;
      getArticles();
    }
  });

  function getArticles(callback) {
    $.ajax({
      url: `/o/adc-dxp-services/promotions/search?search=${searchText}&page=${pageNum}&pageSize=20&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=displayDate:desc`,
      type: 'GET',
      headers: {
        'x-csrf-token': Liferay.authToken,
        'languageId': Liferay.ThemeDisplay.getLanguageId(),
        'groupId': Liferay.ThemeDisplay.getSiteGroupId()
      },
      success: function (articles) {
        let items = articles.items || [];
        let valid = [];

        for (let article of items) {
          if (
            !article.title ||
            !article.displayDate || isNaN(Date.parse(article.displayDate)) ||
            !article.endDate || isNaN(Date.parse(article.endDate))
          ) {
            continue;
          }
          valid.push(article);
        }

        validCards = validCards.concat(valid);
        totalValidCount = validCards.length;

        if (validCards.length < currentDisplayPage * pageSize && pageNum < articles.lastPage) {
          pageNum++;
          getArticles(callback); // recursively load next page
        } else {
          if (callback) callback();
          else loadDisplayPage();
        }
      },
      error: function () {
        emptyResult();
      }
    });
  }

  function loadDisplayPage() {
    $('#gallery-mixed-content').html('');
    const start = (currentDisplayPage - 1) * pageSize;
    const end = start + pageSize;
    const pageArticles = validCards.slice(start, end);

    if (pageArticles.length === 0) {
      emptyResult();
      return;
    }

    for (let article of pageArticles) {
      let cardHtml = generateCard(article);
      $('#gallery-mixed-content').append(cardHtml);
    }

    $('#pageNum').html(`${currentDisplayPage} / ${Math.ceil(totalValidCount / pageSize)}`);
    $('#totalRes').html(totalValidCount);
  }

  function generateCard(article) {
    let endDateObj = new Date(article.endDate);
    let displayDateObj = new Date(article.displayDate);
    let currentDate = new Date();
    let isExpired = endDateObj < currentDate;

    let imgUrl = resolvePreviewImage(article);
    let category = article?.category?.name ?? '';
    let displayDateText = stringfyDate(displayDateObj);
    let endDateText = stringfyDate(endDateObj);

    return `
      <div class="col-md-4 v-space" style="height:300px">
        <a href="/group/portal/promotions/detail?id=${article.articleId}"
          class="bg-white card h-100 overflow-hidden">
          <div class="col mx-auto hover-box position-relative d-flex flex-column justify-content-end overflow-hidden p-3 h-100">
            <img class="img-object-fit" src="${imgUrl}">
            ${!isExpired && article?.attributes?.DiscountDetails?.value ? 
              `<span class="discount-badge">${article.attributes.DiscountDetails.value}</span>` : ''} 
            ${isExpired ? 
              `<span class="expired-badge"><i class="icon-warning-sign mr-1"></i>${expiredLbl}</span>` : ''}
            <div class="flex-column z-index-10 text-white">
              <h5 class="two-lines">${article.title}</h5>
              <p class="badge badge-secondary mb-2">${category}</p>
            </div>
            <small class="text-white z-index-10">
              <i class="icon-calendar mr-2"></i> ${displayDateText}
              <i class="icon-arrow-right mx-2"></i> ${endDateText}
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
    `;
  }

  function emptyResult() {
    var emptyMsg = isEn ? 'There is nothing here yet' : 'لا يوجد محتوى';
    $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`);
    $('#totalRes').html('0');
    $('#pageNum').html('');
  }

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
    if (article?.previewImage) return article.previewImage;
    if (article?.attributes?.PreviewImage?.value) {
      try {
        const parsed = JSON.parse(article.attributes.PreviewImage.value);
        if (parsed?.url) return parsed.url;
      } catch (e) {
        console.error("Error parsing PreviewImage:", e);
      }
    }
    return "/o/ad-customs-theme/images/placeholder-logo.svg";
  }
});
