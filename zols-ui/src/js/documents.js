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
            documents: [],
            document_paths: [],
            document_repository: [],
            messages: [],
            is_edit: false,
            listRepositories: function() {
                var screen_obj = this;
                $.get(base_url + '/document_repositories')
                    .done(function(data) {

                        if (data.length === 0) {
                            $.templates("#empty_document_repositories_template").link("#result", documents_screen);
                        } else {
                            $.observable(screen_obj).setProperty("document_repositories", data);
                            $.templates("#document_repositories_template").link("#panel-aside", documents_screen);
                            if (data && data.length != 0) {
                                screen_obj.setSelectedRepository(data[0]);
                            }
                        }
                    });
            },
            listDocuments: function(document_repository,document) {
                var screen_obj = this;
                var url;
                if(document === undefined) {
                  url =  base_url + '/documents/' +document_repository.name;
                } else {
                  url =  base_url + '/documents/' +document.repositoryName + document.path;
                }
                $.get(url)
                    .done(function(data) {
                      if(document != undefined && screen_obj.document_paths.indexOf(document) == -1) {
                        screen_obj.document_paths.push(document);
                        $.observable(screen_obj).setProperty("document_paths", screen_obj.document_paths);
                        $.templates("#breadcrumb_template").link("#breadcrumb", documents_screen);
                      }
                        if (data.length === 0) {
                            $.templates("#empty_documents_template").link("#result", documents_screen);
                        } else {
                            $.observable(screen_obj).setProperty("documents", data);
                            $.templates("#documents_template").link("#result", documents_screen);
                        }
                    });
            },
            setSelectedRepository: function(data) {
                $.observable(this).setProperty("document_repository", data);
                $.observable(this).setProperty("document_paths", []);
                $.templates("#breadcrumb_template").link("#breadcrumb", documents_screen);
                $.templates("#document_repositories_template").link("#panel-aside", documents_screen);
                this.listDocuments(data);
            },
            navigateTo: function(folder_document){
              var indexOfFolder = this.document_paths.indexOf(folder_document);
              this.document_paths = this.document_paths.slice(0,indexOfFolder+1);
              $.observable(this).setProperty("document_paths", this.document_paths);
              $.templates("#breadcrumb_template").link("#breadcrumb", documents_screen);
              this.listDocuments(undefined,folder_document);

            },
            showMessages: function(messages) {
                $.observable(this).setProperty("messages", messages);
                $.templates("#alert_template").link("#alerts", documents_screen);
            },
            getErrors: function(errResponse) {
                var messages = [];
                errResponse.responseJSON.errors.forEach((item, index, arr) => {
                    messages.push({
                        type: "warning",
                        "message": '[' + item.field + '] - ' + item.defaultMessage
                    });
                });
                return messages;
            },
            selectRepository: function() {
                $.observable(this).setProperty("document_repository", {});
                $.templates("#select_document_repository_type_template").link("#result", documents_screen);
            },
            addRepository: function(type) {
                var screen_obj = this;
                screen_obj.is_edit = false;
                $.observable(screen_obj).setProperty("document_repository", {type:type});
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
                            screen_obj.showMessages([{
                                type: "success",
                                "message": "repository deleted successfully"
                            }]);
                            screen_obj.listRepositories();
                        });
                    });
            },
            removeDocument: function(document) {
              var screen_obj = this;
              $("#confirmationModal .btn-primary")
                  .unbind("click")
                  .bind("click", function() {
                      $("#confirmationModal").modal('hide');
                      $.ajax({
                          method: 'DELETE',
                          url: base_url + '/documents/' + document.repositoryName + document.path,
                          dataType: 'json'
                      }).done(function(data) {
                          screen_obj.showMessages([{
                              type: "success",
                              "message": "document deleted successfully"
                          }]);
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
                        screen_obj.showMessages([{
                            type: "info",
                            "message": "repository updated  successfully"
                        }]);
                        screen_obj.listRepositories();
                    }).error(function(errorResponse) {
                        screen_obj.showMessages(screen_obj.getErrors(errorResponse));
                    });
                } else {
                    $.ajax({
                        method: 'POST',
                        url: base_url + '/document_repositories',
                        dataType: 'json',
                        data: JSON.stringify(document_repository)
                    }).done(function(data) {
                        screen_obj.showMessages([{
                            type: "info",
                            "message": "repository created successfully"
                        }]);
                        screen_obj.listRepositories();
                    }).error(function(errorResponse) {
                        screen_obj.showMessages(screen_obj.getErrors(errorResponse));
                    });
                }


            }
        },
        cnt = 1;






    documents_screen.listRepositories();



}(jQuery));
