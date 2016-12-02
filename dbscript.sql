DROP TABLE note;

CREATE DATABASE notedb;
USE notedb;
CREATE TABLE note (
	noteid BIGINT SIGNED,
    userid VARCHAR(44),
    creation TIMESTAMP,
    lastedited TIMESTAMP,
    deleted BOOL,
    notebody TEXT,
    PRIMARY KEY (noteid)
);


CREATE USER 'notesadmin'@'%' IDENTIFIED BY 'curriemartinoneill';
GRANT ALL ON notedb.* TO 'notesadmin'@'%' IDENTIFIED BY 'curriemartinoneill';
