DELIMITER //

CREATE PROCEDURE add_game (IN newName VARCHAR(50), IN newYear VARCHAR(50), IN newPrice VARCHAR(50), IN newPlatform VARCHAR(50), IN newPublisher VARCHAR(50), IN newGenre VARCHAR(50))
BEGIN
    DECLARE nameFound INTEGER;
    SET nameFound = (SELECT COUNT(*) FROM games WHERE name=newName AND year=newYear);
    IF nameFound > 0 THEN
        INSERT INTO games (name, year, price) VALUES (newName,newYear,newPrice);
    END IF;
END
//

DELIMITER ;

