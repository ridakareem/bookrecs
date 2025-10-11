-- Database Setup Script for Book Recommendation Application
-- Based on the Java code analysis

-- Create the database
CREATE DATABASE IF NOT EXISTS bookdiary;

-- Use the database
USE bookdiary;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    author VARCHAR(255) DEFAULT NULL,
    cover_url VARCHAR(500) DEFAULT NULL,
    genres VARCHAR(500) DEFAULT NULL,
    rating TINYINT DEFAULT NULL,
    year VARCHAR(10) DEFAULT NULL,
    date_added DATETIME DEFAULT CURRENT_TIMESTAMP,
    cover_image VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert some sample data (optional)
-- Sample user
INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123');

-- Sample books for the admin user
INSERT IGNORE INTO books (user_id, title, author, cover_url, genres, rating) VALUES
(1, 'The Great Gatsby', 'F. Scott Fitzgerald', NULL, 'Fiction, Classic', 5),
(1, 'To Kill a Mockingbird', 'Harper Lee', NULL, 'Fiction, Classic', 5),
(1, '1984', 'George Orwell', NULL, 'Fiction, Dystopian', 4);

-- Show the created tables
SHOW TABLES;

-- Show table structures
DESCRIBE users;
DESCRIBE books;
