String.prototype.replaceAt = function(i, char) {
    return this.substr(0, i) + char + this.substr(i);
};

$(document).ready(function() {

    function onRender() {

    }

    if (!$("#name").val()) {
        var contextpath = document.URL.substring(document.URL.indexOf('/',document.URL.indexOf('//')+2));
        contextpath = contextpath.substring(0,contextpath.indexOf('/',2));
        $.ajax(
                {
                    url: contextpath + '/api/templates',
                    type: 'GET',
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(data, textStatus, jqXHR)
                    {
                        $(".wrap").show();

                        var template;
                        if (data.content) {
                            for (index in data.content) {
                                template = data.content[index];
                                var box = $(".wrap").append('<div class="box"><div class="boxInner"><img src="../../..'+ contextpath + '/resources/css/images/icons/entities.svg" alt="' + template.name + '" /><div class="titleBox">' + template.label + '</div></div></div>');
                            }
                        }
                        $(".boxInner img").click(function() {
                            $("#templateName").val($(this).attr('alt'));
                            $("#dynamicForm").attr('name', template.dataType);
                            $("#dynamicForm").makeform(
                                    "../api/entities/{{name}}",
                                    "simple",
                                    "../resources/js/makeform/theme",
                                    onRender,
                                    null);
                            showEdit();
                        });


                    },
                    error: function(jqXHR, textStatus, errorThrown)
                    {
                        console.log(jqXHR + "===" + textStatus);
                    }
                });



    }
    else {
        showEdit();
    }

    function showEdit() {
        $(".wrap").hide();
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
        
        if(method === 'POST') {
            var createPageRequest = new Object();
            createPageRequest.page = $("#my-form").toObject({mode: 'first'});
            createPageRequest.data = $("#dynamicForm").toObject({mode: 'first'});
            $.ajax(
                {
                    url: pathname,
                    type: method,
                    data: JSON.stringify(createPageRequest),
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(data, textStatus, jqXHR)
                    {
                        listing_pathname = listing_pathname.replace('pages','page/'+data.name);
                        window.location.href = listing_pathname;

                    },
                    error: function(jqXHR, textStatus, errorThrown)
                    {
                        console.log(jqXHR + "===" + textStatus);
                    }
                });
        }
        else {
            $.ajax(
                {
                    url: pathname,
                    type: method,
                    data: JSON.stringify($("#my-form").toObject({mode: 'first'})),
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
        }
        
        e.preventDefault();	//STOP default action
    });

    $('#cancel').click(function() {
        window.location.href = listing_pathname;
    });



});