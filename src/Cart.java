import java.io.*;
import java.util.*;

public class Cart {
//	private static final List<String> INVENTORY_HEADER = List.of("ID", "Name", "Stock", "Sold", "Price", "TotalSales");
    
	private static final String path_Inventory = "src/Inventory.csv";
    private static final String path_OrderItems = "src/order_items.csv";
	
	// Parallel lists for cart items
    private ArrayList<String> itemIds = new ArrayList<>();
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<Double> unitPrices = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();
    
    public boolean isEmpty() {
        return itemIds.isEmpty();
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(itemIds));
    }

    // Add item to cart
    public void addItem(String itemId, int quantity) throws IOException {
    	if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    	
    	String[] productData = findProductInInventory(itemId);
        if (productData == null) {
            throw new IllegalArgumentException("Product not found: " + itemId);
        }
        
        int stock = Integer.parseInt(productData[2]);
        if (stock < quantity) {
            throw new IllegalStateException(String.format("Insufficient stock for %s (Available: %d, Requested: %d)",
                    productData[1], stock, quantity));
        }
        
        itemIds.add(itemId);
        itemNames.add(productData[1]);
        unitPrices.add(Double.parseDouble(productData[4]));
        quantities.add(quantity);
    }

    public void removeItem(int index) {
        if (index < 0 || index >= itemIds.size()) {
        	throw new IndexOutOfBoundsException("Invalid item index: " + index);
        }	

        itemIds.remove(index);
        itemNames.remove(index);
        unitPrices.remove(index);
        quantities.remove(index);
    }

    public double getTotal() {
        double total = 0.0;
        for (int i = 0; i < itemIds.size(); i++) {
            total += unitPrices.get(i) * quantities.get(i);
        }
        return total;
    }

    public void displayCart() {
    	if (isEmpty()) {
            System.out.println("Your cart is empty");
            return;
        }

        System.out.println("\n==== Your Cart ====");
        System.out.printf("%-15s %-20s %-10s %-10s%n", 
            "Product ID", "Name", "Quantity", "Price");
        
        for (int i = 0; i < itemIds.size(); i++) {
            System.out.printf("%-15s %-20s %-10d RM%-9.2f%n",
                itemIds.get(i),
                itemNames.get(i),
                quantities.get(i),
                unitPrices.get(i));
        }
        System.out.printf("%nTOTAL: RM%.2f%n", getTotal());
    }

    public void checkout(Order order) throws IOException {
    	if (isEmpty()) {
            throw new IllegalStateException("Cannot checkout empty cart");
        }
    	
        validateStock();

        saveOrderItems(order.getOrderId());
        
        updateInventory();
        
        order.saveOrderToCSV(getTotal());
        
        clearCart();
    }

    private void saveOrderItems(String orderId) throws IOException {
        // Ensure directory exists
        new File(path_OrderItems).getParentFile().mkdirs();
        
        boolean needsHeader = !new File(path_OrderItems).exists() || 
                            new File(path_OrderItems).length() == 0;
        
        try (FileWriter writer = new FileWriter(path_OrderItems, true)) {
            // Write header if needed
            if (needsHeader) {
                writer.write("orderId,productId,quantity,unitPrice,totalPrice\n");
            }
            
            // Write each item
            for (int i = 0; i < itemIds.size(); i++) {
                double totalPrice = unitPrices.get(i) * quantities.get(i);
                writer.write(String.format("%s,%s,%d,%.2f,%.2f%n",
                    orderId,
                    itemIds.get(i),
                    quantities.get(i),
                    unitPrices.get(i),
                    totalPrice));
            }
        }
    }

    private void updateInventory() throws IOException {
        // Read all lines first
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path_Inventory))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        // Update in memory
        for (int i = 1; i < lines.size(); i++) { // Skip header
            String[] fields = lines.get(i).split(",");
            int itemIndex = itemIds.indexOf(fields[0]);
            if (itemIndex >= 0) {
                int stock = Integer.parseInt(fields[2]) - quantities.get(itemIndex);
                int sold = Integer.parseInt(fields[3]) + quantities.get(itemIndex);
                fields[2] = String.valueOf(stock);
                fields[3] = String.valueOf(sold);
                fields[5] = String.format("%.2f", Double.parseDouble(fields[4]) * sold);
                lines.set(i, String.join(",", fields));
            }
        }

        // Write back to original file
        try (FileWriter writer = new FileWriter(path_Inventory)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
    }

    public String[] findProductInInventory(String itemId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path_Inventory))) {
            reader.readLine(); // Skip header
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(itemId)) {
                    return data;
                }
            }
        }
        return null;
    }
    
    private void validateStock() throws IOException {
        for (int i = 0; i < itemIds.size(); i++) {
            String[] product = findProductInInventory(itemIds.get(i));
            if (product == null) {
                throw new IllegalStateException("Product " + itemIds.get(i) + " no longer available");
            }
            
            int stock = Integer.parseInt(product[2]);
            if (stock < quantities.get(i)) {
                throw new IllegalStateException(String.format(
                    "Insufficient stock for %s (Available: %d, Requested: %d)",
                    itemNames.get(i), stock, quantities.get(i)));
            }
        }
	}
    
    public void clearCart() {
        itemIds.clear();
        itemNames.clear();
        unitPrices.clear();
        quantities.clear();
    }
}