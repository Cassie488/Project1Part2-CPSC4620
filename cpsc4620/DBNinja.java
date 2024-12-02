package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/*
 * This file is where you will implement the methods needed to support this application.
 * You will write the code to retrieve and save information to the database and use that
 * information to build the various objects required by the applicaiton.
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. 
 * DO NOT change these constant values.
 * 
 * You can add any helper methods you need, but you must implement all the methods
 * in this class and use them to complete the project.  The autograder will rely on
 * these methods being implemented, so do not delete them or alter their method
 * signatures.
 * 
 * Make sure you properly open and close your DB connections in any method that
 * requires access to the DB.
 * Use the connect_to_db below to open your connection in DBConnector.
 * What is opened must be closed!
 */

/*
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// DO NOT change these variables!
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";

	public enum order_state {
		PREPARED,
		DELIVERED,
		PICKEDUP
	}


	private static boolean connect_to_db() throws SQLException, IOException 
	{

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	public static void addOrder(Order o) throws SQLException, IOException 
	{
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, pickup, pizzas, toppings
		 * on pizzas, order discounts and pizza discounts.
		 * 
		 * This is a KEY method as it must store all the data in the Order object
		 * in the database and make sure all the tables are correctly linked.
		 * 
		 * Remember, if the order is for Dine In, there is no customer...
		 * so the cusomter id coming from the Order object will be -1.
		 * 
		 */
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = DBNinja.getConnection(); // Assuming a DB connection helper method
			conn.setAutoCommit(false); // Begin transaction

			// Insert into Order table
			String orderQuery = "INSERT INTO `Order` (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_IsComplete) VALUES (?, ?, ?, ?, ?, ?)";
			stmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, o.getOrderID());
			stmt.setInt(2, (o.getCustID() == -1 ? null : o.getCustID())); // Handle no customer for Dine In
			stmt.setString(3, o.getOrderType());
			stmt.setTimeStamp(4, o.getDate());
			stmt.setDouble(5, o.getCustPrice());
			stmt.setDouble(6, o.getBusPrice());
			stmt.setBoolean(7, o.getIsComplete());
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				tempID = rs.getInt(1);
			}

			for (Pizza pizza : o.getPizzaList()) {
				addPizza(pizza.getPizzaDate(), o.getOrderID(), pizza);
			}

			for (Discount discount : o.getDiscountList()) {
				String discountQuery = "INSERT INTO Order_Discount (OrderID, DiscountID) VALUES (?, ?)";
				stmt = conn.prepareStatement(discountQuery);
				stmt.setInt(1, o.getOrderID());
				stmt.setInt(2, discount.getDiscountID());
				stmt.executeUpdate();
			}

			//FIX THE COMPARISIONS CASSIE TY
			if ("Delivery"==(o.getOrderType())) {
				String deliveryQuery = "INSERT INTO Delivery (OrderID, HouseNum, Street, City, State, Zip, IsDelivered) VALUES (?, ?, ?, ?, ?, ?, ?)";
				DeliveryOrder deliveryOrder = (DeliveryOrder) o;
				stmt = conn.prepareStatement(deliveryQuery);
				stmt.setInt(1, o.getOrderID());
				stmt.setInt(2, deliveryOrder.getHouseNum());
				stmt.setString(3, deliveryOrder.getStreet());
				stmt.setString(4, deliveryOrder.getCity());
				stmt.setString(5, deliveryOrder.getState());
				stmt.setInt(6, deliveryOrder.getZip());
				stmt.setBoolean(7, deliveryOrder.getIsDelivered());
				stmt.executeUpdate();
			} else if ("DineIn"==(o.getOrderType())) {
				String dineInQuery = "INSERT INTO DineIn (OrderID, TableNumber) VALUES (?, ?)";
				DineinOrder dineinOrder = (DineinOrder) o;
				stmt = conn.prepareStatement(dineInQuery);
				stmt.setInt(1, o.getOrderID());
				stmt.setInt(2, dineinOrder.getTableNum());
				stmt.executeUpdate();
			} else if ("Pickup"==(o.getOrderType())) {
				String pickupQuery = "INSERT INTO Pickup (OrderID, IsPickedUp) VALUES (?, ?)";
				PickupOrder pickupOrder = (PickupOrder) o;
				stmt = conn.prepareStatement(pickupQuery);
				stmt.setInt(1, o.getOrderID());
				stmt.setBoolean(2, pickupOrder.getIsPickedUp()); 
				stmt.executeUpdate();
			}

			conn.commit();
			rs.close();
			stmt.close();
	} catch (SQLException e){
		conn.rollback();
	}
}	
	
	public static int addPizza(java.util.Date d, int orderID, Pizza p) throws SQLException, IOException
	{
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind you must also add the pizza discounts and toppings 
		 * associated with the pizza.
		 * 
		 * NOTE: there is a Date object passed into this method so that the Order
		 * and ALL its Pizzas can be assigned the same DTS.
		 * 
		 * This method returns the id of the pizza just added.
		 * 
		 */

		int PizzaId = -1;

		String pizzaInsertQuery = "INSERT INTO Pizza (pizza_PizzaID, pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, pizza_OrderID) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		 
		 
		 try (PreparedStatement pizzaStmt = conn.prepareStatement(pizzaInsertQuery, Statement.RETURN_GENERATED_KEYS)) {
			 pizzaStmt.setInt(1, p.getPizzaID());
			 pizzaStmt.setString(2, p.getSize());
			 pizzaStmt.setString(3, p.getCrustType());
			 pizzaStmt.setString(4, p.getPizzaState());
			 pizzaStmt.setTimeStamp(5, p.getPizzaDate());
			 pizzaStmt.setDouble(6, p.getCustPrice());
			 pizzaStmt.setDouble(7, p.getBusPrice());
			 pizzaStmt.setInt(8, orderID);

			 pizzaStmt.executeUpdate();
			 try(ResultSet rs = pizzaStmt.getGeneratedKeys()){
				if (rs.next()) {
					PizzaID = rs.getInt(1);
				}
			 }
	 
				 // Add toppings
				 for (Topping topping : p.getToppings()) {
					String sql = "INSERT INTO pizza_topping (PizzaID, ToppingID, pizza_topping_IsDouble) VALUES (?, ?, ?)";
    
					try (PreparedStatement stmt2 = conn.prepareStatement(sql)) {
						stmt2.setInt(1, PizzaID);             
						stmt2.setInt(2, topping.getTopID());
						toppingStmt.setInt(3, topping.getDoubled() ? 1 : 0); 
						stmt2.executeUpdate();  
					}
				 }
	 
				 for (Discount discount : p.getDiscounts()) {
					String sql = "INSERT INTO pizza_discount (PizzaID, DiscountID) VALUES (?, ?)";
    
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {
						stmt.setInt(1, PizzaID);
						stmt.setInt(2, discount.getDiscountID());
						stmt.executeUpdate();
					}
				 }
			 }
			 return PizzaID;
		 }
	}
	
	public static int addCustomer(Customer c) throws SQLException, IOException
	 {
		/*
		 * This method adds a new customer to the database.
		 * 
		 */
		int customerId = -1;

		// SQL query to insert a new customer into the Customer table
		String customerInsertQuery = "INSERT INTO Customer (CustID, customer_FName, customer_LName, customer_PhoneNum) VALUES (?, ?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(customerInsertQuery, Statement.RETURN_GENERATED_KEYS)) {
			// Set the customer's details in the query
			stmt.setInt(1, c.getCustID());
			stmt.setString(2, c.getFName());
			stmt.setString(3, c.getLName());
			stmt.setString(4, c.getPhone());

			// Execute the insert operation
			stmt.executeUpdate();
	
			// Retrieve the generated customer ID
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					customerId = rs.getInt(1); // Get the generated customer ID
					c.setCustID(customerId);  // Update the Customer object with the ID
				}
			}
		}
		 return -1;
	}

	public static void completeOrder(int OrderID, order_state newState ) throws SQLException, IOException
	{
		/*
		 * Mark that order as complete in the database.
		 * Note: if an order is complete, this means all the pizzas are complete as well.
		 * However, it does not mean that the order has been delivered or picked up!
		 *
		 * For newState = PREPARED: mark the order and all associated pizza's as completed
		 * For newState = DELIVERED: mark the delivery status
		 * FOR newState = PICKEDUP: mark the pickup status
		 * 
		 */
		conn = DBNinja.getConnection();
		try{
			if(newState.equals(order_state.PREPARED)){
				String updatePizza = "UPDATE pizza SET pizza_PizzaState=? WHERE ordertable_OrderID=?"; 
				PreparedStatement PizzaStmt = conn.prepareStatement(updatePizza);
				PizzaStmt.setString(1, "COMPLETE");
				PizzaStmt.setInt(2, OrderID);
				PizzaStmt.executeUpdate();

				String updateOrder = "UPDATE ordertable SET ordertable_isComplete=? WHERE ordertable_OrderID=?"; 
				PreparedStatement orderStmt = conn.prepareStatement(updateOrder);
				orderStmt.setBoolean(1, true);
				orderStmt.setInt(2, OrderID);
				orderStmt.executeUpdate();
			} else if (newState.equals(order_state.DELIVERED)){
				String updateDelivery = "UPDATE delivery SET delivery_isDelivered=? WHERE ordertable_OrderID=?"; 
				PreparedStatement DeliveryStmt = conn.prepareStatement(updateDelivery);
				DeliveryStmt.setBoolean(1, true);
				DeliveryStmt.setInt(2, OrderID);
				DeliveryStmt.executeUpdate();
			} else if (newState.equals(order_state.PICKEDUP)) {
				String updatePickup = "UPDATE pickup SET pickup_IsPickedUp=? WHERE ordertable_OrderID=?"; 
				PreparedStatement pickupStmt = conn.prepareStatement(updatePickup);
				pickupStmt.setBoolean(1, true);
				pickupStmt.setInt(2, OrderID);
				pickupStmt.executeUpdate();
			}
			conn.commit();
			if(newState.equals(order_state.PREPARED)){
				PizzaStmt.close();
				orderStmt.close();
			} else if (newState.equals(order_state.DELIVERED)){
				DeliveryStmt.close();
			} else if (newState.equals(order_state.PICKEDUP)) {
				pickupStmt.close();
			}
			conn.close();
		} catch (SQLException e){
			conn.rollback();
		}

	}


	public static ArrayList<Order> getOrders(int status) throws SQLException, IOException
	 {
	/*
	 * Return an ArrayList of orders.
	 * 	status   == 1 => return a list of open (ie oder is not completed)
	 *           == 2 => return a list of completed orders (ie order is complete)
	 *           == 3 => return a list of all the orders
	 * Remember that in Java, we account for supertypes and subtypes
	 * which means that when we create an arrayList of orders, that really
	 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
	 *
	 * You must fully populate the Order object, this includes order discounts,
	 * and pizzas along with the toppings and discounts associated with them.
	 * 
	 * Don't forget to order the data coming from the database appropriately.
	 *
	 */
		conn = DBNinja.getConnection();
		List<Order> orders = new ArrayList<>();

		if(status == 1){
			string queryOrder = "SELECT * FROM ordertable WHERE ordertable_isComplete = false"
		} else if (status == 2){
			string queryOrder = "SELECT * FROM ordertable WHERE ordertable_isComplete = true"
		} else if (status == 3){
			string queryOrder = "SELECT * FROM ordertable"
		}

		stmt = conn.prepareStatement(queryOrder);
		rs = stmt.executeQuery();

		while(rs.next()){
			int OrderID = rs.getInt("ordertable_OrderID");
			int CustID = rs.getInt("ordertable_CustID");
			int OrderType = rs.getString("ordertable_OrderType");
			int OrderDateTime = rs.getTimeStamp("ordertable_OrderDateTime");
			int CustPrice = rs.getDouble("ordertable_CustPrice");
			int BusPrice = rs.getDouble("ordertable_BusPrice");
			int isComplete = rs.getBoolean("ordertable_isComplete");
			if(OrderType.equals("Delivery")){
				order = new DeliveryOrder(OrderID, CustID, OrderType, OrderDateTime, CustPrice, BusPrice, isComplete);
				//need to get delivery specific attributes populated
			} else if(OrderType.equals("DineIn")){
				order = new DineinOrder(OrderID, CustID, OrderType, OrderDateTime, CustPrice, BusPrice, isComplete);
				//need to get dineIn specific attributes populated
			} else if(OrderType.equals("PickUp")){
				order = new PickupOrder(OrderID, CustID, OrderType, OrderDateTime, CustPrice, BusPrice, isComplete);
				//need to get pickup specific attributes populated
			}

			//populate the discount list
			//populate the pizza list here
			orders.add(order)
		}
			
		return null;
	}
	
	public static Order getLastOrder() throws SQLException, IOException 
	{
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there will ALWAYS be a "last order"!
		 */
		 return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException
	 {
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */
		 return null;
	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException 
	{
		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts ordered by discount name.
		 * 
		*/
		return null;
	}

	public static Discount findDiscountByName(String name) throws SQLException, IOException 
	{
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */
		 return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException 
	{
		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/
		return null;
	}

	public static Customer findCustomerByPhone(String phoneNumber)  throws SQLException, IOException 
	{
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */
		 return null;
	}

	public static String getCustomerName(int CustID) throws SQLException, IOException 
	{
		/*
		 * COMPLETED...WORKING Example!
		 * 
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with
		 * your database from Java.  
		 * 
		 * Notice how the connection to the DB made at the start of the 
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		String cname1 = "";
		String cname2 = "";
		String query = "Select customer_FName, customer_LName From customer WHERE customer_CustID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			cname1 = rset.getString(1) + " " + rset.getString(2); 
		}

		/* 
		* an BETTER example of the same query using a prepared statement...
		* with exception handling
		* 
		*/
		try {
			PreparedStatement os;
			ResultSet rset2;
			String query2;
			query2 = "Select customer_FName, customer_LName From customer WHERE customer_CustID=?;";
			os = conn.prepareStatement(query2);
			os.setInt(1, CustID);
			rset2 = os.executeQuery();
			while(rset2.next())
			{
				cname2 = rset2.getString("customer_FName") + " " + rset2.getString("customer_LName"); // note the use of field names in the getSting methods
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// process the error or re-raise the exception to a higher level
		}

		conn.close();

		return cname1;
		// OR
		// return cname2;

	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException 
	{
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		return null;
	}

	public static Topping findToppingByName(String name) throws SQLException, IOException 
	{
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */
		 return null;
	}

	public static ArrayList<Topping> getToppingsOnPizza(Pizza p) throws SQLException, IOException 
	{
		/* 
		 * This method builds an ArrayList of the toppings ON a pizza.
		 * The list can then be added to the Pizza object elsewhere in the
		 */

		return null;	
	}

	public static void addToInventory(int toppingID, double quantity) throws SQLException, IOException 
	{
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */
	}
	
	
	public static ArrayList<Pizza> getPizzas(Order o) throws SQLException, IOException 
	{
		/*
		 * Build an ArrayList of all the Pizzas associated with the Order.
		 * 
		 */
		return null;
	}

	public static ArrayList<Discount> getDiscounts(Order o) throws SQLException, IOException 
	{
		/* 
		 * Build an array list of all the Discounts associted with the Order.
		 * 
		 */

		return null;
	}

	public static ArrayList<Discount> getDiscounts(Pizza p) throws SQLException, IOException 
	{
		/* 
		 * Build an array list of all the Discounts associted with the Pizza.
		 * 
		 */
	
		return null;
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException 
	{
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		return 0.0;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException 
	{
		/* 
		 * Query the database fro the base business price for that size and crust pizza.
		 * 
		*/
		return 0.0;
	}

	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 * 
		 */
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException 
	{
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 * 
		 */
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 * HINT: You need to match the expected output EXACTLY....I would suggest
		 * you look at the printf method (rather that the simple print of println).
		 * It operates the same in Java as it does in C and will make your code
		 * better.
		 * 
		 */
	}
	
	
	
	/*
	 * These private methods help get the individual components of an SQL datetime object. 
	 * You're welcome to keep them or remove them....but they are usefull!
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


