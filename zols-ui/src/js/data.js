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

        $.fn.getLocalizedPropertyNames = function(schema, definitions) {
            var localized = [];
            if (definitions == undefined) {
                definitions = schema.definitions;
            }

            if (schema.localized) {
                localized = schema.localized;
            }
            while (schema["$ref"]) {
                schema = definitions[schema["$ref"].replace("#/definitions/", "")];
                localized = localized.concat($.fn.getLocalizedPropertyNames(schema, definitions));
            }


            return localized.filter(function(elem, index, self) {
                return index == self.indexOf(elem);
            });
        };

        $.fn.getConsolidatedProperties = function(schema) {
            var properties = schema.properties;
            var definitions = schema.definitions;
            while (schema["$ref"]) {

                schema = definitions[schema["$ref"].replace("#/definitions/", "")];
                Object.assign(properties, schema.properties);
            }
            return properties;
        }

        $.fn.setSchemaName = function(schemaName) {
            $.ajax({
                url: base_url + '/schema/' + schemaName + '?enlarged',
                type: "get",
                async: false,
                success: function(data) {
                    var consolidatedProperties = $.fn.getConsolidatedProperties(data);
                    var localizedPropertyNames = $.fn.getLocalizedPropertyNames(data);
                    if (editor) {
                        editor.destroy();
                    }
                    $element.empty();
                    editor = new JSONEditor($element[0], $.extend(settings, {
                        schema: data
                    }));

                    $("button.json-editor-btn-add").each(function(index, element) {
                        var name = $(element).parent().parent().parent().attr('data-schemapath');
                        if (name) {
                            name = name.replace(']', '');
                            name = name.substring(name.lastIndexOf("[") + 1);
                            name = name.substring(name.lastIndexOf(".") + 1);

                            var property = consolidatedProperties[name];
                            if (property && property.options && property.options.lookup) {
                                $.get(base_url + '/schema/' + property.options.lookup).done(function(schema) {

                                        var ta = {
                                            source: function(value, callback) {
                                                $.getJSON("/api/data/"+schema['$id'], {
                                                    q: value
                                                }, function(data) {
                                                    callback(data.content || [])
                                                })
                                            },
                                            displayText:function(item) {
                                              return item[schema.ids[0]];
                                            },
                                            autoSelect: true
                                        };



                                        $(element).parent().parent().children().first().on('click', "div.form-group>input[type='text']", function() {
                                            if ($(this).attr('added') !== 'true') {
                                                $(this).attr('autocomplete', 'off').typeahead(ta);
                                                $(this).attr('added', 'true');
                                            }
                                        });



                                });
                            }
                        }
                    });

                    $("input[type='text'].form-control").each(function(index, element) {
                        var name = $(element).attr('name');
                        var localizedIds = [];
                        if (name) {
                            name = name.replace(']', '');
                            name = name.substring(name.lastIndexOf("[") + 1);

                            var property = consolidatedProperties[name];
                            if (property) {
                                if (property.options && property.options.lookup) {
                                    $.get(base_url + '/schema/' + property.options.lookup).done(function(schema) {
                                      var ta = {
                                          source: function(value, callback) {
                                              $.getJSON("/api/data/"+schema['$id'], {
                                                  q: value
                                              }, function(data) {
                                                  callback(data.content || [])
                                              })
                                          },
                                          displayText:function(item) {
                                            return item[schema.ids[0]];
                                          },
                                          autoSelect: true
                                      };

                                    });
                                } else if (property.type === "string") {

                                    var is_localized = $.inArray(name, localizedPropertyNames);
                                    if (is_localized !== -1) {
                                        var localizedId = "localized-" + new Date().getTime();
                                        localizedIds.push(localizedId);
                                        $(element).attr('id', localizedId);
                                    }
                                }



                            }

                        }
                        if (localizedIds.length !== 0) {

                            if (typeof google !== "undefined") {
                                var locale = localStorage.getItem("locale");
                                if(locale) {
                                  var options = {
                                      sourceLanguage: google.elements.transliteration.LanguageCode.ENGLISH,
                                      destinationLanguage: [locale],
                                      shortcutKey: 'ctrl+g',
                                      transliterationEnabled: true
                                  };

                                  // Create an instance on TransliterationControl with the required
                                  // options.
                                  var control =
                                      new google.elements.transliteration.TransliterationControl(options);
                                  control.makeTransliteratable(localizedIds);
                                }

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
