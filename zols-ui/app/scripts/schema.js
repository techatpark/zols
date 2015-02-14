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
        schema.properties['newProperty' + totalProperties] = {'type': 'string','format': 'text', 'required': false};
        $.fn.renderSchema();
    });

    $("#result").on('click', '.glyphicon-remove', function () {
        //TODO: need to delete the property object from the selectedSchema variable here.
        delete schema.properties[$(this).attr('name')];
        $.fn.renderSchema();
    });

    $.fn.listSchemas = function () {
        $.get(base_url + '/schema').done(function (data) {
            if (data === "") {
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


    $.fn.saveSchema = function () {
        if (isEdit) {
            $.ajax({
                method: 'PUT',
                url: base_url + '/schema/' + schema.id,
                dataType: 'json',
                data: JSON.stringify(schema)
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
                data: JSON.stringify(schema)
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

    $.fn.renderSchema = function () {
        schemaTemplate = $.templates('#schemaForm');
        schema.isEdit = isEdit;
        schemaTemplate.link('#result', schema);
        delete schema.isEdit;
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

    $.fn.listSchemas();

}(jQuery));
