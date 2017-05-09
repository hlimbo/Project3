DELIMITER //

CREATE PROCEDURE add_game (IN newName VARCHAR(50), IN newYear VARCHAR(50), IN newPrice VARCHAR(50), IN newPlatform VARCHAR(50), IN newPublisher VARCHAR(50), IN newGenre VARCHAR(50))
BEGIN
    DECLARE countFound INTEGER;
    DECLARE platformID INTEGER;
    DECLARE gameID INTEGER;
    DECLARE genreID INTEGER;
    DECLARE publisherID INTEGER;
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
        INSERT INTO genre (genre) VALUES (newGenre);
    END IF;
    SET genreID = (SELECT id FROM genre WHERE genre = newGenre);
    SET countFound = (SELECT COUNT(*) FROM genres_of_games WHERE genre_id = genreID AND game_id = gameID);
    IF countFound = 0 THEN 
        INSERT INTO genres_of_games (game_id, genre_id) VALUES (gameID, genreID);
    END IF;
END
//

DELIMITER ;

