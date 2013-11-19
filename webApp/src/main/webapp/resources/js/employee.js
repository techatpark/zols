
var contentData = null;

function onChange() {
    $(function() {
        jQuery("#grid").jqGrid("GridUnload");
        $.extend($.jgrid.defaults, {
            autowidth: true,
            shrinkToFit: true,
            datatype: 'json',
            jsonReader: {
                repeatitems: false,
                root: "content",
                page: function(result) {
                    //Total number of records
                    return result.number + 1;
                },
                total: "totalPages",
                records: "totalElements"
            },
            prmNames: {
                page: "page.page",
                rows: "page.size",
                sort: "page.sort",
                order: "page.sort.dir"
            },
            height: 'auto',
            viewrecords: true,
            rowList: [10, 20, 50, 100],
            altRows: true,
            loadError: function(xhr, status, error) {

            }
        });

        $.extend($.jgrid.edit, {
            closeAfterEdit: true,
            closeAfterAdd: true,
            ajaxEditOptions: {contentType: "application/json"},
            mtype: 'PUT',
            serializeEditData: function(data) {
                delete data.oper;
                return JSON.stringify(data);
            }
        });
        $.extend($.jgrid.del, {
            mtype: 'DELETE',
            serializeDelData: function() {
                return "";
            }
        });
        var delOptions = {
            onclickSubmit: function(params, postdata) {
                console.log(postdata);
                params.url = URL + '/' + postdata;
            }
        };

        var URL = SERVER_URL + '/api/data/' + entityName;

        var colModelDynamic = [];
        colModelDynamic.push({
            name: 'name',
            label: 'Name',
            key: true,
            index: 'name',
            editable: true,
            editoptions: {required: true}
        });

        $.each(contentData.attributes, function(index, attribute) {
            colModelDynamic.push({
                name: attribute.name,
                label: attribute.description,
                key: true,
                index: 'name',
                editable: true,
                editoptions: {required: true}
            });
        });


        var options = {
            url: URL,
            editurl: URL,
            colModel: colModelDynamic,
            caption: entityName,
            pager: '#pager',
            height: 'auto',
            ondblClickRow: function(id) {
                window.location = 'dataListChange.html' + '#id=' + id + "&entity=" + entityName;
            },
            formatter: {idName: "name"}
        };
        $("#grid")
                .jqGrid(options)
                .navGrid('#pager',
                        {search: false, addfunc: function() {
                                window.location = 'dataListChange.html#' + "&entity=" + entityName;
                            }, editfunc: function(data) {
                                window.location = 'dataListChange.html' + '#id=' + data + "&entity=" + entityName;
                            }}, //options
                {}, // edit options
                        {}, // add options 
                        delOptions,
                        {} // search options
                );
    });
}

$(document).ready(function() {

    pathname = window.location.pathname;
    entityName = pathname.substr(pathname.lastIndexOf('/') + 1);

    $.ajax(
            {
                url: SERVER_URL + '/api/entities/' + entityName,
                type: 'GET',
                dataType: "json",
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR)
                {
                    contentData = data.entity;

                    onChange();

                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });
});
