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
        title: "Schemas",
        schemas: [],
        schema: {},
        is_edit: false,
        JSON_TYPES: ['array', 'string', 'integer', 'number', 'boolean'],
        setProperty: function(propName, propValue) {
            $.observable(this).setProperty(propName, propValue);
            $.templates("#schema_screen_template").link("#main_screen", this);
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
        listSchemas: function() {
            $.get(base_url + '/schema')
                .done(function(data) {
                    if (Array.isArray(data) && data.length != 0) {
                        screen_object.setProperty("schemas", data).setProperty("title", "Schemas");
                    } else {
                        screen_object.setProperty("link_groups", []).setProperty("title", "Schemas");
                    }
                });
        },
        addSchema: function(baseSchema) {
            var newSchema = {
                'type': 'object',
                '$type': 'schema',
                properties: {}
            };
            if(baseSchema != undefined) {
              newSchema['$ref'] = baseSchema.name;
            }
            screen_object.setProperty("title", "Schema").setProperty("schema", screen_object.patchedSchema(newSchema));

        },
        editSchema: function(schemaToEdit) {
            $.get(base_url + '/schema/' + schemaToEdit.name)
                .done(function(data) {
                    screen_object.is_edit = true;
                    screen_object.setProperty("title", "Schema").setProperty("schema", screen_object.patchedSchema(data));
                    screen_object.fillTypes();

                });
        },
        addEnumValue: function(data) {
          if(data.prop.enum === undefined) {
            data.prop.enum = [];
          }
          data.prop.enum.push("VALUE"+data.prop.enum.length);
            screen_object.setProperty("title", "Schema");

        },
        removeEnumValue: function(data,index) {
          data.prop.enum.splice(index, 1);
          screen_object.setProperty("title", "Schema");

        },
        addProperty: function() {
            console.log('add prop');
            var totalProperties = Object.keys(this.schema.properties).length;
            this.schema.properties['newProperty' + totalProperties] = this.patchedProperty({});

            screen_object.setProperty("title", "Schema");
        },
        removeProperty: function(propName) {
            delete this.schema.properties[propName];
            screen_object.setProperty("title", "Schema");
        },
        patchedProperty: function(property) {
            return jQuery.extend(true, {
                'type': 'string',
                'format': 'text',
                'items': {
                    'type': "string"
                },
                'required': false,
                'options': {
                    'wysiwyg': false
                }
            }, property);
        },
        patchedSchema: function(schema) {
            var patchedSchema = jQuery.extend(true, {}, schema);

            var properties = patchedSchema.properties;

            var required = patchedSchema.required;



            for (var key in properties) {
                properties[key] = screen_object.patchedProperty(properties[key]);
                if (required) {
                    if (required.indexOf(key) > -1) {
                        properties[key].required = true;
                    }
                }
                if (properties[key]['$ref']) {
                    properties[key].type = properties[key]['$ref'];
                    delete properties[key]['$ref'];
                }

                if (properties[key].type === 'array' && properties[key].items['$ref'] != undefined) {
                    properties[key].items.type = properties[key].items['$ref'];
                    delete properties[key].items['$ref'];
                }

            }

            console.log(patchedSchema);
            return patchedSchema;
        },
        fillTypes: function() {
            var options = '';
            for (var index in this.schemas) {
                var schemaElement = this.schemas[index];
                if (schemaElement.name !== this.schema.name) {
                    options += "<option value='" + schemaElement.name + "'>" + schemaElement.title + "</option>";
                }
            }

            $("select[name='type']").each(function(i, obj) {
                $(obj).append(options);
                $(obj).val($(obj).attr('data-name'));
            });

            $("select[name='arraytypes']").each(function(i, obj) {
                $(obj).append(options);
                $(obj).val($(obj).attr('data-name'));
            });
        },
        removeSchema: function(schema) {

            $("#confirmationModal .btn-primary")
                .unbind("click")
                .bind("click", function() {
                    $("#confirmationModal").modal('hide');
                    $.ajax({
                        method: 'DELETE',
                        url: base_url + '/schema/' + schema.name,
                        dataType: 'json'
                    }).done(function(data) {

                        screen_object.listSchemas();
                        screen_object.showMessages([{
                            type: "success",
                            "message": "Schema deleted successfully"
                        }]);
                    });

                });

            $("#confirmationModal").modal('show');


        },
        saveSchema: function(schema) {
            var v4Schema = this.v4Schema(schema);
            if (screen_object.is_edit) {
                $.ajax({
                    method: 'PUT',
                    url: base_url + '/schema/' + v4Schema.name,
                    dataType: 'json',
                    data: JSON.stringify(v4Schema)
                }).done(function(data) {
                    screen_object.listSchemas();
                    screen_object.showMessages([{
                        type: "success",
                        "message": "Schema saved successfully"
                    }]);
                }).error(function(data) {

                });
            } else {

                $.ajax({
                    method: 'POST',
                    url: base_url + '/schema',
                    dataType: 'json',
                    data: JSON.stringify(v4Schema)
                }).done(function(data) {
                    screen_object.listSchemas();
                    screen_object.showMessages([{
                        type: "success",
                        "message": "Schema created successfully"
                    }]);
                }).error(function(data) {

                });

            }


        },
        v4Schema: function(schema) {
            var patchedSchema = jQuery.extend(true, {}, schema);

            var properties = patchedSchema.properties;

            var required = [];

            for (var key in properties) {
                //repair required
                if (properties[key].required) {
                    required.push(key);
                }
                delete properties[key].required;

                //trim unused
                if (properties[key].format ){
                  if(properties[key].format === 'text') {
                      delete properties[key].format;
                      delete properties[key].options;
                  }
                  if(properties[key].format === 'html') {

                      properties[key].options.wysiwyg = true;
                  }
                }

                if (properties[key].type) {
                    if (properties[key].type === 'array') {
                        if (this.JSON_TYPES.indexOf(properties[key].items.type) === -1) {
                            properties[key].items['$ref'] = properties[key].items.type;
                            delete properties[key].items.type;
                        }
                        delete properties[key].options;
                    } else {
                        delete properties[key].items;
                    }

                    if (this.JSON_TYPES.indexOf(properties[key].type) === -1) {
                        properties[key]['$ref'] = properties[key].type;
                        delete properties[key].type;
                    }

                }
            }

            if (required.length !== 0) {
                patchedSchema.required = required;
            }
            return patchedSchema;
        }

    };

    screen_object.listSchemas();



}(jQuery));
