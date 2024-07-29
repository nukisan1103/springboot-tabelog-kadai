$(function() {
    updateButtonState(); // 初期状態の設定

    $('#chkbox').click(function() {
        updateButtonState();
    });

	function updateButtonState() {
		if ($('#chkbox').prop('checked')) {
			$('#regist').removeClass('disabled');
		} else {
			$('#regist').addClass('disabled');
		}
	}
});