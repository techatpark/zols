(function($) {

    $.fn.makeform = function(formData, themeUrl, theme) {
        var makeform = $(this);
        var templates = new Object();
        
        var dummyProp = new Object();
        dummyProp.type = 'control-open';
        var controlOpenTemplate = loadTemplate(dummyProp);
        dummyProp.type = 'control-close';
        var controlCloseTemplate = loadTemplate(dummyProp);
        dummyProp.type = 'entity-open';
        var entityOpenTemplate = loadTemplate(dummyProp);
        dummyProp.type = 'entity-close';
        var entityCloseTemplate = loadTemplate(dummyProp);    
        
        makeform.empty();
        var properties = formData.properties;
        var property ;
        makeform.append(entityOpenTemplate(formData));
        for (var key in properties) {
            if (properties.hasOwnProperty(key)) {
                property = properties[key];                
                makeform.append(controlOpenTemplate(property));
                makeform.append(getControl(property));
                makeform.append(controlCloseTemplate(property));
            }
        }
        makeform.append(entityCloseTemplate(formData));
        makeform.show();

        function getControl(property) {
            var template = loadTemplate(property);

            if (template) {                
                return template(property);
            }
            return null;

        };

        function loadTemplate(property) {
            var type = property.type;
            var template = null;
            if (templates.hasOwnProperty(type)) {
                template = template = templates[type];
            } else {
                $.ajax({
                    cache: true,
                    url: themeUrl + '/' + theme + '/' + type + '.js',
                    success: function(result) {
                        templates[type] = Handlebars.compile(result);
                        template = templates[type];
                    },
                    dataType: 'text',
                    async: false
                });
            }
            return template;
        }


        return this;
    };




    $.fn.patchNameField = function() {
        if (firstEntity) {

            var nameattribute = new Object();
            nameattribute.name = "name";
            nameattribute.type = "String";
            nameattribute.description = "Name";
            nameattribute.label = "Name";
            if (dataObj) {
                nameattribute.value = dataObj.name;
            }

            namecontrol = getControl(nameattribute);

            nameattribute.type = 'control-open';
            namecontrolOpen = getControl(nameattribute);
            nameattribute.type = 'control-close';
            namecontrolClose = getControl(nameattribute);

            appendHtmlText(namecontrolOpen);
            appendHtmlText(namecontrol);
            appendHtmlText(namecontrolClose);

        }

    };

    $.fn.renderEntity = function(attribute) {
        var entityName = attribute.type;
        if (!attribute.isReference) {
            $.ajax({
                url: entityUrl.replace('{{name}}', entityName),
                success: function(result) {
                    if (result.entity) {

                        entity = result.entity;

                        var entityattribute = entity;
                        entityattribute.type = 'entity-open';
                        entitycontrolOpen = getControl(entityattribute);
                        appendHtmlText(entitycontrolOpen);


                        patchNameField();
                        firstEntity = false;

                        $.each(entity.attributes, function(k, attribute) {
                            if (dataObj) {
                                attribute.value = dataObj[attribute.name];

//                                var x = attribute.value.replace(" ", '-');
//                                console.log("valueew is::" + x);
                            }
                            control = getControl(attribute);
                            if (control) {
                                attribute.type = 'control-open';
                                controlOpen = getControl(attribute);
                                attribute.type = 'control-close';
                                controlClose = getControl(attribute);

                                appendHtmlText(controlOpen);
                                appendHtmlText(control);
                                appendHtmlText(controlClose);
                            }
                            else {
                                if (!attribute.isReference) {
                                    prefix.push(attribute.name);
                                }
                                renderEntity(attribute);
                                if (!attribute.isReference) {
                                    prefix.pop(attribute.name);
                                }

                            }
                        });

                        entityattribute.type = 'entity-close';
                        entitycontrolClose = getControl(entityattribute);
                        appendHtmlText(entitycontrolClose);
                    }

                },
                async: false
            });
        }
        else {
            attribute.master = attribute.type;
            attribute.type = 'Reference';
            control = getControl(attribute);
            attribute.type = 'control-open';
            controlOpen = getControl(attribute);
            attribute.type = 'control-close';
            controlClose = getControl(attribute);

            appendHtmlText(controlOpen);
            appendHtmlText(control);
            appendHtmlText(controlClose);
        }

    };

    $.fn.appendHtmlText = function(htmlText) {
        htmlText += htmlText;
    };
    $.fn.getControl = function(attribute) {
        var template = this.loadTemplate(attribute.type);

        if (template) {
            var prefixString = getPrefixString(attribute);
            if (prefixString) {
                attribute.name = prefixString + '.' + attribute.name;
            }
            return template(attribute);
        }
        return null;

    };

    $.fn.getPrefixString = function(attribute) {
        if (prefix.length !== 0) {
            var prefixString = '';
            $.each(prefix, function(index, prefix) {
                if (index !== 0) {
                    prefixString += '.';
                }
                prefixString += prefix;
            });
            return prefixString;
        }

        return null;

    };

    $.fn.loadTemplate = function(type) {
        var template = null;
        if (templates.hasOwnProperty(type)) {
            template = template = templates[type];
        } else {
            $.ajax({
                cache: true,
                url: themeUrl + '/' + theme + '/' + type + '.js',
                success: function(result) {
                    templates[type] = Handlebars.compile(result);
                    template = templates[type];
                },
                dataType: 'text',
                async: false
            });
        }
        return template;
    };
}(jQuery));