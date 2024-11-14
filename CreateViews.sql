/*Create Views*/
/*Mary Walker Felder and Cassie Phillips*/

CREATE VIEW ToppingPopularity AS 
	SELECT  topping.topping_TopName AS Topping, 
			COUNT(pizza_topping.topping_TopID) AS ToppingCount
	FROM topping JOIN pizza_topping ON topping.topping_TopID = pizza_topping.topping_TopID
	ORDER BY ToppingCount;
        
/*CREATE VIEW ProfitByPizza AS 
	SELECT test
	FROM ;

CREATE VIEW ProfitByOrderType AS 
	SELECT 
	FROM ;
