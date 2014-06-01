(function($) {

    var base_url = "http://localhost:8080/zols";

    var $category = $("#category");
    var $links = $("#items>ul");

    var $sectionone = $("#content>section:nth-child(2)");
    var $sectiontwo = $("#content>section:nth-child(3)");
    var $sectionthree = $("#content>section:nth-child(4)");

    $sectiontwo.hide();
    $sectionthree.hide();

    $("#submitCategory").on("click", function() {
        var formData = $('#category-form').toObject();

        var categoryName = $('#category-form').attr('data-category');
        if (categoryName) {
            $.ajax(
                    {
                        url: base_url + "/api/linkcategories/" + categoryName,
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
                        url: base_url + "/api/linkcategories",
                        type: 'POST',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                loadCategories();
                onSave();
            });
        }


        console.log(formData);
    });

    function createCategory() {
        $sectionone.hide();
        $sectiontwo.show();
        $("form#category-form").removeAttr('category-link');
        $("form#category-form :input").each(function() {
            $(this).val('');
        });
    }

    $("#cancelCategory").on("click", function() {
        $sectiontwo.hide();
        $sectionone.show();
    });

    $("#submitLink").on("click", function() {
        var formData = $('#link-form').toObject();
        formData.categoryName = $category.val();
        formData.parentLinkName = $('#link-form').attr('data-parent');
        var linkName = $('#link-form').attr('data-link');
        if (linkName) {
            $.ajax(
                    {
                        url: base_url + "/api/links/" + linkName,
                        type: 'PUT',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                onSave();
            });
        }
        else {
            $.ajax(
                    {
                        url: base_url + "/api/links",
                        type: 'POST',
                        data: JSON.stringify(formData),
                        dataType: "json",
                        contentType: 'application/json'
                    }).done(function() {
                onSave();
            });
        }

    });
    function onSave() {
        $sectiontwo.hide();
        $sectionthree.hide();
        $sectionone.show();
    }
    $("#cancelLink").on("click", function() {
        $sectionthree.hide();
        $sectionone.show();
    });


    function editCategory() {

        $.ajax({
            url: base_url + "/api/linkcategories/" + $category.val(),
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(category) {
            $("form#category-form").attr('data-category', $category.val());
            $("form#category-form :input").each(function() {
                $(this).val(category[$(this).attr('name')]);
            });
            $sectionone.hide();
            $sectiontwo.show();

        });
    }
    $("#editBtn").on("click", function() {
        editCategory();
    });

    $("#deleteBtn").on("click", function() {
        $.ajax({
            url: base_url + "/api/linkcategories/" + $category.val(),
            type: 'DELETE',
            dataType: 'json',
            contentType: 'application/json'
        }).done(function() {
            loadCategories();
        });
    });

    $category.on("change", function() {
        loadLinksByCategory();
    });




    function loadLinksByCategory() {
        var categoryName = $category.val();
        if (categoryName === '/') {
            createCategory();
        }
        else {
            $links.empty();
            $.ajax({
                url: base_url + "/api/links/categories/" + categoryName,
                dataType: 'json',
                contentType: 'application/json'
            }).done(function(links) {
                var linksHtml = '';
                links.forEach(function(link) {
                    linksHtml += "<li name='" + link.name + "'><p>" + link.label + "</p><a role='edit' href='#'>E</a></li>";
                });
                $links.html(linksHtml);
                addLinkListener();
            });
        }
    }

    $("#addMore").on("click", function() {
        $sectionone.hide();
        $sectiontwo.hide();
        $("form#link-form").removeAttr('data-link');
        $("form#link-form :input").each(function() {
            $(this).val('');
        });
        setParentLink();
        $sectionthree.show();
    });

    function addLinkListener() {
        $("#items>ul li a[role='edit']").on("click", function() {
            editLink($(this).parent().attr('name'));
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
    $(".breadcrumb ul").on('click', 'li:not(:last-child) a', function() {
        var parentLinkName = $(this).attr('data-parent');
        console.log('parentLinkName ' + parentLinkName);



        if (parentLinkName === '/') {
            $(".breadcrumb ul").empty();
            loadLinksByCategory();
        }
        else {
//                if ($(this).parent().index() !== 1) {
            $(".breadcrumb li:gt(" + ($(this).parent().index()) + ")").remove();
            loadLinksByParent($(this).attr('data-parent'));
//                }                             
        }

    });

    function loadLinksByParent(parentLinkName) {
        $links.empty();
        $.ajax({
            url: base_url + "/api/links/children/" + parentLinkName,
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(links) {
            var linksHtml = '';
            links.forEach(function(link) {
                linksHtml += "<li name='" + link.name + "'><p>" + link.label + "</p><a role='edit' href='#'>E</a></li>";
            });
            $links.html(linksHtml);
            addLinkListener();
        });

    }


    function editLink(linkName) {
        $.ajax({
            url: base_url + "/api/links/" + linkName,
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(link) {
            $sectionone.hide();
            $sectiontwo.hide();
            $sectionthree.show();
            $("form#link-form").attr('data-link', linkName);
            setParentLink();
            $("form#link-form :input").each(function() {
                $(this).val(link[$(this).attr('name')]);
            });

        });
    }

    function setParentLink() {
        var $lastParent = $(".breadcrumb ul li:last-child a");
        if ($lastParent) {
            $("form#link-form").attr('data-parent', $lastParent.attr('data-parent'));
        }
        else {
            $("form#link-form").removeAttr('data-parent');
        }
    }



    function loadCategories() {
        $.ajax({
            url: base_url + "/api/linkcategories"
        }).done(function(categories) {
            var categoryOption = '';
            categories.forEach(function(category) {
                categoryOption += "<option value=" + category.name + " >" + category.label + "</option>";
            });
            categoryOption += "<option value='/'>Create New</option>";
            $category.html(categoryOption);
            loadLinksByCategory();
        });
    }

    loadCategories();

}(jQuery));