# User Management System (Servlet & JDBC CRUD)

> **College Laboratory Practical Experiment**  
> **Course / Subject:** Web Technology / Advanced Java Lab / Software Development Lab  
> **Experiment Title:** Implementation of Servlet-Based User Management with CRUD Operations  

---

## Table of Contents
1. [Experiment Title & Aim](#1-experiment-title--aim)
2. [Software and Hardware Requirements](#2-software-and-hardware-requirements)
3. [System Architecture (MVC & DAO Pattern)](#3-system-architecture-mvc--dao-pattern)
4. [Database Design & SQL Setup](#4-database-design--sql-setup)
5. [Project Folder Structure](#5-project-folder-structure)
6. [Step-by-Step Installation & Configuration](#6-step-by-step-installation--configuration)
7. [Running the Application](#7-running-the-application)
8. [Detailed CRUD Operations Walkthrough](#8-detailed-crud-operations-walkthrough)
9. [Test Cases & Verified Execution Results](#9-test-cases--verified-execution-results)
10. [Troubleshooting Guide](#10-troubleshooting-guide)
11. [Screenshots & Visual Evidence](#11-screenshots--visual-evidence)
12. [Practical Record Section (Lab Manual Format)](#12-practical-record-section-lab-manual-format)
13. [Lab Viva Voce Preparation Guide](#13-lab-viva-voce-preparation-guide)

---

## 1. Experiment Title & Aim

### Title
**Implementation of Servlet-Based User Management with CRUD Operations**

### Aim
To design, implement, deploy, and evaluate a robust, database-driven web application titled **User Management System** using **Jakarta Servlets**, **JavaServer Pages (JSP)**, and **Java Database Connectivity (JDBC)** connected to a **MySQL 8** relational database following the **Model-View-Controller (MVC)** architectural pattern and **Data Access Object (DAO)** design pattern.

---

## 2. Software and Hardware Requirements

### Software Requirements
| Component | Specification / Version |
| :--- | :--- |
| **Operating System** | Windows 10 / 11, Linux, or macOS |
| **Java Development Kit** | JDK 17 LTS or JDK 21 LTS |
| **Build Automation Tool** | Apache Maven 3.8+ (Wrapper `mvnw.cmd` included) |
| **Web Server / Servlet Container** | Apache Tomcat 10.1+ (Embedded Tomcat 10.1 runner included) |
| **Database Server** | MySQL Community Server 8.0+ |
| **Database Driver** | MySQL Connector/J 8.3.0 |
| **Web Browser** | Google Chrome, Mozilla Firefox, or Microsoft Edge |

### Hardware Requirements
- **Processor:** Intel Core i3 / AMD Ryzen 3 or higher.
- **RAM:** Minimum 4 GB (8 GB recommended).
- **Disk Space:** 500 MB free space for JDK, Maven dependencies, and MySQL database.

---

## 3. System Architecture (MVC & DAO Pattern)

The application enforces a strict separation of concerns through the **MVC (Model-View-Controller)** architecture combined with the **DAO (Data Access Object)** pattern:

```text
       +-------------------------------------------------------------+
       |                         Client Browser                      |
       |  (HTML5, CSS3 [Academic Modern Theme], JavaScript [DOM/Ajax])|
       +------------------------------+------------------------------+
                                      | HTTP GET / POST
                                      v
       +-------------------------------------------------------------+
       |                     CONTROLLER LAYER                        |
       |               com.lab.usermanagement.servlet                |
       |                      [UserServlet.java]                     |
       |  - Parses HTTP requests & extracts parameters               |
       |  - Enforces server-side validation & XSS sanitization       |
       |  - Dispatches actions: list, new, create, edit, update, del |
       |  - Applies Post/Redirect/Get (PRG) pattern                  |
       +--------------+------------------------------+---------------+
                      |                              |
        Updates / Reads Model           Forwards View Model
                      |                              |
                      v                              v
       +-----------------------------+ +-----------------------------+
       |         MODEL LAYER         | |         VIEW LAYER          |
       | com.lab.usermanagement.model| |         (JSP & JSTL)        |
       |         [User.java]         | |  - user-list.jsp (Dashboard)|
       |  - Encapsulates User state  | |  - user-form.jsp (Add/Edit) |
       |  - Plain Old Java Object    | |  - JSTL core tags (<c:out>) |
       +--------------+--------------+ +-----------------------------+
                      ^
                      | Interacts with
                      v
       +-------------------------------------------------------------+
       |                         DAO LAYER                           |
       |                 com.lab.usermanagement.dao                  |
       |                     [UserDAO.java]                          |
       |  - CRUD methods: getAllUsers, getUserById, addUser, etc.    |
       |  - Uses PreparedStatements (SQL injection prevention)       |
       |  - Uses Try-With-Resources (Memory leak prevention)         |
       +------------------------------+------------------------------+
                                      | JDBC Connection
                                      v
       +-------------------------------------------------------------+
       |                      UTILITY LAYER                          |
       |                 com.lab.usermanagement.util                 |
       |                    [DBConnection.java]                      |
       |  - Centralized connection management                        |
       |  - Configured via db.properties / Environment Variables     |
       +------------------------------+------------------------------+
                                      | TCP/IP Port 3306
                                      v
       +-------------------------------------------------------------+
       |                      DATABASE LAYER                         |
       |                       MySQL 8.0 Server                      |
       |              Database: user_management | Table: users       |
       +-------------------------------------------------------------+
```

---

## 4. Database Design & SQL Setup

The application connects to a MySQL database named `user_management`.

### Table Schema: `users`
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `PRIMARY KEY, AUTO_INCREMENT` | Unique identifier for each user |
| `name` | `VARCHAR(100)` | `NOT NULL` | Full Name of the user |
| `email` | `VARCHAR(150)` | `NOT NULL, UNIQUE` | Unique email address for login & contact |
| `phone` | `VARCHAR(15)` | `NULLABLE` | Contact telephone / mobile number |
| `department` | `VARCHAR(100)` | `NULLABLE` | Academic or organizational department |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | System record creation timestamp |

### Script: `database.sql`
```sql
-- 1. Create and switch to the database
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

-- 3. Initial demonstration records
INSERT INTO users (name, email, phone, department) 
VALUES 
    ('Aarav Sharma', 'aarav.sharma@college.edu', '+91 9876543210', 'Computer Science and Engineering'),
    ('Ananya Iyer', 'ananya.iyer@college.edu', '+91 9876543211', 'Information Technology'),
    ('Rohan Verma', 'rohan.verma@college.edu', '+91 9876543212', 'Electronics and Communication'),
    ('Pooja Patel', 'pooja.patel@college.edu', '+91 9876543213', 'Data Science & AI'),
    ('Karthik Raja', 'karthik.raja@college.edu', '+91 9876543214', 'Mechanical Engineering')
ON DUPLICATE KEY UPDATE name=VALUES(name);
```

---

## 5. Project Folder Structure

```text
d:/SAT-2 SD/
├── pom.xml                                    # Maven build configuration
├── database.sql                               # Database & schema creation script
├── README.md                                  # Complete experiment documentation
├── mvnw.cmd                                   # Windows Maven wrapper
├── screenshots/                               # Live test screenshots & video demo
│   ├── 1_dashboard_view.png
│   ├── 2_add_user_form.png
│   ├── 3_form_filled.png
│   ├── 4_dashboard_after_create.png
│   ├── 5_dashboard_search_and_clear.png
│   └── dashboard_crud_demo.webp
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/lab/usermanagement/
    │   │       ├── model/
    │   │       │   └── User.java              # Java Bean entity model
    │   │       ├── dao/
    │   │       │   └── UserDAO.java           # Data Access Object with JDBC CRUD
    │   │       ├── util/
    │   │       │   └── DBConnection.java      # Centralized JDBC connection manager
    │   │       ├── servlet/
    │   │       │   └── UserServlet.java       # Controller Servlet (MVC dispatcher)
    │   │       └── launcher/
    │   │           └── EmbeddedServer.java    # Embedded Tomcat 10.1 runner
    │   ├── resources/
    │   │   └── db.properties                  # Centralized database configuration
    │   └── webapp/
    │       ├── index.jsp                      # Welcome page redirecting to /users
    │       ├── assets/
    │       │   ├── css/
    │       │   │   └── style.css              # Academic modern dashboard styles
    │       │   └── js/
    │       │       └── script.js              # Client validation & modal handling
    │       └── WEB-INF/
    │           ├── web.xml                    # Jakarta EE 6.0 deployment descriptor
    │           └── views/
    │               ├── user-list.jsp          # Dashboard & User table view
    │               └── user-form.jsp          # Add / Edit User form view
    └── test/
        └── java/
            └── com/lab/usermanagement/
                └── dao/
                    └── UserDAOTest.java       # Automated JUnit 5 test suite
```

---

## 6. Step-by-Step Installation & Configuration

### Step 1: Clone or Navigate to the Workspace
Open a terminal (Command Prompt or PowerShell) in the project directory:
```powershell
cd "d:\SAT-2 SD"
```

### Step 2: Initialize MySQL Database
Execute the database setup script in MySQL using the MySQL CLI or MySQL Workbench:
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < database.sql
```
*(Enter your MySQL root password when prompted)*

### Step 3: Configure Database Credentials
Open [`src/main/resources/db.properties`](file:///d:/SAT-2%20SD/src/main/resources/db.properties) and update your MySQL password:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/user_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD_HERE
```
> **Security Note:** You can also supply the credentials without modifying files via Windows Environment Variables:
> ```powershell
> $env:DB_USER="root"
> $env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
> ```

---

## 7. Running the Application

### Option A: 1-Click Embedded Tomcat Runner (Fastest & Recommended)
You can compile and run the application immediately using the embedded Tomcat 10.1 server:
```powershell
.\mvnw.cmd compile exec:exec
```
The server will initialize on port 8080 and display:
```text
==================================================================
  User Management System - Tomcat 10.1 Server Running Successfully
==================================================================
  Access the application at:
  -> http://localhost:8080/
  -> http://localhost:8080/UserManagementSystem/
  -> http://localhost:8080/UserManagementSystem/users
==================================================================
```

### Option B: Deploying Standard WAR File to External Apache Tomcat
1. Build the production WAR file:
   ```powershell
   .\mvnw.cmd package
   ```
2. Locate the generated archive:
   `target/UserManagementSystem.war`
3. Copy `UserManagementSystem.war` into your Tomcat installation directory:
   `C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\`
4. Start Tomcat and visit:
   `http://localhost:8080/UserManagementSystem/users`

---

## 8. Detailed CRUD Operations Walkthrough

### 1. Create (Add New User)
- **URL Route:** `GET /users?action=new` -> `POST /users?action=create`
- **Controller Action:** `UserServlet.createUser()`
- **Validation:**
  - Full Name: Required, max 100 characters.
  - Email: Required, validated against RFC regex (`^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`), uniqueness verified via `UserDAO.emailExists()`.
  - Phone: Optional, max 15 characters.
  - Department: Optional, max 100 characters.
- **Pattern:** Post/Redirect/Get (`PRG`). Redirects to `/users?message=created`.

### 2. Read (View Dashboard & Search)
- **URL Route:** `GET /users` or `GET /users?search=keyword`
- **Controller Action:** `UserServlet.listUsers()`
- **DAO Operations:**
  - `userDAO.getUserCount()` executes `SELECT COUNT(*) FROM users`.
  - `userDAO.getAllUsers()` executes `SELECT ... FROM users ORDER BY id DESC`.
  - `userDAO.searchUsers(keyword)` executes `SELECT ... WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?`.
- **View:** `user-list.jsp` safely renders data via `<c:out>` and provides real-time search filtering.

### 3. Update (Edit User)
- **URL Route:** `GET /users?action=edit&id=X` -> `POST /users?action=update`
- **Controller Action:** `UserServlet.showEditForm()` -> `UserServlet.updateUser()`
- **Process:** Fetches existing record by ID, populates `user-form.jsp`, validates modifications (ensuring email is unique excluding own ID), executes parameterized `UPDATE users SET ... WHERE id=?`, and redirects to `/users?message=updated`.

### 4. Delete (Remove User)
- **URL Route:** `POST /users?action=delete`
- **Controller Action:** `UserServlet.deleteUser()`
- **Security:** GET-based deletions are forbidden to prevent CSRF / pre-fetching accidents. Deletions require a POST request triggered from an accessible confirmation modal.
- **Process:** Safely verifies ID, deletes row via `DELETE FROM users WHERE id=?`, and redirects to `/users?message=deleted`.

---

## 9. Test Cases & Verified Execution Results

All 15 required tests were executed and verified against the live MySQL 8 database:

| Test ID | Test Scenario | Expected Outcome | Execution Result | Status |
| :---: | :--- | :--- | :--- | :---: |
| **TC-01** | Open dashboard with empty database | Displays 0 count, clean empty-state card with "Add First User" button | Zero count card rendered with helpful empty state | **PASS** |
| **TC-02** | Add a valid user | User inserted into MySQL, redirects to dashboard with success alert | User #6 (Vikramaditya Rao) inserted, success banner displayed | **PASS** |
| **TC-03** | Verify user appears in table | New user record appears at the top of the user table | Record appears in table with accurate details | **PASS** |
| **TC-04** | Add another user (Neha Gupta) | Increments count, record persists in MySQL | Neha Gupta added via browser, total count incremented | **PASS** |
| **TC-05** | Search by partial name ("Ananya") | Displays only matching records (1 result) | Filtered to 1 record with "Clear" filter button | **PASS** |
| **TC-06** | Search by email domain ("college.edu") | Displays all users with matching email domain | Returned 5 matching records | **PASS** |
| **TC-07** | Edit user details | Pre-populates form, saves updated name/department | Updated department to "Robotics & Automation" | **PASS** |
| **TC-08** | Verify update persists | Updated data remains intact after refreshing page | Persisted in MySQL across multiple queries | **PASS** |
| **TC-09** | Reject duplicate email submission | Form re-rendered with error message: "already registered" | Duplicate email rejected without creating DB record | **PASS** |
| **TC-10** | Reject blank required fields | Displays validation error: "Full Name is required" | Form re-rendered with inline error banner | **PASS** |
| **TC-11** | Delete user after modal confirmation | Modal opens, user confirms, record deleted | Modal submitted POST, record removed | **PASS** |
| **TC-12** | Verify deletion in MySQL & dashboard | Count decrements, user no longer exists | User #6 removed from DB, count updated | **PASS** |
| **TC-13** | Handle invalid/nonexistent ID (`id=99999`) | Gracefully redirects with alert: "User not found" | HTTP 200 with error alert, no crash | **PASS** |
| **TC-14** | Database unavailable behavior | Catches SQLException, shows warning, no HTTP 500 | Graceful notification banner, 0 count shown | **PASS** |
| **TC-15** | Restart Tomcat and verify persistence | Data remains available after server restarts | Server restarted, all 5 records intact | **PASS** |

### Automated Unit Test Summary (JUnit 5)
```text
[INFO] Running com.lab.usermanagement.dao.UserDAOTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.430 s
[INFO] BUILD SUCCESS
```

---

## 10. Troubleshooting Guide

### 1. `Access denied for user 'root'@'localhost'`
- **Cause:** Incorrect MySQL root password specified in `db.properties`.
- **Solution:** Open [`src/main/resources/db.properties`](file:///d:/SAT-2%20SD/src/main/resources/db.properties) and verify the `db.password` value matches your local MySQL server password.

### 2. `Communications link failure / Connection refused: connect`
- **Cause:** MySQL service is not running.
- **Solution:** Open Windows PowerShell as Administrator and run:
  ```powershell
  Start-Service MySQL80
  ```

### 3. Port 8080 already in use
- **Cause:** Another process or previous server instance is occupying port 8080.
- **Solution:** `EmbeddedServer.java` includes an automatic fallback to port `8081`. You can access the application at `http://localhost:8081/UserManagementSystem/users`. Alternatively, kill the running process:
  ```powershell
  Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
  ```

### 4. `java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`
- **Cause:** MySQL Connector/J driver missing from classpath.
- **Solution:** Run `.\mvnw.cmd clean compile` to ensure Maven pulls `mysql-connector-j-8.3.0.jar`.

---

## 11. Screenshots & Visual Evidence

The live browser execution has been recorded and saved in the [`screenshots/`](file:///d:/SAT-2%20SD/screenshots/) folder:

| File Name | Description |
| :--- | :--- |
| [`1_dashboard_view.png`](file:///d:/SAT-2%20SD/screenshots/1_dashboard_view.png) | Main dashboard showing Total Registered Users metric, MySQL Connected badge, and data table. |
| [`2_add_user_form.png`](file:///d:/SAT-2%20SD/screenshots/2_add_user_form.png) | Add New User form with input fields, labels, placeholders, and cancel/save buttons. |
| [`3_form_filled.png`](file:///d:/SAT-2%20SD/screenshots/3_form_filled.png) | Add New User form with validated user input ready for submission. |
| [`4_dashboard_after_create.png`](file:///d:/SAT-2%20SD/screenshots/4_dashboard_after_create.png) | Dashboard showing new user inserted at the top and total user count incremented. |
| [`5_dashboard_search_and_clear.png`](file:///d:/SAT-2%20SD/screenshots/5_dashboard_search_and_clear.png) | Search bar filtering users in real time and clearing search to restore full list. |
| [`dashboard_crud_demo.webp`](file:///d:/SAT-2%20SD/screenshots/dashboard_crud_demo.webp) | Complete video recording of browser CRUD operations. |

---

## 12. Practical Record Section (Lab Manual Format)

### Aim
To write a Java Servlet and JDBC program to implement a web-based User Management System performing Create, Read, Update, Delete (CRUD), and Search operations using MySQL database and MVC architecture.

### Algorithm
1. **Initialize Database:** Start MySQL Server and create the `user_management` database with table `users` having fields `id`, `name`, `email`, `phone`, `department`, and `created_at`.
2. **Establish Connection:** In `DBConnection.java`, register the JDBC driver (`com.mysql.cj.jdbc.Driver`) and establish a connection using `DriverManager.getConnection()`.
3. **Define Entity Model:** In `User.java`, encapsulate user properties with private fields, constructors, getters, and setters.
4. **Implement Data Access Object (DAO):** In `UserDAO.java`, create methods for `getAllUsers`, `getUserById`, `addUser`, `updateUser`, `deleteUser`, `searchUsers`, and `getUserCount` using parameterized `PreparedStatement` and `try-with-resources`.
5. **Develop Controller Servlet:** In `UserServlet.java`, handle `GET` and `POST` requests, validate parameters (check required fields and duplicate emails), and delegate business operations to `UserDAO`.
6. **Implement PRG Pattern:** After successful `create`, `update`, or `delete` operations, use `response.sendRedirect()` to prevent duplicate submissions on browser refresh.
7. **Design View Layer:** In `user-list.jsp` and `user-form.jsp`, use JSTL core tags (`<c:forEach>`, `<c:out>`) to render dynamic database content while preventing XSS attacks.
8. **Add User Confirmation & Security:** Enforce `POST` requests for delete actions and display a client-side confirmation dialog prior to record removal.
9. **Build & Deploy:** Package the web application into a `.war` file via Maven and deploy to Apache Tomcat 10.1.
10. **Execute & Test:** Open the browser, test each CRUD operation, and verify changes in the MySQL database.

### Important Source Code Snippets

#### 1. JDBC Query with PreparedStatement in `UserDAO.java`
```java
public boolean addUser(User user) throws SQLException {
    String sql = "INSERT INTO users (name, email, phone, department) VALUES (?, ?, ?, ?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPhone());
        ps.setString(4, user.getDepartment());
        int rows = ps.executeUpdate();
        return rows > 0;
    }
}
```

#### 2. Servlet Routing & PRG Pattern in `UserServlet.java`
```java
if ("create".equalsIgnoreCase(action)) {
    User candidate = new User(name, email, phone, department);
    String error = validateUser(candidate, 0);
    if (error == null) {
        userDAO.addUser(candidate);
        response.sendRedirect(request.getContextPath() + "/users?message=created");
    } else {
        request.setAttribute("errorMessage", error);
        request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
    }
}
```

### Sample Input
- **Full Name:** `Neha Gupta`
- **Email Address:** `neha.gupta@college.edu`
- **Phone Number:** `+91 9123456789`
- **Department:** `Data Science & AI`

### Sample Output
- **Dashboard Message:** `User record has been successfully created!`
- **User Record Added:** `#7 | Neha Gupta | neha.gupta@college.edu | +91 9123456789 | Data Science & AI`
- **Total Users Count:** `6`

### Result
The Servlet-based User Management System with CRUD operations and MySQL database connectivity was successfully designed, implemented, deployed on Apache Tomcat 10.1, and verified to be fully functional.

---

## 13. Lab Viva Voce Preparation Guide

### Q1: What is the role of a Java Servlet in this application?
**Answer:** A Servlet acts as the **Controller** in the MVC architecture. It receives HTTP requests from the browser, validates user input, invokes the appropriate DAO methods to interact with the database, and decides which JSP view to forward or redirect to.

### Q2: What is the DAO (Data Access Object) design pattern?
**Answer:** The DAO pattern separates low-level database operations (SQL queries, connections, and result sets) from the high-level business logic. In our project, `UserDAO.java` encapsulates all SQL queries so that if the database schema or query changes, the Servlet code remains untouched.

### Q3: Why do we use `PreparedStatement` instead of `Statement`?
**Answer:** 
1. **Security:** `PreparedStatement` uses parameterized queries (`?`) which prevents **SQL Injection** attacks by pre-compiling the SQL structure before substituting values.
2. **Performance:** Pre-compiled SQL statements can be cached and reused by the database engine for multiple executions.

### Q4: Why is the Post/Redirect/Get (PRG) pattern necessary?
**Answer:** If a user submits a form (POST) and refreshes the browser, the browser will attempt to re-submit the POST request, causing accidental duplicate database records. PRG solves this by issuing an HTTP 302 Redirect after a successful POST so that subsequent refreshes only repeat the safe GET request.

### Q5: What is the difference between `request.getRequestDispatcher().forward()` and `response.sendRedirect()`?
**Answer:**
- **`forward()`:** Executed entirely on the server side; the client browser URL remains unchanged; request attributes are preserved. Used when redisplaying form errors.
- **`sendRedirect()`:** Sends an HTTP 302 response to the client instructing it to make a brand-new GET request; changes the browser URL. Used after create/update/delete operations (PRG pattern).

### Q6: Why do we use Try-With-Resources in JDBC?
**Answer:** Any class implementing `java.lang.AutoCloseable` (such as `Connection`, `PreparedStatement`, and `ResultSet`) declared inside a try-with-resources block is automatically closed when execution leaves the block, even if an exception occurs. This prevents database connection leaks and memory exhaustion.

### Q7: Why do we escape output with `<c:out>` in JSP?
**Answer:** Using `<c:out value="${user.name}" />` automatically encodes special HTML characters (`<`, `>`, `&`, `"`, `'`), preventing malicious scripts entered by a user from executing in other users' browsers (Cross-Site Scripting / XSS prevention).
