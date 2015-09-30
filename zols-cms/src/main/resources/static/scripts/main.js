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

    // Set an option globally
    JSONEditor.defaults.options.theme = 'bootstrap3';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    $('[data-toggle="tooltip"]').tooltip();

    var schemaTemplate;
    var schema;
    var schemas;
    var confirmationPromise;
    var isEdit = false;

    var JSON_TYPES = ['array', 'string', 'integer', 'number', 'boolean'];

    $('#edit_selected').on('click', function () {
        $.fn.renderSchema();
    });
    $("#del_conf_ok").on('click', function () {
        $("#delete-conf-model").modal('hide');
        confirmationPromise.resolve();
    });
    $("del_conf_cancel").on('click', function () {
        confirmationPromise.reject();
    });

    $('#delete_selected').on('click', function () {
        $("#delete-conf-model").modal('show');
        confirmationPromise = $.Deferred();
        confirmationPromise.done(function () {
            $.fn.deleteSchema();
        });
    });

    $('#result').on('click', '#addAttr', function () {
        var totalProperties = Object.keys(schema.properties).length;
        schema.properties['newProperty' + totalProperties] = $.fn.patchedProperty({});
        $.fn.renderSchema();
    });

    $("#result").on('click', '.glyphicon-remove', function () {
        //TODO: need to delete the property object from the selectedSchema variable here.
        delete schema.properties[$(this).attr('name')];
        $.fn.renderSchema();
    });

    $.fn.patchedProperty = function (property) {
        return jQuery.extend(true, {'type': 'string', 'format': 'text', 'items': {'type': "string"}, 'required': false, 'options': {'wysiwyg': false}}, property);
    };

    $.fn.listSchemas = function () {
        $.get(base_url + '/schema').done(function (data) {
            if (data.length === 0) {
                var template = $.templates("#noSchema");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createSchema();
                });
            } else {
                schemas = data;
                var template = $.templates("#listSchema");
                template.link('#result', {schema: data});

                $('#createSchema').click(function () {
                    $.fn.createSchema();
                });


                $('#result .glyphicon-trash').on('click', function () {
                    schema = schemas[$(this).parent().parent().index()];
                    $("#delete-conf-model").modal('show');
                    confirmationPromise = $.Deferred();
                    confirmationPromise.done(function () {
                        $.fn.deleteSchema();
                    });
                });

                $('#result .glyphicon-edit').on('click', function () {
                    schema = schemas[$(this).parent().parent().index()];
                    isEdit = true;
                    $.fn.renderSchema();
                });

            }
        });
    };


    $.fn.v4Schema = function () {
        var patchedSchema = jQuery.extend(true, {}, schema);

        var properties = patchedSchema.properties;

        var required = [];

        for (var key in properties) {
            //repair required
            if (properties[key].required) {
                required.push(key);
            }
            delete properties[key].required;

            //trim unused
            if (properties[key].format && properties[key].format === 'text') {
                delete properties[key].format;
                delete properties[key].options;
            }

            if (properties[key].type) {
                if (properties[key].type === 'array') {
                    if (JSON_TYPES.indexOf(properties[key].items.type) === -1) {
                        properties[key].items['$ref'] = properties[key].items.type;
                        delete properties[key].items.type;
                    }
                    delete properties[key].options;
                }
                else {
                    delete properties[key].items;
                }

                if (JSON_TYPES.indexOf(properties[key].type) === -1) {
                    properties[key]['$ref'] = properties[key].type;
                    delete properties[key].type;
                }

            }
        }

        if (required.length !== 0) {
            patchedSchema.required = required;
        }
        return patchedSchema;
    };

    $.fn.saveSchema = function () {

        var v4Schema = $.fn.v4Schema(schema);


        console.log(v4Schema);
        if (isEdit) {
            $.ajax({
                method: 'PUT',
                url: base_url + '/schema/' + v4Schema.id,
                dataType: 'json',
                data: JSON.stringify(v4Schema)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        } else {
            $.ajax({
                method: 'POST',
                url: base_url + '/schema',
                dataType: 'json',
                data: JSON.stringify(v4Schema)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }
    };

    $.fn.deleteSchema = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/schema/' + schema.id,
            dataType: 'json'
        }).done(function (data) {
            schemas = null;
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };

    $.fn.v3Schema = function () {
        var patchedSchema = jQuery.extend(true, {}, schema);

        var properties = patchedSchema.properties;

        var required = patchedSchema.required;

        for (var key in properties) {
            properties[key] = $.fn.patchedProperty(properties[key]);
            if (required) {
                if (required.indexOf(key) > -1) {
                    properties[key].required = true;
                }
            }
            if (properties[key]['$ref']) {
                properties[key].type = properties[key]['$ref'];
                delete properties[key]['$ref'];
            }

            if (properties[key].type === 'array') {
                properties[key].items.type = properties[key].items['$ref'];
                delete properties[key].items['$ref'];
            }

        }

        console.log(patchedSchema);
        return patchedSchema;
    };


    $.fn.renderSchema = function () {
        schemaTemplate = $.templates('#schemaForm');
        schema.isEdit = isEdit;
        schema = $.fn.v3Schema();
        schemaTemplate.link('#result', schema);
        delete schema.isEdit;

        var options = '';
        for (var index in schemas) {
            var schemaElement = schemas[index];
            if (schemaElement.id !== schema.id) {
                options += "<option value='" + schemaElement.id + "'>" + schemaElement.title + "</option>";
            }
        }

        $("select[name='type']").each(function (i, obj) {
            $(obj).append(options);
            $(obj).val($(obj).attr('data-name'));
        });

        $("select[name='arraytypes']").each(function (i, obj) {
            $(obj).append(options);
            $(obj).val($(obj).attr('data-name'));
        });


        $.fn.listIdAndLabelFileds();
        $("input[name='name']")
                .focusout(function () {
                    $.fn.listIdAndLabelFileds();
                });
        $('#result form').submit(function (event) {
            event.preventDefault();
            $.fn.saveSchema();
        });
    };

    $.fn.createSchema = function () {
        schema = {'type': 'object', properties: {}};
        isEdit = false;
        $.fn.renderSchema();
    };

    $.fn.refreshList = function () {
        $.fn.listSchemas();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.listIdAndLabelFileds = function () {
        var currentIdField = schema.idField;
        console.log('currentIdField ' + currentIdField);
        $('#idField').find('option').remove();
        $.each(Object.keys(schema.properties), function (i, d) {
            $('#idField').append('<option value="' + d + '">' + d + '</option>');
        });
        $('#idField').val(currentIdField);
        schema.idField = currentIdField;

        var currentLabelField = schema.labelField;
        console.log('currentLabelField ' + currentLabelField);
        $('#labelField').find('option').remove();
        $.each(Object.keys(schema.properties), function (i, d) {
            $('#labelField').append('<option value="' + d + '">' + d + '</option>');
        });
        $('#labelField').val(currentLabelField);
        schema.labelField = currentLabelField;

    };

    $.fn.listSchemas();

}(jQuery));
