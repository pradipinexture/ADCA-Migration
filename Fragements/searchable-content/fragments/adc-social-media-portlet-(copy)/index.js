$(document).ready(function () {
 
        var objArr = [];
        var allArticles = {};
        getArticles();
        //tempArticles();

     
        function getArticles() {

            $.ajax({
                url: `/o/adc-dxp-services/ideas/all`,
                type: 'GET',
                headers: {
                       'x-csrf-token': Liferay.authToken,
                    "languageId": Liferay.ThemeDisplay.getLanguageId(),
                    "groupId": Liferay.ThemeDisplay.getSiteGroupId()
                },
                success: function (articles) {
                    allArticles = articles;
                    processResponse(articles);
                },
                error: function (error) {
                    // Handle the error
                    console.log(error);
                    emptyResult();
                }
            });


        }


        function processResponse(articles) {

            $('.innovations').html('');
            objArr = [];
            var count = 0;

            for (let innovation of articles.Ideas) {

                objArr.push(`<div class="col-12 p-3 mb-3 innovation-card"">
    <div class="media h-100 align-self-center">
      <img class="mr-2 recent-inno-img align-self-center" width="50" src="/o/ad-customs-theme/images/news/innovations/idea.svg" />
      <div class="media-body d-flex justify-content-center flex-column  h-100 ">
        <div class="w-100  mx-auto  d-flex flex-column    h-100 ">
          <div>
            <a href="${innovation.URL}">
              <p class="news-body-p mt-2 mb-0">${innovation.Title}
              </p>
            </a>
            <small>${innovation.user.name}</small>

          </div>
        </div>
      </div>
    </div>
    <a href="${innovation.URL}">
      <p class="recent-inno-p mt-2">
        ${innovation.Description}
      </p>
    </a>
    <div class="d-flex align-items-center justify-content-between">
      <ul class="list-unstyled d-flex list-inline inno-social-btn mb-0">
        <li class="d-flex text-secondary"> <i class="icon-heart" aria-hidden="true"></i><small
            class="align-self-center mx-2">${innovation.LikesCount}</small>
        </li>
        <li class="d-flex text-secondary"><i class="icon-thumbs-down" aria-hidden="true"></i> <small
            class="align-self-center mx-2">${innovation.CommentsCount}</small>
        </li>
      </ul>

      <small class="text-secondary"> <i class="icon-calendar" aria-hidden="true"></i>
        ${stringfyDate(innovation.Date)}</small>
    </div>
  </div>`);

                $('.innovations').append(objArr[count])

                count++;

            }


        }


        function emptyResult() {

            var emptyMsg = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'There is nothing here yet' : 'لا يوجد محتوى'
            $('.innovations').html(`<div class="col p-5 text-center text-muted">${emptyMsg}</div>`)
        }
        
        function stringfyDate(dateStr) {

let date = new Date(dateStr);
let locale = Liferay.ThemeDisplay.getLanguageId() == 'en_US' ? 'en-AE':'ar';
let formattedDate = date.toLocaleDateString(locale, {
  day: '2-digit',
  month: 'long',
  year: 'numeric'
});

    return formattedDate
  }
  





    });