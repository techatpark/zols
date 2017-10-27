'use strict';
(function($) {
    if (localStorage.getItem("locale")) {
        $("#language_selector").val(localStorage.getItem("locale"));
    }

    $("#language_selector")
        .on("change", function() {
            localStorage.setItem("locale", $(this).val());
            $.fn.setLocale();
        });

    $.fn.setLocale = function() {

        if (localStorage.getItem("locale") && localStorage.getItem("locale") != 'en') {
          $.ajaxSetup({
              contentType: 'application/json',
              headers: {
                  'Accept-Language': localStorage.getItem("locale")
              }
          });
        }else {
          $.ajaxSetup({
                contentType: 'application/json'
            });
        }


    }

    $.fn.setLocale();
}(jQuery));
