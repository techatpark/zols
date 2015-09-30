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
    $('#groupnameLbl').hide();

    var template;
    var selectedGroup;
    var listOfCategories;
    var parentLinks = [];
    var selectedLink;
    var listOfLinks;

    var confirmationPromise;

    $('#edit_selected').on('click', function () {
        $.fn.renderGroup();
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
            $.fn.deleteSelectedGroup();
        });
    });

    $.fn.listCategories = function () {
        $.get(base_url + '/link_groups').done(function (data) {
            if (data.length === 0) {
                $('#groupHeader').hide();
                var template = $.templates("#noGroup");
                template.link('#result', {});
                $('#result a').click(function () {
                    $.fn.createGroup();
                });
                $('#linksBreadcrumb').empty();
                parentLinks = [];
            } else {
                $('#groupHeader').show();
                listOfCategories = data;
                var template = $.templates("#listGroup");
                template.link('#categories', {group: data});
                $('#createGroup').click(function () {
                    $.fn.createGroup();
                });
                $('#categories .catName').on('click', function () {
                    $.fn.setSelectedGroup($.view(this).data);
                });

                if (data.length > 0) {
                    $.fn.setSelectedGroup(data[0]);
                }
            }



        });
    };

    $.fn.setSelectedGroup = function (selectedGroupData) {
        parentLinks = [];
        $('[data-bind-col="groupname"]').text(selectedGroupData.label);
        selectedGroup = selectedGroupData;
        $('#linksBreadcrumb').empty();
        $.get(base_url + '/link_groups/' + selectedGroup.name + '/first_level_links').done(function (data) {
            $.fn.listLinks(data);
        });

    };
    $.fn.listLinks = function (listofLinks) {
        if (listofLinks.length === 0) {
            var template = $.templates("#noLink");
            template.link('#result', {});
            $('#result a').click(function () {
                $.fn.createLink();
            });
        } else {
            listOfLinks = {link: listofLinks};
            var template = $.templates("#listLink");
            template.link('#result', listOfLinks);
            $('#addMoreLinkBtn').on('click', function () {
                $.fn.createLink();
            });

            $('#result li a').on('click', function () {
                $.fn.addParentLink($.view(this).data);
            });

            $('#result .glyphicon-trash').on('click', function () {
                selectedLink = listOfLinks.link[$(this).parent().parent().index()];
                $("#delete-conf-model").modal('show');
                confirmationPromise = $.Deferred();
                confirmationPromise.done(function () {
                    $.fn.deleteLink();
                });
            });

            $('#result .glyphicon-edit').on('click', function () {
                selectedLink = listOfLinks.link[$(this).parent().parent().index()];
                $.fn.renderLink();
            });
        }




    };

    $.fn.addParentLink = function (ParentLinkData) {
        parentLinks.push(ParentLinkData);
        $.fn.listChildren(ParentLinkData);
    };

    $.fn.listChildren = function (ParentLinkData) {
        $.get(base_url + '/links/childen_of/' + ParentLinkData.name).done(function (data) {
            $.fn.listLinks(data);
        });
        template = $.templates("#breadcrumb");
        template.link('#linksBreadcrumb', {parentLink: parentLinks});
        $('#linksBreadcrumb a').on('click', function () {
            var selectedLinkIndex = $(this).parent().index();
            if (selectedLinkIndex === 0) {
                parentLinks = [];
                $.fn.refreshList();
            } else {
                var parentLinkData = parentLinks[selectedLinkIndex - 1];
                parentLinks = parentLinks.slice(0, selectedLinkIndex);
                $.fn.listChildren(parentLinkData);
            }

        });
    };

    $.fn.saveGroup = function () {
        if (selectedGroup.isEdit) {
            delete selectedGroup.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/link_groups/' + selectedGroup.name,
                dataType: 'json',
                data: JSON.stringify(selectedGroup)
            }).done(function (data) {
                $.fn.listCategories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }
        else {
            $.ajax({
                method: 'POST',
                url: base_url + '/link_groups',
                dataType: 'json',
                data: JSON.stringify(selectedGroup)
            }).done(function (data) {
                $.fn.listCategories();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };
    $.fn.deleteSelectedGroup = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/link_groups/' + selectedGroup.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.listCategories();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderGroup = function () {
        if (selectedGroup && selectedGroup.name) {
            selectedGroup.isEdit = true;
        }
        template = $.templates("#catetoryForm");
        template.link('#result', selectedGroup);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveGroup();
        });
        $('#groupHeader').hide();
        $('#pageTitle').text('Link Group');
    };


    $.fn.createGroup = function () {
        selectedGroup = {};
        $.fn.renderGroup();
    };

    $.fn.refreshList = function () {
        if (parentLinks.length !== 0) {
            $.fn.listChildren(parentLinks[parentLinks.length - 1]);
        }
        else if (!listOfCategories) {
            $.fn.listCategories();
        }
        else {
            $.fn.setSelectedGroup(selectedGroup);
        }

        $('#group-list').show();
        $('#groupnameLbl').hide();
        $('#groupHeader').show();

        $('#pageTitle').text('Links');

    };

    $.fn.saveLink = function () {
        selectedLink.groupName = selectedGroup.name;
        if (parentLinks.length !== 0) {
            selectedLink.parentLinkName = parentLinks[parentLinks.length - 1].name;
        }

        if (selectedLink.isEdit) {
            delete selectedLink.isEdit;
            $.ajax({
                method: 'PUT',
                url: base_url + '/links/' + selectedLink.name,
                dataType: 'json',
                data: JSON.stringify(selectedLink)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        } else {
            $.ajax({
                method: 'POST',
                url: base_url + '/links',
                dataType: 'json',
                data: JSON.stringify(selectedLink)
            }).done(function (data) {
                $.fn.refreshList();
            }).error(function (data) {
                $.fn.onError(data);
            });
        }

    };


    $.fn.deleteLink = function () {
        $.ajax({
            method: 'DELETE',
            url: base_url + '/links/' + selectedLink.name,
            dataType: 'json'
        }).done(function (data) {
            $.fn.refreshList();
        }).error(function (data) {
            $.fn.onError(data);
        });
    };
    $.fn.renderLink = function () {
        if (selectedLink && selectedLink.name) {
            selectedLink.isEdit = true;
        }
        template = $.templates("#linkForm");
        template.link('#result', selectedLink);
        $("#result form").submit(function (event) {
            event.preventDefault();
            $.fn.saveLink();
        });
        $('#group-list').hide();
        $('#groupnameLbl').show();

        $('#pageTitle').text('Link');
    };

    $.fn.createLink = function () {
        selectedLink = {};
        $.fn.renderLink();
    };

    $.fn.onError = function (data) {
        $("#result").prepend('<div class="alert alert-danger"><a href="#" class="close" data-dismiss="alert">&times;</a><strong>Error ! </strong>There was a problem. Please contact admin</div>');
    };


    $.fn.listCategories();


}(jQuery));