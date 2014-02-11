String.prototype.replaceAt = function(i, char) {
    return this.substr(0, i) + char + this.substr(i);
};

$(document).ready(function() {
    


    if (!$("#type").val()) {
        $(".wrap").show();
        $(".boxInner img").click(function() {
            $("#type").val($(this).attr('alt'));
            showEdit();
        });
        
    }
    else {
        showEdit();
    }

    function showEdit() {
        $(".wrap").hide();
        if ($("#type").val() !== 'ftp') {
            $("#host").parent().hide();
            $("#rootFolder").parent().hide();
            $("#userName").parent().hide();
            $("#password").parent().hide();
        }
        if ($("#type").val() === 'classpath') {
            $("#path").parent().hide();
        }
        $("#my-form").show();
    }

    var pathname = window.location.pathname;
    var method = window.location.pathname;
    var listing_pathname;
    if (pathname.indexOf("/add") !== -1) {
        listing_pathname = pathname.substr(0, pathname.lastIndexOf('/add'));
        pathname = pathname.substr(0, pathname.lastIndexOf('/add'));
        pathname = pathname.replace(/(\s*\/)(?![\s\S]*\/)([^:]*)$/, "/api/$2");
        method = 'POST';
    }
    else {
        listing_pathname = pathname.substr(0, pathname.lastIndexOf('/'));

        pathname = pathname.replaceAt(pathname.substr(0, pathname.lastIndexOf('/')).lastIndexOf('/'), '/api');
        method = 'PUT';
    }

    $("#my-form").submit(function(e)
    {
        console.log(JSON.stringify($("#my-form").toObject({mode: 'first'})));

        $.ajax(
                {
                    url: pathname,
                    type: method,
                    data: JSON.stringify($("#my-form").toObject({mode: 'first'})),
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
        e.preventDefault();	//STOP default action
    });

    $('#cancel').click(function() {
        window.location.href = listing_pathname;
    });

    $("#my-form").dropdownFiller();

});