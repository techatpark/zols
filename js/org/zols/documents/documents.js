(function($) {

    var base_url = "http://localhost:8080";

    var $category = $("#category");
    var $links = $("#items>ul");
    var $categorySelector = $(".categorySelector");

    $('#addFile').click(function() {
        var name = 'files[' + $('#files').children().length + ']';
        $('#files').append('<li><input type="file" /><button type="button">X</button></li>');
        $('#files input:last').attr('name', name);
    });

    $("#files li button").click(function() {
        $(this).parent().remove();
    });
    
    $("#uploadForm").attr("action",base_url+"/api/documents/file");

    function show(sectionName) {
        $("#content>section").hide();
        $("#content>section#" + sectionName).show();
    }

    show('listing');

    $("#submitCategory").on("click", function() {
        var formData = $('#category-form').toObject();

        var categoryName = $('#category-form').attr('data-category');
        if (categoryName) {
            $.ajax(
                    {
                        url: base_url + "/api/document_repositories/" + categoryName,
                        type: 'PUT',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                loadCategories();
                onSave();
            });
        }
        else {
            $.ajax(
                    {
                        url: base_url + "/api/document_repositories",
                        type: 'POST',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                loadCategories();
                onSave();
            });
        }

    });

    $(".breadcrumb ul").on('click', 'li:not(:last-child) a', function() {
        var parentLinkName = $(this).attr('data-parent');
        if (parentLinkName === '/') {
            $(".breadcrumb ul").empty();
            loadLinksByCategory();
        }
        else {
            $(".breadcrumb li:gt(" + ($(this).parent().index()) + ")").remove();
            loadLinksByParent($(this).attr('data-parent'));
        }
    });



    $("#cancelCategory").on("click", function() {
        show("listing");
    });

    $("#submitLink").on("click", function() {
        var formData = $('#link-form').toObject();
        formData.categoryName = $category.val();
        formData.parentLinkName = $('#link-form').attr('data-parent');
        var linkName = $('#link-form').attr('data-link');
        if (linkName) {
            $.ajax(
                    {
                        url: base_url + "/api/documents/" + linkName,
                        type: 'PUT',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                onSave();
                refreshLinks();
            });
        }
        else {
            $.ajax(
                    {
                        url: base_url + "/api/documents",
                        type: 'POST',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                onSave();
                refreshLinks();
            });
        }

    });

    function onSave() {
        show("listing");
        $categorySelector.show();
    }

    $("#cancelLink").on("click", function() {
        show("listing");
    });


    $("#editBtn").on("click", function() {
        editCategory();
    });

    $("#deleteBtn").on("click", function() {
        deleteCategory($category.val()).done(function() {
            loadCategories();
        });
    });

    $category.on("change", function() {
        loadLinksByCategory();
    });

    $("#createNew").on("click", function() {
        createCategory();
    });

    $("#createLink").on("click", function() {
        createLink();
    });

    $("#addMore").on("click", function() {
        createLink();
    });

    $("#delLink").on("click", function() {
        deleteLink($(this).parent().attr('data-link'));
    });

    $("#choose_repo_type li a").on("click", function() {
        showCategoryWithType($(this).attr('data-type'));
    });

    function createCategory() {
        show("choose_repo_type");
    }

    function showCategoryWithType(type) {
        show("categoryform");

        $("form#category-form").removeAttr('category-link');

        $("form#category-form :input").each(function() {
            $(this).val('');
        });

        $("form#category-form :input[name='type']").val(type);

        if (type !== 'ftp') {
            $("form#category-form :input[name='host']").parent().hide();
            $("form#category-form :input[name='rootFolder']").parent().hide();
            $("form#category-form :input[name='userName']").parent().hide();
            $("form#category-form :input[name='password']").parent().hide();
        }
        else {
            $("form#category-form :input[name='host']").parent().show();
            $("form#category-form :input[name='rootFolder']").parent().show();
            $("form#category-form :input[name='userName']").parent().show();
            $("form#category-form :input[name='password']").parent().show();
        }
    }

    function editCategory() {
        $.ajax({
            url: base_url + "/api/document_repositories/" + $category.val(),
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(category) {
            $("form#category-form").attr('data-category', $category.val());
            showCategoryWithType(category.type);
            $("form#category-form :input").each(function() {
                $(this).val(category[$(this).attr('name')]);
            });
            show("categoryform");
        });
    }

    function createLink() {
        $("form#link-form").removeAttr('data-link');
        $("form#link-form :input").each(function() {
            $(this).val('');
        });
        setParentLink();
        show("linkform");

    }

    function deleteCategory(categoryName) {
        return $.ajax({
            url: base_url + "/api/document_repositories/" + categoryName,
            type: 'DELETE',
            dataType: 'json',
            contentType: 'application/json'
        });
    }

    function refreshLinks() {
        var selectedParent = getSelectedParent();
        if (selectedParent) {
            loadLinksByParent(selectedParent);
        }
        else {
            loadLinksByCategory();
        }
    }

    function getSelectedParent() {
        if ($('.breadcrumb ul li').length > 0) {
            return $(".breadcrumb ul li:last-child a").attr('data-parent');
        }
        return null;
    }

    function loadLinksByCategory() {
        var categoryName = $category.val();
        if (categoryName === '/') {
            createCategory();
        }
        else {
            $links.empty();
            $.ajax({
                url: base_url + "/api/documents/" + categoryName,
                dataType: 'json',
                contentType: 'application/json'
            }).done(function(links) {
                if (links && links.length > 0) {
                    createLinks(links);
                }
                else {
                    show("nolinks");
                }
            });
        }
    }

    function createLinks(links) {
        var linksHtml = '';
        links.forEach(function(link) {
            linksHtml += "<li name='" + link.fileName + "' dir='" + link.isDir + "'><p>" + link.fileName + "</p><a role='edit' href='#'>E</a><a role='delete' href='#deleteLink'>-</a></li>";
        });
        $links.html(linksHtml);
        addLinkListener();
    }

    function loadLinksByParent(parentLinkName) {
        $links.empty();
        $.ajax({
            url: base_url + "/api/documents/childen_of/" + parentLinkName,
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(links) {
            createLinks(links);
        });
    }


    function addLinkListener() {
        $("#items>ul li a").on("click", function() {
            if ($(this).attr('role') === 'edit') {
                editLink($(this).parent().attr('name'));
            } else {
                $("#deleteLink div").attr("data-link", $(this).parent().attr('name'));
            }

        });
        $("#items>ul li p").on("click", function() {
            navigateInto($(this).parent().attr('name'));
        });
    }

    function navigateInto(linkName) {
        if ($('.breadcrumb ul li').length === 0) {
            $('.breadcrumb ul').append('<li><a href="javascript://" data-parent="/">/</a></li>');
        }
        $('.breadcrumb ul').append('<li><a href="javascript://" data-parent="' + linkName + '">' + linkName + '</a></li>');
        loadLinksByParent(linkName);
    }



    function editLink(linkName) {
        $.ajax({
            url: base_url + "/api/documents/" + linkName,
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(link) {
            show("linkform");
            $("form#link-form").attr('data-link', linkName);
            setParentLink();
            $("form#link-form :input").each(function() {
                $(this).val(link[$(this).attr('name')]);
            });

        });
    }

    function deleteLink(linkName) {
        $.ajax({
            url: base_url + "/api/documents/" + linkName,
            type: 'DELETE',
            dataType: 'json',
            contentType: 'application/json'
        }).done(function() {
            refreshLinks();
        });
    }

    function setParentLink() {
        var selectedParent = getSelectedParent();
        if (selectedParent) {
            $("form#link-form").attr('data-parent', selectedParent);
        }
        else {
            $("form#link-form").removeAttr('data-parent');
        }
    }



    function loadCategories() {
        $.ajax({
            url: base_url + "/api/document_repositories"
        }).done(function(categories) {
            $category.empty();
            if (categories) {
                var categoryOption = '';
                categories.forEach(function(category) {
                    categoryOption += "<option value=" + category.name + " >" + category.label + "</option>";
                });
                categoryOption += "<optgroup label=''><option value='/'>Create New</option></optgroup><optgroup ></optgroup>";
                $category.html(categoryOption);
                loadLinksByCategory();
            }
            else {
                show("nocategory");
                $categorySelector.hide();
            }

        });
    }

    loadCategories();

}(jQuery));