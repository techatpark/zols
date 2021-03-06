'use strict';
(function($) {

    $.fn.setLocale = function() {

        var locale = $.fn.getParameter("lang");

        if (locale != undefined) {
            if (locale === "" || locale === "en") {
                localStorage.removeItem("locale");
                $(".nav>.dropdown>.dropdown-menu>li[name='en']").remove();
            } else {
                localStorage.setItem("locale", locale);
                $(".nav>.dropdown>.dropdown-toggle").html($(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']>a").html() + '<b class="caret"></b>');
                $(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']").remove();
            }
        } else {
            locale = localStorage.getItem("locale");
            if (locale == undefined) {
                locale = 'en';
            }
            $(".nav>.dropdown>.dropdown-toggle").html($(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']>a").html() + '<b class="caret"></b>');
            $(".nav>.dropdown>.dropdown-menu>li[name='" + locale + "']").remove();
        }

        if (localStorage.getItem("locale") && localStorage.getItem("locale") != 'en') {
            $.ajaxSetup({
                contentType: 'application/json',
                headers: {
                    'Accept-Language': localStorage.getItem("locale")
                }
            });
        } else {
            $.ajaxSetup({
                contentType: 'application/json'
            });
        }




    }

    $.fn.getParameter = function(name) {
        if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search))
            return decodeURIComponent(name[1]);
    }

    $.fn.setLocale();

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

    $(".nav>.dropdown>.dropdown-menu>li").on('click', function() {
        if ($(this).attr('name') === "en") {
            $.fn.setParameter("lang", "")
        } else {
            $.fn.setParameter("lang", $(this).attr('name'))
        }

    });

    var search_schema = localStorage.getItem("search_schema");

    if (search_schema == undefined) {
        search_schema = 'asset';
        localStorage.setItem("search_schema", search_schema);
    }

    var $li = $(".search-panel>.dropdown-menu>li[name='" + search_schema + "']");

    $("#search_param").attr("placeholder", "Search " + search_schema);
    $("#search_concept>.glyphicon").first().attr("class", $li.find(".glyphicon").first().attr('class'));
    $(".search-panel>.dropdown-menu>li").show();
    $("#search_form").attr("action", "/browse/" + search_schema);
    $li.hide();

    $(".search-panel>.dropdown-menu>li").on('click', function() {
        var search_schema = $(this).attr('name');
        localStorage.setItem("search_schema", search_schema);
        $("#search_param").attr("placeholder", "Search " + search_schema);
        $("#search_concept>.glyphicon").first().attr("class", $(this).find(".glyphicon").first().attr('class'));
        $(".search-panel>.dropdown-menu>li").show();
        $("#search_form").attr("action", "/browse/" + search_schema);
        $(this).hide();
    });

    $("#fql_flag").click(function() {
      $("#search_param").val("").focus();
        if ($("#fql_flag").hasClass("fql-on")) {
            $("#fql_flag").removeClass("fql-on");
            $('#search_param').highlightWithinTextarea('destroy');
            $("#search_param").typeahead(ta);
        } else {
            $("#fql_flag").addClass("fql-on");
            $('#search_param').typeahead('destroy');
            $("#search_param").highlightWithinTextarea({
                highlight: [{
                        highlight: 'AND',
                        className: 'red'
                    },
                    {
                        highlight: ['status','codec','origin','title'],
                        className: 'blue'
                    },
                    {
                        highlight: '=',
                        className: 'yellow'
                    }
                ]
            });
        }
    });

    var ta = {
        source: function(value, callback) {

            $.getJSON("http://localhost:8080/api/data/" + localStorage.getItem("search_schema"), {
                q: value,
                size: 5

            }, function(data) {
                callback(data.content || [])
            })


        },
        displayText: function(item) {
            return item.title;
        },
        highlighter: function(text, item) {
            var html = $('<div><img src="' + item.imageUrl + '" alt="Hi"></div>');
            return html.append(document.createTextNode(item.title)).html();
        },
        autoSelect: false
    };

    $("#search_form").submit(function(event) {
        event.preventDefault();
        var search_schema = localStorage.getItem("search_schema");
        if ($("#fql_flag").hasClass("fql-on")) {
            var search_query = $("#search_param").val();;
            location.href = "/browse/" + search_schema + "?" + search_query.replace(new RegExp(' AND ', 'gi'), '&').replace(new RegExp(' = ', 'gi'), '=');
        } else {
            location.href = "/browse/" + search_schema + "?q=" + $("#search_param").val();
        }
    });

    var $input = $("#search_param");
    $input.typeahead(ta);
    $input.change(function() {
        var current = $input.typeahead("getActive");
        if (current) {
            // Some item from your model is active!
            if (current.title == $input.val()) {
                location.href = "/asset/" + current.id;

            } else {
                // This means it is only a partial match, you can either add a new item
                // or take the active if you don't want new items
            }
        } else {
            // Nothing is active so it is a new value (or maybe empty value)
        }
    });

}(jQuery));
