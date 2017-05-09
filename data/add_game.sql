DELIMITER //

CREATE PROCEDURE add_game (IN newName VARCHAR(200), IN newYear YEAR, IN newPrice INTEGER, IN newPlatform VARCHAR(200), IN newPublisher VARCHAR(200), IN newGenre VARCHAR(200))
BEGIN
    DECLARE countFound INTEGER;
    DECLARE platformID INTEGER;
    DECLARE gameID INTEGER;
    DECLARE genreID INTEGER;
    DECLARE publisherID INTEGER;
    DECLARE doRollback BOOL DEFAULT 0;
    DECLARE nullFound CONDITION FOR SQLSTATE '45000';
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
        BEGIN
            ROLLBACK;
            RESIGNAL nullFound;
        END;
    START TRANSACTION;
    IF newName IS NULL THEN
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Name can not be null', MYSQL_ERRNO='1001';
    END IF;
    IF newYear IS NULL THEN
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Year can not be null', MYSQL_ERRNO='1001';
    END IF;
    IF newPrice IS NULL THEN
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Price can not be null', MYSQL_ERRNO='1001';
    END IF;
    IF newPlatform IS NULL THEN
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Platform can not be null', MYSQL_ERRNO='1001';
    END IF;
    SET countFound = (SELECT COUNT(*) FROM games WHERE name=newName AND year=newYear);
    IF countFound = 0 THEN
        INSERT INTO games (name, year, price) VALUES (newName,newYear,newPrice);
    END IF;
    SET countFound = (SELECT COUNT(id) FROM platforms WHERE platform = newPlatform);
    IF countFound = 0 THEN 
        INSERT INTO platforms (platform) VALUES (newPlatform);
    END IF;
    SET platformID = (SELECT id FROM platforms WHERE platform = newPlatform);
    SET gameID = (SELECT id FROM games WHERE name = newName AND year = newYear);
    SET countFound = (SELECT COUNT(*) FROM platforms_of_games WHERE platform_id = platformID AND game_id = gameID);
    IF countFound = 0 THEN 
        INSERT INTO platforms_of_games (game_id, platform_id) VALUES (gameID, platformID);
    END IF;
    SET countFound = (SELECT COUNT(*) FROM genres WHERE genre = newGenre);
    IF countFound = 0 THEN 
        INSERT INTO genres (genre) VALUES (newGenre);
    END IF;
    SET genreID = (SELECT id FROM genres WHERE genre = newGenre);
    SET countFound = (SELECT COUNT(*) FROM genres_of_games WHERE genre_id = genreID AND game_id = gameID);
    IF countFound = 0 THEN 
        INSERT INTO genres_of_games (game_id, genre_id) VALUES (gameID, genreID);
    END IF;
    SET countFound = (SELECT COUNT(*) FROM publishers WHERE publisher = newPublisher);
    IF countFound = 0 THEN 
        INSERT INTO publishers (publisher) VALUES (newPublisher);
    END IF;
    SET publisherID = (SELECT id FROM publishers WHERE publisher = newPublisher);
    SET countFound = (SELECT COUNT(*) FROM publishers_of_games WHERE publisher_id = publisherID AND platform_id = platformID AND game_id = gameID);
    IF countFound = 0 THEN 
        INSERT INTO publishers_of_games (publisher_id, platform_id, game_id) VALUES (publisherID,platformID,gameID);
    END IF;
END
//

DELIMITER ;

