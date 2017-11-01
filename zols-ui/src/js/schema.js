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
            screen_object.fillTypes();
            screen_object.sortProperties();
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
                        screen_object.setProperty("schemas", []).setProperty("title", "Schemas");
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
              newSchema['$ref'] = baseSchema.$id;
            }
            screen_object.is_edit = false;
            screen_object.setProperty("title", "Schema").setProperty("schema", screen_object.patchedSchema(newSchema));
            $(".alert").remove();
            $('#schema_title').off();
            $('#schema_title').on('input', function() {
                screen_object.schema.title = $('#schema_title').val();
                screen_object.schema.$id =$('#schema_title').val().replace(' ','_').toLowerCase();
                $('#schema_id').val(screen_object.schema.$id);
            }).focus();


        },
        sortProperties: function() {
          var patchedSchema = screen_object.schema;
          var properties = patchedSchema.properties;

          if(properties != undefined) {
            var keysSorted = Object.keys(properties).sort(function(a,b){return properties[a].propertyOrder-properties[b].propertyOrder})

            var $people = $('#schemaLayout'),
            $peopleli = $people.children('.panel-primary');

            $peopleli.sort(function(a,b){
                  var an = keysSorted.indexOf(a.getAttribute('data-name')),
                    bn = keysSorted.indexOf(b.getAttribute('data-name'));

                  if(an > bn) {
                    return 1;
                  }
                  if(an < bn) {
                    return -1;
                  }
                  return 0;
            });

            $peopleli.detach().appendTo($people);
          }


        },
        editSchema: function(schemaToEdit) {
            $.get(base_url + '/schema/' + schemaToEdit.$id)
                .done(function(data) {
                    screen_object.is_edit = true;

                    screen_object.setProperty("title", "Schema").setProperty("schema", screen_object.patchedSchema(data));
                    screen_object.setLabelFields();
                    $(".alert").remove();
                    $('#schema_title').off();

                });
        },
        setLabelFields: function() {
          var properties = screen_object.schema.properties;
          var label = screen_object.schema.label;
          if(properties != undefined) {
            for (var key in properties) {
              $('#schema-label')
             .append($("<option></option>")
                        .attr("value",key)
                        .text(properties[key].title));
            }
            $('#schema-label').val(label);

          }

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
            var totalProperties = Object.keys(this.schema.properties).length;
            var patchedProperty = this.patchedProperty({});
            patchedProperty.title = 'New Property' +  totalProperties;
            patchedProperty.propertyOrder = totalProperties+1;
            var propName = 'newProperty' + totalProperties;
            this.schema.properties[propName] = patchedProperty;

            screen_object.setProperty("title", "Schema");

            var name_txtbox = $("#schemaLayout div.panel:last-child input[name='name']");

            $("#schemaLayout div.panel:last-child input[name='title']").focus().select().on('input', function() {
              delete screen_object.schema.properties[name_txtbox.val()]
              var val = $(this).val().replace(' ','_').toLowerCase();
              name_txtbox.val(val);
              patchedProperty.title = $(this).val();
              screen_object.schema.properties[name_txtbox.val()] = patchedProperty;
            });
            screen_object.setLabelFields();
        },
        removeProperty: function(propName) {
            delete this.schema.properties[propName];
            screen_object.setProperty("title", "Schema");
            screen_object.setLabelFields();
        },
        patchedProperty: function(property) {
            return jQuery.extend(true, {
                'type': 'integer',
                'format': 'text',
                'items': {
                    'type': "string"
                },
                'required': false,
                'ids': false,
                'localized': false,
                'options': {
                    'wysiwyg': false,'lookup':''
                }
            }, property);
        },
        patchedSchema: function(schema) {
            var patchedSchema = jQuery.extend(true, {}, schema);

            var properties = patchedSchema.properties;

            var required = patchedSchema.required;

            var ids = patchedSchema.ids;

            var localized = patchedSchema.localized;



            for (var key in properties) {
                properties[key] = screen_object.patchedProperty(properties[key]);
                if (required) {
                    if (required.indexOf(key) > -1) {
                        properties[key].required = true;
                    }
                }
                if (localized) {
                    if (localized.indexOf(key) > -1) {
                        properties[key].localized = true;
                    }
                }

                if (ids) {
                    if (ids.indexOf(key) > -1) {
                        properties[key].ids = true;
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

            return patchedSchema;
        },
        fillTypes: function() {
            var options = '';
            for (var index in this.schemas) {
                var schemaElement = this.schemas[index];
                if (schemaElement.$id !== this.schema.$id) {
                    options += "<option value='" + schemaElement.$id + "'>" + schemaElement.title + "</option>";
                }
            }

            $("select[name='lookup']").each(function(i, obj) {
                $(obj).append(options);
                $(obj).val($(obj).attr('data-name'));
            });

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
                        url: base_url + '/schema/' + schema.$id,
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
            console.log(v4Schema);

            if (screen_object.is_edit) {
                $.ajax({
                    method: 'PUT',
                    url: base_url + '/schema/' + v4Schema.$id,
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


            if(!patchedSchema.idField) {
              delete patchedSchema.idField;
              delete patchedSchema.labelField;
            }
            var properties = patchedSchema.properties;

            var required = [];

            var ids = [];
            var localized = [];



            for (var key in properties) {
                //repair required
                if (properties[key].required) {
                    required.push(key);
                }
                delete properties[key].required;

                //repair ids
                if (properties[key].ids) {
                    ids.push(key);
                }
                delete properties[key].ids;

                //repair localized
                if (properties[key].localized) {
                    localized.push(key);
                }
                delete properties[key].localized;

                //trim unused
                if (properties[key].format ){
                  if(properties[key].format === 'text') {
                      delete properties[key].format;

                  }
                  if(properties[key].format === 'html') {
                      properties[key].options.wysiwyg = true;
                  }
                }

                if(properties[key].options) {
                  if(!properties[key].options.wysiwyg) {
                    delete properties[key].options.wysiwyg;
                  }
                  if(properties[key].options.lookup == "") {
                    delete properties[key].options.lookup;
                  }

                  if(jQuery.isEmptyObject(properties[key].options)) {
                    delete properties[key].options;
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

            //PATHCH for Enum Binding

            for (var key in properties) {
              if(properties[key].enum) {
                properties[key].enum =[];
              }
            }
            $( "input[data-enum-name]" ).each(function( index, element ) {
              var key = $(element).attr('data-enum-name');
              patchedSchema.properties[key].enum[index] = $(element).val();
            });

            if (required.length !== 0) {
                patchedSchema.required = required;
            }

            if (ids.length !== 0) {
                patchedSchema.ids = ids;
            }

            if (localized.length !== 0) {
                patchedSchema.localized = localized;
            }

            return patchedSchema;
        }

    };

    screen_object.listSchemas();



}(jQuery));
