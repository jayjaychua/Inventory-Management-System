import java.io.*;
import java.util.*;

public abstract class User {
    // Constants and Static Variables
    protected static final String PATH_USER = "user.csv"; // Points to the file where all user data (ID, name, phone, etc.) is stored.
    protected static final ArrayList<User> allUsers = new ArrayList<>(); // Holds all registered users in memory
    
    // Scanner for input
    protected Scanner sc = new Scanner(System.in);
    
    // Common User Information
    protected String uId;
    protected String uName;
    protected String uPhone;
    
    // References to other classes
    protected Order order = new Order();
    protected Item item = new Item();
    
    // Default constructor
    public User() {
    }
    
    // Constructor with common fields
    public User(String uId, String uName, String uPhone) {
        this.uId = uId;
        this.uName = uName;
        this.uPhone = uPhone;
    }
    
    // Common setters
    public void setUid() {
        System.out.print("Enter your ID: ");
        uId = sc.nextLine();
    }
    
    public void setName() {
        System.out.println("Enter your name: ");
        uName = sc.nextLine();
    }
    
    public void setPhone() {
        System.out.println("Enter your phone number: ");
        uPhone = sc.nextLine();
    }
    
    // Common getters
    public String getUId() {
        return uId;
    }
    
    public String getUName() {
        return uName;
    }
    
    public String getUPhone() {
        return uPhone;
    }
    
    // Abstract methods that subclasses must implement
    public abstract String getUserType();
    public abstract boolean authenticate(String... credentials);
    
    // Method to convert user data to CSV format
    public abstract String toCSVString();
    
    // Static method to create appropriate user type (Create the right subclass based on uType)
    public static User createUser(String uId, String uType, String uPassword, String uName, String uPhone) {
        // If user type is "Walk_In", create a WalkInUser object
        if (uType.equals("Walk_In")) {
            return new WalkInUser(uId, uName, uPhone);
        } 
        // If user type is "Online", create an OnlineUser object with password
        else if (uType.equals("Online")) {
            return new OnlineUser(uId, uName, uPhone, uPassword);
        } 
        // If user type is "Admin", create an AdminUser object (name and phone not needed)
        else if (uType.equals("Admin")) {
            return new AdminUser(uId, uPassword);
        }
        // If user type is unknown, return null
        return null;
    }
    
    // Load all users from CSV (Read the user.csv file and populates the allUser list)
    public static void loadCustomersFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATH_USER))) {
            String line;
            // Read each line from the CSV file
            while ((line = reader.readLine()) != null) {
                // Split line by commas into data fields
                String[] data = line.split(",");
                // Ensure the line has at least 5 elements (to avoid errors)
                if (data.length >= 5) {
                        String uId = data[0];         // User ID
                        String uType = data[1];       // User type (Walk_In, Online, Admin)
                        String uPassword = data[2];   // Password
                        String uName = data[3];       // Name
                        String uPhone = data[4];      // Phone number
                    
                    // Create user object based on type and add to list
                    User user = createUser(uId, uType, uPassword, uName, uPhone);
                    if (user != null) {
                        allUsers.add(user);
                    }
                }
            }
        } catch (IOException e) {
            // Handle error if file cannot be read
            System.out.println("Error loading customer data: " + e.getMessage());
        }
    }
    
    // Save new user to CSV (Appends the current user's data into the csv file)
    public void saveCustomerToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_USER, true))) {
            writer.write(this.toCSVString() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving customer data: " + e.getMessage());
        }
    }
    
    // Check if user ID already exists
    protected static boolean isUserIdTaken(String userId) {
        // Loop through all users to check for a matching ID
        for (User user : allUsers) {
            if (user.getUId().equals(userId)) {
                return true; // ID is already taken
            }
        }
        return false; // ID is available
    }
    
    // Registration method - common for all users
    public void userRegistration() {
        System.out.println("\n=== User Registration ===");
        
        // Get user ID first and check if it's already taken
        System.out.print("Create your ID: ");
        uId = sc.nextLine();
        
        // Check if ID is already taken
        if (isUserIdTaken(uId)) {
            System.out.println("User ID already exists. Please try again with a different ID.");
            return;
        }
        
        // Set common fields
        System.out.print("Create your name: ");
        uName = sc.nextLine();
        System.out.print("Enter your phone number: ");
        uPhone = sc.nextLine();
        
        // Each subclass will complete its own registration by overriding completeRegistration()
        completeRegistration();
        
        // Add to allUsers list and save to CSV
        allUsers.add(this);
        saveCustomerToCSV();
        System.out.println("Registration successful! Welcome, " + uName + "!");
    }
    
    // Abstract method to be implemented by subclasses to complete registration
    protected abstract void completeRegistration();
    
    // Main login method
    public static void userLogin() {
        Scanner sc = new Scanner(System.in);

        // Maximum number of login attempts
        int attempts = 3;
        // Track if the user successfully logs in
        boolean loggedIn = false;
        
        System.out.println("\n=== Customer Login ===");
        System.out.println("Please enter your type: ");
        System.out.println("1. Walk-In");
        System.out.println("2. Online");
        System.out.print("Enter Choice: ");
        
        int typeChoice;
        try {
            // Read user choice (1 or 2)
            typeChoice = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            // Handle non-integer input
            System.out.println("Invalid input! Please enter a number.");
            sc.nextLine(); // Clear invalid input from Scanner
            return; // Exit method and return to menu
        }
        
        // Declare a variable to store the selected user type
        String userType;
        switch(typeChoice) {
            case 1:
                userType = "Walk_In";
                break;
            case 2:
                userType = "Online";
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu...");
                return;
        }
        
        // Start login attempts loop
        while (!loggedIn && attempts > 0) {
            System.out.println("\n=== Attempting Login (" + attempts + " left) ===");
            
            // Prompt for user ID
            System.out.print("Enter your ID: ");
            String inputId = sc.nextLine();
            
            // Different login process based on user type
            if (userType.equals("Walk_In")) {
                System.out.print("Enter your phone number: ");
                String inputPhone = sc.nextLine();
                
                // Loop through all users to find a matching Walk-In user
                for (User user : allUsers) {
                    if (user instanceof WalkInUser && 
                        user.authenticate(inputId, inputPhone)) {
                        System.out.println("Login Successful!");
                        System.out.println("Welcome " + user.getUName() + "!");
                        loggedIn = true;
                        user.customerMenu(); // Redirect to customer menu
                        return; // Exit after successful login
                    }
                }
            } else if (userType.equals("Online")) {
                // For Online users, ask for password
                System.out.print("Enter your password: ");
                String inputPass = sc.nextLine();
                
                // Loop through all users to find a matching Online user
                for (User user : allUsers) {
                    if (user instanceof OnlineUser && 
                        user.authenticate(inputId, inputPass)) {
                        System.out.println("Login Successful!");
                        System.out.println("Welcome " + user.getUName() + "!");
                        loggedIn = true; // Redirect to customer menu
                        user.customerMenu(); // Exit after successful login
                        return;
                    }
                }
            }
            
            // If no match found, reduce attempt count and show error
            attempts--;
            System.out.println("Invalid credentials. Please try again.");
        }
        
        // If all attempts used and still not logged in
        if (!loggedIn) {
            System.out.println("You have exceeded the maximum number of attempts. Returning to main menu...");
        }
    }
    
    // Customer menu - Displays options for customers and handles order process
    public void customerMenu() {
        boolean inMenu = true; // Control flag for the menu loop
        
        while (inMenu) {
            // Display the customer menu options
            System.out.println("\n=== Welcome to The Inventory management system! ===");
            System.out.println("1. Start Order");
            System.out.println("2. Order History");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                // Read the user's menu choice
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                // Handle invalid input (non-numeric)
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear invalid input
                continue; // Restart the loop
            }
            
            switch (choice) {
                case 1: // Start Order process
                    try {
                        // 1. Display the bakery menu to the customer
                        System.out.println("=== Welcome to Our Bakery ===");
                        item.menu();
                        
                        // 2. Initialize a new shopping cart
                        Cart cart = new Cart();
                        boolean ordering = true;
                        
                        // 3. Ordering loop - allows customer to add multiple items
                        while (ordering) {
                            System.out.print("\nEnter product ID to add to cart (or 'checkout' to finish): ");
                            String input = sc.nextLine().trim();
                            
                            if (input.equalsIgnoreCase("checkout")) {
                                // Exit ordering loop if customer inputs "checkout"
                                ordering = false;
                            } else {
                                try {
                                    // Get quantity for the selected product
                                    System.out.print("Enter quantity: ");
                                    int quantity = Integer.parseInt(sc.nextLine());
                                    
                                    // Add item to cart and display updated cart
                                    cart.addItem(input, quantity);
                                    System.out.println("Added to cart!");
                                    cart.displayCart();
                                } catch (Exception e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                            }
                        }
                        
                        // 4. Checkout process
                        if (!cart.getItems().isEmpty()) {
                            // Get customer information for the order
                            String phone = getUPhone();
                            String type = getUserType();

                            // Create new order with customer information
                            Order order = new Order(phone, type);
                            double totalAmount = cart.getTotal(); // Get cart total

                            System.out.println("\n=== Payment Options ===");
                            System.out.println("1. Cash");
                            System.out.println("2. Credit Card");
                            System.out.print("Select payment method (1 or 2): ");
                            
                            // Get payment method choice
                            int paymentChoice;
                            try {
                                paymentChoice = Integer.parseInt(sc.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Defaulting to cash payment.");
                                paymentChoice = 1; // Default to cash payment
                            }
                            
                            // Determine payment method based on choice
                            String paymentMethod = (paymentChoice == 1) ? "cash" : "card";

                            // Process the payment using the Order class method
                            boolean paymentSuccess = order.processPayment(totalAmount, paymentMethod, sc);
                            
                            if (paymentSuccess) {
                                order.completeOrder(); // Update order status to COMPLETED
                                cart.checkout(order);  // Finalize order and save to storage
                                
                                // Confirm order completion to customer
                                System.out.println("\nOrder completed! Your order ID is: " + order.getOrderId());
                                System.out.println("Thank you for your purchase!");
                            } else {
                                // If payment failed
                                System.out.println("\nPayment failed. Order has been cancelled.");
                                order.cancelOrder();
                            }
                        } else {
                            System.out.println("Your cart is empty. Goodbye!");
                        }
                        
                    } catch (Exception e) {
                        System.err.println("System error: " + e.getMessage());
                    } 
                    break;
                
                case 2: // View Order History
                    try {
                        // Display this customer's order history using their phone number
                        Order.displayCustomerHistory(this.getUPhone());
                    } catch (IOException e) {
                        System.out.println("Error accessing order history: " + e.getMessage());
                    }
                    break;
                
                case 0: // Exit the customer menu
                    System.out.println("Exiting...");
                    inMenu = false; // End the menu loop
                    break;
                
                default: // Handle invalid menu choices
                    System.out.println("Invalid Choice! Please try again.");
            }
        }
    }
    
    // Admin menu - only for Admin users
    public void adminMenu() {
        // Boolean to control the admin menu loop
        boolean adminInMenu = true;
        
        while (adminInMenu) {
            System.out.println("\n=== Welcome to Admin Menu ===");
            System.out.println("1. View Customer list");
            System.out.println("2. Inventory Management");
            System.out.println("3. Generate Sales Report");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                // Handle non-numeric input
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear invalid input
                continue; // Retry menu
            }
            
            // Handle admin choice using switch
            switch (choice) {
                case 1:
                    // Display registered customer list
                    System.out.println("\n=== Registered Customer List ===");
                    System.out.println("Total customers: " + (allUsers.size() - 1)); // Exclude admin
                    for (User user : allUsers) {
                        if (!(user instanceof AdminUser)) {
                            // Print user details if not an admin
                            System.out.println("ID: " + user.getUId() + 
                                " | Type: " + user.getUserType() + 
                                " | Name: " + user.getUName() + 
                                " | Phone: " + user.getUPhone());
                        }
                    }
                    System.out.println("\nPress Enter to continue...");
                    sc.nextLine(); // Wait for enter key
                    break;
                
                case 2:
                    // Go to inventory management section
                    handleInventoryManagement();
                    break;
                
                case 3:
                    // Generate and display sales report
                    try {
                        item.report();
                        System.out.println("\nPress Enter to continue...");
                        sc.nextLine();
                    } catch (IOException e) {
                        System.out.println("Error reading menu file: " + e.getMessage());
                        System.out.println("\nPress Enter to continue...");
                        sc.nextLine();
                    }
                    break;
                
                case 4:
                    // Exit admin menu
                    System.out.println("Exiting to main menu...");
                    adminInMenu = false;
                    break;
                
                default:
                    // Handle invalid option
                    System.out.println("Invalid Choice! Please try again.");
                    System.out.println("\nPress Enter to continue...");
                    sc.nextLine();
            }
        }
    }
    
    // Inventory management helper method
    private void handleInventoryManagement() {
        boolean inventoryMenu = true;
        
        while (inventoryMenu) {
            System.out.println("\n=== Inventory Management ===");
            System.out.println("1. View Inventory List");
            System.out.println("2. Add New Item");
            System.out.println("3. Remove Item");
            System.out.println("4. Restock Item");
            System.out.println("5. Edit Item Price");
            System.out.println("6. Check Low Stock Alert");
            System.out.println("7. Stock Prediction");
            System.out.println("0. Return to Admin Menu");
            System.out.print("Enter your choice: ");
            
            int invChoice;
            try {
                invChoice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine(); // Clear invalid input
                continue;
            }
            
            // If admin chooses to exit inventory menu
            if (invChoice == 0) {
                System.out.println("Returning to Admin Menu...");
                return; // Exit the method entirely
            }
            
            switch (invChoice) {
                case 1:
                    try {
                        item.report(); // Display current inventory
                    } catch (IOException e) {
                        System.out.println("Error displaying inventory: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        item.addItem(); // Add a new item to inventory
                        System.out.println("Item added successfully!");
                    } catch (IOException e) {
                        System.out.println("Error adding item: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        item.removeItem(); // Remove an item from inventory
                        System.out.println("Item removed successfully!");
                    } catch (IOException e) {
                        System.out.println("Error removing item: " + e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        item.restock(); // Restock an existing item
                        System.out.println("Item restocked successfully!");
                    } catch (IOException e) {
                        System.out.println("Error restocking item: " + e.getMessage());
                    }
                    break;
                case 5:
                    try {
                        item.editPrice(); // Edit the price of an item
                        System.out.println("Price updated successfully!");
                    } catch (IOException e) {
                        System.out.println("Error updating price: " + e.getMessage());
                    }
                    break;
                case 6:
                    try {
                        // Ask for threshold value
                        System.out.print("Enter stock threshold for alert: ");
                        int threshold = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        
                        item.lowStockAlert(threshold); // Call the new low stock alert method
                    } catch (Exception e) {
                        System.out.println("Error checking low stock: " + e.getMessage());
                    }
                    break;
                case 7:
                    try {
                        // Ask for number of days to predict
                        System.out.print("Enter number of days for prediction: ");
                        int days = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        
                        item.predictStock(days); // Call the new stock prediction method
                    } catch (Exception e) {
                        System.out.println("Error generating stock prediction: " + e.getMessage());
                    }
                    break;
                default:
                    // Handle invalid input
                    System.out.println("Invalid choice! Please try again.");
            }
            
            // Wait for enter key before refreshing inventory menu
            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
        }
    }
}