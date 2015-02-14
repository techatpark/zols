'use strict';

(function ($) {
    var base_url = baseURL();

    function baseURL() {
        var url = 'http://localhost:8080/api';
        if (location.href.indexOf(":3000/") === -1) {
            var pathArray = location.href.split('/');
            url = pathArray[0] + '//' + pathArray[2] + '/api';
        }
        return url;
    }
    var link;
    var template;
    var templates;
    var page_request;

    $.ajaxSetup({
        contentType: 'application/json'
    });

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
            else if ($(this).attr('data-type') === 'create_new_page') {
                $.get(base_url + '/templates')
                        .done(function (data) {
                            $.fn.listTemplates(data);
                        });
            }
        });
    };

    $.fn.listTemplates = function (listofTemplates) {

        if (listofTemplates === "") {
            var template = $.templates("#noTemplate");
            template.link('#result', {});
            $('#result a').click(function () {
                $.fn.createTemplate();
            });
        } else {
            templates = listofTemplates;
            var template = $.templates("#listTemplates");
            template.link('#result', {link: listofTemplates});
            $('#result .glyphicon-arrow-right').on('click', function () {
                page_request = {};
                page_request.linkName = $('#result').attr('data-link-name');
                page_request.template = listofTemplates[$(this).parent().parent().index()];
                var template = $.templates("#data_entry");
                template.link('#result', page_request);
                console.log(page_request);
                var editor = $("#editor_holder").jsonEditor({schemaName: page_request.template.dataType});

                $("#result form").submit(function (event) {
                    event.preventDefault();
                    page_request.data = editor.getValue();
                    $.ajax({
                        method: 'POST',
                        url: base_url + '/pages',
                        dataType: 'json',
                        data: JSON.stringify(page_request)
                    }).done(function (data) {
                        window.location = base_url + '/../pages/'+data.name;
                    }).error(function (data) {
                        $.fn.onError(data);
                    });
                });



            });
        }
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