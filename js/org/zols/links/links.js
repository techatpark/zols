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
        $.ajax(
                {
                    url: base_url + "/api/linkcategories",
                    type: 'POST',
                    data: JSON.stringify(formData),
                    dataType: "json",
                    contentType: 'application/json'
                }).done(function() {
            loadCategories();
            $sectiontwo.hide();
            $sectionone.show();
        });

        console.log(formData);
    });

    function createCategory() {
        $sectionone.hide();
        $sectiontwo.show();
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
        console.log(formData);
    });
    $("#cancelLink").on("click", function() {
        $sectionthree.hide();
        $sectionone.show();
    });



    $("#editBtn").on("click", function() {
        $.ajax({
            url: base_url + "/api/linkcategories/" + $category.val(),
            dataType: 'json',
            contentType: 'application/json'
        }).done(function(category) {
            $sectionone.hide();
            $sectiontwo.show();
            $("form#category-form :input").each(function() {
                $(this).val(category[$(this).attr('name')]);
            });
        });
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
        $("form#link-form :input").each(function() {
            $(this).val('');
        });
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

        $('.breadcrumb a').click(function() {
            var parentLinkName = $(this).attr('data-parent');
            if (parentLinkName === '/') {
                $(".breadcrumb ul").empty();
                loadLinksByCategory();
            }
            else {
                if ($(this).parent().index() !== 1) {
                    $(".breadcrumb li:gt(" + ($(this).parent().index() - 1) + ")").remove();
                    loadLinksByParent($(this).attr('data-parent'));
                }                             
            }
            
        });

        loadLinksByParent(linkName);
    }


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
            $("form#link-form :input").each(function() {
                $(this).val(link[$(this).attr('name')]);
            });
        });
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