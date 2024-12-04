package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static java.sql.Types.NULL;

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
		connect_to_db();
		PreparedStatement stmt = null;

		try {
			conn.setAutoCommit(false); // Begin transaction

			// Insert into Order table
			String orderQuery = "INSERT INTO `Order` (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_IsComplete) VALUES (?, ?, ?, ?, ?, ?)";
			stmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, o.getOrderID());
			if (o.getCustID() != -1) {
				stmt.setInt(2, o.getCustID()); // Handle no customer for Dine In
			}
			else {
				stmt.setNull(2, NULL);
			}
			stmt.setString(3, o.getOrderType());
			stmt.setString(4, o.getDate());
			stmt.setDouble(5, o.getCustPrice());
			stmt.setDouble(6, o.getBusPrice());
			stmt.setBoolean(7, o.getIsComplete());
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int tempID = rs.getInt(1);
			}

			for (Pizza pizza : o.getPizzaList()) {
				String pizzaDate = pizza.getPizzaDate();
				Date date = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss").parse(pizzaDate);
				addPizza(date, o.getOrderID(), pizza);
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

				//NEED TO PARSE ADDRESS AND INSERT
				/*
				stmt.setInt(1, o.getOrderID);
				stmt.setInt(2, HouseNum);
				stmt.setString(3, Street);
				stmt.setString(4, City);
				stmt.setString(5, State);
				stmt.setInt(6, Zip);
				stmt.setBoolean(7, IsDelivered);
				*/
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
		} catch (ParseException e) {
			throw new RuntimeException(e);
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
		connect_to_db();

		int PizzaID = -1;
		ResultSet rs = null;
		PreparedStatement stmtTopping = null;
		PreparedStatement stmtDiscount = null;

		String pizzaInsertQuery = "INSERT INTO Pizza (pizza_PizzaID, pizza_Size, pizza_CrustType, pizza_PizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice, pizza_OrderID) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";



		try (PreparedStatement pizzaStmt = conn.prepareStatement(pizzaInsertQuery, Statement.RETURN_GENERATED_KEYS)) {
			pizzaStmt.setInt(1, p.getPizzaID());
			pizzaStmt.setString(2, p.getSize());
			pizzaStmt.setString(3, p.getCrustType());
			pizzaStmt.setString(4, p.getPizzaState());
			pizzaStmt.setString(5, p.getPizzaDate());
			pizzaStmt.setDouble(6, p.getCustPrice());
			pizzaStmt.setDouble(7, p.getBusPrice());
			pizzaStmt.setInt(8, orderID);

			pizzaStmt.executeUpdate();
			rs = pizzaStmt.getGeneratedKeys();
			if (rs.next()) {
				PizzaID = rs.getInt(1);
			}

			// Add toppings
			for (Topping topping : p.getToppings()) {
				String sql = "INSERT INTO pizza_topping (PizzaID, ToppingID, pizza_topping_IsDouble) VALUES (?, ?, ?)";

				stmtTopping = conn.prepareStatement(sql);
				stmtTopping.setInt(1, PizzaID);
				stmtTopping.setInt(2, topping.getTopID());
				stmtTopping.setInt(3, topping.getDoubled() ? 1 : 0);
				stmtTopping.executeUpdate();
			}

			for (Discount discount : p.getDiscounts()) {
				String sql = "INSERT INTO pizza_discount (PizzaID, DiscountID) VALUES (?, ?)";

				stmtDiscount = conn.prepareStatement(sql);
				stmtDiscount.setInt(1, PizzaID);
				stmtDiscount.setInt(2, discount.getDiscountID());
				stmtDiscount.executeUpdate();
			}

			pizzaStmt.close();
			rs.close();
			stmtTopping.close();
			stmtDiscount.close();
			conn.close();
		}
		return PizzaID;
	}

	//Tested and Works
	public static int addCustomer(Customer c) throws SQLException, IOException
	{
		/*
		 * This method adds a new customer to the database.
		 *
		 */
		connect_to_db();
		int customerId = -1;
		ResultSet rs = null;

		// SQL query to insert a new customer into the Customer table
		String customerInsertQuery = "INSERT INTO customer (customer_FName, customer_LName, customer_PhoneNum) VALUES (?, ?, ?)";

		try (PreparedStatement stmt = conn.prepareStatement(customerInsertQuery, Statement.RETURN_GENERATED_KEYS)) {
			// Set the customer's details in the query
			stmt.setString(1, c.getFName());
			stmt.setString(2, c.getLName());
			stmt.setString(3, c.getPhone());

			// Execute the insert operation
			stmt.executeUpdate();

			// Retrieve the generated customer ID
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				customerId = rs.getInt(1); // Get the generated customer ID
				c.setCustID(customerId);  // Update the Customer object with the ID
			}
			rs.close();
			conn.close();
			stmt.close();
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
		connect_to_db();
		PreparedStatement PizzaStmt = null;
		PreparedStatement orderStmt = null;
		PreparedStatement DeliveryStmt = null;
		PreparedStatement pickupStmt = null;
		try{
			if(newState.equals(order_state.PREPARED)){
				String updatePizza = "UPDATE pizza SET pizza_PizzaState=? WHERE ordertable_OrderID=?";
				PizzaStmt = conn.prepareStatement(updatePizza);
				PizzaStmt.setString(1, "COMPLETE");
				PizzaStmt.setInt(2, OrderID);
				PizzaStmt.executeUpdate();

				String updateOrder = "UPDATE ordertable SET ordertable_isComplete=? WHERE ordertable_OrderID=?";
				orderStmt = conn.prepareStatement(updateOrder);
				orderStmt.setBoolean(1, true);
				orderStmt.setInt(2, OrderID);
				orderStmt.executeUpdate();
			} else if (newState.equals(order_state.DELIVERED)){
				String updateDelivery = "UPDATE delivery SET delivery_isDelivered=? WHERE ordertable_OrderID=?";
				DeliveryStmt = conn.prepareStatement(updateDelivery);
				DeliveryStmt.setBoolean(1, true);
				DeliveryStmt.setInt(2, OrderID);
				DeliveryStmt.executeUpdate();
			} else if (newState.equals(order_state.PICKEDUP)) {
				String updatePickup = "UPDATE pickup SET pickup_IsPickedUp=? WHERE ordertable_OrderID=?";
				pickupStmt = conn.prepareStatement(updatePickup);
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
		connect_to_db();
		List<Order> ordersList = new ArrayList<>();
		PreparedStatement stmtDelivery = null;
		PreparedStatement stmtDineIn = null;
		PreparedStatement stmtPickUp = null;
		PreparedStatement stmt = null;
		ResultSet rsOrder = null;
		ResultSet rsDelivery = null;
		ResultSet rsDineIn = null;
		ResultSet rsPickUp = null;
		int tableNum = -1;
		boolean ispickedUp = true;
		Order order = null;

		String queryOrder = null;
		if(status == 1){
			queryOrder = "SELECT * FROM ordertable WHERE ordertable_isComplete = false";
		} else if (status == 2){
			queryOrder = "SELECT * FROM ordertable WHERE ordertable_isComplete = true";
		} else if (status == 3){
			queryOrder = "SELECT * FROM ordertable";
		}

		stmt = conn.prepareStatement(queryOrder);
		rsOrder = stmt.executeQuery();

		while(rsOrder.next()){
			int OrderID = rsOrder.getInt("ordertable_OrderID");
			int CustID = rsOrder.getInt("customer_CustID");
			String OrderType = rsOrder.getString("ordertable_OrderType");
			String OrderString = rsOrder.getString("ordertable_OrderDateTime");
			double CustPrice = rsOrder.getDouble("ordertable_CustPrice");
			double BusPrice = rsOrder.getDouble("ordertable_BusPrice");
			Boolean isComplete = rsOrder.getBoolean("ordertable_isComplete");


			if(OrderType.equals("Delivery")){

				String queryOrderDelivery = "SELECT delivery_HouseNum, delivery_Street, delivery_City, delivery_State, delivery_Zip, delivery_IsDelivered FROM delivery WHERE ordertable_OrderID = ?";

				stmtDelivery = conn.prepareStatement(queryOrderDelivery);
				stmtDelivery.setInt(1, OrderID);
				rsDelivery = stmtDelivery.executeQuery();

				if(rsDelivery.next()){
					int houseNum = rsDelivery.getInt("delivery_HouseNum");
					String street = rsDelivery.getString("delivery_Street");
					String city = rsDelivery.getString("delivery_City");
					String state = rsDelivery.getString("delivery_State");
					int zipCode = rsDelivery.getInt("delivery_Zip");
					boolean IsDelivered = rsDelivery.getBoolean("delivery_IsDelivered");


					String address = houseNum + " " + street + ", " + city + ", " + state + " " + zipCode;

					order = new DeliveryOrder(OrderID, CustID, OrderString, CustPrice, BusPrice, isComplete, address, IsDelivered);
				}
				rsDelivery.close();
				stmtDelivery.close();


			} else if(OrderType.equals("DineIn")){

				String queryOrderDineIn = "SELECT dinein_TableNum FROM dinein WHERE ordertable_OrderID = ?";

				stmtDineIn = conn.prepareStatement(queryOrderDineIn);
				stmtDineIn.setInt(1, OrderID);
				rsDineIn = stmtDineIn.executeQuery();

				if(rsDineIn.next()){
					tableNum = rsDineIn.getInt("dinein_TableNum");
				}
				order = new DineinOrder(OrderID, CustID, OrderString, CustPrice, BusPrice, isComplete, tableNum);
				rsDineIn.close();
				stmtDineIn.close();

			} else if(OrderType.equals("PickUp")){

				String queryOrderPickup = "SELECT pickup_IsPickedUp FROM pickup WHERE ordertable_OrderID = ?";

				stmtPickUp = conn.prepareStatement(queryOrderPickup);
				stmtPickUp.setInt(1, OrderID);
				rsPickUp = stmtPickUp.executeQuery();

				if(rsPickUp.next()){
					ispickedUp = rsPickUp.getBoolean("pickup_IsPickedUP");
				}
				order = new PickupOrder(OrderID, CustID, OrderString, CustPrice, BusPrice, ispickedUp, isComplete);

				rsPickUp.close();
				stmtPickUp.close();
			}

			//populate the discount list
			ArrayList<Discount> discountList = new ArrayList<>();
			discountList = getDiscounts(order);
			order.setDiscountList(discountList);



			//populate the pizza list here
			ArrayList<Pizza> pizzasList = new ArrayList<>();
			pizzasList = getPizzas(order);
			order.setPizzaList(pizzasList);

			ordersList.add(order);
		}
		stmt.close();
		rsOrder.close();
		return null;
	}

	public static Order getLastOrder() throws SQLException, IOException
	{
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there will ALWAYS be a "last order"!
		 */
		connect_to_db();

		Order latest = null;
		PreparedStatement stmtLatest = null;
		ResultSet rsLatest = null;

		//need to figure out how to determine order added to database
		// is it by time or date?
		String latestQuery = "";

		stmtLatest = conn.prepareStatement(latestQuery);
		rsLatest = stmtLatest.executeQuery();


		conn.close();
		return latest;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException
	{
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *
		 */
		connect_to_db();

		ArrayList<Order> dateOrder = new ArrayList<>();
		PreparedStatement stmtDated = null;
		ResultSet rsDated = null;

		//need to figure out how to determine order added to database
		// is it by time or date?
		String datedQuery = "";

		stmtDated = conn.prepareStatement(datedQuery);
		rsDated = stmtDated.executeQuery();


		conn.close();
		if(!dateOrder.isEmpty()){
			return dateOrder;
		}
		else {
			return null;
		}
	}

	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException
	{
		/*
		 * Query the database for all the available discounts and
		 * return them in an arrayList of discounts ordered by discount name.
		 *
		 */
		connect_to_db();
		ArrayList<Discount> discountList = new ArrayList<>();

		PreparedStatement stmtDiscount = null;
		ResultSet rsDiscount = null;

		String discountQuery = "SELECT * FROM discount ORDER BY discount_DiscountName ASC";

		stmtDiscount = conn.prepareStatement(discountQuery);
		rsDiscount = stmtDiscount.executeQuery();

		while(rsDiscount.next()){
			int discountID = rsDiscount.getInt("discount_DiscountID");
			String discountName = rsDiscount.getString("discount_DiscountName");
			double discountAmount = rsDiscount.getDouble("discount_Amount");
			Boolean discountPercent = rsDiscount.getBoolean("discount_IsPercent");

			Discount discount = new Discount(discountID, discountName, discountAmount, discountPercent);
			discountList.add(discount);
		}

		rsDiscount.close();
		stmtDiscount.close();
		conn.close();
		return discountList;
	}

	public static Discount findDiscountByName(String name) throws SQLException, IOException
	{
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *
		 */


		connect_to_db();

		Discount discount = null;
		PreparedStatement stmtDiscount = null;
		ResultSet rsDiscount = null;

		String discountQuery = "SELECT discount_DiscountID, discount_Amount, discount_IsPercent " +
				"FROM discount WHERE discount_DiscountName = ?";

		stmtDiscount = conn.prepareStatement(discountQuery);
		stmtDiscount.setString(1, name);
		rsDiscount = stmtDiscount.executeQuery();

		if (rsDiscount.next()){
			int discountID = rsDiscount.getInt("discount_DiscountID");
			double discountAmount = rsDiscount.getDouble("discount_Amount");
			Boolean discountPercent = rsDiscount.getBoolean("discount_IsPercent");

			discount = new Discount(discountID, name, discountAmount, discountPercent);
		}

		rsDiscount.close();
		stmtDiscount.close();
		conn.close();
		return discount;
	}

	//Tested and Works
	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException
	{
		/*
		 * Query the data for all the customers and return an arrayList of all the customers.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		connect_to_db();
		ArrayList<Customer> customerList = new ArrayList<>();

		PreparedStatement stmtCustomer = null;
		ResultSet rsCustomer = null;

		String discountQuery = "SELECT * FROM customer ORDER BY customer_LName ASC";

		stmtCustomer = conn.prepareStatement(discountQuery);
		rsCustomer = stmtCustomer.executeQuery();

		while(rsCustomer.next()){
			int custID = rsCustomer.getInt("customer_CustID");
			String custFname = rsCustomer.getString("customer_FName");
			String custLName = rsCustomer.getString("customer_LName");
			String custPhoneNum = rsCustomer.getString("customer_PhoneNum");

			Customer customer = new Customer(custID, custFname, custLName, custPhoneNum);
			customerList.add(customer);
		}

		rsCustomer.close();
		stmtCustomer.close();
		conn.close();
		return customerList;
	}

	public static Customer findCustomerByPhone(String phoneNumber)  throws SQLException, IOException
	{
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *
		 */
		connect_to_db();
		int custID = -1;
		String custFirstName = null;
		String custLastName = null;
		String customerQuery = "SELECT customer_CustID, customer_FName, customer_LName " +
				"FROM customer WHERE customer_PhoneNum = ?";

		PreparedStatement stmtCustomer = conn.prepareStatement(customerQuery);
		stmtCustomer.setString(1, phoneNumber);
		ResultSet rsCustomer = stmtCustomer.executeQuery();

		if(rsCustomer.next()){
			custID = rsCustomer.getInt("customer_CustID");
			custFirstName = rsCustomer.getString("customer_FName");
			custLastName = rsCustomer.getString("customer_LName");
		}
		else {
			return null;
		}

		Customer customer = new Customer(custID, custFirstName, custLastName, phoneNumber);

		conn.close();
		stmtCustomer.close();
		rsCustomer.close();
		return customer;
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
		connect_to_db();
		ArrayList<Topping> toppingList = new ArrayList<>();

		PreparedStatement stmtTopping = null;
		ResultSet rsToppping = null;

		String discountQuery = "SELECT * FROM topping ORDER BY topping_TopName ASC";

		stmtTopping = conn.prepareStatement(discountQuery);
		rsToppping = stmtTopping.executeQuery();

		while(rsToppping.next()){
			int toppingID = rsToppping.getInt("topping_TopID");
			String topName = rsToppping.getString("topping_TopName");
			double smallAMT = rsToppping.getDouble("topping_SmallAMT");
			double medAMT = rsToppping.getDouble("topping_MedAMT");
			double lgAMT = rsToppping.getDouble("topping_LgAMT");
			double xLAMT = rsToppping.getDouble("topping_XLAMT");
			double custPrice = rsToppping.getDouble("topping_CustPrice");
			double busPrice = rsToppping.getDouble("topping_BusPrice");
			int minINVT = rsToppping.getInt("topping_MinINVT");
			int curINVT = rsToppping.getInt("topping_CurINVT");

			Topping topping = new Topping(toppingID, topName, smallAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
			toppingList.add(topping);
		}

		rsToppping.close();
		stmtTopping.close();
		conn.close();
		return toppingList;
	}

	public static Topping findToppingByName(String name) throws SQLException, IOException
	{
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *
		 */
		connect_to_db();

		Topping topping = null;
		PreparedStatement stmtTopping = null;
		ResultSet rsToppping = null;

		String discountQuery = "SELECT * FROM topping " +
				"WHERE topping_TopName = ?";

		stmtTopping = conn.prepareStatement(discountQuery);
		stmtTopping.setString(1, name);
		rsToppping = stmtTopping.executeQuery();

		if (rsToppping.next()){
			int toppingID = rsToppping.getInt("topping_TopID");
			double smallAMT = rsToppping.getDouble("topping_SmallAMT");
			double medAMT = rsToppping.getDouble("topping_MedAMT");
			double lgAMT = rsToppping.getDouble("topping_LgAMT");
			double xLAMT = rsToppping.getDouble("topping_XLAMT");
			double custPrice = rsToppping.getDouble("topping_CustPrice");
			double busPrice = rsToppping.getDouble("topping_BusPrice");
			int minINVT = rsToppping.getInt("topping_MinINVT");
			int curINVT = rsToppping.getInt("topping_CurINVT");

			topping = new Topping(toppingID, name, smallAMT, medAMT, lgAMT, xLAMT, custPrice, busPrice, minINVT, curINVT);
		}

		rsToppping.close();
		stmtTopping.close();
		conn.close();
		return topping;
	}

	public static ArrayList<Topping> getToppingsOnPizza(Pizza p) throws SQLException, IOException
	{
		/*
		 * This method builds an ArrayList of the toppings ON a pizza.
		 * The list can then be added to the Pizza object elsewhere in the
		 */
		connect_to_db();
		PreparedStatement stmtTopping = null;
		ResultSet rsTopping = null;
		ArrayList<Topping> toppingsList = new ArrayList<>();

		String toppingQuery = "SELECT pizza_topping.topping_TopID, pizza_topping.pizza_topping_IsDouble, " +
				"topping.topping_TopName, topping.topping_SmallAMT, topping.topping_MedAMT, topping.topping_LgAMT, topping.topping_XLAMT, " +
				"topping.topping_CusPrice, topping.topping_BusPrice, topping.topping_MinINVT, topping.topping_CurINVT " +
				"FROM pizza_topping " +
				"JOIN topping ON pizza_topping.topping_TopID = topping.topping_TopID " +
				"WHERE pizza_PizzaID = ?";

		stmtTopping = conn.prepareStatement(toppingQuery);
		stmtTopping.setInt(1, p.getPizzaID());
		rsTopping = stmtTopping.executeQuery();

		while(rsTopping.next()){
			int topID = rsTopping.getInt("topping_TopID");
			Boolean isDoubleTopping = rsTopping.getBoolean("pizza_topping_IsDouble");
			String topName = rsTopping.getString("topping_TopName");
			double smallAMT = rsTopping.getDouble("topping_SmallAMT");
			double medAMT = rsTopping.getDouble("topping_MedAMT");
			double lgAMT = rsTopping.getDouble("topping_LgAMT");
			double xLAMT = rsTopping.getDouble("topping_XLAMT");
			double custPrice = rsTopping.getDouble("topping_CusPrice");
			double busPrice = rsTopping.getDouble("topping_BusPrice");
			int minINVT = rsTopping.getInt("topping_MinINVT");
			int curINVT = rsTopping.getInt("topping_CurINVT");

			Topping topping = new Topping(topID, topName, smallAMT, medAMT, lgAMT, xLAMT,
					custPrice, busPrice, minINVT, curINVT);
			topping.setDoubled(isDoubleTopping);

			toppingsList.add(topping);

		}
		stmtTopping.close();
		rsTopping.close();
		conn.close();
		return toppingsList;
	}

	//Tested and Works
	public static void addToInventory(int toppingID, double quantity) throws SQLException, IOException
	{
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 *
		 * */
		connect_to_db();
		PreparedStatement stmtTopping = null;

		String toppingQuery = "UPDATE topping SET topping_CurINVT = topping_CurINVT + ? " +
				"WHERE topping_TopID = ?";

		stmtTopping = conn.prepareStatement(toppingQuery);
		stmtTopping.setDouble(1, quantity);
		stmtTopping.setInt(2, toppingID);

		stmtTopping.executeUpdate();
		stmtTopping.close();

		conn.close();
	}


	public static ArrayList<Pizza> getPizzas(Order o) throws SQLException, IOException
	{
		/*
		 * Build an ArrayList of all the Pizzas associated with the Order.
		 *
		 */
		//cassie can do this
		connect_to_db();
		PreparedStatement stmtPizza = null;
		ResultSet rsPizza = null;
		String PizzaQuery = "SELECT pizza_PizzaID, pizza_Size, pizza_CrustType, pizza_pizzaState, pizza_PizzaDate, pizza_CustPrice, pizza_BusPrice FROM pizza WHERE ordertable_OrderID = ?";

		stmtPizza = conn.prepareStatement(PizzaQuery);
		stmtPizza.setInt(1, o.getOrderID());
		rsPizza = stmtPizza.executeQuery();

		ArrayList<Pizza> pizzasList = new ArrayList<>();
		while(rsPizza.next()){

			int pizzaID = rsPizza.getInt("pizza_PizzaID");
			String pizzaSize = rsPizza.getString("pizza_Size");
			String pizzaCrustType = rsPizza.getString("pizza_CrustType");
			String pizzaState = rsPizza.getString("pizza_pizzaState");
			String pizzaDate = rsPizza.getString("pizza_PizzaDate");
			double pizzaCustPrice = rsPizza.getDouble("pizza_CustPrice");
			double pizzaBusPrice = rsPizza.getDouble("pizza_BusPrice");

			Pizza pizza = new Pizza(pizzaID, pizzaSize, pizzaCrustType, o.getOrderID(), pizzaState, pizzaDate,
					pizzaCustPrice, pizzaBusPrice);


			//get discounts
			ArrayList<Discount> discountListPizza = new ArrayList<>();
			discountListPizza = getDiscounts(pizza);
			pizza.setDiscounts(discountListPizza);


			//get toppings
			ArrayList<Topping> toppingList = new ArrayList<>();
			toppingList = getToppingsOnPizza(pizza);
			pizza.setToppings(toppingList);


			pizzasList.add(pizza);

		}

		rsPizza.close();
		stmtPizza.close();
		conn.close();
		return pizzasList;
	}

	public static ArrayList<Discount> getDiscounts(Order o) throws SQLException, IOException
	{
		/*
		 * Build an array list of all the Discounts associted with the Order.
		 *
		 */
		connect_to_db();
		ResultSet rsDiscount = null;
		PreparedStatement stmtDiscount = null;

		ArrayList<Discount> discountList = new ArrayList<>();

		String discountQuery = "SELECT " +
				"order_discount.discount_DiscountID, discount.discount_DiscountName, " +
				"discount.discount_Amount, discount.discount_IsPercent " +
				"FROM order_discount " +
				"JOIN discount ON order_discount.discount_DiscountID = discount.discount_DiscountID " +
				"WHERE order_discount.ordertable_OrderID = ?";

		stmtDiscount = conn.prepareStatement(discountQuery);
		stmtDiscount.setInt(1, o.getOrderID());
		rsDiscount = stmtDiscount.executeQuery();

		while(rsDiscount.next()){
			int discountID = rsDiscount.getInt("discount_DiscountID");
			String discountName = rsDiscount.getString("discount_DiscountName");
			double amount = rsDiscount.getDouble("discount_Amount");
			Boolean isPercent = rsDiscount.getBoolean("discount_IsPercent");

			Discount discount = new Discount(discountID, discountName, amount, isPercent);

			discountList.add(discount);

		}
		stmtDiscount.close();
		rsDiscount.close();
		conn.close();
		return discountList;
	}

	public static ArrayList<Discount> getDiscounts(Pizza p) throws SQLException, IOException
	{
		/*
		 * Build an array list of all the Discounts associted with the Pizza.
		 *
		 */
		connect_to_db();
		ResultSet rsDiscount = null;
		PreparedStatement stmtDiscount = null;

		ArrayList<Discount> discountList = new ArrayList<>();

		String discountQuery = "SELECT " +
				"pizza_discount.discount_DiscountID, discount.discount_DiscountName, " +
				"discount.discount_Amount, discount.discount_IsPercent " +
				"FROM pizza_discount " +
				"JOIN discount ON pizza_discount.discount_DiscountID = discount.discount_DiscountID " +
				"WHERE pizza_discount.pizza_PizzaID = ?";

		stmtDiscount = conn.prepareStatement(discountQuery);
		stmtDiscount.setInt(1, p.getPizzaID());
		rsDiscount = stmtDiscount.executeQuery();

		while(rsDiscount.next()){
			int discountID = rsDiscount.getInt("discount_DiscountID");
			String discountName = rsDiscount.getString("discount_DiscountName");
			double amount = rsDiscount.getDouble("discount_Amount");
			Boolean isPercent = rsDiscount.getBoolean("discount_IsPercent");

			Discount discount = new Discount(discountID, discountName, amount, isPercent);

			discountList.add(discount);

		}
		stmtDiscount.close();
		rsDiscount.close();
		conn.close();
		return discountList;
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database fro the base customer price for that size and crust pizza.
		 *
		 */
		//cassie can do this
		connect_to_db();

		double customerPrice = 0.0;
		PreparedStatement stmtCustPrice = null;
		ResultSet rsCustPrice = null;

		String discountQuery = "SELECT baseprice_CustPrice FROM baseprice " +
				"WHERE baseprice_Size = ? AND baseprice_CrustType = ?";

		stmtCustPrice = conn.prepareStatement(discountQuery);
		stmtCustPrice.setString(1, size);
		stmtCustPrice.setString(2, crust);
		rsCustPrice = stmtCustPrice.executeQuery();

		if (rsCustPrice.next()){
			customerPrice = rsCustPrice.getDouble("baseprice_CustPrice");
		}

		rsCustPrice.close();
		stmtCustPrice.close();
		conn.close();
		return customerPrice;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException
	{
		/*
		 * Query the database fro the base business price for that size and crust pizza.
		 *
		 */

		//cassie can do this
		connect_to_db();

		double businessPrice = 0.0;
		PreparedStatement stmtBusPrice = null;
		ResultSet rsBusPrice = null;

		String discountQuery = "SELECT baseprice_BusPrice FROM baseprice " +
				"WHERE baseprice_Size = ? AND baseprice_CrustType = ?";

		stmtBusPrice = conn.prepareStatement(discountQuery);
		stmtBusPrice.setString(1, size);
		stmtBusPrice.setString(2, crust);
		rsBusPrice = stmtBusPrice.executeQuery();

		if (rsBusPrice.next()){
			businessPrice = rsBusPrice.getDouble("baseprice_BusPrice");
		}

		rsBusPrice.close();
		stmtBusPrice.close();
		conn.close();
		return businessPrice;
	}


	//Tested and Works
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
		connect_to_db();
		PreparedStatement toppingView = null;
		ResultSet rsView = null;
		String view = "SELECT *" + "FROM ToppingPopularity";
		toppingView = conn.prepareStatement(view);
		rsView = toppingView.executeQuery(view);

		System.out.println("Topping             Topping Count");
		System.out.println("-------             -------------");

		while (rsView.next()) {
			String name = rsView.getString("Topping");
			Integer count = rsView.getInt("ToppingCount");

			System.out.printf("%-20s%-13d\n",name, count);
		}
		conn.close();

	}

	//Tested and Works
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
		connect_to_db();
		PreparedStatement profitPizzaView = null;
		ResultSet rsView = null;
		String view = "SELECT *" + "FROM ProfitByPizza";
		profitPizzaView = conn.prepareStatement(view);
		rsView = profitPizzaView.executeQuery(view);

		System.out.println("Pizza Size          Pizza Crust         Profit              Last Order Date");
		System.out.println("----------          -----------         ------              ---------------");

		while (rsView.next()) {
			String size = rsView.getString("Size");
			String crust = rsView.getString("Crust");
			Float profit = rsView.getFloat("Profit");
			String month = rsView.getString("OrderMonth");
			System.out.printf("%-20s%-20s%-20.2f%-15s\n",size, crust, profit, month);
		}
		conn.close();
	}

	//Tested and Works
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
		connect_to_db();
		PreparedStatement profitOrderView = null;
		ResultSet rsView = null;
		String view = "SELECT *" + "FROM ProfitByOrderType";
		profitOrderView = conn.prepareStatement(view);
		rsView = profitOrderView.executeQuery(view);

		System.out.println("Customer Type       Order Month         Total Order Price   Total Order Cost    Profit");
		System.out.println("-------------       -----------         -----------------   ----------------    ------");

		while (rsView.next()) {
			String type = rsView.getString("customerType");
			String month = rsView.getString("OrderMonth");
			Float price = rsView.getFloat("TotalOrderPrice");
			Float cost = rsView.getFloat("TotalOrderCost");
			Float profit = rsView.getFloat("Profit");
			System.out.printf("%-20s%-20s%-20.2f%-20.2f%-6.2f\n",(type == null) ? " " : type,month,price,cost,profit);
		}
		conn.close();
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
}