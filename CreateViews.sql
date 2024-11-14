/*Create Views*/
/*Mary Walker Felder and Cassie Phillips*/

CREATE VIEW ToppingPopularity AS 
	SELECT  topping.topping_TopName AS Topping, 
			COUNT(pizza_topping.topping_TopID) AS ToppingCount
	FROM topping JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
	ORDER BY ToppingCount;
        
/*CREATE VIEW ProfitByPizza AS 
	SELECT 
		pizza.pizza_Size AS Size,
		pizza.pizza_CrustType AS Crust,
		(pizza.pizza_CustPrice - pizza.pizza_BusPrice) AS Profit,
		DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y") AS OrderMonth
	FROM 
		pizza
		JOIN ordertable ON pizza.ordertable_OrderID = ordertable.ordertable.OrderID
	GROUP BY
		OrderMonth
	ORDER BY
		Profit DESC; 

CREATE VIEW ProfitByOrderType AS 
	SELECT 
	FROM ;
