(function($) {
    var compiled = {};
    $.fn.handlebars = function(template, data) {
        var component = this;
        $.ajax({
            url: '/core/templates/' + template ,
            async: false
        }).done(function(src) {
            component.append(Handlebars.compile(src)(data));
        });


    };
})(jQuery);