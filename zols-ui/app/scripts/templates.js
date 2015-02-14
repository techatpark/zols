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

    $('[data-toggle="tooltip"]').tooltip();
    $('#templateRepositorynameLbl').hide();

    var template;
    var selectedTemplateRepository;
    var listOfCategories;
    var selectedTemplate;
    var listOfTemplates;

    var confirmationPromise;

    $('#edit_selected').on('click', function () {
        $.fn.renderTemplateRepository();
    });
    $("#del_conf_ok").on('click', function () {
        $("#delete-conf-model").modal('hide');
        confirmationPromise.resolve();
    });
    $("del_conf_cancel").on('click', function () {
        confirmationPromise.reject();
    });

    $('#delete_selected').on('click', function () {
        $("#delete-conf-model").modal('show');
        confirmationPromise = $.Deferred();
        confirmationPromise.done(function () {
            $.fn.deleteSelectedTemplateRepository();
        });
    });

    $.fn.listTemplateRepositories = function () {
        $.get(base_url + '/template_repositories').done(function (data) {
            if (data === "") {
                $('#templateRepositoryHeader').hide();
                var template = $.templates("#noTemplateRepository");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createTemplateRepository();
                });
            } else {
                $('#templateRepositoryHeader').show();
                listOfCategories = data;
                var template = $.templates("#listTemplateRepository");
                template.link('#categories', {templateRepository: data});
                $('#createTemplateRepository').click(function () {
                    $.fn.createTemplateRepository();
                });
                $('#categories .catName').on('click', function () {
                    $.fn.setSelectedTemplateRepository($.view(this).data);
                });

                if (data.length > 0) {
                    $.fn.setSelectedTemplateRepository(data[0]);
                }
            }

        });
    };

    $.fn.setSelectedTemplateRepository = function (selectedTemplateRepositoryData) {

        $('[data-bind-col="templateRepositoryname"]').text(selectedTemplateRepositoryData.label);
        selectedTemplateRepository = selectedTemplateRepositoryData;

        $.get(base_url + '/template_repositories/' + selectedTemplateRepository.name + '/first_level_templates').done(function (data) {
            $.fn.listTemplates(data);
        });

    };
    $.fn.listTemplates = function (listofTemplates) {
        if (listofTemplates === "") {
            var template = $.templates("#noTemplate");
            template.link('#result', {});
            $('#result a').click(function () {
                $.fn.createTemplate();
            });
        } else {
            listOfTemplates = {link: listofTemplates};
            var template = $.templates("#listTemplate");
            template.link('#result', listOfTemplates);
            $('#addMoreTemplateBtn').on('click', function () {
                $.fn.createTemplate();
            });

            $('#result li a').on('click', function () {
                $.fn.addParentTemplate($.view(this).data);
            });

            $('#result .glyphicon-trash').on('click', function () {
                selectedTemplate = listOfTemplates.link[$(this).parent().parent().index()];
                $("#delete-conf-model").modal('show');
                confirmationPromise = $.Deferred();
                confirmationPromise.done(function () {
                    $.fn.deleteTemplate();
                });
            });

            $('#result .glyphicon-edit').on('click', function () {
                selectedTemplate = listOfTemplates.link[$(this).parent().parent().index()];
                $.fn.renderTemplate();
            });
        }




    };



    $.fn.saveTemplateRepository = function () {
        if (selectedTemplateRepository.isEdit) {
            delete selectedTemplateRepository.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/template_repositories/' + selectedTemplateRepository.name,
                dataType: 'json',
                data: JSON.stringify(selectedTemplateRepository)
            }).done(function (data) {
                $.fn.listTemplateRepositories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }
        else {
            $.ajax({
                method: 'POST',
                url: base_url + '/template_repositories',
                dataType: 'json',
                data: JSON.stringify(selectedTemplateRepository)
            }).done(function (data) {
                $.fn.listTemplateRepositories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };
    $.fn.deleteSelectedTemplateRepository = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/template_repositories/' + selectedTemplateRepository.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.listTemplateRepositories();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderTemplateRepository = function () {
        if (selectedTemplateRepository && selectedTemplateRepository.name) {
            selectedTemplateRepository.isEdit = true;
        }
        template = $.templates("#repositoryForm");
        template.link('#result', selectedTemplateRepository);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveTemplateRepository();
        });
        $('#templateRepositoryHeader').hide();
        $('#pageTitle').text('Template Repository');
        $.fn.listTemplatePaths();
    };


    $.fn.createTemplateRepository = function () {
        $('#templateRepositoryHeader').hide();
        selectedTemplateRepository = {};
        template = $.templates("#getTemplateRepositoryType");
        template.link('#result', selectedTemplateRepository);

        $('#result a').click(function () {
            selectedTemplateRepository.type = $(this).attr('data-type');
            $.fn.renderTemplateRepository();
        });

        listOfCategories = null;
    };

    $.fn.refreshList = function () {
        if (!listOfCategories) {
            $.fn.listTemplateRepositories();
        }
        else {
            $.fn.setSelectedTemplateRepository(selectedTemplateRepository);
        }

        $('#templateRepository-list').show();
        $('#templateRepositorynameLbl').hide();
        $('#templateRepositoryHeader').show();
        $('#pageTitle').text('Templates');
    };

    $.fn.saveTemplate = function () {
        selectedTemplate.repositoryName = selectedTemplateRepository.name;

        if (selectedTemplate.isEdit) {
            delete selectedTemplate.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/templates/' + selectedTemplate.name,
                dataType: 'json',
                data: JSON.stringify(selectedTemplate)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        } else {
            $.ajax({
                method: 'POST',
                url: base_url + '/templates',
                dataType: 'json',
                data: JSON.stringify(selectedTemplate)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };


    $.fn.deleteTemplate = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/templates/' + selectedTemplate.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderTemplate = function () {
        if (selectedTemplate && selectedTemplate.name) {
            selectedTemplate.isEdit = true;
        }
        template = $.templates("#templateForm");
        template.link('#result', selectedTemplate);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveTemplate();
        });
        $('#templateRepository-list').hide();
        $('#templateRepositorynameLbl').show();
        $('#pageTitle').text('Template');
        
        $.fn.listTemplatePaths();

    };


    $.fn.listTemplatePaths = function () {
        $.get(base_url + '/template_repositories/'+selectedTemplateRepository.name+'/valid_templates').done(function (data) {
            $.each(data, function (i, d) {
                // You will need to alter the below to get the right values from your json object.  Guessing that d.id / d.modelName are columns in your carModels data
                $('#path').append('<option value="' + d.value + '">' + d.label + '</option>');
            });
            $('#path').val(selectedTemplate.path);
        });
        
    };

    $.fn.createTemplate = function () {
        selectedTemplate = {};
        $.fn.renderTemplate();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };

    $.fn.listTemplateRepositories();

}(jQuery));