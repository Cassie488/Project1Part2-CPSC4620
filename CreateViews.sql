/*Create Views*/
/*Mary Walker Felder and Cassie Phillips*/

CREATE VIEW ToppingPopularity AS 
	SELECT  topping.topping_TopName AS Topping, 
			(CAST((count(case when pizza_topping.pizza_topping_IsDouble = 0 then 1 end) +
					(count(case when pizza_topping.pizza_topping_IsDouble != 0 then 2 end)*2)) AS DECIMAL(4,0))) AS ToppingCount
	FROM topping LEFT JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
    GROUP BY topping.topping_TopName
	ORDER BY ToppingCount DESC, Topping ASC;
        
CREATE VIEW ProfitByPizza AS 
	SELECT 
		pizza.pizza_Size AS Size,
		pizza.pizza_CrustType AS Crust,
		SUM(pizza.pizza_CustPrice - pizza.pizza_BusPrice) AS Profit,
		DATE_FORMAT(pizza_PizzaDate, "%c/%Y") AS OrderMonth
	FROM 
		pizza
	GROUP BY
		Size, Crust, OrderMonth
	ORDER BY
		Profit; 

CREATE VIEW ProfitByOrderType AS 
	SELECT 
		TEMP1.customerType,
		TEMP1.OrderMonth,
		TEMP1.TotalOrderPrice, 
		TEMP1.TotalOrderCost, 
		TEMP1.Profit
	FROM 
			(SELECT 
				ordertable_OrderType AS customerType,
				DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%c/%Y") AS OrderMonth,
				SUM(ordertable_CustPrice) AS TotalOrderPrice, 
				SUM(ordertable_BusPrice) AS TotalOrderCost, 
				((SUM(ordertable_CustPrice)) - (SUM(ordertable_BusPrice))) AS Profit, 
                1 as o
			FROM
				ordertable
			GROUP BY
				ordertable_OrderType, DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%c/%Y")
			UNION SELECT 
				NULL, 
                'Grand Total', 
                SUM(ordertable_CustPrice), 
                SUM(ordertable_BusPrice), 
                SUM((ordertable_CustPrice - ordertable_BusPrice)),
                2 as o
                FROM ordertable ) TEMP1
	ORDER BY
		o, CASE customerType 
				WHEN 'dinein' THEN 1
                WHEN 'pickup' THEN 2 
                WHEN 'delivery' THEN 3
                ELSE 4
                END, TEMP1.Profit DESC;