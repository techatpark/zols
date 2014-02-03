String.prototype.replaceAt = function(i, char) {
    return this.substr(0, i) + char + this.substr(i);
};

$(document).ready(function() {

    var pathname = window.location.pathname;
    var method = window.location.pathname;
    var listing_pathname;
    if (pathname.indexOf("/add") !== -1) {
        listing_pathname = pathname.substr(0, pathname.lastIndexOf('/add'));
        pathname = '../../api/data/' + $("#dynamicForm").attr('name');
        method = 'POST';
    }
    else {
        listing_pathname = pathname.substr(0, pathname.lastIndexOf('/'));
        pathname = '../../api/data/' + $("#dynamicForm").attr('name')+'/'+$("#dynamicForm").attr('title');
        method = 'PUT';
    }

    function onRender() {

    }
    if ($("#dynamicForm").attr("title")) {
        $.ajax({
            cache: true,
            url: "../../api/data/" + $("#dynamicForm").attr("name") + "/" + $("#dynamicForm").attr("title"),
            success: function(result) {
                $("#dynamicForm").makeform(
                        "../../api/entities/{{name}}",
                        "simple",
                        "../../resources/js/makeform/theme",
                        onRender,
                        JSON.parse(result));

            },
            dataType: 'html',
            async: false
        });

    } else {
        $("#dynamicForm").makeform(
                "../../api/entities/{{name}}",
                "simple",
                "../../resources/js/makeform/theme",
                onRender,
                null);
    }



    $('#cancel').click(function() {
        window.location.href = listing_pathname;
    });

    $("#submit").click(function() {
        $.ajax(
                {
                    url: pathname,
                    type: method,
                    data: JSON.stringify($("#dynamicForm").toObject({mode: 'first'})),
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(data, textStatus, jqXHR)
                    {
                        window.location.href = listing_pathname;
                    },
                    error: function(jqXHR, textStatus, errorThrown)
                    {
                        console.log(jqXHR + "===" + textStatus);
                    }
                });
    });

    function printJSON() {

    }
});
