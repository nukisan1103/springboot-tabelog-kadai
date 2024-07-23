$(function() {
    $('#regist').attr('disabled', 'disabled'); 
        $('#chkbox').click(function() { 
        if ( $(this).prop('checked') == false ) {　
            $('#regist').attr('disabled', 'disabled');　
        } else {
            $('#regist').removeAttr('disabled');　
        }
    });
});