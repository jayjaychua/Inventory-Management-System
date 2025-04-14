
public class PaymentCard implements Payment{
	private String cardNumber;
    private String expiryDate;
    private String cvv;
	

	@Override
	public void payment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean processPayment(double amount) {
		// Simulate credit card processing
        System.out.println("Processing credit card payment...");
        System.out.printf("Charging RM%.2f to card ending in %s\n", amount, cardNumber.substring(cardNumber.length() - 4));
        
        return true; // Assume payment always succeeds for this example
	}

	@Override
	public String getPaymentDetails() {
		return String.format("Credit Card ending in %s", cardNumber.substring(cardNumber.length() - 4));
	}

}
