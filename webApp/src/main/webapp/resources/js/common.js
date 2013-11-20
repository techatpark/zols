function getData(URL, _type, _dataType, _contentType, _async) {
    var _data;
    var _response = $.ajax({
        url: URL,
        type: _type,
        dataType: _dataType,
        contentType: _contentType,
        async: _async,
        success: function(data, textStatus, jqXHR)
        {
            _data = data;

        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            console.log(jqXHR + "===" + textStatus);
        }
    });
    if (_response.status === 200) {
        return _data;
    }
}


