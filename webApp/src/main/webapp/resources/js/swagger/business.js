var clientId = "your-client-id";
var realm = "your-realm";

$(function() {

    var docLocation = document.location.href.split('/businessapi')[0] + "/api-docs/business-api";
    window.swaggerUi = new SwaggerUi({
        url: docLocation,
        dom_id: "swagger-ui-container",
        onComplete: function(swaggerApi, swaggerUi) {
            if (typeof initOAuth == "function")
                initOAuth();
        },
        onFailure: function(data) {
            if (console) {
                console.log("Unable to Load SwaggerUI");
            }
        },
        docExpansion: "none"
    }
    );

    $('#input_apiKey').change(function() {
        var key = $('#input_apiKey')[0].value;
        console.log("key: " + key);
        if (key && key.trim() != "") {
            console.log("added key " + key);
            window.authorizations.add("key", new ApiKeyAuthorization("api_key", key, "query"));
        }
    })
    window.swaggerUi.load();
});
