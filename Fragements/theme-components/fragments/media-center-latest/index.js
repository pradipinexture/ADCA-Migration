$(document).ready(function () {


    var objArr = [];
    var searchText = '';
    var pageNum = 1;
    var pageSize = 12;
    var categoryId = 4069808;
    var startDate = '';
    var endDate = '';
    var allArticles = {};
    getArticles();
 
    var rtlVal = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? false : true;

var isEn = Liferay.ThemeDisplay.getLanguageId() == "en_US" ? true : false;
  var readMore = isEn
? "Read More"
    : "اقرأ المزيد";

  var  expiredLbl = isEn ? "Expired" : "منتهي";
    function getArticles() {
  
      $.ajax({
        url: `/o/adc-dxp-services/events?search=${searchText}&page=${pageNum}&pageSize=${pageSize}&calendarId=${categoryId}&startDate=${startDate}&endDate=${endDate}&sort=startTime:desc`,
        type: 'GET',
        headers: {
          'Authorization': 'Bearer ' + Liferay.authToken,
          "languageId": Liferay.ThemeDisplay.getLanguageId(),
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
  
  
  
    function processResponse(articles) {
      if(articles.totalCount<1){
        if(Liferay.ThemeDisplay.getLanguageId()==='en_US')
              $('.events').html('<h3>There is nothing here yet</h3>');
              else{
                              $('.events').html('<h3>لا يوجد محتوى</h3>');

              }

      }
      $('.events .events-carousel').html('');
      objArr = [];
  
      const chunked = chunk(articles.items, 6)
      for (let i = 0; i < chunked.length; i++) {
        let str = `<div class="item"><div class="row">`
        for (let article of chunked[i]) {
            
    let startTime = new Date(article.startTime);
    // Get the current date and time
    let currentDate = new Date();
    // Compare the dates
    let isExpired = startTime < currentDate;
          str += (`<div class="col-md-4 v-space">
      <a href="/group/portal/events/detail?id=${article.calendarBookingId}"
      class="card events-card rounded-10 h-100">
        ${isExpired ? '<span class="expired-badge"><i class="icon-warning-sign mr-1"></i>'+expiredLbl+' </span>'  : ''}
        <div class="card-body">
          <div class="row h-100">
            <div class="col-4 pr-0">
              <div class="secondary-bg-card font-b">
                <div class="inner-card">
                  <p class="text-center text-white m-0 calendar-container-text"> ${stringfyDate(article.startTime
            , 1)} <b>${stringfyDate(article.startTime
              , 2)} </b class="text-uppercase"> ${stringfyDate(article.startTime
                , 3)} </p>
                </div>
              </div>
            </div>
            <div class="col-8 mt-3">
              <div class="card-text">
                <p class="card-text font-b three-lines"> ${article.title} </p><small><i class="icon-calendar mr-2"></i> ${stringfyDate(article.startTime
                  , 4)} </small>
              </div>
            </div>
            <div class="col-12">
              <p class="card-text small justify-content-end v-align-gap"> ${readMore} <button
                  class="btn btn-secondary-outline rounded-sm-btn" ><i class="icon-arrow-right"></i></button></p>
            </div>
          </div>
        </div>
      </a>
    </div>`);
  
  
   
          
  
        }
        str += `</div></div>`;
        objArr.push(str);
        $('.events .events-carousel').append(objArr[i])
  
      }
   
    
      $(".events-carousel").owlCarousel({
      loop: true,
      margin: 10,
      rtl: rtlVal,
      nav: false,
      dots: false,
        items: 1,
      });
  
    }
  
    
    const chunk = (arr, n) =>
      arr.reduce((acc, cur, i) => {
        if (i % n === 0) acc.push([cur])
        else acc[acc.length - 1].push(cur)
        return acc
      }, [])
  
  
  
  
  
    function stringfyDate(date, type) {
  
      // Convert the string to a Date object
      var date = new Date(Date.parse(date));
      // Use the .toLocaleString() method to format the date and time
      var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE' : 'ar';
      let options = {};
      switch (type) {
        case 1:
          options = {
            day: '2-digit',
          }
          break;
  
        case 2:
          options = {
            month: 'short',
          }
          break;
  
        case 3:
          options = {
            year: 'numeric',
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
  
  
    $(".media-next-btn").click(function () {
      $(".gallery-carousel").trigger("next.owl.carousel", [400]);
    });
  
    $(".media-prev-btn").click(function () {
      $(".gallery-carousel").trigger("prev.owl.carousel", [400]);
    });
  
    $(".bot-next-btn").click(function () {
      $(".botcast-carousel").trigger("next.owl.carousel", [400]);
    });
  
    $(".bot-prev-btn").click(function () {
      $(".botcast-carousel").trigger("prev.owl.carousel", [400]);
    });
  
  
    $(".event-next-btn").click(function () {
  
      $(".events-carousel").trigger("next.owl.carousel", [400]);
    });
  
    $(".event-prev-btn").click(function () {
      $(".events-carousel").trigger("prev.owl.carousel", [400]);
    });
  
   
    $('.owl-carousel.gallery-carousel').owlCarousel({
      loop: true,
      margin: 10,
      nav: false,
      dots: false,
      responsive: {
        0: {
          items: 1
        },
        600: {
          items: 1
        },
        1000: {
          items: 1
        }
      }
    })
  
  
  
  
  })