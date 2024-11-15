/*Create Views*/
/*Mary Walker Felder and Cassie Phillips*/

CREATE VIEW ToppingPopularity AS 
	SELECT  topping.topping_TopName AS Topping, 
			ROUND(COUNT(pizza_topping.topping_TopID),0) AS ToppingCount
	FROM topping LEFT JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
    GROUP BY topping.topping_TopID 
	ORDER BY ToppingCount DESC, Topping ASC;
        
CREATE VIEW ProfitByPizza AS 
	SELECT 
		pizza.pizza_Size AS Size,
		pizza.pizza_CrustType AS Crust,
		SUM(pizza.pizza_CustPrice - pizza.pizza_BusPrice) AS Profit,
		DATE_FORMAT(pizza_PizzaDate, "%m/%Y") AS OrderMonth
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
				DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y") AS OrderMonth,
				SUM(ordertable_CustPrice) AS FullPrice, 
				SUM(ordertable_BusPrice) AS TotalOrderCost, 
				(ordertable_CustPrice - ordertable_BusPrice) AS Profit, 
				discount.discount_Amount AS discountAmount,
                1 as o,
				CASE WHEN discount.discount_IsPercent = 1 
					THEN
						FullPrice * (1-discountAmount)
					ELSE
						FullPrice - discountAmount
				END as TotalOrderPrice
			FROM
				ordertable
				JOIN order_discount ON order_discount.ordertable_OrderID = ordertable.ordertable_OrderID
				JOIN discount ON discount.discount_DiscountID = order_discount.discount_DiscountID
			GROUP BY
				ordertable_OrderType, DATE_FORMAT(ordertable.ordertable_OrderDateTime, "%m/%Y")
			UNION SELECT 
				NULL, 
                'Grand Total', 
                SUM(ordertable_CustPrice), 
                SUM(ordertable_BusPrice), 
                SUM((ordertable_CustPrice - ordertable_BusPrice)),
                2 as o
                FROM ordertable ) TEMP1
	ORDER BY
		o, customerType, OrderMonth, Profit;
		