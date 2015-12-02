CREATE TABLE SQL_LOCK
(
  lockName  VARCHAR(255)  NOT NULL,
  ownerName  VARCHAR(100)  NOT NULL,
  started  VARCHAR(255)  NOT NULL,
  comments  VARCHAR(255),
  hostAddress  VARCHAR(100) NOT NULL,
    CONSTRAINT sql_lock_name PRIMARY KEY (lockName)
);


commit;
exit;
