DELIMITER //

CREATE PROCEDURE add_game (newName, newYear, newPrice, newPlatform, newPublisher, newGenre)
BEGIN
    DECLARE;
    SELECT name FROM games WHERE name=newName;
    IF THEN
        INSERT INTO games (name, year, price) VALUES (newName,newYear,newPrice);
    END
    SELECT genre FROM genres WHERE genre=newGenre;
    IF THEN
        INSERT INTO genres (genre) VALUES (newGenre);
    END
    SELECT publisher FROM publishers WHERE publisher=newPublisher;
    IF THEN
        INSERT INTO publishers (publisher) VALUES (newPublisher);
    END
    SELECT platform FROM platforms WHERE platform=newPlatform;
    IF THEN
        INSERT INTO platforms (publisher) VALUES (newPlatform);
    END
END
//

DELIMITER ;
