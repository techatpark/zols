'use strict';

(function ($) {

    // Set an option globally
    JSONEditor.defaults.options.theme = 'bootstrap3';

    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
        contentType: 'application/json'
    });

    
    $.fn.showEditor = function (schemaName) {
        $.get(base_url + '/schema/' + schemaName).done(function (data) {
            $("#editor_holder").html("");
            var element = document.getElementById('editor_holder');
            editor = new JSONEditor(element, {schema: data, disable_properties: true, disable_collapse: true, disable_edit_json: true});
        });
    };

    $("#saveBtn").click(function (e) {
        var value = editor.getValue();
        console.log(value);
    });


   

    $.fn.showEditor('54841af344aebc43ee3b12f6');

}(jQuery));