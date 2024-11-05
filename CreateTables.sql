/*Create ERD Tables*/

DROP SCHEMA IF EXISTS PizzaDB;
CREATE SCHEMA PizzaDB;
USE PizzaDB;

CREATE TABLE baseprice
(
	
	baseprice_Size VARCHAR(30) NOT NULL,
    baseprice_CrustType VARCHAR(30) NOT NULL,
    baseprice_CustPrice DECIMAL(5,2),
    baseprice_BusPrice DECIMAL(5,2),
    PRIMARY KEY(baseprice_Size, baseprice_CrustType)
);

CREATE TABLE customer
(
	customer_CustID INT PRIMARY KEY NOT NULL,
    customer_FName VARCHAR(30),
    customer_LName VARCHAR(30),
    customer_PhoneNum VARCHAR(30)
);

CREATE TABLE topping
(
	topping_TopID INT PRIMARY KEY NOT NULL,
    topping_TopName VARCHAR(30),
    topping_SmallAMT DECIMAL(5,2),
    topping_MedAMT DECIMAL(5,2),
    topping_LgAMT DECIMAL(5,2),
    topping_XLAMT DECIMAL(5,2),
    topping_CustPrice DECIMAL(5,2),
    topping_BusPrice DECIMAL(5,2),
    topping_MinINVT INT,
    topping_CurINVT INT
);

CREATE TABLE discount
(
	discount_DiscountID INT PRIMARY KEY NOT NULL,
    discount_DiscountName VARCHAR(30),
    discount_Amount DECIMAL(5,2),
    discount_IsPercent BOOLEAN
);

CREATE TABLE ordertable
(
	ordertable_OrderID INT PRIMARY KEY NOT NULL,
    customer_CustID INT,
    ordertable_OrderType VARCHAR(30),
    ordertable_OrderDateTime DATETIME,
    ordertable_CustPrice DECIMAL(5,2),
    ordertable_BusPrice DECIMAL(5,2),
    ordertable_isComplete BOOLEAN,
    FOREIGN KEY(customer_CustID) REFERENCES customer(customer_CustID)
);

CREATE TABLE pizza
(
	pizza_PizzaID INT PRIMARY KEY NOT NULL,
    pizza_Size VARCHAR(30),
    pizza_CrustType VARCHAR(30),
    pizza_PizzaState VARCHAR(30),
    pizza_PizzaDate DATETIME,
    pizza_CustPrice DECIMAL(5,2),
    pizza_BusPrice DECIMAL(5,2),
    ordertable_OrderID INT,
    FOREIGN KEY(ordertable_OrderID) REFERENCES ordertable(ordertable_OrderID),
    FOREIGN KEY(pizza_Size) REFERENCES baseprice(baseprice_Size),
    FOREIGN KEY(pizza_CrustType) REFERENCES baseprice(baseprice_CrustType)
);

CREATE TABLE pizza_topping
(
	pizza_PizzaID INT NOT NULL,
    topping_TopID INT NOT NULL,
    pizza_topping_IsDouble INT,
    PRIMARY KEY(pizza_PizzaID, topping_TopID),
    FOREIGN KEY(pizza_PizzaID) REFERENCES pizza(pizza_PizzaID),
    FOREIGN KEY(topping_TopID) REFERENCES topping(topping_TopID)
);

CREATE TABLE pizza_discount
(
	pizza_PizzaID INT NOT NULL, 
    discount_DiscountID INT NOT NULL,
    PRIMARY KEY(pizza_PizzaID, discount_DiscountID),
    FOREIGN KEY(pizza_PizzaID) REFERENCES pizza(pizza_PizzaID),
    FOREIGN KEY(discount_DiscountID) REFERENCES discount(discount_DiscountID)
);

CREATE TABLE order_discount
(
	ordertable_OrderID INT NOT NULL, 
    discount_DiscountID INT NOT NULL,
    PRIMARY KEY(ordertable_OrderID, discount_DiscountID),
    FOREIGN KEY(ordertable_OrderID) REFERENCES ordertable(ordertable_OrderID),
    FOREIGN KEY(discount_DiscountID) REFERENCES discount(discount_DiscountID)
);

CREATE TABLE pickup
(
	ordertable_OrderID INT PRIMARY KEY NOT NULL, 
    pickup_IsPickedUp BOOLEAN,
    FOREIGN KEY(ordertable_OrderID) REFERENCES ordertable(ordertable_OrderID)
);

CREATE TABLE delivery
(
	ordertable_OrderID INT PRIMARY KEY NOT NULL, 
    delivery_HouseNum INT,
    delivery_Street VARCHAR(30),
    delivery_City VARCHAR(30),
    delivery_State VARCHAR(2),
    delivery_Zip INT,
    delivery_IsDelivered BOOLEAN,
    FOREIGN KEY(ordertable_OrderID) REFERENCES ordertable(ordertable_OrderID)
);

 CREATE TABLE dinein
(
	ordertable_OrderID INT PRIMARY KEY NOT NULL, 
    dinein_TableNum INT,
    FOREIGN KEY(ordertable_OrderID) REFERENCES ordertable(ordertable_OrderID)
);