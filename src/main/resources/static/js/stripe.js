


const stripe = Stripe('pk_test_51PMUvOP4bt7o7owHU0aOjKQLxXcQukLjrF7w97CscZ3G36kfjRxBLVs3SOK8mSZKSqWLedefOOLD9X4DV7cO3V1k00Lw0a7X99');
const paymentButton = document.querySelector('#regist');

paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});

function change() {
	const submitBtn = document.getElementById('regist');
	const checkboxes = document.querySelectorAll('input[name="kiyaku"]:checked');
	if (checkboxes.length === 0) {
		submitBtn.disabled = true;
	} else {
		submitBtn.disabled = false;
	}
}
