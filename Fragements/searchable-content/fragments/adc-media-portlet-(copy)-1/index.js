$(document).ready(function () {


var objArr = [];
var searchText = '';
var pageNum = 1;
var pageSize = 6;
var categoryId = -1;
var startDate = '';
var endDate = '';
var allArticles = {};
var isEn = Liferay.ThemeDisplay.getLanguageId() == "en_US" ? true : false;
  var readMore = isEn
? "Read More"
    : "اقرأ المزيد";

  var  expiredLbl = isEn ? "Expired" : "منتهي";
getArticles();
getCategories();





function getCategories() {

  $.ajax({
    url: `/o/adc-dxp-services/events/calendars?pageSize=999`,
    type: 'GET',
    headers: {
       'groupId': Liferay.ThemeDisplay.getSiteGroupId(),
      "languageId": Liferay.ThemeDisplay.getLanguageId(),
      'groupId': Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (categories) {
      $('#categoriesSelect').append(`<option value=-1>-</option>`)
      for (let cat of categories.items) {
        $('#categoriesSelect').append(`<option value="${cat.primaryKey}">${cat.name}</option>`)

      }
    },
    error: function (error) {
      // Handle the error
    }
  });

}

function getArticles() {

  $.ajax({
    url: `/o/adc-dxp-services/events?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&calendarId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=startTime:desc`,
    type: 'GET',
    headers: {
       'groupId': Liferay.ThemeDisplay.getSiteGroupId(),
      "languageId": Liferay.ThemeDisplay.getLanguageId(),
      'groupId': Liferay.ThemeDisplay.getSiteGroupId(),
    },
    success: function (articles) {
      allArticles = articles;
      processResponse(articles);
    },
    error: function (error) {
      // Handle the error
       emptyResult();
    }
  });


}

$(document).on('click', '.events .next-btn', function (e) {
  if (pageNum < allArticles.lastPage) {
    pageNum++;
    getArticles();
  }

});

$(document).on('click', '.events .prev-btn', function (e) {

  if (pageNum > 1) {
    pageNum--;
    getArticles();
  }

});

$(".events .search-input").on("keydown", function search(e) {
  if (e.keyCode == 13) {
    searchText = $(this).val();
    pageNum = 1;
    getArticles();
  }
});


function processResponse(articles) {
  $('.events #gallery-mixed-content').html('');
  objArr = [];
  var count = 0;

  if (articles.items.length <= 0) { emptyResult(); }
  for (let article of articles.items) {

   let startTime = new Date(article.startTime);
    // Get the current date and time
    let currentDate = new Date();
    // Compare the dates
    let isExpired = startTime < currentDate;
    
    objArr.push(`<div class="col-md-4 v-space">
    <a 
    href="/group/portal/events/detail?id=${article.calendarBookingId}"
    class="card events-card rounded-10 h-100">
         ${isExpired ? '<span class="expired-badge"><i class="icon-warning-sign mr-1"></i>'+expiredLbl+' </span>'  : ''}
      <div class="card-body">
        <div class="row h-100">
          <div class="col-4 pr-0">
            <div class="secondary-bg-card font-b">
              <div class="inner-card">
                <p class="text-center text-white m-0 calendar-container-text">${stringfyDate(article.startTime, 1)} <b class="text-uppercase">${stringfyDate(article.startTime, 2)} </b> ${stringfyDate(article.startTime, 3)} </p>
              </div>
            </div>
          </div>
          <div class="col-8 mt-3">
            <div class="card-text">
              <p class="card-text font-b three-lines"> ${article.title} </p><small><i class="icon-calendar mr-1"></i> ${stringfyDate(article.startTime
      , 4)} </small>
            </div>
          </div>
          <div class="col-12">
            <p class="card-text small justify-content-end v-align-gap"> ${readMore} <button
                class="btn btn-secondary-outline rounded-sm-btn"
                ><i class="icon-arrow-right"></i></button></p>
          </div>
        </div>
      </div>
    </a>
  </div>`);


    $('.events #gallery-mixed-content').append(objArr[count])

    count++;

  }
  $('.events #pageNum').html(pageNum + ' / ' + articles.lastPage);
  $('.events #totalRes').html(articles.totalCount);

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
  startDate = '';
  endDate = '';
  $('.advance-search .form-control').val('');
  getArticles();
})
$('#startDate').on('change', function () { startDate = $(this).val(); getArticles(); })
$('#endDate').on('change', function () { endDate = $(this).val(); getArticles(); })


$('#getPast').on('change', function () {
  today = new Date();
  dateStr = today.getDate() + '-' + (today.getMonth() + 1) + '-' + today.getFullYear();
  endDate = startDate;
  startDate ='';
  pageNum=1;
  getArticles();
   $('.advance-search .datepicker.form-control').val('');
})

$('#getUpComing').on('change', function () {
  today = new Date();
  dateStr = today.getDate() + '-' + (today.getMonth() + 1) + '-' + today.getFullYear();
  endDate = '';
  startDate =dateStr;
  pageNum=1;
    getArticles();
     $('.advance-search .datepicker.form-control').val('');
})


$('#getAll').on('change', function () {
  endDate = '';
  startDate ='';
  pageNum=1;
    getArticles();
})



function stringfyDate(date, type) {

  // Convert the string to a Date object
  var date = new Date(Date.parse(date));
  // Use the .toLocaleString() method to format the date and time
  var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
  let options = {};
  switch (type) {
    case 1:
      options = { day: '2-digit', }
      break;

    case 2:
      options = { month: 'short', }
      break;

    case 3:
      options = { year: 'numeric', }
      break;

    case 4:
    options = {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }

      break;


  }
  var formattedDate = date.toLocaleString(locale, options);
  console.log(type,formattedDate)
  return formattedDate;
}

});