$(document).ready(function () {


  var objArr = [];
  var allArticles = {};
 getArticles();
 
  var rtlVal = $("html").attr("dir") == "rtl" ? true : false;

 
  function getArticles() {

    $.ajax({
      url: `/o/adc-dxp-services/social-medias/posts?socialMediaName=YouTube&maxResults=5`,
      type: 'GET',
      headers: {
        'Authorization': 'Bearer ' + Liferay.authToken,
        "languageId": Liferay.ThemeDisplay.getLanguageId(),
         'groupId': Liferay.ThemeDisplay.getSiteGroupId()
      },
      success: function (articles) {
        allArticles = articles;
       // processResponse(articles);
      },
      error: function (error) {
        // Handle the error
      }
    });


  }
 

  function processResponse(articles) {
    $('.tweets-carousel').html('');
    objArr = [];
    var count = 0;
    if (articles?.Posts?.length <= 0) { emptyResult(); }
    for (let post of articles?.Posts) {

      objArr.push(`<div class="slide">

	<div class="card tweet-card">
		<div class="card-body">
			<div class="d-flex">
				<div class="twitter-avatar">
					<img src="${post?.Enterprise?.Logo}" class="img-fluid" alt="" />
				</div>
				<h5 class="card-title font-b"> <a target="_blank" href="${post?.Enterprise?.URL}">
						@${post?.Enterprise?.Name} </a></h5>
			</div>
			<a class="text-dark card-text small my-3 d-block " target="_blank" href="${post?.URL}">${post?.Title}</a>
			<small>${stringfyDate(post?.ContentDate)}</small>
		</div>
	</div>
</div>`);

      $('.tweets-carousel').append(objArr[count])

      count++;

    }
    
     $(".tweets-carousel").owlCarousel({
    loop: false,
    dots: false,
    autoplay: true,
    autoplayTimeout: 50000,
    autoplayHoverPause: true,
    margin: 25,
    nav: false,
    navRewind: false,
    center: false,
    rtl: rtlVal,
    responsive: {
      0: {
        items: 1,
        margin: 0,
      },
      600: {
        items: 2,
      },
      1000: {
        items: 3,
      },
    },
  });
    
   
    
  }


  function emptyResult() {

    var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى'
    $('.tweets-carousel').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
  }

 
  $(".tweets-next-btn").click(function () {
    $(".tweets-carousel").trigger("next.owl.carousel", [400]);
  });
  $(".tweets-prev-btn").click(function () {
    $(".tweets-carousel").trigger("prev.owl.carousel", [400]);
  });
  

  function stringfyDate(date) {
    
// Convert the string to a Date object
var date = new Date(Date.parse(date));
// Use the .toLocaleString() method to format the date and time
var locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US'? 'en' : 'ar';
var formattedDate = date.toLocaleString(locale, {
  year: 'numeric',
  month: 'long',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
 
});

    return formattedDate;
    
    
    
  }
  
  
  

});