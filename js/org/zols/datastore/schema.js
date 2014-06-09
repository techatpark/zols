(function($) {

    var base_url = "http://localhost:8080/zols";
    
    $("#data-form").hide();

    $("#generateBtn").on('click', function() {
        var formData = $('#schema-form').toObject();
        console.log("Generating form for Schema ") ;
        console.log(formData) ;
        $('#data-form').makeform(formData,'../../../../js/makeform/theme/','simple');        
    });
    
    $("#showDataBtn").on('click', function() {
        var formData = $('#data-form').toObject();
        console.log("Retriving Dynamic Form Data ") ;
        console.log(formData) ;               
    });

    $("#schema-form input[data-role='property_name']").on('change', function() {
        var $textBox = $(this);
        var newName = $textBox.val();
        var oldName = $textBox.data('previous-value');
        $textBox.data('previous-value', newName);
        $("#schema-form *[name]").each(function(i, data) {
            var $element = $(this);
            var nameOfElement = $element.attr('name');
            var stringToFind = oldName + ".";
            var stringToReplace = newName + ".";
            if (nameOfElement.indexOf(stringToFind) !== -1) {
                var re = new RegExp(stringToFind, 'g');
                $element.attr('name',nameOfElement.replace(re, stringToReplace));                
            }            
        });
        
        $("#schema-form input[type='checkbox'][value='"+oldName+"']").val(newName);   
    });



}(jQuery));