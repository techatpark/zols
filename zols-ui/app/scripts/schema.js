'use strict';

(function ($) {
    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    $('[data-toggle="tooltip"]').tooltip();

    var schema;
    var selectedSchema;
    var listOfSchemas;
    var confirmationPromise;

    $('#edit_selected').on('click', function () {
        $.fn.renderSchemaRepository();
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
            $.fn.deleteSelectedTemplateRepository();
        });
    });

    $.fn.getSchemas = function () {
        $.get(base_url + '/schema').done(function (data) {
            $.fn.listSchemas(data);
        });
    };

    $.fn.listSchemas = function (listofSchemas) {
        if (listofSchemas === "") {
            var schema = $.templates("#noSchema");
            schema.link('#result', {});
            $('#result a').click(function () {
                $.fn.createSchema();
            });
        } else {
            listOfSchemas = {link: listofSchemas};
            var schema = $.templates("#listSchema");
            schema.link('#result', listOfSchemas);
            $('#addMoreSchemaBtn').on('click', function () {
                $.fn.createSchema();
            });

            $('#result li a').on('click', function () {
                $.fn.addParentTemplate($.view(this).data);
            });

            $('#result .glyphicon-trash').on('click', function () {
                selectedSchema = listOfSchemas.link[$(this).parent().parent().index()];
                $("#delete-conf-model").modal('show');
                confirmationPromise = $.Deferred();
                confirmationPromise.done(function () {
                    $.fn.deleteSchema();
                });
            });

            $('#result .glyphicon-edit').on('click', function () {
                selectedSchema = listOfSchemas.link[$(this).parent().parent().index()];
                $.get(base_url + '/schema/' + selectedSchema.id).done(function (data) {
                    selectedSchema = data;
                    $.fn.renderSchema();
                });
            });
        }




    };


    $.fn.refreshList = function () {
        $.fn.getSchemas();
     
    };

    $.fn.saveSchema = function () {

        selectedSchema.schema = JSON.parse(selectedSchema.schema);
        //selectedSchema.schema.type = selectedSchema.schemaType;
        if (selectedSchema.isEdit) {
            delete selectedSchema.isEdit;
            delete selectedSchema.schema.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/schema/' + selectedSchema.id,
                dataType: 'json',
                data: JSON.stringify(selectedSchema.schema)
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
                data: JSON.stringify(selectedSchema.schema)
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
            url: base_url + '/schema/' + selectedSchema.id,
            dataType: 'json'
        }).done(function (data) {
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderSchema = function () {
        if (selectedSchema && selectedSchema.id) {
            selectedSchema.isEdit = true;
            selectedSchema.schema = JSON.stringify(selectedSchema);
        }
        schema = $.templates("#schemaForm");
        schema.link('#result', selectedSchema);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveSchema();
        });
    };

    $.fn.createSchema = function () {
        selectedSchema = {};
        $.fn.renderSchema();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.getSchemas();

}(jQuery));