# MySQL Database Setup Guide for DriveX

## Prerequisites
- MySQL Server installed and running on localhost:3306
- Java 17 or higher
- Maven (for dependency management)

## Step 1: Install MySQL (if not already installed)

### Windows:
1. Download MySQL Installer from https://dev.mysql.com/downloads/installer/
2. Run the installer and select "Developer Default"
3. Set root password (remember this - you'll need it)
4. Complete the installation

### Verify MySQL is running:
```bash
# Open Command Prompt/PowerShell
mysql -u root -p
# Enter your password when prompted
```

## Step 2: Create the Database

Run the schema.sql script to create the database and tables:

### Option A: Using MySQL Command Line
```bash
mysql -u root -p < schema.sql
```

### Option B: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Run SQL Script
4. Select `schema.sql` from your DriveX project folder
5. Click Run

### Option C: Manual Execution
```sql
-- Open MySQL Workbench or command line and run:
CREATE DATABASE IF NOT EXISTS drivex;
USE drivex;

-- Then run the rest of the schema.sql content
```

## Step 3: Configure Database Credentials

Edit `car-rental/src/com/carrental/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/drivex";
private static final String USERNAME = "root";  // Change if using different user
private static final String PASSWORD = "root";  // Change to your MySQL password
```

**Important**: Update the `PASSWORD` field with your actual MySQL root password.

## Step 4: Add MySQL JDBC Driver

### Using Maven (Recommended):
The `pom.xml` file already includes the MySQL JDBC driver dependency. Run:

```bash
mvn clean install
```

### Manual Setup (if not using Maven):
1. Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/
2. Extract the zip file
3. Copy `mysql-connector-j-8.0.33.jar` to your project's lib folder
4. Add the jar to your IntelliJ IDEA project:
   - File → Project Structure → Modules → Dependencies
   - Click + → JARs or directories
   - Select the mysql-connector jar file

## Step 5: Test the Connection

Run the application from IntelliJ IDEA:
1. Open `Main.java`
2. Right-click and select "Run 'Main.main()'"

You should see:
```
✅ Connected to MySQL database: drivex
        CAR RENTAL SYSTEM
--------------------------------------------------
```

## Troubleshooting

### Error: "Communications link failure"
- Ensure MySQL server is running
- Check that MySQL is on port 3306
- Verify firewall isn't blocking the connection

### Error: "Access denied for user 'root'@'localhost'"
- Check your MySQL password in DatabaseConnection.java
- Ensure the username/password are correct

### Error: "Unknown database 'drivex'"
- Run the schema.sql script to create the database
- Verify the database was created: `SHOW DATABASES;`

### Error: "Table doesn't exist"
- Ensure you ran the complete schema.sql script
- Check tables exist: `USE drivex; SHOW TABLES;`

## Verify Database Structure

After setup, verify the tables were created correctly:

```sql
USE drivex;
SHOW TABLES;
-- Should show: bookings, cars, users

DESCRIBE users;
DESCRIBE cars;
DESCRIBE bookings;
```

## Data Persistence

With MySQL integration:
- ✅ Users, cars, and bookings persist across application restarts
- ✅ Data is stored in the `drivex` database
- ✅ All CRUD operations now use MySQL instead of in-memory storage

## Next Steps

1. Register a test user through the CLI
2. Add some cars as admin
3. Create bookings and verify they persist in the database
4. Check the database directly using MySQL Workbench or command line

## Database Queries for Testing

```sql
-- View all users
SELECT * FROM users;

-- View all cars
SELECT * FROM cars;

-- View all bookings
SELECT * FROM bookings;

-- View bookings with customer and car details
SELECT 
    b.id,
    b.booking_reference,
    CONCAT(u.first_name, ' ', u.last_name) AS customer,
    CONCAT(c.brand, ' ', c.model) AS car,
    b.start_date,
    b.end_date,
    b.total_amount,
    b.status
FROM bookings b
JOIN users u ON b.customer_id = u.id
JOIN cars c ON b.car_id = c.id;
```
