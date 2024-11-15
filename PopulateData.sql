/*Populate Data*/
/*Mary Walker Felder and Cassie Phillips*/

INSERT INTO topping (topping_TopName, topping_SmallAMT, topping_MedAMT, topping_LgAMT, topping_XLAMT, topping_CustPrice, topping_BusPrice, topping_MinINVT, topping_CurINVT)
VALUES 
('Pepperoni', '2', '2.75', '3.5','4.5', '1.25', '0.2', '50', '100'),
('Sausage', '2.5', '3', '3.5','4.25', '1.25', '0.15', '50', '100'),
('Ham', '2', '2.5', '3.25','4', '1.5', '0.15', '25', '78'),
('Chicken', '1.5', '2', '3.25','4', '1.25', '0.25', '25', '56'),
('Green Pepper', '1', '1.5', '2','2.5', '1.25', '0.02', '25', '79'),
('Onion', '1', '1.5', '2','2.75', '1.25', '0.02', '25', '85'),
('Roma Tomato', '2', '3', '3.5','4.5', '1.25', '0.03', '10', '86'),
('Mushrooms', '1.5', '2', '2.5','3', '1.25', '0.1', '50', '52'),
('Black Olives', '0.75', '1', '1.5','2', '1.25', '0.1', '25', '39'),
('Pineapple', '1', '1.25', '1.75','2', '1.25', '0.25', '0', '15'),
('Jalapenos', '0.5', '0.75', '1.25','1.75', '1.25', '0.05', '0', '64'),
('Banana Peppers', '0.6', '1', '1.3','1.75', '1.25', '0.05', '0', '36'),
('Regular Cheese', '2', '3.5', '5','7', '1.25', '0.12', '50', '250'),
('Four Cheese Blend', '2', '3.5', '5','7', '1.25', '0.15', '25', '150'),
('Feta Cheese', '1.75', '3', '4','5.5', '1.25', '0.18', '0', '75'),
('Goat Cheese', '1.6', '2.75', '4','5.5', '1.25', '0.2', '0', '54'),
('Bacon', '1', '1.5', '2','3', '1.25', '0.25', '0', '89');
	
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
('Employee','0.15',TRUE),
('Lunch Special Medium','1.00',FALSE),
('Lunch Special Large','2.00',FALSE),
('Specialty Pizza','1.50',FALSE),
('Happy Hour','0.10',TRUE),
('Gameday Special','0.20',TRUE);

INSERT INTO customer
VALUES
('1', 'Andrew', 'Wilkes-Krier', '864-254-5861'),
('2', 'Matt', 'Engers', '864-474-9953'),
('3', 'Frank', 'Turner', '864-232-8944'),
('4', 'Milo', 'Auckerman', ' 864-878-5679');

-- Order One --
INSERT INTO ordertable (ordertable_OrderID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
VALUES
('1', 'dinein', '2024-03-05 12:03:00', '19.75', '3.68', '1');

INSERT INTO dinein
VALUES
('1', '21');

INSERT INTO pizza (pizza_PizzaID, pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
VALUES
('1', 'Large', 'Thin', 'complete', '2024-03-05 12:03:00', '19.75', '3.68', '1');

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
(SELECT ordertable_OrderID, '4' FROM ordertable
WHERE ordertable.ordertable_OrderType = 'dinein' AND ordertable.ordertable_OrderDateTime = '2024-04-03 12:05:00');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Medium', 'Pan', 'complete', ordertable_OrderDateTime, '12.85', '3.23', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'dinein' AND ordertable.ordertable_OrderDateTime = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '15', '0' FROM pizza WHERE pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '9', '0' FROM pizza WHERE pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping 
(SELECT pizza_PizzaID, '7', '0' FROM pizza WHERE pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '8', '0' FROM pizza WHERE pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping (pizza_PizzaID, topping_TopID, pizza_topping_IsDouble)
(SELECT pizza_PizzaID, '12', '0' FROM pizza WHERE pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_discount
(SELECT pizza_PizzaID, discount_DiscountID FROM pizza JOIN discount 
WHERE pizza.pizza_PizzaDate = '2024-04-03 12:05:00' AND pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND discount.discount_DiscountName = 'Lunch Special Medium');

INSERT INTO pizza_discount
(SELECT pizza_PizzaID, discount_DiscountID FROM pizza JOIN discount 
WHERE pizza.pizza_PizzaDate = '2024-04-03 12:05:00' AND pizza.pizza_Size = 'Medium' AND pizza.pizza_CrustType = 'Pan' AND discount.discount_DiscountName = 'Specialty Pizza');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Small', 'Original', 'complete', ordertable_OrderDateTime, '6.93', '1.40', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'dinein' AND ordertable.ordertable_OrderDateTime = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '13', '0' FROM pizza WHERE pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '4', '0' FROM pizza WHERE pizza.pizza_Size = 'Small' AND pizza.pizza_CrustType = 'Original' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

INSERT INTO pizza_topping
(SELECT pizza_PizzaID, '12', '0' FROM pizza WHERE pizza.pizza_Size = 'Small' AND pizza.pizza_CrustType = 'Original' AND pizza.pizza_PizzaDate = '2024-04-03 12:05:00');

-- Order Three --
INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
(SELECT customer.customer_CustID, 'pickup', '2024-03-03 9:30:00', '89.28', '19.80', '1' FROM customer 
WHERE customer_FName = 'Andrew' AND customer_LName = 'Wilkes-Krier');

INSERT INTO pickup
(SELECT ordertable.ordertable_OrderID, '1' FROM ordertable WHERE ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('4', '1', '0'), ('4', '13', '0');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('5', '1', '0'), ('5', '13', '0');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('6', '1', '0'), ('6', '13', '0');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('7', '1', '0'), ('7', '13', '0');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('8', '1', '0'), ('8', '13', '0');

INSERT INTO pizza (pizza_Size, pizza_CrustType, pizza_PizzaState,  pizza_PizzaDate,  pizza_CustPrice, pizza_BusPrice, ordertable_OrderID)
(SELECT 'Large', 'Original', 'complete', ordertable_OrderDateTime, '14.88', '3.30', ordertable_OrderID FROM ordertable
WHERE ordertable.ordertable_OrderType = 'pickup' AND ordertable.ordertable_OrderDateTime = '2024-03-03 9:30:00');

INSERT INTO pizza_topping
VALUES
('9', '1', '0'), ('9', '13', '0');

-- Order Four -- 
INSERT INTO ordertable (customer_CustID, ordertable_OrderType, ordertable_OrderDateTime, ordertable_CustPrice, ordertable_BusPrice, ordertable_isComplete)
(SELECT customer.customer_CustID, 'pickup', '2024-04-20 7:11:00', '89.28', '19.80', '1' FROM customer 
WHERE customer_FName = 'Andrew' AND customer_LName = 'Wilkes-Krier');

