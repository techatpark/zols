'use strict';
(function ($) {

    var base_url = baseURL();

    function baseURL() {
        var url = 'http://localhost:8080/api';
        if (location.href.indexOf(":3000/") === -1) {
            var pathArray = location.href.split('/');
            url = pathArray[0] + '//' + pathArray[2] + '/api';
        }
        return url;
    }

    $.ajaxSetup({
        contentType: 'application/json'
    });
    
    $('#schemaHeader').hide();
    
    $('.carousel-inner a').on('click', function () {
        var schemaName = $(this).attr('name');
        $('#schemaHeader').show();
        $('#categorynameLbl').text(schemaName);
    });

}(jQuery));