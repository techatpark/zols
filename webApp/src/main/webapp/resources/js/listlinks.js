(function($) {
    $.fn.loadCategories = function() {
        $("#link_category").empty();
        $.ajax({
            url: 'api/links/categories',
            type: 'GET',
            dataType: "json",
            contentType: 'application/json',
            success: function(data, textStatus, jqXHR)
            {
                var categoryOption = '';
                $.each(data.content, function(key, value) {
                    categoryOption += "<option value=" + value.name + " >" + value.label + "</option>";
                });
                $("#link_category").html(categoryOption);
                $(this).loadLinksByCategory();

            },
            error: function(jqXHR, textStatus, errorThrown)
            {
                return false;
                console.log(jqXHR + "===" + textStatus);
            }

        });
        
    };
    $.fn.loadLinksByCategory = function() {
        var _url = 'api/links/categories/' + $("#link_category").val();
        var linksOfCategory = getData(_url, 'GET', 'json', '', 'application/json', false);
        $("#childLink").empty();
        if (linksOfCategory.length > 0) {
            $("#childLink").handlebars('childList',linksOfCategory);
            $("#childLink").append("<li><a href='#'>Add More</a></li>");
        } else {
            $("#childLink").append("<li><a href='#'>Add</a></li>");
        }

        $("#childLink a:last-child").click(function() {
            $(this).addLinkUnder();
        });
        $(".addChild").click(function() {
            $(this).addChild($(this).attr('data-parent'));
        });

    };
    $.fn.addChild = function(parentLinkName) {
        window.location = 'links/addchild/' + parentLinkName;
    };
    $.fn.addLinkUnder = function() {
        window.location = 'links/addunder/' + $("#link_category").val();
    };
    $.fn.removeCategory = function() {
        $.ajax(
                {
                    url: 'api/links/categories/' + $("#link_category").val(),
                    type: 'DELETE',
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(data, textStatus, jqXHR)
                    {
                        $(this).loadCategories();
                    },
                    error: function(jqXHR, textStatus, errorThrown)
                    {
                        console.log(jqXHR + "===" + textStatus);
                    }
                });
    };


    $(this).loadCategories();

    $('#addCategoryBtn').click(function() {
        window.location = 'links/categories/add';
    });
    $('#removeCategoryBtn').click(function() {
        $(this).removeCategory();
    });

    $("#link_category").change(function() {
        $(this).loadLinksByCategory();
    });

})(jQuery);