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
