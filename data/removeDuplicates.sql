CREATE TABLE TemporaryTable LIKE genres_of_games;
INSERT INTO TemporaryTable SELECT DISTINCT * FROM genres_of_games;
DELETE FROM genres_of_games;
INSERT INTO genres_of_games SELECT * FROM TemporaryTable;
DROP TABLE TemporaryTable;

CREATE TABLE TemporaryTable LIKE publishers_of_games;
INSERT INTO TemporaryTable SELECT DISTINCT * FROM publishers_of_games;
DELETE FROM publishers_of_games;
INSERT INTO publishers_of_games SELECT * FROM TemporaryTable;
DROP TABLE TemporaryTable;

CREATE TABLE TemporaryTable LIKE platforms_of_games;
INSERT INTO TemporaryTable SELECT DISTINCT * FROM platforms_of_games;
DELETE FROM platforms_of_games;
INSERT INTO platforms_of_games SELECT * FROM TemporaryTable;
DROP TABLE TemporaryTable;
