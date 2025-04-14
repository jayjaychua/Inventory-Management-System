import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// AdminUser class inherits from the User abstract class
public class AdminUser extends User {
    
    // Field to store the admin's password
    private String adminPassword;
    
    // Static constants for default admin ID and password
    private static final String DEFAULT_ADMIN_ID = "Admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "123";
    
    // Constructor with parameters - used when creating a custom admin
    public AdminUser(String uId, String adminPassword) {
        super(uId, "Admin", "N/A");
        this.adminPassword = adminPassword;
    }
    
    // Default constructor - creates admin with default credentials
    public AdminUser() {
        super(DEFAULT_ADMIN_ID, "Admin", "N/A");
        this.adminPassword = DEFAULT_ADMIN_PASSWORD;
    }
    
    // Getter for adminPassword
    public String getAdminPassword() {
        return adminPassword;
    }
    
    // Setter for adminPassword
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    // Admin login method for console input login with attempts
    public void adminLogin() {
        int attempts = 3; // Number of login attempts
        boolean loggedIn = false;

        while (!loggedIn && attempts > 0) {
        System.out.println("\n=== Admin Login ===");
        System.out.println("Please enter your username: ");
        String inName = sc.nextLine();
        System.out.println("Please enter your password: ");
        String inPass = sc.nextLine();
        
        // Check credentials against default admin
        if (inName.equals(DEFAULT_ADMIN_ID) && inPass.equals(DEFAULT_ADMIN_PASSWORD)){
            System.out.println("Welcome " + inName + "!");
            loggedIn = true;
            adminMenu(); // Show admin menu upon successful login
         } else {
            attempts--; // Decrease remaining attempts
            System.out.println("Invalid username or password. Please try again. You have " + attempts + " attempts left.");  
         }
        }
        // If login fails after all attempts
        if (!loggedIn) {
            System.out.println("You have exceeded the maximum number of attempts. Exiting...");
        }
    }

    // Override method to return user type as "Admin"
    @Override
    public String getUserType() {
        return "Admin";
    }
    
    // Override authentication method for Admin - checks ID and password
    @Override
    public boolean authenticate(String... credentials) {
        // Admin authenticates with ID and password
        if (credentials.length < 2) return false;
        
        String inputId = credentials[0];
        String inputPassword = credentials[1];
        
        return this.uId.equals(inputId) && this.adminPassword.equals(inputPassword);
    }
    
    // Convert admin user info into CSV format string
    @Override
    public String toCSVString() {
        // Format: Admin,Admin,adminPassword,Admin,N/A
        return uId + ",Admin," + adminPassword + ",Admin,N/A";
    }
    
    // Called when registering a new admin (optional or extended use)
    @Override
    protected void completeRegistration() {
        // Admin registration would typically be handled differently
        // This method would only be used if creating additional admin accounts
        System.out.print("Create admin password: ");
        adminPassword = sc.nextLine();
        System.out.println("Admin user registration complete!");
    }
    
    // Static method to create default admin account if not present in allUsers
    public static void createDefaultAdmin() {
        boolean adminExists = false;
        
        // Loop through users to check for default admin
        for (User user : allUsers) {
            if (user instanceof AdminUser && user.getUId().equals(DEFAULT_ADMIN_ID)) {
                adminExists = true;
                break;
            }
        }
        
        // If not found, create and add to list + save to file
        if (!adminExists) {
            AdminUser admin = new AdminUser(); // Create default admin
            allUsers.add(admin); // Add to user list
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_USER, true))) {
                writer.write(admin.toCSVString() + "\n"); // Save to CSV
            } catch (IOException e) {
                System.out.println("Error saving admin data: " + e.getMessage());
            }
            System.out.println("Default admin account created.");
        }
    }
    
    // Prevent admin from accessing the customer menu
    @Override
    public void customerMenu() {
        System.out.println("Administrators cannot access the customer menu.");
        System.out.println("Please use the admin menu instead.");
        adminMenu();
    }
}