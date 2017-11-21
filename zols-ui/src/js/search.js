'use strict';

(function($) {


    $(".terms-filter-group>li").on('click', function() {
        var filterName = $(this).attr('name');
        var filterValue = $(this).attr('value');
        var filterParameterValue = $.fn.getParameter(filterName);
        if(filterParameterValue == undefined) {
          $.fn.setParameter(filterName,filterValue);
        }
        else if(filterParameterValue.indexOf(filterValue) === -1){
          $.fn.setParameter(filterName,filterParameterValue+","+filterValue);
        }
    });

    $(".minmax-filter-group>input[type='range']").on('input', function() {
      var filterName = $(this).attr('name');
      var filterValue =  $(this).val();
      var filterParameterValue = $.fn.getParameter(filterName);

        $.fn.setParameter(filterName,"["+ filterValue +"-" + $(this).attr('max') + "]");
      
    });

    $.fn.getParameter = function(name) {
        if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
            return decodeURIComponent(name[1]);
    }

    $.fn.setParameter = function(key, value) {
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

        //this will reload the page, it's likely better to store this until finished
        document.location.search = kvp.join('&');
    }

}(jQuery));
