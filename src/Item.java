//BufferedReader, FileReader, IOException
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Item {
	
	private String itemId, itemName;
	private int sold;
	private int stock;
	private double unitPrice, totalSales;
	
	//path to csv file
	String path = "src/Inventory.csv";
	
	//instantiating BufferedReader and Scanner 
	BufferedReader rr = null;
	BufferedWriter uw = null;
	Scanner input = new Scanner(System.in);

	
	//to store each line read
	String line = "";
	
	//ArrayLists used to copy the current data in CSV file
	ArrayList<String> copy = new ArrayList<>();
	ArrayList<String> update = new ArrayList<>();
	
	//display method for report
	public void report() throws IOException {
		System.out.println("\n=== Kooks Sales Report ===");
		try {
			//calling BufferedReader rr to read from CSV file
			rr = new BufferedReader(new FileReader(path));
			rr.readLine();
				
			  // CLI formatting
			  System.out.printf("%-8s %-22s %-10s %-10s %-10s %-15s%n", 
			            "ID", "Item Name", "Price", "Stock", "Sold", "Total Sales");
			        System.out.println("-------------------------------------------------------------------------------");

		    String line;
            while ((line = rr.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    System.out.printf("%-8s %-22s %-10.2f %-10s %-10s %-15.2f\n", 
                    		parts[0], 
                    		parts[1], 
                    		Double.parseDouble(parts[4]), 
                    		parts[2], 
                    		parts[3].isEmpty() ? "0" : parts[3],  
                    		parts[5].isEmpty() ? 0 : Double.parseDouble(parts[5]));
				}
				
			}
				
		} catch(Exception e) {
			
		}	
	}
	
	// display method for menu 
	public void menu() throws IOException{
			System.out.println("\n========== Kooks Menu ============");
			try {
				
				// instantiating BufferedReader for this method
				rr = new BufferedReader(new FileReader(path));
				
				rr.readLine();
				
				 // CLI formatting
				 System.out.printf("%-8s %-22s %-12s %-8s\n", "ID", "Item Name", "Price", "Stock");
			        System.out.println("-----------------------------------------------");
			    
	            String line;
	            while ((line = rr.readLine()) != null) {
	                String[] parts = line.split(",");
	                if (parts.length >= 5) {
	                    System.out.printf("%-8s %-22s RM%-9.2f %-8s\n", parts[0], parts[1], Double.parseDouble(parts[4]), parts[2]);
					}
					
				}
					
			} catch(Exception e) {
					
			}
			
	}	
	
	public void refreshMenu() throws IOException{
		 File file = new File(path);
		 long lastModified = file.lastModified();
		 
		 menu();
		 
		 while (true) {
		        // Check if file was modified since last check
		        if (file.lastModified() != lastModified) {
		            lastModified = file.lastModified();
		            System.out.println("\nFile updated! Refreshing menu...");
		            menu();
		        }
		        
		        // Sleep before checking again
		        try {
		            Thread.sleep(2000); // Check every 2 seconds
		        } catch (InterruptedException e) {
		            Thread.currentThread().interrupt();
		            break;
		        }
		    }
	}

	    
	    
	    
	
	
	public void addItem() throws IOException {
		// printing options for user
		System.out.println("Enter new item ID:" );
		itemId = input.nextLine();
		System.out.println("Enter new item name: ");
		itemName = input.nextLine();
		System.out.println("Enter stock quantity: ");
		stock = Integer.parseInt(input.nextLine());
		System.out.println("Enter unit price: ");
		unitPrice = Double.parseDouble(input.nextLine());

		// formatting how the data will be stored into the CSV file
		String newData = String.format("%s,%s,%d, %d, %.2f,%.2f", itemId, itemName, stock, sold, unitPrice, totalSales);
		
		try {
			// using the BufferedWriter to write new data entry into the CSV file 
			uw = new BufferedWriter(new FileWriter(path,true));
			uw.write(newData);
			uw.newLine();
			
		} catch(Exception e) {
			
		} finally {
			uw.close();
		}
		
	}
	
	boolean itemFound = false;
	
	public void removeItem() throws IOException {
		// used to search for itemId, to be compared with CSV file
		String delete = "";
		
		System.out.println("Enter item ID you would like to delete: ");
		delete = input.nextLine();
		
		
		try {
			rr = new BufferedReader(new FileReader(path));
			// used to search for data if or if not empty
			String line = "";
			// to copy header
			String header = rr.readLine();
			if(header != null) {
				copy.add(header);
			}

			while((line = rr.readLine()) != null) {
				String[] fields = line.split(",");
			
			// checks if the fields read include the itemId we want to remove
			// if it doesn't then it'll copy the line into the copy ArrayList
			// else the boolean itemFound turns true
				if(fields.length > 0 && !fields[0].equals(delete)) {
					copy.add(line);
				} else {
					itemFound = true;
				}
			}
		} catch(Exception e) {
			
		} finally {
			rr.close();
		}
		
		// if itemId is not found sysout this line
		if(!itemFound) {
			System.out.println("Item was not found " +delete);
		}
		
		try {
			uw = new BufferedWriter(new FileWriter(path));
			// for loop to print out copied data and replace old data
			for(String row : copy) {
				uw.write(row);
				uw.newLine();
			}
			
		} catch (Exception e) {
			
		} finally {
			uw.close();
		}
	}
	
	public void restock() throws IOException {
		// set position of fields column
		final int ID_COLUMN = 0;   
		final int STOCK_COLUMN = 2; 
		// store itemId to be searched and changed
		String append = "";
		// new quantity to be added
		int newQty;
	
		System.out.println("Enter the item ID you would like to append: ");
		append = input.nextLine();
		System.out.println("Enter quantity to be added: ");
		newQty = Integer.parseInt(input.nextLine());
		
		
		try {
			rr = new BufferedReader(new FileReader(path));
			// search and check if line is empty
			String line = "";
			// found flag
			Boolean found = false;
			// copy header
			String header = rr.readLine();
			if(header != null) {
				update.add(header);
			}
			
			while((line = rr.readLine()) != null) {
				String[] fields = line.split(",");
			// if loop to search for the itemId to be edited	
				if(fields.length >= 3 && fields[0].equals(append)) {
					// reading old stock values from CSV, parseInt because storing in String [] array 
	                int stock = Integer.parseInt(fields[STOCK_COLUMN]);
	                // adding old quantity with new quantity
					fields[STOCK_COLUMN] = String.valueOf(stock += newQty);
					found = true;
				
				} 
			
				update.add(String.join(",", fields));
			}
			
			if(!found) {
				System.out.println("Item ID was not found: " +append);
			}
			
		} catch (Exception e) {
			
		} finally {
			rr.close();
		}
		
		try {
			uw = new BufferedWriter(new FileWriter(path));
			// for loop to replace new data
			for (String update: update) {
				uw.write(update);
				uw.newLine();
			}
			
		} catch(Exception e) {
			
		} finally {
			uw.close();
		}
		
	}
	
	public void editPrice() throws IOException {
		// set position of fields column
		final int ID_COLUMN = 0;   
		final int PRICE_COLUMN = 4; 
		// store itemId to be searched and changed
		String append = "";
		// new price to be changed
		double newPrice;
	
		System.out.println("Enter the item ID you would like to append: ");
		append = input.nextLine();
		System.out.println("Enter the new price: ");
		newPrice = Double.parseDouble(input.nextLine());
		
		
		try {
			rr = new BufferedReader(new FileReader(path));
			// search and check if line is empty
			String line = "";
			// found flag
			Boolean found = false;
			// copy header
			String header = rr.readLine();
			if(header != null) {
				update.add(header);
			}
			
			while((line = rr.readLine()) != null) {
				String[] fields = line.split(",");
				
				if(fields.length >= 5 && fields[0].equals(append)) {
					// reading old price values from CSV, String.valueOf because array is a String, but price is double 
					fields[PRICE_COLUMN] = String.valueOf(newPrice);
					found = true;
				
				} 
			
				update.add(String.join(",", fields));
			}
			
			if(!found) {
				System.out.println("Item ID was not found: " +append);
			}
			
		} catch (Exception e) {
			
		} finally {
			rr.close();
		}
		
		try {
			uw = new BufferedWriter(new FileWriter(path));
			// for loop to replace old data with new data
			for (String update: update) {
				uw.write(update);
				uw.newLine();
			}
			
		} catch(Exception e) {
			
		} finally {
			uw.close();
		}
		
	}

	public void lowStockAlert(int threshold) throws IOException {
		System.out.println("\n=== Low Stock Alert ===");
		boolean lowStockFound = false;
		
		try {
			// Read from CSV file
			rr = new BufferedReader(new FileReader(path));
			// Skip header
			rr.readLine();
			
			System.out.printf("%-8s %-22s %-10s %-15s%n", 
					"ID", "Item Name", "Stock", "Status");
			System.out.println("-----------------------------------------------------------");
			
			String line;
			while ((line = rr.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 3) {
					int currentStock = Integer.parseInt(parts[2].trim());
					
					// Check if stock is below threshold
					if (currentStock <= threshold) {
						System.out.printf("%-8s %-22s %-10d %-15s%n", 
								parts[0], parts[1], currentStock, 
								currentStock == 0 ? "OUT OF STOCK!" : "LOW STOCK!");
						lowStockFound = true;
					}
				}
			}
			
			if (!lowStockFound) {
				System.out.println("No items below threshold of " + threshold);
			}
			
		} catch (Exception e) {
			System.out.println("Error checking low stock: " + e.getMessage());
		} finally {
			if (rr != null) {
				rr.close();
			}
		}
	}

	public void predictStockDuration() throws IOException {
		System.out.println("\n=== Stock Duration Prediction ===");
		
		// Map to track sales by item ID
		Map<String, Integer> totalSalesByItem = new HashMap<>();
		Map<String, LocalDateTime> firstOrderDate = new HashMap<>();
		Map<String, LocalDateTime> lastOrderDate = new HashMap<>();
		
		// Read order_items.csv to gather sales data
		File orderItemsFile = new File("src/order_items.csv");
		if (orderItemsFile.exists()) {
			try (BufferedReader itemReader = new BufferedReader(new FileReader(orderItemsFile))) {
				// Skip header
				itemReader.readLine();
				
				String line;
				while ((line = itemReader.readLine()) != null) {
					if (line.trim().isEmpty()) continue;
					
					String[] fields = line.split(",");
					if (fields.length >= 5) {
						String orderId = fields[0];
						String itemId = fields[1];
						int quantity = Integer.parseInt(fields[2].trim());
						
						// Add to total sales for this item
						totalSalesByItem.put(itemId, 
							totalSalesByItem.getOrDefault(itemId, 0) + quantity);
						
						// Find order date by looking up order ID in orders.csv
						try (BufferedReader orderReader = new BufferedReader(new FileReader("src/orders.csv"))) {
							// Skip header
							orderReader.readLine();
							
							String orderLine;
							while ((orderLine = orderReader.readLine()) != null) {
								String[] orderFields = orderLine.split(",");
								if (orderFields.length >= 3 && orderFields[0].equals(orderId)) {
									// Parse the date
									DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
									LocalDateTime orderDate = LocalDateTime.parse(orderFields[2], formatter);
									
									// Update first order date if this is earlier
									if (!firstOrderDate.containsKey(itemId) || 
										orderDate.isBefore(firstOrderDate.get(itemId))) {
										firstOrderDate.put(itemId, orderDate);
									}
									
									// Update last order date if this is later
									if (!lastOrderDate.containsKey(itemId) || 
										orderDate.isAfter(lastOrderDate.get(itemId))) {
										lastOrderDate.put(itemId, orderDate);
									}
									
									break;
								}
							}
						} catch (Exception e) {
							// Handle error reading orders file
							System.out.println("Error reading orders file: " + e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error reading order items: " + e.getMessage());
			}
		}
		
		// Now read inventory and calculate predictions
		try {
			// Read from CSV file
			rr = new BufferedReader(new FileReader(path));
			// Skip header
			rr.readLine();
			
			System.out.printf("%-8s %-22s %-10s %-15s %-15s%n", 
					"ID", "Item Name", "Current", "Daily Sales", "Days Remaining");
			System.out.println("-----------------------------------------------------------------------------");
			
			String line;
			while ((line = rr.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 3) {
					String id = parts[0];
					String name = parts[1];
					int currentStock = Integer.parseInt(parts[2].trim());
					
					double dailySalesRate = 0.0;
					String daysRemaining = "∞ (No sales data)";
					
					// If we have sales data for this item
					if (totalSalesByItem.containsKey(id) && 
						firstOrderDate.containsKey(id) && 
						lastOrderDate.containsKey(id)) {
						
						int totalSold = totalSalesByItem.get(id);
						LocalDateTime first = firstOrderDate.get(id);
						LocalDateTime last = lastOrderDate.get(id);
						
						// Calculate the time span in days
						long daysDifference = java.time.Duration.between(first, last).toDays();
						
						// Ensure we don't divide by zero - use at least 1 day
						daysDifference = Math.max(1, daysDifference);
						
						// Calculate daily sales rate based on actual sales history
						dailySalesRate = (double) totalSold / daysDifference;
						
						// Calculate how many days the stock will last
						if (dailySalesRate > 0) {
							double days = currentStock / dailySalesRate;
							daysRemaining = String.format("%.1f days", days);
							
							// Add alert for critically low stock
							if (days < 7) {
								daysRemaining += " (CRITICAL!)";
							} else if (days < 14) {
								daysRemaining += " (LOW)";
							}
						}
					}
					
					System.out.printf("%-8s %-22s %-10d %-15.2f %-15s%n", 
							id, name, currentStock, dailySalesRate, daysRemaining);
				}
			}
			
		} catch (Exception e) {
			System.out.println("Error predicting stock duration: " + e.getMessage());
		} finally {
			if (rr != null) {
				rr.close();
			}
		}
	}
}

	