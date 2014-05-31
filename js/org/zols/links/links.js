(function($) {

    var base_url = "http://localhost:8080/zols";

    var $category = $("#category");
    var $links = $("#items>ul");

    $category.on("change", function() {
        loadLinksByCategory($category.val());
    });

    $links.on("click", function() {
        alert('link');
    });

    function loadLinksByCategory(categoryName) {
        if (categoryName !== '/') {
            $links.empty();
            $.ajax({
                url: base_url + "/api/links/categories/" + categoryName,
                dataType: 'json',
                contentType: 'application/json'
            }).done(function(links) {
                var linksHtml = '';
                links.forEach(function(link) {
                    linksHtml += "<li><a href='#'>Test</a></li>";
                });                
                $links.html(linksHtml);
            });
        }
        else {

        }
    }

    function loadCategories() {
        $.ajax({
            url: base_url + "/api/linkcategories"
        }).done(function(data) {
            var categoryOption = '';
            $.each(data.content, function(key, value) {
                categoryOption += "<option value=" + value.name + " >" + value.label + "</option>";
            });
            categoryOption += "<option value='/'>Create New</option>";
            $category.html(categoryOption);
        });
    }

    loadCategories();

}(jQuery));