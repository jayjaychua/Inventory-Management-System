import java.io.*; //BufferedReader, FileReader, IOException
//import java.text.DateFormat.Field;
import java.util.*; // ArrayList, Array, List

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
	
}

	