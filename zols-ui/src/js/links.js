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

    var screen_object = {
      title:"Links",
      link_group:{},
      link_groups:[],

      listGroups: function() {
        $.get(base_url + '/link_groups')
            .done(function(data) {

                  if(Array.isArray(data) && data.length != 0) {
                      $.observable(screen_object).setProperty("link_groups", data);
                      $.observable(screen_object).setProperty("link_group", data[0]);
                      $.templates("#links_screen_template").link("#links_screen", screen_object);
                  }

            });
      },
      setLinkGroup: function(data) {
        $.observable(this).setProperty("link_group", data);
      }

    };

    screen_object.listGroups();

    

}(jQuery));
