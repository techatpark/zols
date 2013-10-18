(function($) {
    var compiled = {};
    $.fn.handlebars = function(template, data) {
        var component = this;
        $.ajax({
            url: '/resources/templates/' + template + '.hbs',
            async: false
        }).done(function(src) {
            component.append(Handlebars.compile(src)(data));
        });



    };
})(jQuery);