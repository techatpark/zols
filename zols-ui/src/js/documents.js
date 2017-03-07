'use strict';

(function($) {
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

    var documents_screen = {
            document_repositories: [],
            document_repository: [],
            is_edit: false,
            listRepositories: function() {
                var screen_obj = this;
                $.get(base_url + '/document_repositories')
                    .done(function(data) {
                      $.observable(screen_obj).setProperty("document_repositories", data);
                      $.templates("#document_repositories_template").link("#result", documents_screen);
                    });
            },

            addRepository: function() {
                var screen_obj = this;
                screen_obj.is_edit = false;
                $.observable(screen_obj).setProperty("document_repository", {});
                $.templates("#document_repository_template").link("#result", documents_screen);
            },

            removeRepository: function(document_repository) {
                var screen_obj = this;


                $("#confirmationModal .btn-primary")
                    .unbind("click")
                    .bind("click", function() {
                        $("#confirmationModal").modal('hide');
                        $.ajax({
                            method: 'DELETE',
                            url: base_url + '/document_repositories/' + document_repository.name,
                            dataType: 'json'
                        }).done(function(data) {
                            screen_obj.listRepositories();
                        });
                    });





            },
            editRepositories: function(document_repository) {
                var screen_obj = this;
                $.get(base_url + '/document_repositories/' + document_repository.name)
                    .done(function(data) {
                        screen_obj.is_edit = true;
                        $.observable(screen_obj).setProperty("document_repository", data);
                        $.templates("#document_repository_template").link("#result", documents_screen);
                    });

            },
            saveRepository: function(document_repository) {
                var screen_obj = this;
                if (this.is_edit) {
                    $.ajax({
                        method: 'PUT',
                        url: base_url + '/document_repositories/' + document_repository.name,
                        dataType: 'json',
                        data: JSON.stringify(document_repository)
                    }).done(function(data) {
                        screen_obj.listRepositories();
                    })
                } else {
                    $.ajax({
                        method: 'POST',
                        url: base_url + '/document_repositories',
                        dataType: 'json',
                        data: JSON.stringify(document_repository)
                    }).done(function(data) {
                        screen_obj.listRepositories();
                    })
                }


            }
        },
        cnt = 1;






    documents_screen.listRepositories();

}(jQuery));
