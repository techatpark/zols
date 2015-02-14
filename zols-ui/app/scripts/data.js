'use strict';
(function ($) {

    $.fn.jsonEditor = function (options) {

        // This is the easiest way to have default options.

        var $element = this;
        var settings = $.extend({theme: 'bootstrap3', disable_properties: true, disable_collapse: true, disable_edit_json: true}, JSONEditor.defaults.options);

        settings = $.extend(settings, options);

        var editor;
        var base_url = baseURL();

        function baseURL() {
            var url = 'http://localhost:8080/api';
            if (location.href.indexOf(":3000/") === -1) {
                var pathArray = location.href.split('/');
                url = pathArray[0] + '//' + pathArray[2] + '/api';
            }
            return url;
        }

        $.ajaxSetup({
            contentType: 'application/json'
        });


        $.fn.setSchemaName = function (schemaName) {
            $.get(base_url + '/schema/' + schemaName).done(function (data) {
                $element.empty();
                editor = new JSONEditor($element[0], $.extend(settings, {schema: data}));
            });
        };

        $.fn.getValue = function () {
            return editor.getValue();
        };

        $.fn.setSchemaName(options.schemaName);

        return this;

    };

}(jQuery));