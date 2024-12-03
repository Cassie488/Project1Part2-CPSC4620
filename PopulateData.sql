/*Populate Data*/
/*Mary Walker Felder and Cassie Phillips*/

INSERT INTO topping (topping_TopName, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT, topping_CustPrice, topping_BusPrice, topping_MinINVT, topping_CurINVT)
VALUES 
('Pepperoni', '2', '2.75', '3.5','4.5', '1.25', '0.2', '50', '100'),
('Sausage', '2.5', '3', '3.5','4.25', '1.25', '0.15', '50', '100'),
('Ham', '2', '2.5', '3.25','4', '1.5', '0.15', '25', '78'),
('Chicken', '1.5', '2', '2.25','3', '1.75', '0.25', '25', '56'),
('Green Pepper', '1', '1.5', '2','2.5', '0.5', '0.02', '25', '79'),
('Onion', '1', '1.5', '2','2.75', '0.5', '0.02', '25', '85'),
('Roma Tomato', '2', '3', '3.5','4.5', '0.75', '0.03', '10', '86'),
('Mushrooms', '1.5', '2', '2.5','3', '0.75', '0.1', '50', '52'),
('Black Olives', '0.75', '1', '1.5','2', '0.6', '0.1', '25', '39'),
('Pineapple', '1', '1.25', '1.75','2', '1', '0.25', '0', '15'),
('Jalapenos', '0.5', '0.75', '1.25','1.75', '0.5', '0.05', '0', '64'),
('Banana Peppers', '0.6', '1', '1.3','1.75', '0.5', '0.05', '0', '36'),
('Regular Cheese', '2', '3.5', '5','7', '0.5', '0.12', '50', '250'),
('Four Cheese Blend', '2', '3.5', '5','7', '1', '0.15', '25', '150'),
('Feta Cheese', '1.75', '3', '4','5.5', '1.5', '0.18', '0', '75'),
('Goat Cheese', '1.6', '2.75', '4','5.5', '1.5', '0.2', '0', '54'),
('Bacon', '1', '1.5', '2','3', '1.5', '0.25', '0', '89');
	
INSERT INTO baseprice
VALUES
('Small','Thin','3','0.5'),
('Small','Original','3','0.75'),
('Small','Pan','3.5','1'),
('Small','Gluten-Free','4','2'),
('Medium','Thin','5','1'),
('Medium','Original','5','1.5'),
('Medium','Pan','6','2.25'),
('Medium','Gluten-Free','6.25','3'),
('Large','Thin','8','1.25'),
('Large','Original','8','2'),
('Large','Pan','9','3'),
('Large','Gluten-Free','9.5','4'),
('XLarge','Thin','10','2'),
('XLarge','Original','10','3'),
('XLarge','Pan','11.5','4.5'),
('XLarge','Gluten-Free','12.5','6');

INSERT INTO discount (discount_DiscountName, discount_Amount, discount_IsPercent)
VALUES 
('Employee','15',TRUE),
('Lunch Special Medium','1.00',FALSE),
('Lunch Special Large','2.00',FALSE),
('Specialty Pizza','1.50',FALSE),
('Happy Hour','10',TRUE),
('Gameday Special','20',TRUE);

INSERT INTO customer
VALUES
('1', 'Andrew', 'Wilkes-Krier', '8642545861'),
('2', 'Matt', 'Engers', '8644749953'),
('3', 'Frank', 'Turner', '8642328944'),
('4', 'Milo', 'Auckerman', '8648785679');

-- Order One --
INSERT INTO ordertable (ordertable_OrderID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('1', 'dinein', '2024-03-05 12:03:00', '19.75', '3.68', '1');

INSERT INTO dinein
VALUES
('1', '21');

INSERT INTO pizza 
VALUES
('1', 'Large', 'Thin', 'completed', '2024-03-05 12:03:00', '19.75', '3.68', '1');

INSERT INTO pizza_topping
VALUES
('1', '13', '1'),
('1', '1', '0'),
('1', '2', '0');

INSERT INTO pizza_discount
VALUES
('1', '3');

-- Order Two -- 
INSERT INTO ordertable (ordertable_OrderID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('2', 'dinein', '2024-04-03 12:05:00', '19.78', '4.63', '1');

INSERT INTO dinein
VALUES
('2', '4');

INSERT INTO pizza
VALUES
('2', 'Medium', 'Pan', 'completed', '2024-04-03 12:05:00', '12.85', '3.23', '2');

INSERT INTO pizza_topping
VALUES
('2', '15', '0'),
('2', '9', '0'),
('2', '7', '0'),
('2', '8', '0'),
('2', '12', '0');

INSERT INTO pizza_discount
VALUES
('2', '4');

INSERT INTO pizza
VALUES
('3', 'Small', 'Original', 'completed', '2024-04-03 12:05:00', '6.93', '1.40', '2');

INSERT INTO pizza_topping
VALUES
('3', '13', '0'),
('3', '4', '0'),
('3', '12', '0');

INSERT INTO order_discount
VALUES
('2', '2');

-- Order Three --
INSERT INTO ordertable (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('3', '1', 'pickup', '2024-03-03 21:30:00', '89.28', '19.80', '1');

INSERT INTO pickup
VALUES
('3', '1');

INSERT INTO pizza
VALUES
('4', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3'),
('5', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3'),
('6', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3'),
('7', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3'),
('8', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3'),
('9', 'Large', 'Original', 'completed', '2024-03-03 21:30:00', '14.88', '3.30', '3');

INSERT INTO pizza_topping
VALUES
('4', '1', '0'), 
('4', '13', '0'),
('5', '1', '0'), 
('5', '13', '0'),
('6', '1', '0'), 
('6', '13', '0'),
('7', '1', '0'), 
('7', '13', '0'),
('8', '1', '0'), 
('8', '13', '0'),
('9', '1', '0'), 
('9', '13', '0');

-- Order Four -- 
INSERT INTO ordertable (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('4', '1', 'delivery', '2024-04-20 19:11:00', '68.95', '23.62', '1');

INSERT INTO delivery 
VALUES
('4', '115', 'Party Blvd', 'Anderson', 'SC', '29621', '1');

INSERT INTO pizza
VALUES
('10', 'XLarge', 'Original', 'completed', '2024-04-20 19:11:00', '27.94', '9.19', '4'),
('11', 'XLarge', 'Original', 'completed', '2024-04-20 19:11:00', '31.50', '6.25', '4'),
('12', 'XLarge', 'Original', 'completed', '2024-04-20 19:11:00', '26.75', '8.18', '4');

INSERT INTO pizza_topping
VALUES
('10', '1', '0'), 
('10', '2', '0'), 
('10', '14', '0'), 
('11', '3', '1'), 
('11', '10', '1'), 
('11', '14', '0'), 
('12', '4', '0'), 
('12', '17', '0'),
('12', '14', '0');

INSERT INTO pizza_discount
VALUES
('11', '4');

INSERT INTO order_discount
VALUES
('4', '6');

-- Order Five --
INSERT INTO ordertable (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('5', '2', 'pickup', '2024-03-02 17:30:00', '27.45', '7.88', '1');

INSERT INTO pickup
VALUES
('5', '1');

INSERT INTO pizza
VALUES
('13', 'XLarge', 'Gluten-Free', 'completed', '2024-03-02 17:30:00', '27.45', '7.88', '5');

INSERT INTO pizza_topping
VALUES
('13', '5', '0'), 
('13', '6', '0'), 
('13', '7', '0'), 
('13', '8', '0'), 
('13', '9', '0'), 
('13', '16', '0');

INSERT INTO pizza_discount
VALUES
('13', '4');

-- Order Six --
INSERT INTO ordertable (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('6', '3', 'delivery', '2024-03-02 18:17:00', '25.81', '4.24', '1');

INSERT INTO delivery 
VALUES
('6', '6745', 'Wessex St', 'Anderson', 'SC', '29621', '1');

INSERT INTO pizza
VALUES
('14', 'Large', 'Thin', 'completed', '2024-03-02 18:17:00', '25.81', '4.24', '6');

INSERT INTO pizza_topping
VALUES
('14', '4', '0'), 
('14', '5', '0'), 
('14', '6', '0'), 
('14', '8', '0'), 
('14', '14', '1');

-- Order Seven --
INSERT INTO ordertable (ordertable_OrderID, customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('7', '4', 'delivery', '2024-04-13 20:32:00', '31.66', '6.00', '1');

INSERT INTO delivery 
VALUES
('7', '8879', 'Suburban', 'Anderson', 'SC', '29621', '1');

INSERT INTO pizza
VALUES
('15', 'Large', 'Thin', 'completed', '2024-04-13 20:32:00', '18.00', '2.75', '7'),
('16', 'Large', 'Thin', 'completed', '2024-04-13 20:32:00', '19.25', '3.25', '7');

INSERT INTO pizza_topping
VALUES
('15', '14', '1'), 
('16', '1', '1'), 
('16', '13', '0');

INSERT INTO order_discount
VALUES
('7', '1');