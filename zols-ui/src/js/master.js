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
    var dataPage;
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

                    $.fn.listData(0);
                });
            }

        });
    };

    $.fn.listData = function (pageNumber) {
        data = null;
        isEdit = false;
        var listUrl = base_url + '/data/' + schema.name + '?page='+pageNumber;
        var searchBox = $('#schemaHeader .input-group .form-control');
        if(searchBox && searchBox.val()) {
            listUrl = listUrl + "&q=" + searchBox.val();
        }
        $.get(listUrl).done(function (dataList) {
            if (dataList === "") {
                var template = $.templates("#noData");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.renderData();
                });
            } else {
                $("#schemaHeader").show();

                var displayContent = dataList.content.map(function(item){
                   var displayItem = {};
                   displayItem.labelField = item[schema.labelField];
                   displayItem.idField = item[schema.idField];
                   return displayItem;
                });
                dataList.displayContent = displayContent;


                dataPage = {page: dataList};


                var template = $.templates("#listData");
                template.link('#result', dataPage);
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
                    var result = $.grep(dataList.content, function(item){ return item[schema.idField] == data.idField; });
                    data = result[0];
                });
                $('#result .glyphicon-edit').on('click', function () {
                    isEdit = true;

                    data = $.view(this).data;

                    var result = $.grep(dataList.content, function(item){ return item[schema.idField] == data.idField; });
                    data = result[0];
                    $.fn.renderData();
                });
                $('#result .pager li').not(".disabled").on('click', function () {
                    if($(this).index() === 0) {
                        $.fn.listData(dataPage.page.number-1);
                    }else {
                        $.fn.listData(dataPage.page.number+1);
                    }
                });
                $('#schemaHeader .input-group .btn-default').on('click', function () {
                    $.fn.listData(0);
                });
            }
        });


    };

    $.fn.renderData = function () {
        $("#schemaHeader").hide();
        var template = $.templates("#data_entry");
        template.link('#result', {});

        if(data && data["$type"]) {
          delete data["$type"];
        }
        var editor = $("#editor_holder").jsonEditor({schemaName: schema.name, value: data});

        $("#result form").submit(function (event) {
            event.preventDefault();
            if (isEdit) {
                var value = editor.getValue();
                $.ajax({
                    method: 'PUT',
                    url: base_url + '/data/' + schema.name + "/" + value[schema.idField],
                    dataType: 'json',
                    data: JSON.stringify(value)
                }).done(function (data) {
                    $.fn.listData(0);
                }).error(function (data) {
                    $.fn.onError(data);
                });
            } else {
                $.ajax({
                    method: 'POST',
                    url: base_url + '/data/' + schema.name,
                    dataType: 'json',
                    data: JSON.stringify(editor.getValue())
                }).done(function (data) {
                    $.fn.listData(0);
                }).error(function (data) {
                    $.fn.onError(data);
                });
            }

        });
    };

    $.fn.deleteData = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/data/' + schema.name + '/' + data[schema.idField],
            dataType: 'json'
        }).done(function (data) {
            $.fn.listData(0);
        }).error(function (data) {
            $.fn.onError(data);
        });
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.listSchemas();

}(jQuery));
