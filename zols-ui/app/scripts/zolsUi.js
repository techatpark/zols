'use strict';


function listFirstCategory(){
  $.get('http://localhost:8080/api/link_categories').done(function(data){
    var template = $.templates("#viewLink");

    var htmlOutput = template.render(data[0]);

    $("#result").html(htmlOutput);

  });
}
var currentData;
var template;
var changeHandler = function (ev, eventArgs) {
  console.log(currentData);
    var message = "The new '" + eventArgs.path + "' is '"
    + eventArgs.value + "'.";
  }
function editFirstCategory(){
  $.get('http://localhost:8080/api/link_categories').done(function(data){
    template = $.templates("#editLink");

    currentData = data[0];
    console.log(currentData);
    template.link('#result', currentData);


    $(currentData).on("propertyChange", changeHandler); 
  });
}

function editCategory(){

  editFirstCategory();
}


