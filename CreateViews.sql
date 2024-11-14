/*Create Views*/
/*Mary Walker Felder and Cassie Phillips*/

CREATE VIEW ToppingPopularity AS 
	SELECT  topping.topping_TopName AS Topping, 
			COUNT(pizza_topping.topping_TopID) AS ToppingCount
	FROM topping JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
	ORDER BY ToppingCount;
        
CREATE VIEW ProfitByPizza AS 
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

CREATE VIEW ProfitByOrderType AS 
	SELECT 
		customerType,
		OrderMonth,
		TotalOrderPrice, 
		TotalOrderCost, 
		Profit
	FROM 
			(SELECT 
				ordertable_OrderType AS customerType,
				DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y") AS OrderMonth,
				ordertable_CustPrice AS TotalOrderPrice, 
				ordertable_BusPrice AS TotalOrderCost, 
				(ordertable_CustPrice - ordertable_BusPrice) AS Profit
			FROM
				ordertable
			GROUP BY
				ordertable_OrderType, DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y")) TEMP1

		UNION ALL

			(SELECT
				NULL,
				'Grand Total',
				SUM(ordertable_CustPrice),
				SUM(ordertable_BusPrice),
				SUM(((ordertable_CustPrice - ordertable_BusPrice)))
			FROM
				ordertable
			GROUP BY
				ordertable_OrderType, DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y")) 
	ORDER BY
		(CASE
			WHEN OrderMonth = 'Grand Total' THEN 1
			ELSE 0
		END),
		customerType, 
		Profit;
		