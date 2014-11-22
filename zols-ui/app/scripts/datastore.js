'use strict';

(function ($) {
    var base_url = 'http://localhost:8080/api';

    // Set an option globally
    JSONEditor.defaults.options.theme = 'bootstrap3';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    $('[data-toggle="tooltip"]').tooltip();
    $('#schemanameLbl').hide();

    var schema;
    var template;
    var selectedSchema;
    var listOfCategories;
    var selectedTemplate;
    var listOfTemplates;

    var editor;

    var confirmationPromise;

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

    $.fn.listSchemas = function () {
        $.get(base_url + '/schema').done(function (data) {
            if (data === "") {
                $('#schemaHeader').hide();
                var template = $.templates("#noSchema");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createSchema();
                });
            } else {
                $('#schemaHeader').show();
                listOfCategories = data;
                var template = $.templates("#listSchema");
                template.link('#categories', {schema: data});
                $('#createSchema').click(function () {
                    $.fn.createSchema();
                });
                $('#categories .catName').on('click', function () {
                    $.fn.setSelectedSchema($.view(this).data);
                });

                if (data.length > 0) {
                    $.fn.setSelectedSchema(data[0]);
                }
            }



        });
    };

    $.fn.setSelectedSchema = function (selectedSchemaData) {

        $('[data-bind-col="schemaname"]').text(selectedSchemaData.title);
        selectedSchema = selectedSchemaData;

        $.get(base_url + '/data/' + selectedSchema.id).done(function (data) {
            $.fn.listTemplates(data);
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
            listOfTemplates = {link: listofTemplates};
            var template = $.templates("#listTemplate");
            template.link('#result', listOfTemplates);
            $('#addMoreTemplateBtn').on('click', function () {
                $.fn.createTemplate();
            });

            $('#result li a').on('click', function () {
                $.fn.addParentTemplate($.view(this).data);
            });

            $('#result .glyphicon-trash').on('click', function () {
                selectedTemplate = listOfTemplates.link[$(this).parent().parent().index()];
                $("#delete-conf-model").modal('show');
                confirmationPromise = $.Deferred();
                confirmationPromise.done(function () {
                    $.fn.deleteTemplate();
                });
            });

            $('#result .glyphicon-edit').on('click', function () {
                selectedTemplate = listOfTemplates.link[$(this).parent().parent().index()];
                $.fn.renderTemplate();
            });
        }




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
                listOfCategories = null;
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
                listOfCategories = null;
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
            listOfCategories = null;
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

    $.fn.refreshList = function () {

        $('#schema-list').show();
        $('#schemanameLbl').hide();
        $('#schemaHeader').show();
        if (!listOfCategories) {
            $.fn.listSchemas();
        }
        else {
            $.fn.setSelectedSchema(selectedSchema);
        }
    };

    $.fn.saveTemplate = function () {
        selectedTemplate.repositoryName = selectedSchema.name;

        if (selectedTemplate.isEdit) {
            delete selectedTemplate.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/data/' + selectedSchema.id + '/' +selectedTemplate.name,
                dataType: 'json',
                data: JSON.stringify(selectedTemplate)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        } else {
            $.ajax({
                method: 'POST',
                url: base_url + '/data/'+ selectedSchema.id ,
                dataType: 'json',
                data: JSON.stringify(selectedTemplate)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };


    $.fn.deleteTemplate = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/data/' + selectedSchema.id + '/'+ selectedTemplate.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderTemplate = function () {

        $.get(base_url + '/schema/' + selectedSchema.id).done(function (data) {

            if (selectedTemplate && selectedTemplate.name) {
                selectedTemplate.isEdit = true;
            }
            template = $.templates("#linkForm");
            template.link('#result', selectedTemplate);
            $("#result form").submit(function (event) {
                event.preventDefault();
                $.fn.saveTemplate();
            });
            $('#schema-list').hide();
            $('#schemanameLbl').show();

            $("#editor_holder").empty();
            var element = document.getElementById('editor_holder');
            editor = new JSONEditor(element, {schema: data, disable_properties: true, disable_collapse: true, disable_edit_json: true});
            if (selectedTemplate.isEdit) {
                editor.setValue(selectedTemplate);
            }

        });



    };

    $.fn.createTemplate = function () {
        selectedTemplate = {};
        $.fn.renderTemplate();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.listSchemas();

}(jQuery));