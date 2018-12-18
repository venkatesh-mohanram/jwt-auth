-- --------------------------------------------------------
-- 
-- Database: vasi_learning
-- 
-- --------------------------------------------------------
-- 
-- DDL Commands
-- 
CREATE DATABASE IF NOT EXISTS vasi_learning;
USE vasi_learning;

DROP TABLE IF EXISTS vl_category;
CREATE TABLE IF NOT EXISTS vl_category (
	category_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	category_name VARCHAR(30) UNIQUE,
	category_image BLOB,
	category_root BOOLEAN DEFAULT TRUE,
	category_parent INT DEFAULT NULL,
	FOREIGN KEY (category_parent) 
		REFERENCES vl_category(category_id) 
		ON UPDATE CASCADE
);

DROP TABLE IF EXISTS vl_user_type;
CREATE TABLE IF NOT EXISTS vl_user_type (
	type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	type_name VARCHAR(30) UNIQUE -- Some types are ADMIN, CONTENT_PROVIDER, USER
);

DROP TABLE IF EXISTS vl_user;
CREATE TABLE IF NOT EXISTS vl_user (
	user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_firstname VARCHAR(30),
	user_lastname VARCHAR(30),
	user_email VARCHAR(80) UNIQUE,
	user_password VARCHAR(100),
	user_dob DATE,
	user_mobile VARCHAR(20) UNIQUE,
	user_country VARCHAR(25),
	user_type INT,
	FOREIGN KEY (user_type) 
		REFERENCES vl_user_type(type_id) 
		ON UPDATE CASCADE
);

DROP TABLE IF EXISTS vl_content;
CREATE TABLE IF NOT EXISTS vl_content (
	content_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	content_title VARCHAR(30) UNIQUE,
	content_description TEXT,
	content_author INT,
	content_link VARCHAR(100),
	content_category INT,
	FOREIGN KEY (content_author) 
		REFERENCES vl_user(user_id)
		ON UPDATE CASCADE,
	FOREIGN KEY (content_category) 
		REFERENCES vl_category(category_id)
		ON UPDATE CASCADE
);

DROP TABLE IF EXISTS vl_content_comment;
CREATE TABLE IF NOT EXISTS vl_content_comment (
	comment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	content_id INT,
	user_id INT,
	comment_date DATE,
	comment_message TEXT,
	comment_hide BOOLEAN,
	FOREIGN KEY (content_id) 
		REFERENCES vl_content(content_id)
		ON UPDATE CASCADE,
	FOREIGN KEY (user_id) 
		REFERENCES vl_user(user_id)
		ON UPDATE CASCADE
	
);

DROP TABLE IF EXISTS vl_content_metadata_type;
CREATE TABLE IF NOT EXISTS vl_content_metadata_type (
	metadata_type_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	metadata_type_name VARCHAR(20) UNIQUE -- Like VIEW, LIKE, DISLIKE, FAVORITE
);

DROP TABLE IF EXISTS vl_content_metadata;
CREATE TABLE IF NOT EXISTS vl_content_metadata (
	metadata_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	metadata_type INT,
	user_id INT,
	FOREIGN KEY (metadata_type) 
		REFERENCES vl_content_metadata_type(metadata_type_id)
		ON UPDATE CASCADE,
	FOREIGN KEY (user_id) 
		REFERENCES vl_user(user_id)
		ON UPDATE CASCADE
);

DROP TABLE IF EXISTS vl_access_type;
CREATE TABLE IF NOT EXISTS vl_access_type (
	access_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	access_name VARCHAR(20) UNIQUE-- Like NEW, FREEMIUM, PREMIUM
);

DROP TABLE IF EXISTS vl_subscription;
CREATE TABLE IF NOT EXISTS vl_subscription (
	subscription_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT,
	access_type INT,
	expiry_date DATE,
	FOREIGN KEY (user_id) 
		REFERENCES vl_user(user_id)
		ON UPDATE CASCADE,
	FOREIGN KEY (access_type) 
		REFERENCES vl_access_type(access_id)
		ON UPDATE CASCADE
);

DROP TABLE IF EXISTS vl_payment;
CREATE TABLE IF NOT EXISTS vl_payment (
	payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT,
	payment_amount VARCHAR(10),
	payment_from_bank VARCHAR(15),
	payment_date DATE,
	payment_ref VARCHAR(25),
	FOREIGN KEY (user_id) 
		REFERENCES vl_user(user_id)	
		ON UPDATE CASCADE
);


-- ------------------------------------------------
-- 
-- DML commands
-- 
-- ------------------------------------------------

-- 
-- Insert new ROWS
-- 
INSERT INTO vl_user_type (type_name)
VALUES
('ADMIN'),
('CONTENT_PROVIDER'),
('USER');

INSERT INTO vl_content_metadata_type (metadata_type_name)
VALUES
('VIEW'),
('LIKE'),
('DISLIKE'),
('FAVORITE');

INSERT INTO vl_access_type (access_name)
VALUES
('NEW'),
('FREEMIUM'),
('PREMIUM');

