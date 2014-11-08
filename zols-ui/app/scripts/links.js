'use strict';

(function($) {
    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
      contentType: 'application/json',
    })

    var template;

    var selectedCategory;
    var confirmationPromise;

    $('#edit_selected').on('click',function(){
      $.fn.editSelectedCategory();
    });
    $("#del_conf_ok").on('click', function(){
      $("#delete-conf-model").modal('hide');
      confirmationPromise.resolve();
    });
    $("del_conf_cancel").on('click', function(){
      confirmationPromise.reject();
    });

    $('#delete_selected').on('click',function(){
      $("#delete-conf-model").modal('show');
      confirmationPromise = $.Deferred();
      confirmationPromise.done(function(){
        $.fn.deleteSelectedCategory();
      });
    });

    $.fn.listFirstCategory = function() {
        $.get(base_url + '/link_categories').done(function(data) {
            var template = $.templates("#viewLink");
            template.link('#categories',{ category : data});
            $('#createCategory').click(function() {
              $.fn.createCategory();
            });
            if(data.length >0){
              $($('#category-list').find('button')[0].children[0]).text(data[0].name);
              selectedCategory = data[0];
              $.fn.editSelectedCategory();
            }
            $('#categories .catName').on('click',function(){
              $($(this).closest('.btn-group').find('button')[0].children[0]).text($.view(this).data.name);
              selectedCategory = $.view(this).data;
            })
        });
    };

    $.fn.saveCategory = function() {
      console.log(selectedCategory);
      $.ajax({
        method: 'POST',
        url: base_url + '/link_categories',
        dataType : 'json',
        data:  JSON.stringify(selectedCategory)
      }).done(function(data) {
        alert("Data Loaded: " + data);
      });
    };
    $.fn.deleteSelectedCategory = function() {
      $.ajax({
        method: 'DELETE',
        url: base_url + '/link_categories/'+ selectedCategory.name,
        dataType : 'json'
      }).done(function(data) {
        $.fn.listFirstCategory();
      });
    };
    $.fn.editSelectedCategory = function() {
        template = $.templates("#editLink");
        template.link('#result', selectedCategory);
    };

    $.fn.createCategory = function() {
        selectedCategory = {};
        template = $.templates("#editLink");
        template.link('#result', selectedCategory);
    };


    $.fn.listFirstCategory();


}(jQuery));


