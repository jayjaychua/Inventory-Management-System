// WalkInUser class extending the abstract User class
public class WalkInUser extends User {

    // Constructor to initialize fields directly
    public WalkInUser(String uId, String uName, String uPhone) {
        super(uId, uName, uPhone);
    }
    
    // Default constructor for creating a blank user (fields can be set later)
    public WalkInUser() {
        super(); // Call default constructor of User
    }
    
    // Override method to identify this as a "Walk_In" user type
    @Override
    public String getUserType() {
        return "Walk_In";
    }
    
    // Authentication method for walk-in users (ID + phone number)
    @Override
    public boolean authenticate(String... credentials) {
        // Walk-in users authenticate with ID and phone number
        if (credentials.length < 2) return false;
        
        String inputId = credentials[0]; // First input is user ID
        String inputPhone = credentials[1]; // Second input is phone number
        
        // Return true if both match this user's data
        return this.uId.equals(inputId) && this.uPhone.equals(inputPhone);
    }
    
    // Convert WalkInUser data to a CSV line for file storage
    @Override
    public String toCSVString() {
        // Format: uId,Walk_In,N/A,uName,uPhone
        // N/A is used in place of a password since walk-in users don't use one
        return uId + ",Walk_In,N/A," + uName + "," + uPhone;
    }
    
    // Final step for walk-in registration (no password needed)
    @Override
    protected void completeRegistration() {
        // Walk-in users don't need additional data during registration
        // Password is set to N/A for walk-in users
        System.out.println("Walk-in user registration complete!");
    }
}