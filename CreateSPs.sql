DROP PROCEDURE IF EXISTS PROCEDURE1;
delimiter $$
CREATE PROCEDURE PROCEDURE1()
BEGIN
    
END;
$$
delimeter ;


DROP PROCEDURE IF EXISTS PROCEDURE2;
delimiter $$
CREATE PROCEDURE PROCEDURE2()
BEGIN
    
END;
$$
delimeter ;


delimiter $$
CREATE FUNCTION no_of_years(date1 date) RETURNS int DETERMINISTIC
BEGIN
 DECLARE date2 DATE;
  Select current_date()into date2;
  RETURN year(date2)-year(date1);
END 
$$
delimiter ;

delimiter $$
CREATE FUNCTION no_of_years(date1 date) RETURNS int DETERMINISTIC
BEGIN
 DECLARE date2 DATE;
  Select current_date()into date2;
  RETURN year(date2)-year(date1);
END 
$$
delimiter ;

DROP TRIGGER IF EXISTS UPDATE1
delimiter $$
CREATE TRIGGER UPDATE1
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
delimiter;

DROP TRIGGER IF EXISTS UPDATE2
delimiter $$
CREATE TRIGGER UPDATE2
AFTER UPDATE ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
delimiter;

DROP TRIGGER IF EXISTS INSERT1
CREATE TRIGGER INSERT1
AFTER INSERT ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
delimiter;

DROP TRIGGER IF EXISTS inserttriggerforpizzabusprice
CREATE TRIGGER inserttriggerforpizzabusprice
AFTER INSERT ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
delimiter;

DROP TRIGGER IF EXISTS INSERT2
CREATE TRIGGER INSERT2
AFTER INSERT ON pizza
FOR EACH ROW
BEGIN
   SELECT
	pizza)
END;
$$
delimiter;
