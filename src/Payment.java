public interface Payment {
    boolean processPayment(double amount);
    String getPaymentDetails();
    
    public void payment();
}