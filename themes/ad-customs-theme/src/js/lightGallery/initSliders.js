
$(document).ready(function () {

    var rtlVal = $("html").attr("dir") == 'rtl' ? true : false;
    setOwlCarousel();


    function setSlider(slider, Num) {
        if (slider.length && Num >= 6) {
            slider.each(function () {
                $(this).slick({
                    slidesToShow: Num,
                    // slidesToScroll: 1,
                    rtl: rtlVal,
                    autoplay: true,
                    touchThreshold:100,
                    swipeToSlide:true,
                    autoplaySpeed: 2500,
                    arrows: true,
                    dots: false,
                    pauseOnHover: false,
                    responsive: [{
                        breakpoint: 1245,
                        settings: { slidesToShow: Num - 1 }
                    }, {
                        breakpoint: 1050,
                        settings: { slidesToShow: Num - 2 }
                    }, {
                        breakpoint: 855,
                        settings: { slidesToShow: Num - 3 }
                    }, {
                        breakpoint: 675,
                        settings: { slidesToShow: Num - 4 }
                    }, {
                        breakpoint: 500,
                        settings: { slidesToShow: Num - 5 }
                    }]
                });
            });
        } else if (slider.length && Num < 6) {

            slider.each(function () {
                $(this).slick({
                    slidesToShow: Num,
                    slidesToScroll: 1,
                    rtl: rtlVal,
                    autoplay: false,
                    autoplaySpeed: 2500,
                    arrows: true,
                    dots: false,
                    pauseOnHover: false,
                    responsive: [{
                        breakpoint: 1000,
                        settings: { slidesToShow: 3 }
                    }, {
                        breakpoint: 800,
                        settings: { slidesToShow: 2 }
                    }, {
                        breakpoint: 700,
                        settings: { slidesToShow: 1 }
                    }]
                });
            });
        }




    }

    $('.upcomingProjects').on('click', function () {
        $('.currentProjects').removeClass('active');
        $(this).addClass('active');
        $('#currentProjectsTab').addClass('height-0');
        $('#upcomingProjectsTab').removeClass('height-0');

    });

    $('.currentProjects').on('click', function () {
        $('.upcomingProjects').removeClass('active');
        $(this).addClass('active');
        $('#currentProjectsTab').removeClass('height-0');
        $('#upcomingProjectsTab').addClass('height-0');

    });



    var kpiLoaded = setInterval(() => {

        if ($('.slider-6').find('.slide').length) {

                setSlider($('.slider-6'), 6);

                clearInterval(kpiLoaded);
             
        }

    }, 100);


    var upComingprojectsLoaded = setInterval(() => {

        if ($('#upcomingProjectsTab .owl-carousel .item').length > 0) {
            setOwlCarousel();
            clearInterval(upComingprojectsLoaded);

        }

    }, 100);

    var currentProjectsLoaded = setInterval(() => {

        if ($('#currentProjectsTab .owl-carousel .item').length > 0) {
            setOwlCarousel();
            clearInterval(currentProjectsLoaded);

        }

    }, 100);

    // var latestPublicationsLoaded = setInterval(() => {

    //     if ($('#latestPublications.owl-slider .owl-carousel .item').length > 0) {
    //         setOwlCarousel();
    //         clearInterval(latestPublicationsLoaded);

    //     }

    // }, 100);


    setTimeout(() => {
        clearInterval(upComingprojectsLoaded);
        clearInterval(currentProjectsLoaded);
        clearInterval(kpiLoaded);
        // clearInterval(latestPublicationsLoaded);


    }, 5000);





    function setOwlCarousel() {
        $(".owl-carousel").owlCarousel({
            autoplay: false,
            rewind: false, /* use rewind if you don't want loop */
            margin: 34,
            /*
           animateOut: 'fadeOut',
           animateIn: 'fadeIn',
           */
            responsiveClass: true,
            autoHeight: true,
            autoplayTimeout: 7000,
            smartSpeed: 800,
            navRewind: false,
            nav: true,
            dots: false,
            rtl: rtlVal,
            responsive: {
                0: {
                    items: 1
                },

                600: {
                    items: 2
                },

                1024: {
                    items: 3
                },

                1366: {
                    items: 4
                }
            }
        });
    }


});


