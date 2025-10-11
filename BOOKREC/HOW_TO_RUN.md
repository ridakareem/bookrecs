# How to Run "The Book Shelf" Application

This guide will help you run your Book Recommendation application on Windows with XAMPP.

## Prerequisites

1. **Java Development Kit (JDK)** - Version 8 or higher
2. **XAMPP** - For MySQL database
3. **Command Prompt** or **PowerShell**

## Step-by-Step Instructions

### Step 1: Start XAMPP Services

1. Open **XAMPP Control Panel**
2. Start **Apache** and **MySQL** services
3. Verify both services show "Running" status

### Step 2: Set Up Database (if not done already)

1. Open browser and go to: `http://localhost/phpmyadmin`
2. Click **SQL** tab
3. Copy and paste the content from `setup_database.sql`
4. Click **Go** to execute

### Step 3: Compile the Application

Open **Command Prompt** and navigate to your project directory:

```cmd
cd C:\Users\ridak\OneDrive\Desktop\oops\bookrecs\BOOKREC
```

#### Compile with MySQL Connector:

```cmd
javac -cp "lib\mysql-connector-j-8.4.0.jar;src" -d out src\ui\*.java src\utils\*.java src\integrations\*.java
```

This command:
- `-cp` includes the MySQL connector JAR file in the classpath
- `-d out` compiles classes to the `out` directory
- Compiles all Java files in `src\ui`, `src\utils`, and `src\integrations`

### Step 4: Run the Application

```cmd
java -cp "lib\mysql-connector-j-8.4.0.jar;out" ui.App
```

## Application Flow

When you run the application, here's what happens:

1. **Database Check**: App tries to connect to MySQL database
2. **If Database Ready**: Shows login screen
3. **If Database Not Ready**: Shows database setup screen
4. **After Login**: Shows main application window with:
   - Add Book
   - View Books  
   - Search Online

## Login Credentials

Use the default admin account created by the setup script:
- **Username**: `admin`
- **Password**: `admin123`

## Alternative: Run Individual Components

### Run Splash Screen First:
```cmd
java -cp "lib\mysql-connector-j-8.4.0.jar;out" ui.SplashFrame
```

### Run Console Version (BookLogger):
```cmd
cd C:\Users\ridak\OneDrive\Desktop\oops\bookrecs
javac -cp "BOOKREC\lib\mysql-connector-j-8.4.0.jar" BookLogger.java DBConnection.java
java -cp "BOOKREC\lib\mysql-connector-j-8.4.0.jar;." BookLogger
```

### Run Console Login:
```cmd
cd C:\Users\ridak\OneDrive\Desktop\oops\bookrecs
javac -cp "BOOKREC\lib\mysql-connector-j-8.4.0.jar" login.java DBConnection.java
java -cp "BOOKREC\lib\mysql-connector-j-8.4.0.jar;." Login
```

## Troubleshooting

### Common Issues:

1. **"Class not found" error**:
   - Make sure you're in the correct directory
   - Check that the `out` folder contains compiled `.class` files
   - Verify the MySQL connector JAR path is correct

2. **Database connection error**:
   - Ensure XAMPP MySQL is running
   - Check that the database `bookdiary` exists
   - Verify `db.properties` configuration

3. **Compilation errors**:
   - Make sure JDK is installed and in PATH
   - Check that all source files are in the correct package structure

### Quick Fix Commands:

**Clean and recompile:**
```cmd
rmdir /s /q out
mkdir out
javac -cp "lib\mysql-connector-j-8.4.0.jar;src" -d out src\ui\*.java src\utils\*.java src\integrations\*.java
```

**Check Java version:**
```cmd
java -version
javac -version
```

## Application Features

Once running, you can:

1. **Add Books**: Manually add books to your collection
2. **View Books**: See all your books in a nice table view
3. **Search Online**: Search for books using Google Books API
4. **User Management**: Sign up new users or login existing ones

## File Structure

```
BOOKREC/
├── src/
│   ├── ui/           # User interface classes
│   ├── utils/        # Database utilities
│   └── integrations/ # External API clients
├── lib/              # MySQL connector JAR
├── out/              # Compiled classes
└── db.properties     # Database configuration
```

## Next Steps

After the application is running:
1. Create a new user account or use the admin account
2. Add some books to test the functionality
3. Try the online search feature
4. Explore the different panels in the main window

Enjoy your Book Shelf application! 📚
