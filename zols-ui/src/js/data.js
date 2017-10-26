'use strict';
(function($) {

    $.fn.jsonEditor = function(options) {

        // This is the easiest way to have default options.

        var $element = this;
        var settings = $.extend({
            theme: 'bootstrap3',
            disable_properties: true,
            disable_collapse: true,
            disable_edit_json: true
        }, JSONEditor.defaults.options);

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


        $.fn.setSchemaName = function(schemaName) {
            $.ajax({
                url: base_url + '/schema/' + schemaName + '?enlarged',
                type: "get",
                async: false,
                success: function(data) {
                    if (editor) {
                        editor.destroy();
                    }
                    $element.empty();
                    editor = new JSONEditor($element[0], $.extend(settings, {
                        schema: data
                    }));

                    $("input[type='text'].form-control").each(function(index, element) {
                        var name = $(element).attr('name');
                        if (name) {
                            name = name.replace(']', '');
                            name = name.substring(name.lastIndexOf("[") + 1);

                            var property = data.properties[name];
                            if (property && property.options && property.options.lookup) {

                                $.get(base_url + '/schema/' + property.options.lookup).done(function(schema) {




                                    $.get(base_url + '/data/' + schema['$id']).done(function(data) {
                                    $(element).attr( 'autocomplete', 'off' ).typeahead({
                                        source: data.content,
                                        displayText:function(item) {
                                          return item[schema.ids[0]];
                                        },
                                        autoSelect: true
                                      });
                                    });

                                });


                            }

                        }

                    });

                    if (settings.value) {
                        editor.setValue(settings.value);
                    }

                },
                error: function() {
                    connectionError();
                }
            });

        };

        $.fn.getValue = function() {
            return editor.getValue();
        };

        $.fn.setValue = function(value) {
            editor.setValue(value);
        };
        $.fn.setSchemaName(options.schemaName);

        return this;

    };

}(jQuery));
