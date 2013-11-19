$(document).ready(function() {
    console.log(SERVER_URL + '/api/entities');
    var id = getUrlVars()["id"];
    var entityName = getUrlVars()["entity"];
    onChange(entityName, id);
});

function getUrlVars() {
    var vars = {};
    var parts = window.location.href.replace(/[#&]+([^=&]+)=([^&]*)/gi, function(m, key, value) {
        vars[key] = value;
    });
    return vars;
}

function onChange(entityName, id) {

    $.ajax(
            {
                url: SERVER_URL + '/api/entities/' + entityName,
                type: 'GET',
                dataType: "json",
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR)
                {
                    $('#dynamicForm').dynaForm('form_open', '');

                    $.each(data.entity.attributes, function(index, value) {
                        $('#dynamicForm').dynaForm(value.type, value);
                    });



                    $('#dynamicForm').dynaForm('submit', '');
                    $('#dynamicForm').dynaForm('form_close', '');
                    $('#dynamicForm').dynaRender();

                    $("#dynarene").submit(function(e) {
                        var jsonData = {};

                        jsonData = $("#dynarene").toObject({mode: 'first'});

                        if (id !== null && id !== "" && id !== undefined) {
                            jsonData.name = id;

                            onUpdateData(JSON.stringify(jsonData), entityName, id);
                        } else {
                            onPostData(JSON.stringify(jsonData), entityName);
                        }

                        e.preventDefault();
                    });


                    getData2Form(entityName, id);

                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });
}

function getData2Form(entityName, id) {

    $.ajax(
            {
                url: SERVER_URL + '/api/data/' + entityName,
                type: 'GET',
                dataType: "json",
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR)
                {
                    $.each(data.content, function(key, content) {
                        if (content.name === id) {
                            console.log('content ' + content);
                            js2form(document.getElementById('dynamicForm'), content);

                        }

                    });

                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });

}
function onPostData(jsonData, entityName) {
    $.ajax(
            {
                url: SERVER_URL + '/api/data/' + entityName,
                type: 'POST',
                dataType: "json",
                contentType: 'application/json',
                data: jsonData,
                success: function(data, textStatus, jqXHR)
                {
                    console.log(data);
                    window.location.href = "index.html";


                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });
}
function onUpdateData(jsonData, entityName, id) {

    $.ajax(
            {
                url: SERVER_URL + '/api/data/' + entityName + '/' + id,
                type: 'PUT',
                dataType: "json",
                contentType: 'application/json',
                data: jsonData,
                success: function(data, textStatus, jqXHR)
                {
                    //console.log(data);
                    window.location.href = "index.html";


                },
                error: function(jqXHR, textStatus, errorThrown)
                {
                    console.log(jqXHR + "===" + textStatus);
                }
            });
}


