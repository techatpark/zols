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

    $.ajaxSetup({
        contentType: 'application/json'
    });

    $('[data-toggle="tooltip"]').tooltip();
    $('#categorynameLbl').hide();

    var template;
    var selectedCategory;
    var listOfCategories;
    var parentLinks = [];
    var selectedLink;
    var listOfLinks;

    var confirmationPromise;

    $('#edit_selected').on('click', function () {
        $.fn.renderCategory();
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
            $.fn.deleteSelectedCategory();
        });
    });

    $.fn.listCategories = function () {
        $.get(base_url + '/link_categories').done(function (data) {
            if (data === "") {
                $('#categoryHeader').hide();
                var template = $.templates("#noCategory");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createCategory();
                });
                $('#linksBreadcrumb').empty();
                parentLinks = [];
            } else {
                $('#categoryHeader').show();
                listOfCategories = data;
                var template = $.templates("#listCategory");
                template.link('#categories', {category: data});
                $('#createCategory').click(function () {
                    $.fn.createCategory();
                });
                $('#categories .catName').on('click', function () {
                    $.fn.setSelectedCategory($.view(this).data);
                });

                if (data.length > 0) {
                    $.fn.setSelectedCategory(data[0]);
                }
            }



        });
    };

    $.fn.setSelectedCategory = function (selectedCategoryData) {
        parentLinks = [];
        $('[data-bind-col="categoryname"]').text(selectedCategoryData.label);
        selectedCategory = selectedCategoryData;
        $('#linksBreadcrumb').empty();
        $.get(base_url + '/link_categories/' + selectedCategory.name + '/first_level_links').done(function (data) {
            $.fn.listLinks(data);
        });

    };
    $.fn.listLinks = function (listofLinks) {
        if (listofLinks === "") {
            var template = $.templates("#noLink");
            template.link('#result', {});
            $('#result a').click(function () {
                $.fn.createLink();
            });
        } else {
            listOfLinks = {link: listofLinks};
            var template = $.templates("#listLink");
            template.link('#result', listOfLinks);
            $('#addMoreLinkBtn').on('click', function () {
                $.fn.createLink();
            });

            $('#result li a').on('click', function () {
                $.fn.addParentLink($.view(this).data);
            });

            $('#result .glyphicon-trash').on('click', function () {
                selectedLink = listOfLinks.link[$(this).parent().parent().index()];
                $("#delete-conf-model").modal('show');
                confirmationPromise = $.Deferred();
                confirmationPromise.done(function () {
                    $.fn.deleteLink();
                });
            });

            $('#result .glyphicon-edit').on('click', function () {
                selectedLink = listOfLinks.link[$(this).parent().parent().index()];
                $.fn.renderLink();
            });
        }




    };

    $.fn.addParentLink = function (ParentLinkData) {
        parentLinks.push(ParentLinkData);
        $.fn.listChildren(ParentLinkData);
    };

    $.fn.listChildren = function (ParentLinkData) {
        $.get(base_url + '/links/childen_of/' + ParentLinkData.name).done(function (data) {
            $.fn.listLinks(data);
        });
        template = $.templates("#breadcrumb");
        template.link('#linksBreadcrumb', {parentLink: parentLinks});
        $('#linksBreadcrumb a').on('click', function () {
            var selectedLinkIndex = $(this).parent().index();
            if (selectedLinkIndex === 0) {
                parentLinks = [];
                $.fn.refreshList();
            } else {
                var parentLinkData = parentLinks[selectedLinkIndex - 1];
                parentLinks = parentLinks.slice(0, selectedLinkIndex);
                $.fn.listChildren(parentLinkData);
            }

        });
    };

    $.fn.saveCategory = function () {
        if (selectedCategory.isEdit) {
            delete selectedCategory.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/link_categories/' + selectedCategory.name,
                dataType: 'json',
                data: JSON.stringify(selectedCategory)
            }).done(function (data) {
                $.fn.listCategories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }
        else {
            $.ajax({
                method: 'POST',
                url: base_url + '/link_categories',
                dataType: 'json',
                data: JSON.stringify(selectedCategory)
            }).done(function (data) {
                $.fn.listCategories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };
    $.fn.deleteSelectedCategory = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/link_categories/' + selectedCategory.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.listCategories();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderCategory = function () {
        if (selectedCategory && selectedCategory.name) {
            selectedCategory.isEdit = true;
        }
        template = $.templates("#catetoryForm");
        template.link('#result', selectedCategory);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveCategory();
        });
        $('#categoryHeader').hide();
        $('#pageTitle').text('Link Category');
    };


    $.fn.createCategory = function () {
        selectedCategory = {};
        $.fn.renderCategory();
    };

    $.fn.refreshList = function () {
        if (parentLinks.length !== 0) {
            $.fn.listChildren(parentLinks[parentLinks.length - 1]);
        }
        else if (!listOfCategories) {
            $.fn.listCategories();
        }
        else {
            $.fn.setSelectedCategory(selectedCategory);
        }

        $('#category-list').show();
        $('#categorynameLbl').hide();
        $('#categoryHeader').show();

        $('#pageTitle').text('Links');

    };

    $.fn.saveLink = function () {
        selectedLink.categoryName = selectedCategory.name;
        if (parentLinks.length !== 0) {
            selectedLink.parentLinkName = parentLinks[parentLinks.length - 1].name;
        }

        if (selectedLink.isEdit) {
            delete selectedLink.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/links/' + selectedLink.name,
                dataType: 'json',
                data: JSON.stringify(selectedLink)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        } else {
            $.ajax({
                method: 'POST',
                url: base_url + '/links',
                dataType: 'json',
                data: JSON.stringify(selectedLink)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };


    $.fn.deleteLink = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/links/' + selectedLink.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderLink = function () {
        if (selectedLink && selectedLink.name) {
            selectedLink.isEdit = true;
        }
        template = $.templates("#linkForm");
        template.link('#result', selectedLink);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveLink();
        });
        $('#category-list').hide();
        $('#categorynameLbl').show();

        $('#pageTitle').text('Link');
    };

    $.fn.createLink = function () {
        selectedLink = {};
        $.fn.renderLink();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };


    $.fn.listCategories();


}(jQuery));