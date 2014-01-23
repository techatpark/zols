(function($) {

    var formDiv;

    $.fn.dropdownFiller = function() {
        formDiv = this;        
        $('select').each(function(index, value) {
            if ($(this).is(':empty')) {
                var masterName = $(this).attr('data-master');
                if (masterName) {
                    formDiv.loadData($(this));
                }
            }
        });

        return this;
    };

    $.fn.loadData = function(selectBox) {

        $.ajax({
            url: '/zols/master/' + selectBox.attr('data-master'),
            success: function(result) {
                $.each(result, function() {
                    selectBox.append($("<option />").val(this.name).text(this.label));
                });
            }
        });



    };

}(jQuery));