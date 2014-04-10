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
                if (selectBox.attr('value')) {
                    selectBox.val(selectBox.attr('value'));
                }
            }
        });

        // Load From Data Store
        if (selectBox.is(':empty')) {
            $.ajax({
                url: '/zols/master/all/' + selectBox.attr('data-master'),
                success: function(result) {
                    $.each(result.content, function() {
                        selectBox.append($("<option />").val(this.name).text(this.name));
                    });
                    if (selectBox.attr('value')) {
                        selectBox.val(selectBox.attr('value'));
                    }
                },
                error: function(error) {
                    
                }
            });
        }

    };

}(jQuery));