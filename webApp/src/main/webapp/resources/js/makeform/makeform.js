(function($) {

    var formDiv;

    $.fn.makeform = function(entityUrl, theme, themeUrl, onRender) {
        formDiv = this;
        this.themeUrl = themeUrl;
        this.theme = theme;
        this.onRender = onRender;
        this.entityUrl = entityUrl;
        this.prefix = [];
        formDiv.empty();
        formDiv.renderEntity(formDiv.attr('name'));
        formDiv.onRender();
        $('.jqte-test').jqte();

        return this;
    };

    $.fn.renderEntity = function(entityName) {

        $.ajax({
            url: formDiv.entityUrl.replace('{{name}}', entityName),
            success: function(result) {
                if (result.entity) {
                    entity = result.entity;

                    $.each(result.entity.attributes, function(k, attribute) {

                        control = formDiv.getControl(attribute);

                        console.log('control ' +control);
                        if (control) {
                            attribute.type = 'control-open';
                            controlOpen = formDiv.getControl(attribute);
                            attribute.type = 'control-close';
                            controlClose = formDiv.getControl(attribute);

                            formDiv.append(controlOpen);
                            formDiv.append(control);
                            formDiv.append(controlClose);
                        }
                        else {
                            formDiv.prefix.push(attribute.name);
                            formDiv.renderEntity(attribute.type);
                            formDiv.prefix.pop(attribute.name);
                        }
                    });
                }

            },
            async: false
        });
    };
    $.fn.getControl = function(attribute) {
        var template = this.loadTemplate(attribute.type);
        console.log('template ' + template);
        if (template) {
            var prefixString = formDiv.getPrefixString(attribute);
            if (prefixString) {
                attribute.name = prefixString + '.' + attribute.name;
            }
            return template(attribute);
        }
        return null;

    };

    $.fn.getPrefixString = function(attribute) {
        if (formDiv.prefix.length !== 0) {
            var prefixString = '';
            $.each(formDiv.prefix, function(index, prefix) {
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

        $.ajax({
            cache: true,
            url: formDiv.themeUrl + '/' + formDiv.theme + '/' + type + '.js',
            success: function(result) {
                console.log('result on temp' + result);
                template = Handlebars.compile(result);
            },
            dataType:'html',
            async: false
        });


        return template;

    };
}(jQuery));