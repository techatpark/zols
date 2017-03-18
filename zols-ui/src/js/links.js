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
      link:{},
      links:[],
      setProperty:function(propName,propValue) {
        $.observable(this).setProperty(propName, propValue);
        $.templates("#links_screen_template").link("#links_screen", this);
        return this;
      },
      listGroups: function() {
        $.get(base_url + '/link_groups')
            .done(function(data) {
                  if(Array.isArray(data) && data.length != 0) {
                      screen_object.setProperty("link_groups", data).setProperty("link_group", data[0]);

                  }
            });
      },
      setLinkGroup: function(data) {
        this.setProperty("link_group", data);
      }

    };

    screen_object.listGroups();



}(jQuery));
