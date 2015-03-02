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

    var schemas;
    var schema;
    var data;
    var listofData;
    var confirmationPromise;
    var isEdit = false;

    $.ajaxSetup({
        contentType: 'application/json'
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


    $.fn.listSchemas = function () {
        $('#schemaHeader').hide();
        $.get(base_url + '/schema').done(function (data) {
            if (data === "") {
                $('#templateRepositoryHeader').hide();
                var template = $.templates("#noTemplateRepository");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createTemplateRepository();
                });
            } else {
                schemas = data;
                var template = $.templates("#listSchemas");
                template.link('#result', {schema: data});
                $('.carousel-inner a').on('click', function () {
                    $('#schemaHeader').show();
                    schema = $.view(this).data;
                    $('#categorynameLbl').text(schema.title);

                    $.fn.listData();
                });
            }

        });
    };

    $.fn.listData = function () {
        data = null;
        isEdit = false;
        $.get(base_url + '/data/' + schema.id).done(function (dataList) {
            if (dataList === "") {
                var template = $.templates("#noData");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createData();
                });
            } else {
                listofData = {link: dataList};
                var template = $.templates("#listData");
                template.link('#result', listofData);
                $('#addMoreDataBtn').on('click', function () {
                    $.fn.renderData();
                });

                $('#result .glyphicon-trash').on('click', function () {

                    $("#delete-conf-model").modal('show');
                    confirmationPromise = $.Deferred();
                    confirmationPromise.done(function () {
                        $.fn.deleteData();
                    });
                    data = $.view(this).data;
                });

                $('#result .glyphicon-edit').on('click', function () {
                    isEdit = true;
                    data = $.view(this).data;
                    $.fn.renderData();
                });
            }
        });


    };

    $.fn.renderData = function () {
        var template = $.templates("#data_entry");
        template.link('#result', {});
        var editor = $("#editor_holder").jsonEditor({schemaName: schema.id, value: data});

        $("#result form").submit(function (event) {
            event.preventDefault();
            if (isEdit) {
                var value = editor.getValue();
                $.ajax({
                    method: 'PUT',
                    url: base_url + '/data/' + schema.id + "/" + value[schema.idField],
                    dataType: 'json',
                    data: JSON.stringify(value)
                }).done(function (data) {
                    $.fn.listData();
                }).error(function (data) {
                    $.fn.onError(data);
                });
            } else {
                $.ajax({
                    method: 'POST',
                    url: base_url + '/data/' + schema.id,
                    dataType: 'json',
                    data: JSON.stringify(editor.getValue())
                }).done(function (data) {
                    $.fn.listData();
                }).error(function (data) {
                    $.fn.onError(data);
                });
            }

        });
    };

    $.fn.deleteData = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/data/' + schema.id + '/' + data[schema.idField],
            dataType: 'json'
        }).done(function (data) {
            $.fn.listData();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };


    $.fn.listSchemas();

}(jQuery));