-- ==============================================================
-- College Lab Experiment: Servlet-Based User Management System
-- Database Creation and Schema Initialization Script
-- Compatible with MySQL 8.0+
-- ==============================================================

-- 1. Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS user_management;
USE user_management;

-- 2. Create the users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(15),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. (Optional) Sample demonstration records for testing
-- These records demonstrate a populated dashboard immediately after setup.
INSERT INTO users (name, email, phone, department) 
VALUES 
    ('Aarav Sharma', 'aarav.sharma@college.edu', '+91 9876543210', 'Computer Science and Engineering'),
    ('Ananya Iyer', 'ananya.iyer@college.edu', '+91 9876543211', 'Information Technology'),
    ('Rohan Verma', 'rohan.verma@college.edu', '+91 9876543212', 'Electronics and Communication'),
    ('Pooja Patel', 'pooja.patel@college.edu', '+91 9876543213', 'Data Science & AI'),
    ('Karthik Raja', 'karthik.raja@college.edu', '+91 9876543214', 'Mechanical Engineering')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. Verification Query
SELECT * FROM users;
