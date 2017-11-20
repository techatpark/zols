'use strict';
(function($) {

    $.fn.setLocale = function() {

        var locale = $.fn.getParameter("lang");

        if (locale != undefined) {
            if (locale === "" || locale === "en") {
                localStorage.removeItem("locale");
                $(".nav>.dropdown>.dropdown-menu>li[name='en']").remove();
            } else {
                localStorage.setItem("locale", locale);
                $(".nav>.dropdown>.dropdown-toggle").html($(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']>a").html() + '<b class="caret"></b>');
                $(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']").remove();
            }
        }else {
          locale = localStorage.getItem("locale");
          if(locale == undefined) {
            locale = 'en';
          }
          $(".nav>.dropdown>.dropdown-toggle").html($(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']>a").html() + '<b class="caret"></b>');
          $(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']").remove();
        }

        if (localStorage.getItem("locale") && localStorage.getItem("locale") != 'en') {
            $.ajaxSetup({
                contentType: 'application/json',
                headers: {
                    'Accept-Language': localStorage.getItem("locale")
                }
            });
        } else {
            $.ajaxSetup({
                contentType: 'application/json'
            });
        }


    }

    $.fn.getParameter = function(name) {
        if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
            return decodeURIComponent(name[1]);
    }

    $.fn.setLocale();

    $.fn.setParameter = function(key, value) {
        key = encodeURI(key);
        value = encodeURI(value);

        var kvp = document.location.search.substr(1).split('&');

        var i = kvp.length;
        var x;
        while (i--) {
            x = kvp[i].split('=');

            if (x[0] == key) {
                x[1] = value;
                kvp[i] = x.join('=');
                break;
            }
        }

        if (i < 0) {
            kvp[kvp.length] = [key, value].join('=');
        }

        //this will reload the page, it's likely better to store this until finished
        document.location.search = kvp.join('&');
    }

    $(".nav>.dropdown>.dropdown-menu>li").on('click', function() {
        if($(this).attr('name') === "en"){
          $.fn.setParameter("lang","")
        }else {
          $.fn.setParameter("lang",$(this).attr('name'))
        }

    });

}(jQuery));
