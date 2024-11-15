CREATE PROCEDURE profitOfPizza()
BEGIN
    SELECT 
		pizza.pizza_Size AS Size,
		pizza.pizza_CrustType AS Crust,
		(pizza.pizza_CustPrice - pizza.pizza_BusPrice) AS Profit,
		DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y") AS OrderMonth
	FROM 
		pizza
		JOIN ordertable ON pizza.ordertable_OrderID = ordertable.ordertable_OrderID
	GROUP BY
		OrderMonth
	ORDER BY
		Profit DESC; 
END;



CREATE PROCEDURE toppingsForView()
BEGIN
    SELECT  
        topping.topping_TopName AS Topping, 
		COUNT(pizza_topping.topping_TopID) AS ToppingCount
	FROM 
        topping 
        JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
	ORDER BY 
        ToppingCount
END;

CREATE FUNCTION getMonthYearFromDate(date1 DATE) RETURNS VARCHAR(20) DETERMINISTIC
	RETURN(DATE_FORMAT(date1, "%m/%Y"));