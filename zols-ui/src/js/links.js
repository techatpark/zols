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

    var screen_object = {
        title: "Links",
        link_groups: [],
        link_group: {},
        links: [],
        link: {},
        parentLinks: [],
        is_edit: false,
        setProperty: function(propName, propValue) {
            $.observable(this).setProperty(propName, propValue);
            $.templates("#links_screen_template").link("#links_screen", this);
            return this;
        },
        showMessages: function(messages) {
            this.setProperty("messages", messages);
            $.templates("#alert_template").link("#alerts", this);
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
        listGroups: function() {
            $.get(base_url + '/link_groups')
                .done(function(data) {
                    if (Array.isArray(data) && data.length != 0) {
                        screen_object.setProperty("link_groups", data).setProperty("title", "Links");
                        screen_object.setLinkGroup(data[0]);
                    } else {
                        screen_object.setProperty("link_groups", []).setProperty("title", "Links");
                    }
                });
        },
        setLinkGroup: function(data) {
            screen_object.setProperty("link_group", data);
            this.parentLinks = [];
            screen_object.listLinks();
        },
        setParentLink: function(parent_link) {
            var indexOfParentLink = this.parentLinks.indexOf(parent_link);
            if (indexOfParentLink === -1) {
                this.parentLinks.push(parent_link);
            } else {
                this.parentLinks = this.parentLinks.slice(0, indexOfParentLink + 1);
            }
            this.listLinks();
        },
        addGroup: function() {
            screen_object.is_edit = false;
            screen_object.setProperty("title", "Link Group").setProperty("link_group", {});
        },
        addLink: function() {
            screen_object.setProperty("title", "Link").setProperty("link", {});
        },
        listLinks: function() {
            if (this.parentLinks.length === 0) {
                $.get(base_url + '/links/for/' + this.link_group.name)
                    .done(function(data) {
                        screen_object.setProperty("title", "Links").setProperty("links", data);
                    });
            } else {
                $.get(base_url + '/links/under/' + this.parentLinks[this.parentLinks.length - 1].name)
                    .done(function(data) {
                        screen_object.setProperty("title", "Links").setProperty("links", data);
                    });
            }

        },
        editGroup: function() {
            $.get(base_url + '/link_groups/' + this.link_group.name)
                .done(function(data) {
                    screen_object.is_edit = true;
                    screen_object.setProperty("title", "Link Group").setProperty("link_group", data);
                });
        },
        editlink: function(data) {
            $.get(base_url + '/links/' + data.name)
                .done(function(data) {
                    screen_object.is_edit = true;
                    screen_object.setProperty("title", "Link").setProperty("link", data);
                });       
        },
        removeGroup: function(link_group) {

            $("#confirmationModal .btn-primary")
                .unbind("click")
                .bind("click", function() {
                    $("#confirmationModal").modal('hide');
                    $.ajax({
                        method: 'DELETE',
                        url: base_url + '/link_groups/' + link_group.name,
                        dataType: 'json'
                    }).done(function(data) {

                        screen_object.listGroups();
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Group deleted successfully"
                        }]);
                    });

                });

            $("#confirmationModal").modal('show');


        },
        saveGroup: function() {
            if (document.forms["link_group_form"].checkValidity()) {
                if (screen_object.is_edit) {
                    $.ajax({
                        method: 'PUT',
                        url: base_url + '/link_groups/' + this.link_group.name,
                        dataType: 'json',
                        data: JSON.stringify(this.link_group)
                    }).done(function(data) {
                        screen_object.setLinkGroup(screen_object.link_group);
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Group saved successfully"
                        }]);
                    });
                } else {
                    $.ajax({
                        method: 'POST',
                        url: base_url + '/link_groups',
                        dataType: 'json',
                        data: JSON.stringify(this.link_group)
                    }).done(function(data) {
                        screen_object.listGroups();
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Group created successfully"
                        }]);
                    });
                }
            }

        },
        removelink: function(link) {
            $("#confirmationModal .btn-primary")
                .unbind("click")
                .bind("click", function() {
                    $("#confirmationModal").modal('hide');
                    $.ajax({
                        method: 'DELETE',
                        url: base_url + '/links/' + link.name,
                        dataType: 'json'
                    }).done(function(data) {
                        screen_object.listLinks();
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Link deleted successfully"
                        }]);
                    });
                });
            $("#confirmationModal").modal('show');
        },
        saveLink: function() {
            if (document.forms["link_form"].checkValidity()) {
                if (screen_object.is_edit) {
                    $.ajax({
                        method: 'PUT',
                        url: base_url + '/links/' + this.link.name,
                        dataType: 'json',
                        data: JSON.stringify(this.link)
                    }).done(function(data) {
                        screen_object.listLinks();
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Link saved successfully"
                        }]);
                    });
                } else {
                    if (this.parentLinks.length === 0) {
                        $.ajax({
                            method: 'POST',
                            url: base_url + '/links/for/' + this.link_group.name,
                            dataType: 'json',
                            data: JSON.stringify(this.link)
                        }).done(function(data) {
                            screen_object.listLinks();
                            screen_object.showMessages([{
                                type: "success",
                                "message": "Link created successfully"
                            }]);
                        });
                    } else {
                        $.ajax({
                            method: 'POST',
                            url: base_url + '/links/under/' + this.parentLinks[this.parentLinks.length - 1].name,
                            dataType: 'json',
                            data: JSON.stringify(this.link)
                        }).done(function(data) {
                            screen_object.listLinks();
                            screen_object.showMessages([{
                                type: "success",
                                "message": "Link created successfully"
                            }]);
                        });
                    }
                }

            }


        }

    };

    screen_object.listGroups();



}(jQuery));
