$(document).ready(function () {


  var objArr = [];
  var searchText = '';
  var pageNum = 1;
  var pageSize = 6;
  var categoryId = -1;
  var startDate = '';
  var endDate = '';
  var allArticles = {};
  getArticles();
 
  function getArticles() {

    $.ajax({
      url: `/o/adc-dxp-services/events?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&calendarId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=startTime:desc`,
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

  

  function processResponse(articles) {
    $('.events #gallery-mixed-content').html('');
    objArr = [];
    var count = 0;

    if (articles.items.length <= 0) { emptyResult(); }
    for (let article of articles.items) {

      objArr.push(`<div class="col-md-4 v-space">
      <a href="/group/portal/events/detail?id=${article.calendarBookingId}"
      class="card events-card rounded-10 h-100">
        <div class="card-body">
          <div class="row h-100">
            <div class="col-4 pr-0">
              <div class="secondary-bg-card font-b">
                <div class="inner-card">
                  <p class="text-center text-white m-0 calendar-container-text"> ${stringfyDate(article.startTime
                  ,1)} <b>${stringfyDate(article.startTime
                  ,2)} </b> ${stringfyDate(article.startTime
                  ,3)} </p>
                </div>
              </div>
            </div>
            <div class="col-8 mt-3">
              <div class="card-text">
                <p class="card-text font-b three-lines"> ${article.title} </p><small><i class="icon-calendar mr-2"></i> ${stringfyDate(article.startTime
                  ,4)} </small>
              </div>
            </div>
            <div class="col-12">
              <p class="card-text small justify-content-end v-align-gap"> Read More <button
                  class="btn btn-secondary-outline rounded-sm-btn"
                  ><i class="icon-arrow-right"></i></button></p>
            </div>
          </div>
        </div>
      </a>
    </div>`);
 

      $('.events #latest-events ').append(objArr[count])

      count++;

    }
    $('.events #pageNum').html(pageNum + ' / ' + articles.lastPage);
    $('.events #totalRes').html(articles.totalCount);

  }


  function emptyResult() {

    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : ' لا يوجد محتوى'
    $('#gallery-mixed-content').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
  }

 
 


function stringfyDate(date, type) {

  // Convert the string to a Date object
  var date = new Date(Date.parse(date));
  // Use the .toLocaleString() method to format the date and time
  var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
  let options = {};
  switch (type) {
    case 1:
      options = {
        year: 'numeric',

      }
      break;

    case 2:
      options = {

        month: 'short',


      }
      break;

    case 3:
      options = {

        day: '2-digit',


      }
      break;

    case 4:
      options = {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',

      }

      break;


  }
  var formattedDate = date.toLocaleString(locale, options);

  return formattedDate;
}


});