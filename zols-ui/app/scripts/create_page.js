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

    $.fn.linkUrl = function () {
        $.ajax({
            method: 'PATCH',
            url: base_url + '/links/' + link.name + '/link_url',
            dataType: 'json',
            data: link.targetUrl
        }).done(function (data) {
            $.fn.listPageOptions();
        }).error(function (data) {
            $.fn.onError(data);
        });

    };


    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.listPageOptions();





}(jQuery));