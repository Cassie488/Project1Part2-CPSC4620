DROP PROCEDURE IF EXISTS getOrderDiscount;
DELIMITER $$
CREATE PROCEDURE GetOrderDiscount(IN idForDiscount INT, OUT discountReturn INT)
BEGIN
	SET discountReturn = (
		SELECT
			discount_Amount
		FROM
			discount
		WHERE
			discount_discountID = 
				(SELECT
					discount_DiscountID
				FROM
					order_discount
				WHERE
					ordertable_OrderID = idForDiscount
				)
	);
END;
$$
DELIMITER ;


DROP PROCEDURE IF EXISTS getPizzaDiscount;
DELIMITER $$
CREATE PROCEDURE GetPizzaDiscount(IN idForDiscount INT, OUT discountReturn INT)
BEGIN
	SET discountReturn = (
		SELECT
			discount_Amount
		FROM
			discount
		WHERE
			discount_discountID = 
				(SELECT
					discount_DiscountID
				FROM
					pizza_discount
				WHERE
					pizza_PizzaID = idForDiscount
				)
	);
END;
$$
DELIMITER ;


DELIMITER $$
CREATE FUNCTION CalcOrderPrice(date1 date) RETURNS int DETERMINISTIC
BEGIN
	
END 
$$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION CalcPizzaPrice(date1 date) RETURNS int DETERMINISTIC
BEGIN
	
END 
$$
DELIMITER ;

DROP TRIGGER IF EXISTS UpdatePizzaPrice
DELIMITER $$
CREATE TRIGGER UpdatePizzaPrice
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
DELIMITER;

DROP TRIGGER IF EXISTS UpdateBasePrice
DELIMITER $$
CREATE TRIGGER UpdateBasePrice
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
DELIMITER;

DROP TRIGGER IF EXISTS TopppingsAndInventory
DELIMITER $$
CREATE TRIGGER TopppingsAndInventory
AFTER INSERT ON pizza
FOR EACH ROW
BEGIN
	IF (
		SELECT 
			topping_MinINVT,
			topping_CurINVT
		FROM 
			topping
		WHERE 
			topping_MinINVT > topping_CurINVT
		) 
		THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'There are not enough toppings';
	 END IF;
END;
$$
DELIMITER;

DROP TRIGGER IF EXISTS InsertPhoneNumber
DELIMITER $$
CREATE TRIGGER InsertPhoneNumber
AFTER INSERT 
ON pizza FOR EACH ROW
BEGIN
	SELECT
		replace(customer_PhoneNum, '-', '') customer_PhoneNum
	FROM
		customer
END;
$$
DELIMITER;
