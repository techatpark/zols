'use strict';

(function($) {
    var base_url = 'http://localhost:8080/api';

    $.ajaxSetup({
      contentType: 'application/json',
    })

    var template;

    var selectedCategory;
    var currentLinks;
    var listOfLinks;
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

    $.fn.listCategories = function() {
        $.get(base_url + '/link_categories').done(function(data) {
            var template = $.templates("#viewLink");
            template.link('#categories',{ category : data});
            $('#createCategory').click(function() {
              $.fn.createCategory();
            });
            if(data.length >0){
              $.fn.setSelectedCategory(data[0]);
            }
            $('#categories .catName').on('click',function(){
              $.fn.setSelectedCategory($.view(this).data);
            })
        });
    };
    
    $.fn.setSelectedCategory = function(selectedCategoryData) {
        $($('#category-list').find('button')[0].children[0]).text(selectedCategoryData.name);
    	selectedCategory = selectedCategoryData;
    	$.fn.listCategoryLinks();
    };
    $.fn.listCategoryLinks = function(){
    	  $.get(base_url + '/link_categories/'+selectedCategory.name+'/first_level_links').done(function(data) {
              var template = $.templates("#listLink");
              currentLinks = {link :data}
              
              template.link('#result',currentLinks);
              $('#result li a').on('click', function(){
            	  console.log( $.view(this).data);
            	  $.get(base_url + '/links/childen_of/'+$.view(this).data.name).done(function(data) {
            		  currentLinks = {link :data};
            		  template.link('#result',currentLinks);
            	  });
              });
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
        $.fn.listCategories();
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


    $.fn.listCategories();


}(jQuery));