# Database Setup Instructions for XAMPP

This guide will help you set up the MySQL database for your Book Recommendation Application using XAMPP.

## Prerequisites

1. **XAMPP** - Make sure XAMPP is installed and running on your system
2. **MySQL service** should be running in XAMPP Control Panel
3. **phpMyAdmin** (included with XAMPP) for easier database management

## Database Configuration

Based on your `db.properties` file, the application expects:
- **Host**: 127.0.0.1 (localhost)
- **Port**: 3306
- **Database**: bookdiary
- **Username**: root
- **Password**: (empty - no password)

## Setup Steps

### Step 1: Start XAMPP Services

1. Open **XAMPP Control Panel**
2. Start **Apache** and **MySQL** services (click "Start" buttons)
3. Make sure both services show "Running" status

### Option 1: Using phpMyAdmin (Recommended)

1. Open your web browser and go to: `http://localhost/phpmyadmin`
2. Click on **SQL** tab at the top
3. Copy and paste the entire content from `setup_database.sql` file
4. Click **Go** to execute the script

### Option 2: Using XAMPP MySQL Command Line

1. Open Command Prompt as Administrator
2. Navigate to XAMPP MySQL directory:
   ```cmd
   cd C:\xampp\mysql\bin
   ```
3. Connect to MySQL:
   ```cmd
   mysql -u root
   ```
   (No password needed for default XAMPP setup)

4. Run the setup script:
   ```sql
   source C:\Users\ridak\OneDrive\Desktop\oops\bookrecs\BOOKREC\setup_database.sql
   ```

### Option 3: Copy and Paste SQL Commands in phpMyAdmin

1. Open phpMyAdmin (`http://localhost/phpmyadmin`)
2. Click on **SQL** tab
3. Copy and paste the following commands:

```sql
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
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NULL,
    cover_url VARCHAR(500) NULL,
    genres VARCHAR(500) NULL,
    rating TINYINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## Verify Setup

### In phpMyAdmin:
1. Go to `http://localhost/phpmyadmin`
2. Click on **bookdiary** database in the left sidebar
3. You should see two tables: `books` and `users`
4. Click on each table to verify the structure

### Using SQL (in phpMyAdmin SQL tab):
```sql
USE bookdiary;
SHOW TABLES;
DESCRIBE users;
DESCRIBE books;
```

You should see:
- `users` table with columns: id, username, password
- `books` table with columns: id, user_id, title, author, cover_url, genres, rating, created_at

## Sample Data

The setup script includes some sample data:
- A default admin user (username: admin, password: admin123)
- Three sample books for testing

## Troubleshooting

### If you get connection errors:
1. Make sure XAMPP MySQL service is running (check XAMPP Control Panel)
2. Check if the port 3306 is correct
3. Verify the username and password in `db.properties` (should be root with no password)
4. Try accessing phpMyAdmin first: `http://localhost/phpmyadmin`

### If you get permission errors:
1. Make sure you're connecting as root user (default XAMPP setup)
2. In phpMyAdmin, you can grant permissions through the **Privileges** tab
3. Or use SQL commands in phpMyAdmin:
   ```sql
   GRANT ALL PRIVILEGES ON bookdiary.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

### If tables already exist:
The script uses `CREATE TABLE IF NOT EXISTS` so it's safe to run multiple times.

## Next Steps

Once the database is set up, you can:
1. Run your Java application
2. Use the login functionality with the default admin account
3. Add more books through the application interface
