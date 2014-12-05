'use strict';

(function ($) {
    var base_url = 'http://localhost:8080/api';
    var link;
    var template;

    $.fn.listPageOptions = function () {
        template = $.templates("#pageOptions");
        template.link('#result', null);
        $('#result a').click(function () {
            if ($(this).attr('data-type') === 'link_url') {
                link = {name: $('#result').attr('data-link-name'), targetUrl: ""};
                template = $.templates("#linkUrlForm");
                template.link('#result', link);

                $("#result form").submit(function (event) {
                    event.preventDefault();
                    $.fn.linkUrl();
                });

            }
        });
    };
    
    $.fn.linkUrl = function() {
        console.log(link);
        $.fn.listPageOptions();
    };

    $.fn.listPageOptions();




}(jQuery));