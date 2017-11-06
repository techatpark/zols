'use strict';

(function($) {


    var filters = {};
    $('#query-filters li.active').each(function() {
        if(filterName != "$type") {
          var filterName = $(this).children().first().text();
          var values = [];
          var index = 0;
          $(this).children().each(function() {
              if (index !== 0) {
                  values.push($(this).children().first().text());
              }
              index++;
          });
          filters[filterName] = values;
        }

    });
    console.log(filters);
    $('#query-filters .glyphicon-remove-circle').on("click", function(e) {
        removeFilter($(this).closest("li").children().first().text(), $(this).parent().prev().html());
    });
    $(".term-filters .panel-body ul li a").on("click", function(e) {
        setFilter($(this).parent().attr('name'), $(this).attr('name'));
    });

    function addFilter(name, value) {

        if (filters[name]) {
            if (filters[name].indexOf(value) === -1) {
                filters[name].push(value);
            }
        } else {
            filters[name] = [value];
        }
        openUpdatedFilterUrl();
    }

    function setFilter(name, value) {
        if(name != "$type") {
          filters[name] = value;
          openUpdatedFilterUrl();
        }

    }

    function removeFilter(name, value) {
        console.log('removefilter ' + name + ' == ' + value);
        var i = filters[name].indexOf(value);
        if (i !== -1) {
            filters[name].splice(i, 1);
            if (filters[name].length === 0) {
                delete filters[name];
            }
        }
        openUpdatedFilterUrl();
    }


    function openUpdatedFilterUrl() {
        var filterTexts = [];
        for (var filterName in filters) {
            if(filterName != "$type") {
              if (Object.prototype.toString.call(filters[filterName]) === '[object Array]') {
                  filterTexts.push(filterName + "=" + filters[filterName].join(','));
              } else {
                  filterTexts.push(filterName + "=" + filters[filterName]);
              }
            }

        }
        var filteredUrl;
        if (filterTexts.length !== 0) {
            filteredUrl = $("#features_items").attr('name') + '?' + filterTexts.join('&');
        } else {
            filteredUrl = $("#features_items").attr('name');
        }
        window.location = filteredUrl;
    }

    // With JQuery
    var sliders = $('.range-slider').slider({
        formatter: function(value) {
            return 'Current value: ' + value;
        }
    });
    if (sliders != undefined) {
        sliders.on('slideEnd', function(e) {
            setFilter($(this).parent().attr('name'), '[' + e.value[0] + '-' + e.value[1] + ']');
        });
    }


}(jQuery));
