'use strict';

(function($) {
    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    var template;
    var selectedCategory;
    var parentLinks = [];
    var selectedLink;
    var currentLinks;

    var confirmationPromise;

    $('#edit_selected').on('click', function() {
        $.fn.editSelectedCategory();
    });
    $("#del_conf_ok").on('click', function() {
        $("#delete-conf-model").modal('hide');
        confirmationPromise.resolve();
    });
    $("del_conf_cancel").on('click', function() {
        confirmationPromise.reject();
    });

    $('#delete_selected').on('click', function() {
        $("#delete-conf-model").modal('show');
        confirmationPromise = $.Deferred();
        confirmationPromise.done(function() {
            $.fn.deleteSelectedCategory();
        });
    });

    $.fn.listCategories = function() {
        $.get(base_url + '/link_categories').done(function(data) {
            var template = $.templates("#listCategory");
            template.link('#categories', {category: data});
            $('#createCategory').click(function() {
                $.fn.createCategory();
            });
            if (data.length > 0) {
                $.fn.setSelectedCategory(data[0]);
            }
            $('#categories .catName').on('click', function() {
                $.fn.setSelectedCategory($.view(this).data);
            });
            parentLinks = [];
        });
    };

    $.fn.setSelectedCategory = function(selectedCategoryData) {
        $($('#category-list').find('button')[0].children[0]).text(selectedCategoryData.name);
        selectedCategory = selectedCategoryData;
        $('#linksBreadcrumb').empty();
        $.get(base_url + '/link_categories/' + selectedCategory.name + '/first_level_links').done(function(data) {
            $.fn.listCategoryLinks(data);
        });

    };
    $.fn.listCategoryLinks = function(listofLinks) {
        currentLinks = {link: listofLinks};
        var template = $.templates("#listLink");
        template.link('#result', currentLinks);
        $('#addMoreLinkBtn').on('click', function() {
            $.fn.createLink();
        });

        $('#result li a').on('click', function() {
            $.fn.addParentLink($.view(this).data);
        });

    };

    $.fn.addParentLink = function(ParentLinkData) {
        parentLinks.push(ParentLinkData);
        $.fn.listChildren(ParentLinkData);
    };

    $.fn.listChildren = function(ParentLinkData) {
        $.get(base_url + '/links/childen_of/' + ParentLinkData.name).done(function(data) {
            $.fn.listCategoryLinks(data);
        });
        template = $.templates("#breadcrumb");
        template.link('#linksBreadcrumb', {parentLink: parentLinks});
        $('#linksBreadcrumb a').on('click', function() {
            var selectedLinkIndex = $(this).parent().index();
            if (selectedLinkIndex === 0) {

            } else {
                var parentLinkData = parentLinks[selectedLinkIndex - 1];
                parentLinks = parentLinks.slice(0, selectedLinkIndex);
                $.fn.listChildren(parentLinkData);
            }

        });
    };

    $.fn.saveCategory = function() {
        if (selectedCategory.isEdit) {
            delete selectedCategory.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/link_categories/' + selectedCategory.name,
                dataType: 'json',
                data: JSON.stringify(selectedCategory)
            }).done(function(data) {
                $.fn.listCategories();
            });
        }
        else {
            $.ajax({
                method: 'POST',
                url: base_url + '/link_categories',
                dataType: 'json',
                data: JSON.stringify(selectedCategory)
            }).done(function(data) {
                $.fn.listCategories();
            });
        }

    };
    $.fn.deleteSelectedCategory = function() {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/link_categories/' + selectedCategory.name,
            dataType: 'json'
        }).done(function(data) {
            $.fn.listCategories();
        });
    };
    $.fn.editSelectedCategory = function() {
        if (selectedCategory) {
            template = $.templates("#catetoryForm");
            selectedCategory.isEdit = true;
            template.link('#result', selectedCategory);
        }
    };

    $.fn.createCategory = function() {
        selectedCategory = {};
        template = $.templates("#catetoryForm");
        template.link('#result', selectedCategory);
    };

    $.fn.createLink = function() {
        selectedLink = {};
        template = $.templates("#linkForm");
        template.link('#result', selectedLink);
    };

    $.fn.refreshList = function() {
        if (parentLinks.length !== 0) {
            $fn.listChildren(parentLinks[parentLinks.length - 1]);
        }
        else {
            $.fn.listCategories();
        }
    };

    $.fn.saveLink = function() {
        console.log(selectedLink);
        selectedLink.categoryName = selectedCategory.name;
        if (parentLinks.length !== 0) {
            selectedLink.parentLinkName = parentLinks[parentLinks.length - 1].name;
        }
        $.ajax({
            method: 'POST',
            url: base_url + '/links',
            dataType: 'json',
            data: JSON.stringify(selectedLink)
        }).done(function(data) {
            $.fn.refreshList();
        });
    };


    $.fn.listCategories();


}(jQuery));