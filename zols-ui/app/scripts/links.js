'use strict';

(function($) {    
    var base_url = 'http://localhost:8080/api';
    var currentData;
    var template;

    $.fn.listFirstCategory = function() {
        $.get(base_url + '/link_categories').done(function(data) {
            var template = $.templates("#viewLink");
            var htmlOutput = template.render(data[0]);
            $("#result").html(htmlOutput);
        });
    };

    $.fn.changeHandler = function(ev, eventArgs) {
        console.log(currentData);
        var message = "The new '" + eventArgs.path + "' is '"
                + eventArgs.value + "'.";
    };
    $.fn.editFirstCategory = function() {
        $.get(base_url+'/link_categories').done(function(data) {
            template = $.templates("#editLink");
            currentData = data[0];
            console.log(currentData);
            template.link('#result', currentData);
            $(currentData).on("propertyChange", $.fn.changeHandler);
        });
    };

    $.fn.editCategory = function() {
        $.fn.editFirstCategory();
    };
    
    $.fn.listFirstCategory();


}(jQuery));


