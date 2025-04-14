// OnlineUser class extending the abstract User class
public class OnlineUser extends User {
    // Additional field specific to OnlineUser for password
    private String uPassword;
    
    // Constructor that initializes all fields (called when registering or loading an OnlineUser)
    public OnlineUser(String uId, String uName, String uPhone, String uPassword) {
        super(uId, uName, uPhone);
        this.uPassword = uPassword;
    }
    
    // Default constructor
    public OnlineUser() {
        super(); // Call superclass default constructor
    } 
    
    // Additional getter and setter
    public String getUPassword() {
        return uPassword;
    }
    
    public void setPassword(String uPassword) {
        this.uPassword = uPassword;
    }
    
    public void setPassword() {
        System.out.println("Enter your password: ");
        this.uPassword = sc.nextLine();
    }
    
    // Override method to specify user type as "Online"
    @Override
    public String getUserType() {
        return "Online";
    }
    
    // Override authentication logic for OnlineUser
    @Override
    public boolean authenticate(String... credentials) {
        // Online users must provide 2 credentials: ID and password
        if (credentials.length < 2) return false;
        
        String inputId = credentials[0]; // Extract input ID
        String inputPassword = credentials[1]; // Extract input password
        
        // Check if both ID and password match this user's credentials
        return this.uId.equals(inputId) && this.uPassword.equals(inputPassword);
    }
    
    // Convert OnlineUser object to a CSV line format
    @Override
    public String toCSVString() {
        // Format: uId,Online,uPassword,uName,uPhone
        return uId + ",Online," + uPassword + "," + uName + "," + uPhone;
    }
    
    // Final step for online registration: prompt for password and complete message
    @Override
    protected void completeRegistration() {
        // Online users need to set a password
        System.out.print("Create your password: ");
        uPassword = sc.nextLine();
        System.out.println("Online user registration complete!");
    }
}