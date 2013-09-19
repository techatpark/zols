$(function() {

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
        sortname: 'title',
        sortorder: 'asc',
        height: 'auto',
        viewrecords: true,
        rowList: [10, 20, 50, 100],
        altRows: true,
        loadError: function(xhr, status, error) {
            alert(error);
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

    var editOptions = {
        onclickSubmit: function(params, postdata) {
            params.url = URL + '/' + postdata.name;
        }
    };
    var addOptions = {mtype: "POST",
        onclickSubmit: function(params, postdata) {
            delete postdata.id;
            params.url = URL ;
        }};
    var delOptions = {
        onclickSubmit: function(params, postdata) {
            params.url = URL + '/' + postdata;
        }
    };

    var URL = 'schemas';
    var options = {
        url: URL,
        editurl: URL,
        colModel: [
            {
                name: 'name',
                label: 'Name',
                key: true,
                index: 'name',
                editable: true,
                editoptions: {required: true}
            },
            {
                name: 'label',
                label: 'Label',
                index: 'label',
                editable: true,
                editrules: {required: true}
            },
            {
                name: 'description',
                label: 'Description',
                index: 'description',
                editable: true,
                editrules: {required: true}
            }
        ],
        caption: "Schemas",
        pager: '#pager',
        height: 'auto',
        ondblClickRow: function(id) {
            jQuery(this).jqGrid('editGridRow', id, editOptions);
        }
    };

    $("#grid")
            .jqGrid(options)
            .navGrid('#pager',
            {}, //options
            editOptions,
            addOptions,
            delOptions,
            {} // search options
    );

});
