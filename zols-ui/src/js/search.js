'use strict';

(function($) {


    $(".terms-filter-group>li").on('click', function() {

        var filterName = $(this).attr('name');
        var filterValue = $(this).attr('value');
        var filterParameterValue = $.fn.getParameter(filterName);
        if (filterParameterValue == undefined) {
            $.fn.setRequestParameter(filterName, filterValue);
        } else if (filterParameterValue.indexOf(filterValue) === -1) {
            $.fn.setRequestParameter(filterName, filterParameterValue + "," + filterValue);
        }
    });

    $(".minmax-filter-group>input[type='range']").on('input', function() {
        var filterName = $(this).attr('name');
        var filterValue = $(this).val();
        var filterParameterValue = $.fn.getParameter(filterName);

        $.fn.setRequestParameter(filterName, "[" + filterValue + "-" + $(this).attr('max') + "]");

    });

    $("#query-filters").find(".glyphicon-remove").on('click', function() {
        $.fn.removeParameter($(this).attr('name'));
    });

    $.fn.getParameter = function(name) {
        if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
            return decodeURIComponent(name[1]);
    }

    $.fn.removeParameter = function(key) {
        var
            param,
            params_arr = [],
            queryString = document.location.search;

        if (queryString !== "") {
            params_arr = queryString.substr(1).split('&');
            for (var i = params_arr.length - 1; i >= 0; i -= 1) {
                param = params_arr[i].split("=")[0];
                if (param === key) {
                    params_arr.splice(i, 1);
                }
            }
            document.location.search = params_arr.join("&");
        }


    }

    $.fn.setRequestParameter = function(key, value) {
        key = encodeURI(key);
        value = encodeURI(value);

        var kvp = document.location.search.substr(1).split('&');


        var i = kvp.length;


        var x;
        while (i--) {
            x = kvp[i].split('=');

            if (x[0] == key) {
                x[1] = value;
                kvp[i] = x.join('=');
                break;
            }
        }

        if (i < 0) {
            kvp[kvp.length] = [key, value].join('=');
        }

        var param ;
        for (var j = kvp.length - 1; j >= 0; j -= 1) {
            param = kvp[j].split("=")[0];
            if (param === 'page' || param === 'size') {
                kvp.splice(j, 1);
            }
        }

        
        //this will reload the page, it's likely better to store this until finished
        document.location.search = kvp.join('&');
    }

}(jQuery));
