var URL = '../api/';

function loadData(entityName, contentData) {
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
            $("#grid").clearGridData();
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
            params.url = URL + 'data/' + entityName + '/' + postdata;
        }
    };

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
            label: attribute.label,
            key: true,
            index: 'name',
            editable: true,
            editoptions: {required: true}
        });
    });
    var options = {
        url: URL + 'data/' + entityName,
        editurl: URL + 'data/' + entityName,
        colModel: colModelDynamic,
        caption: contentData.label,
        pager: '#pager',
        height: 'auto',
        ondblClickRow: function(id) {
            //edit url
            window.location = entityName + '/' + id;
        },
        formatter: {idName: "name"}
    };
    $("#grid")
            .jqGrid(options)
            .navGrid('#pager',
                    {search: false, addfunc: function() {
                            window.location = entityName + '/add';
                        }, editfunc: function(data) {
                            window.location = entityName + '/' + data;
                        }}, //options
            {}, // edit options
                    {}, // add options 
                    delOptions,
                    {} // search options
            );
}

$(document).ready(function() {
    var pathname = window.location.pathname;
    var entityName = pathname.substr(pathname.lastIndexOf('/') + 1);
    $.ajax(
            {
                url: URL + "entities/" + entityName,
                type: 'GET',
                dataType: "json",
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR)
                {
                    loadData(entityName, data.entity);

                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });
});
