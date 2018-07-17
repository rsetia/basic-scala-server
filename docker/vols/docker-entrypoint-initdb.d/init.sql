CREATE DATABASE IF NOT EXISTS main; 
USE main; 

CREATE TABLE IF NOT EXISTS users (
  id int NOT NULL AUTO_INCREMENT,
  username varchar(20) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp, 
  updated_at TIMESTAMP NOT NULL DEFAULT now() ON UPDATE now(),
  PRIMARY KEY (id)
);
