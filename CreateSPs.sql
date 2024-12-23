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
CREATE FUNCTION CalcOrderPrice(orderID INT, discount INT) RETURNS int DETERMINISTIC
BEGIN
	DECLARE totalPrice INT;
	DECLARE totalPriceWithoutDiscount INT;
	DECLARE discountPercentage INT;
	SET totalPriceWithoutDiscount = (
		SELECT
			SUM(ordertable_CustPrice) 
		FROM 
			ordertable
		WHERE
			ordertable_OrderID = orderID
	);
	SET discountPercentage = (
		SELECT
			discount.discount_IsPercent
		FROM 
			discount
			JOIN order_discount ON discount.discount_DiscountID = order_discount.discount_DiscountID
		WHERE
			order_discount.ordertable_OrderID = orderID
	);

	IF(discountPercentage = 1) THEN
		SET totalPrice = totalPriceWithoutDiscount * (1 - discount);
	ELSE
		SET totalPrice = totalPriceWithoutDiscount - discount; 
	END IF;

	RETURN (totalPrice);
	
END 
$$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION CalcPizzaPrice(pizzaID INT) RETURNS int DETERMINISTIC
BEGIN
	DECLARE totalPrice INT;
	DECLARE totalToppings INT;
	DECLARE discountPercentage INT;
	DECLARE totalPizza INT;
	DECLARE tempTopping INT;
	DECLARE discount INT;
	SET totalPizza = (
		SELECT
			baseprice.baseprice_CustPrice
		FROM 
			baseprice,
			pizza
		WHERE
			baseprice.baseprice_Size = pizza.pizza_Size
			AND baseprice.baseprice_CrustType = pizza.CrustType
	);

	SET totalToppings = (
		SELECT
			SUM(IF(pizza_topping.pizza_topping_IsDouble = 1, topping.topping_CustPrice * 2, topping.topping_CustPrice))		
		FROM
			topping
			JOIN pizza_topping ON pizza_topping.topping_TopID = topping.topping_TopID
			JOIN pizza ON pizza_topping.pizza_PizzaID = pizza.pizza_PizzaID
		WHERE
			pizza.pizza_PizzaID = pizzaID
			AND pizza_top.ping.topping_TopID = topping.topping_TopID
	);

	SET discountPercentage = (
		SELECT
			discount.discount_IsPercent
		FROM 
			discount
			JOIN pizza_discount ON discount.discount_DiscountID = pizza_discount.discount_DiscountID
		WHERE
			pizza_discount.pizza_PizzaID = orderID
	);

	SET discount = (
		SELECT
			discount.discount_Amount
		FROM 
			discount
			JOIN pizza_discount ON discount.discount_DiscountID = pizza_discount.discount_DiscountID
		WHERE
			pizza_discount.pizza_PizzaID = orderID
	);


	IF(discountPercentage = 1) THEN
		SET totalPrice = totalToppings + totalPizza * (1 - discount);
	ELSE
		SET totalPrice = totalToppings + totalPizza - discount; 
	END IF;

	RETURN (totalPrice);
END 
$$
DELIMITER ;
/*
DROP TRIGGER IF EXISTS UpdatePizzaPrice
DELIMITER $$
CREATE TRIGGER UpdatePizzaPrice
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
   IF(new.baseprice_Size != old.baseprice_Size) THEN
		SELECT CalcPizzaPrice(new.pizzaID INT);
	ELSE IF (new.baseprice_Size != old.baseprice_Size)
		SELECT CalcPizzaPrice(new.pizzaID INT);
	END IF;
END;
$$
DELIMITER;

DROP TRIGGER IF EXISTS pizzaToppings
DELIMITER $$
CREATE TRIGGER pizzaToppings
AFTER UPDATE ON pizza_toppinfs
FOR EACH ROW
BEGIN
	IF(new.pizza_topping_IsDouble != old.pizza_topping_IsDouble) THEN
		SELECT CalcPizzaPrice(new.pizzaID INT);
	END IF;
		
END;
$$
DELIMITER;
*/