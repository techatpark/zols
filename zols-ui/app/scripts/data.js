'use strict';

(function ($) {

    // Set an option globally
    JSONEditor.defaults.options.theme = 'bootstrap3';

    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    $('[data-toggle="tooltip"]').tooltip();
    $('#schemanameLbl').hide();

    var selectedTemplateRepository;
    var listOfCategories;
    var editor;

    var confirmationPromise;

    $('#edit_selected').on('click', function () {
        $.fn.renderTemplateRepository();
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

    $.fn.listSchema = function () {
        $.get(base_url + '/schema').done(function (data) {
            if (data === "") {
                $('#schemaHeader').hide();
                var template = $.templates("#noTemplateRepository");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createTemplateRepository();
                });
            } else {
                $('#schemaHeader').show();
                listOfCategories = data;
                var template = $.templates("#listTemplateRepository");
                template.link('#categories', {schema: data});
                $('#createTemplateRepository').click(function () {
                    $.fn.createTemplateRepository();
                });
                $('#categories .catName').on('click', function () {
                    $.fn.setSelectedTemplateRepository($.view(this).data);
                });

                if (data.length > 0) {
                    $.fn.setSelectedTemplateRepository(data[0]);
                }
            }



        });
    };

    $.fn.setSelectedTemplateRepository = function (selectedTemplateRepositoryData) {

        $('[data-bind-col="schemaname"]').text(selectedTemplateRepositoryData.title);
        selectedTemplateRepository = selectedTemplateRepositoryData;

        $.get(base_url + '/schema/' + selectedTemplateRepository.id).done(function (data) {
            $.fn.listTemplates(data);
        });

    };
    $.fn.listTemplates = function (data) {
        $("#editor_holder").html("");
        var element = document.getElementById('editor_holder');

        editor = new JSONEditor(element, {schema: data, disable_properties: true, disable_collapse: true, disable_edit_json: true});



    };

    $("#saveBtn").click(function (e) {
        var value = editor.getValue();
        console.log(value);
    });


    $.fn.refreshList = function () {
        if (!listOfCategories) {
            $.fn.listSchema();
        }
        else {
            $.fn.setSelectedTemplateRepository(selectedTemplateRepository);
        }

        $('#schema-list').show();
        $('#schemanameLbl').hide();
        $('#schemaHeader').show();
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

    $.fn.listSchema();

}(jQuery));