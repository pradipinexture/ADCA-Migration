var videoDefaults = {
    videoMaxWidth: '855px',

    autoplayFirstVideo: true,

    youtubePlayerParams: false,
    vimeoPlayerParams: false,
    dailymotionPlayerParams: false,
    vkPlayerParams: false,
    videojs: false,
    videojsOptions: {}
};

var Video = function(element) {

    this.el = element;

    this.core = window.lgData[this.el.getAttribute('lg-uid')];
    this.core.s = Object.assign({}, videoDefaults, this.core.s);

    this.videoLoaded = false;

    this.init();

    return this;
};

Video.prototype.init = function() {
    var _this = this;

    // Event triggered when video url found without poster
    utils.on(_this.core.el, 'hasVideo.lgtm', function(event) {
        _this.core.___slide[event.detail.index].querySelector('.lg-video').insertAdjacentHTML('beforeend', _this.loadVideo(event.detail.src, 'lg-object', true, event.detail.index, event.detail.html));
        if (event.detail.html) {
            if (_this.core.s.videojs) {
                try {
                    videojs(_this.core.___slide[event.detail.index].querySelector('.lg-html5'), _this.core.s.videojsOptions, function() {
                        if (!_this.videoLoaded && event.detail.index === _this.core.index && _this.core.s.autoplayFirstVideo) {
                            this.play();
                        }
                    });
                } catch (e) {
                    console.error('lightGallery:- Make sure you have included videojs');
                }
            } else {
                if(!_this.videoLoaded && event.detail.index === _this.core.index && _this.core.s.autoplayFirstVideo) {
                    _this.core.___slide[event.detail.index].querySelector('.lg-html5').play();
                }
            }
        }
    });

    // Set max width for video
    utils.on(_this.core.el, 'onAferAppendSlide.lgtm', function(event) {
        if (_this.core.___slide[event.detail.index].querySelector('.lg-video-cont')) {
            _this.core.___slide[event.detail.index].querySelector('.lg-video-cont').style.maxWidth = _this.core.s.videoMaxWidth;
            _this.videoLoaded = true;
        }
    });

    var loadOnClick = function($el) {
        // check slide has poster
        if (utils.hasClass($el.querySelector('.lg-object'), 'lg-has-poster') && ($el.querySelector('.lg-object').style.display !== 'none')) {

            // check already video element present
            if (!utils.hasClass($el, 'lg-has-video')) {

                utils.addClass($el, 'lg-video-playing');
                utils.addClass($el, 'lg-has-video');

                var _src;
                var _html;
                var _loadVideo = function(_src, _html) {

                    $el.querySelector('.lg-video').insertAdjacentHTML('beforeend', _this.loadVideo(_src, '', false, _this.core.index, _html));

                    if (_html) {
                        if (_this.core.s.videojs) {
                            try {
                                videojs(_this.core.___slide[_this.core.index].querySelector('.lg-html5'), _this.core.s.videojsOptions, function() {
                                    this.play();
                                });
                            } catch (e) {
                                console.error('lightGallery:- Make sure you have included videojs');
                            }
                        } else {
                            _this.core.___slide[_this.core.index].querySelector('.lg-html5').play();
                        }
                    }

                };

                if (_this.core.s.dynamic) {

                    _src = _this.core.s.dynamicEl[_this.core.index].src;
                    _html = _this.core.s.dynamicEl[_this.core.index].html;

                    _loadVideo(_src, _html);

                } else {

                    _src = _this.core.items[_this.core.index].getAttribute('href') || _this.core.items[_this.core.index].getAttribute('data-src');
                    _html = _this.core.items[_this.core.index].getAttribute('data-html');

                    _loadVideo(_src, _html);

                }

                var $tempImg = $el.querySelector('.lg-object');
                $el.querySelector('.lg-video').appendChild($tempImg);

                // @todo loading icon for html5 videos also
                // for showing the loading indicator while loading video
                if (!utils.hasClass($el.querySelector('.lg-video-object'), 'lg-html5')) {
                    utils.removeClass($el, 'lg-complete');
                    utils.on($el.querySelector('.lg-video-object'), 'load.lg error.lg', function() {
                        utils.addClass($el, 'lg-complete');
                    });
                }

            } else {

                var youtubePlayer = $el.querySelector('.lg-youtube');
                var vimeoPlayer = $el.querySelector('.lg-vimeo');
                var dailymotionPlayer = $el.querySelector('.lg-dailymotion');
                var html5Player = $el.querySelector('.lg-html5');
                if (youtubePlayer) {
                    youtubePlayer.contentWindow.postMessage('{"event":"command","func":"playVideo","args":""}', '*');
                } else if (vimeoPlayer) {
                    try {
                        new Vimeo.Player(vimeoPlayer).play().catch(function(error) {
                            console.error('error playing the video:', error.name);
                        });
                    } catch (e) {
                        console.warn('lightGallery:- Make sure you have included https://github.com/vimeo/player.js');
                    }
                } else if (dailymotionPlayer) {
                    dailymotionPlayer.contentWindow.postMessage('play', '*');

                } else if (html5Player) {
                    if (_this.core.s.videojs) {
                        try {
                            videojs(html5Player).play();
                        } catch (e) {
                            console.error('lightGallery:- Make sure you have included videojs');
                        }
                    } else {
                        html5Player.play();
                    }
                }

                utils.addClass($el, 'lg-video-playing');

            }
        }
    };

    if (_this.core.doCss() && _this.core.items.length > 1 && ((_this.core.s.enableSwipe && _this.core.isTouch) || (_this.core.s.enableDrag && !_this.core.isTouch))) {
        utils.on(_this.core.el, 'onSlideClick.lgtm', function() {
            var $el = _this.core.___slide[_this.core.index];
            loadOnClick($el);
        });
    } else {

        // For IE 9 and bellow
        for (var i = 0; i < _this.core.___slide.length; i++) {

            /*jshint loopfunc: true */
            (function(index) {
                utils.on(_this.core.___slide[index], 'click.lg', function() {
                    loadOnClick(_this.core.___slide[index]);
                });
            })(i);
        }
    }

    utils.on(_this.core.el, 'onBeforeSlide.lgtm', function(event) {

        var $videoSlide = _this.core.___slide[event.detail.prevIndex];
        var youtubePlayer = $videoSlide.querySelector('.lg-youtube');
        var vimeoPlayer = $videoSlide.querySelector('.lg-vimeo');
        var dailymotionPlayer = $videoSlide.querySelector('.lg-dailymotion');
        var vkPlayer = $videoSlide.querySelector('.lg-vk');
        var html5Player = $videoSlide.querySelector('.lg-html5');
        if (youtubePlayer) {
            youtubePlayer.contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":""}', '*');
        } else if (vimeoPlayer) {
            try {
                new Vimeo.Player(vimeoPlayer).pause().catch(function(error) {
                    console.error('Unable to pause the video:', error.name);
                });
            } catch (e) {
                console.warn('lightGallery:- Make sure you have included https://github.com/vimeo/player.js');
            }
        } else if (dailymotionPlayer) {
            dailymotionPlayer.contentWindow.postMessage('pause', '*');

        } else if (html5Player) {
            if (_this.core.s.videojs) {
                try {
                    videojs(html5Player).pause();
                } catch (e) {
                    console.error('lightGallery:- Make sure you have included videojs');
                }
            } else {
                html5Player.pause();
            }
        } if (vkPlayer) {

            vkPlayer.setAttribute('src', vkPlayer.getAttribute('src').replace('&autoplay', '&noplay'));
        }

        var _src;
        if (_this.core.s.dynamic) {
            _src = _this.core.s.dynamicEl[event.detail.index].src;
        } else {
            _src = _this.core.items[event.detail.index].getAttribute('href') || _this.core.items[event.detail.index].getAttribute('data-src');

        }

        var _isVideo = _this.core.isVideo(_src, event.detail.index) || {};
        if (_isVideo.youtube || _isVideo.vimeo || _isVideo.dailymotion || _isVideo.vk) {
            utils.addClass(_this.core.outer, 'lg-hide-download');
        }

        //$videoSlide.addClass('lg-complete');

    });

    utils.on(_this.core.el, 'onAfterSlide.lgtm', function(event) {
        utils.removeClass(_this.core.___slide[event.detail.prevIndex], 'lg-video-playing');
    });
};

Video.prototype.loadVideo = function(src, addClass, noposter, index, html) {
    var video = '';
    var autoplay = 1;
    var a = '';
    var isVideo = this.core.isVideo(src, index) || {};

    // Enable autoplay for first video if poster doesn't exist
    if (noposter) {
        if (this.videoLoaded) {
            autoplay = 0;
        } else {
            autoplay = this.core.s.autoplayFirstVideo ? 1 : 0;
        }
    }

    var videoTitle;

    if (this.core.s.dynamic) {
        videoTitle = this.core.s.dynamicEl[index].title;
    } else {
        videoTitle = this.core.items[index].getAttribute('title');
        if(!videoTitle) {
            var firstImage = this.core.items[index].querySelector('img');
            if (firstImage) {
                videoTitle = firstImage.getAttribute('alt');
            }
        }
    }


    videoTitle = videoTitle ? 'title="' + videoTitle + '"' : '';

    if (isVideo.youtube) {

        a = '?wmode=opaque&autoplay=' + autoplay + '&enablejsapi=1';
        if (this.core.s.youtubePlayerParams) {
            a = a + '&' + utils.param(this.core.s.youtubePlayerParams);
        }

        video = '<iframe allow="autoplay" class="lg-video-object lg-youtube ' + addClass + '" ' + videoTitle + ' width="560" height="315" src="//www.youtube.com/embed/' + isVideo.youtube[1] + a + '" frameborder="0" allowfullscreen></iframe>';

    } else if (isVideo.vimeo) {

        a = '?autoplay=' + autoplay;
        if (this.core.s.vimeoPlayerParams) {
            a = a + '&' + utils.param(this.core.s.vimeoPlayerParams);
        }

        video = '<iframe allow="autoplay" class="lg-video-object lg-vimeo ' + addClass + '" ' + videoTitle + ' width="560" height="315"  src="//player.vimeo.com/video/' + isVideo.vimeo[1] + a + '" frameborder="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>';

    } else if (isVideo.dailymotion) {

        a = '?wmode=opaque&autoplay=' + autoplay + '&api=postMessage';
        if (this.core.s.dailymotionPlayerParams) {
            a = a + '&' + utils.param(this.core.s.dailymotionPlayerParams);
        }

        video = '<iframe allow="autoplay" class="lg-video-object lg-dailymotion ' + addClass + '" ' + videoTitle + ' width="560" height="315" src="//www.dailymotion.com/embed/video/' + isVideo.dailymotion[1] + a + '" frameborder="0" allowfullscreen></iframe>';

    } else if (isVideo.html5) {
        var fL = html.substring(0, 1);
        if (fL === '.' || fL === '#') {
            html = document.querySelector(html).innerHTML;
        }

        video = html;

    } else if (isVideo.vk) {

        a = '&autoplay=' + autoplay;
        if (this.core.s.vkPlayerParams) {
            a = a + '&' + utils.param(this.core.s.vkPlayerParams);
        }

        video = '<iframe allow="autoplay" class="lg-video-object lg-vk ' + addClass + '" ' + videoTitle + '  width="560" height="315" src="//vk.com/video_ext.php?' + isVideo.vk[1] + a + '" frameborder="0" allowfullscreen></iframe>';

    }

    return video;
};

Video.prototype.destroy = function() {
    this.videoLoaded = false;
};

window.lgModules.video = Video;