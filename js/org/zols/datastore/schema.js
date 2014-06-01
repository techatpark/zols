(function($) {

    var base_url = "http://localhost:8080/zols";


    $("#submitCategory").on('click', function() {
        var formData = $('#schema-form').toObject();
        console.log(JSON.stringify(formData));
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