(function($) {
    var compiled = {};
    $.fn.handlebars = function(templateName, data) {
        var component = this;        
        compileTemplate = Handlebars.compile($('#'+templateName).html());
        component.append(compileTemplate(data));
        
    };
})(jQuery);