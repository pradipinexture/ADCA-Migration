window.onload = function () {
  // wow.js initialization
  new WOW().init();

  wow = new WOW({
    boxClass: "wow", // default
    animateClass: "animated", // default
    offset: 0, // default
    mobile: true, // default
    live: true, // default
  });
};

$(document).ready(function () {

  if ($(this).width() > 991) {
    $(".sidebar").removeClass("sidebar-closed");
  } else {
    $(".sidebar").addClass("sidebar-closed");
    $(".main-container").removeClass("shrink");
  }

  $('[data-target="#chatbot-container"]').on("click", function () {
    $("#polls-container").removeClass("show");
    $('[data-target="#polls-container"]').addClass("collapsed");
    var x = document.getElementById("chatbot-container");

    if ($(x).children().length === 0) {
      initSdk("Bots");
      console.log("Init chatbot");
    }
  });

  $('[data-target="#polls-container"]').on("click", function () {
    $("#chatbot-container").removeClass("show");
    $('[data-target="#chatbot-container"]').addClass("collapsed");
  });



  var rtlVal = $("html").attr("dir") == "rtl" ? true : false;

  // sidebar nav
  $(".sidebar-btn").click(function () {
    $(".sidebar-btn")
      .addClass("btn-light")
      .removeClass("active-section")
      .removeClass("btn-primary-filled");

    $(this)
      .addClass("active-section")
      .addClass("btn-primary-filled")
      .removeClass("btn-light");
  });
  // owl carousel presets
  $(".carousel-col-3").owlCarousel({
    margin: 30,
    autoplay: true,
    nav: false,
    dots: false,
    navRewind: false,
    rtl: rtlVal,
    navText: [
      "<i class='icon-chevron-left'></i>",
      "<i class='icon-chevron-right'></i>",
    ],
    responsive: {
      0: {
        items: 1,
      },
      600: {
        items: 2,
      },
      1000: {
        items: 3,
      },
    },
  });
  $(".carousel-center").owlCarousel({
    loop: true,
    autoplay: true,
    autoplayTimeout: 4000,
    autoplayHoverPause: true,
    margin: 30,
    nav: true,
    navRewind: false,
    center: true,
    rtl: rtlVal,
    navText: [
      "<i class='icon-chevron-left'></i>",
      "<i class='icon-chevron-right'></i>",
    ],
    responsive: {
      0: {
        items: 1,
      },
      600: {
        items: 2,
      },
      1000: {
        items: 3,
      },
    },
  });
  $(".custom-nav-carousel").owlCarousel({
    loop: true,
    dots: false,
    autoplay: true,
    autoplayTimeout: 50000,
    autoplayHoverPause: true,
    margin: 25,
    nav: false,
    navRewind: false,
    center: true,
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

  //custom carousel navigation
  $(".apps-next-btn").click(function () {
    $(".apps-carousel").trigger("next.owl.carousel", [400]); //  optional speed parameter
  });
  $(".apps-prev-btn").click(function () {
    $(".apps-carousel").trigger("prev.owl.carousel", [400]);
  });
  $(".news-next-btn").click(function () {
    $(".news-carousel").trigger("next.owl.carousel", [400]);
  });
  $(".news-prev-btn").click(function () {
    $(".news-carousel").trigger("prev.owl.carousel", [400]);
  });

  // custom tooltips
  // $(".sidebar-btn").tooltip({
  //   trigger: "manual",
  //   template:
  //     '<div class="tooltip " role="tooltip"><div class="arrow custom-arrow"></div><div class="tooltip-inner tooltip-custom-inner"></div></div>',
  // });
  $('[data-toggle="tooltip"]').tooltip({
    template:
      '<div class="tooltip " role="tooltip"><div class="arrow custom-arrow"></div><div class="tooltip-inner tooltip-custom-inner"></div></div>',
  });
  // set delay after user hovers on sidebar nav item
  // var timeoutID;
  // $(".sidebar-btn").mouseenter(function () {
  //   timeoutID = setTimeout(() => {
  //     $(this).tooltip("show");
  //   }, 400);
  // });

  // $(".sidebar-btn").mouseleave(function () {
  //   clearTimeout(timeoutID);
  //   $(this).tooltip("hide");
  // });
  // enable tooltips globally

  // copy link to clipboard with updating tooltip
  $(".share-btn").click(function () {
    $(this)
      .attr("title", "copied")
      .attr("data-original-title", "Copied!")
      .tooltip("show"); // update tooltip inner text

    navigator.clipboard.writeText(window.location.href); // copy url to clipboard
  });
  $(".share-btn").on("hidden.bs.tooltip", function () {
    $(this)
      .attr("title", "copied")
      .attr("data-original-title", "Copy URL to clipboard"); // reset tooltip inner text
  });

  // shift sidebar on scroll
});

$("#main-search-form").submit(function (event) {
  var x = document.getElementById("search-input").value;
  window.location.href = "/group/portal/search?q=" + x;
  event.preventDefault();
});

/* Toggle side bar */

$(window).resize(function () {
  if ($(this).width() > 991) {
    $(".sidebar").removeClass("sidebar-closed");
  } else {
    $(".sidebar").addClass("sidebar-closed");
    $(".main-container").removeClass("shrink");
  }
});

function toggleSideBar() {
  $(".sidebar").toggleClass("sidebar-closed");
  $(".main-container").toggleClass("shrink");
}


Liferay.on('allPortletsReady',function() {
  
  //console.log('allPortletsReady');
     $('.ui-datepicker').remove();
if ($('.datepicker').length > 0) {
      console.log('length > 0');

 if (Liferay.ThemeDisplay.getLanguageId() != "en_US") {

  

   $(".datepicker").datepicker({
     closeText: "إغلاق",
     prevText: "السابق",
     nextText: "التالي",
     currentText: "اليوم",
     monthNames: [
       "يناير",
       "فبراير",
       "مارس",
       "أبريل",
       "مايو",
       "يونيو",
       "يوليو",
       "أغسطس",
       "سبتمبر",
       "أكتوبر",
       "نوفمبر",
       "ديسمبر",
     ],
     monthNamesShort: [
       "يناير",
       "فبراير",
       "مارس",
       "أبريل",
       "مايو",
       "يونيو",
       "يوليو",
       "أغسطس",
       "سبتمبر",
       "أكتوبر",
       "نوفمبر",
       "ديسمبر",
     ],
     dayNames: [
       "الأحد",
       "الاثنين",
       "الثلاثاء",
       "الأربعاء",
       "الخميس",
       "الجمعة",
       "السبت",
     ],
     dayNamesShort: [
       "أحد",
       "اثنين",
       "ثلاثاء",
       "أربعاء",
       "خميس",
       "جمعة",
       "سبت",
     ],
     dayNamesMin: ["ح", "ن", "ث", "ر", "خ", "ج", "س"],
     weekHeader: "أسبوع",
     dateFormat: "dd-mm-yy",
     firstDay: 0,
     isRTL: true,
     showMonthAfterYear: false,
     yearSuffix: "",
     changeMonth: true,
     changeYear: true,
   });
 } else {
   $(".datepicker").datepicker({
     changeMonth: true,
     changeYear: true,
     dateFormat: "dd-mm-yy",
   });
 }
}
});