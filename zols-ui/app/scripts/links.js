'use strict';

(function($) {
    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
        contentType: 'application/json',
    });

    var template;
    var selectedCategory;
    var selectedParantLinkName;
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
            selectedParantLinkName = null;
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
            $.fn.setSelectedParentLink($.view(this).data);
        });

    };

    $.fn.setSelectedParentLink = function(ParentLink) {
        selectedParantLinkName = ParentLink;
        $.get(base_url + '/links/childen_of/' + selectedParantLinkName.name).done(function(data) {
            $.fn.listCategoryLinks(data);
        });
        $('#linksBreadcrumb').append('<li><a href="#">'+selectedParantLinkName.label+'</a></li>');
        
    };

    $.fn.saveCategory = function() {
        console.log(selectedCategory);
        $.ajax({
            method: 'POST',
            url: base_url + '/link_categories',
            dataType: 'json',
            data: JSON.stringify(selectedCategory)
        }).done(function(data) {
            alert("Data Loaded: " + data);
        });
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
        template = $.templates("#catetoryForm");
        template.link('#result', selectedCategory);
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

    $.fn.saveLink = function() {
        console.log(selectedLink);
        selectedLink.categoryName = selectedCategory.name;
        selectedLink.parentLinkName = selectedParantLinkName;
        $.ajax({
            method: 'POST',
            url: base_url + '/links',
            dataType: 'json',
            data: JSON.stringify(selectedLink)
        }).done(function(data) {
            alert("Data Loaded: " + data);
        });
    };


    $.fn.listCategories();


}(jQuery));