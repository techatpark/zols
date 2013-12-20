(function($) {
    $.fn.loadCategories = function() {
        $("#link_category").empty();
        $("#childLink").empty();
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
        $(this).loadLinks(linksOfCategory);
    };

    $.fn.loadLinksByParent = function(parentLink) {
        var _url = 'api/links/children/' + parentLink.attr('data-parent');
        var children = getData(_url, 'GET', 'json', '', 'application/json', false);
        $('.breadcrumb ul').append('<li><a href="javascript://" data-parent="'+parentLink.attr('data-parent')+'">'+parentLink.html()+'</a></li>');
        $(this).loadLinks(children);
        
    };


    $.fn.loadLinks = function(links) {
        $("#childLink").empty();
        if (links.length > 0) {
            $("#childLink").handlebars('childList', links);
            $("#childLink").append("<li><a href='javascript://' class='addMoreLink'>Add More</a></li>");
        } else {
            $("#childLink").append("<li><a href='javascript://' class='addMoreLink'>Add</a></li>");
        }

        $(".addMoreLink").click(function() {
            $(this).addLinkUnder();
        });
        $(".addChild").click(function() {
            $(this).addChild($(this).attr('data-parent'));
        });
        $(".editlink").click(function() {
            $(this).editLink($(this).attr('data-parent'));
        });
        $('.removeChild').click(function() {
            $(this).removeLink($(this));            
        });

        $(".parentLink").click(function() {
            $(this).loadLinksByParent($(this));
        });
        
        $('.breadcrumb a').click(function() {

            if($(this).parent().index() !== 0 ) {
                $(".breadcrumb li:gt("+($(this).parent().index()-1)+")").remove();
            }
            else {
                $(".breadcrumb ul").empty();
            }
            $(this).loadLinksByParent($(this));
            
        });
    };


    $.fn.editLink = function(parentLinkName) {
        window.location = 'links/edit/' + parentLinkName;
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

    $.fn.removeLink = function(linkToRemove) {
        $.ajax(
                {
                    url: 'api/links/' + linkToRemove.attr('data-parent'),
                    type: 'DELETE',
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(data, textStatus, jqXHR)
                    {
                        linkToRemove.parent().remove();
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