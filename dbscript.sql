USE notedb;
DROP TABLE note;

CREATE TABLE note (
	noteid BIGINT SIGNED,
    userid VARCHAR(44),
    creation DATETIME,
    lastedited DATETIME,
    deleted BOOL,
    notebody TEXT,
    PRIMARY KEY (userid, noteid)
);


CREATE USER 'notesadmin'@'%' IDENTIFIED BY 'curriemartinoneill';
GRANT ALL ON notedb.* TO 'notesadmin'@'%' IDENTIFIED BY 'curriemartinoneill';
