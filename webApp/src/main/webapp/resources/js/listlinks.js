(function($) {
    $.fn.loadCategories = function() {
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
                $("#link_category").change(function() {                    
                    $(this).loadLinksByCategory($("#link_category").val());
                });
            },
            error: function(jqXHR, textStatus, errorThrown)
            {
                return false;
                console.log(jqXHR + "===" + textStatus);
            }
        });
    };
    $.fn.loadLinksByCategory = function(categoryName) {
        var _url = 'api/links/categories/' + categoryName;
        var _response = getData(_url, 'GET', 'json', '', 'application/json', false);
        $("#childLink").empty();
        if (_response.length > 0) {
            dynamicChildList(_response);
        } else {
            $("#childLink").html("<li> No Record!</li>");
        }
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

})(jQuery);