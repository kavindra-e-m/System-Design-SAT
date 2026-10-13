package com.lab.usermanagement.dao;

import com.lab.usermanagement.model.User;
import com.lab.usermanagement.util.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite for UserDAO operations.
 * Validates CRUD, Search, Count, Duplicate Email handling, and boundary cases.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        // Configure H2 in-memory DB in MySQL compatibility mode for automated testing
        DBConnection.configure(
                "org.h2.Driver",
                "jdbc:h2:mem:user_management;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );

        userDAO = new UserDAO();

        // Create table in test database
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(150) NOT NULL UNIQUE, " +
                    "phone VARCHAR(15), " +
                    "department VARCHAR(100), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

    @BeforeEach
    public void cleanTable() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Empty database returns zero count and empty list")
    public void testEmptyDatabase() throws SQLException {
        assertEquals(0, userDAO.getUserCount());
        List<User> users = userDAO.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Insert valid user and verify persistence")
    public void testAddUser() throws SQLException {
        User user = new User("Aarav Sharma", "aarav@college.edu", "9876543210", "Computer Science");
        boolean created = userDAO.addUser(user);

        assertTrue(created);
        assertTrue(user.getId() > 0, "Generated ID should be positive");
        assertEquals(1, userDAO.getUserCount());

        User fetched = userDAO.getUserById(user.getId());
        assertNotNull(fetched);
        assertEquals("Aarav Sharma", fetched.getName());
        assertEquals("aarav@college.edu", fetched.getEmail());
        assertEquals("9876543210", fetched.getPhone());
        assertEquals("Computer Science", fetched.getDepartment());
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Reject duplicate email insertion")
    public void testDuplicateEmail() throws SQLException {
        User user1 = new User("Aarav Sharma", "duplicate@college.edu", "9876543210", "CSE");
        assertTrue(userDAO.addUser(user1));

        // Attempt to insert another user with the exact same email
        User user2 = new User("Different Name", "duplicate@college.edu", "1122334455", "IT");
        assertThrows(SQLException.class, () -> {
            userDAO.addUser(user2);
        });

        // Verify total count is still 1
        assertEquals(1, userDAO.getUserCount());
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Search users by partial name and partial email")
    public void testSearchUsers() throws SQLException {
        userDAO.addUser(new User("Aarav Sharma", "aarav@college.edu", "9876543210", "CSE"));
        userDAO.addUser(new User("Ananya Iyer", "ananya@college.edu", "9876543211", "IT"));
        userDAO.addUser(new User("Rohan Verma", "rohan@yahoo.com", "9876543212", "ECE"));

        // Search by partial name "Aarav"
        List<User> searchByName = userDAO.searchUsers("Aarav");
        assertEquals(1, searchByName.size());
        assertEquals("Aarav Sharma", searchByName.get(0).getName());

        // Search by domain in email "yahoo"
        List<User> searchByEmail = userDAO.searchUsers("yahoo");
        assertEquals(1, searchByEmail.size());
        assertEquals("Rohan Verma", searchByEmail.get(0).getName());

        // Search matching multiple records "college.edu"
        List<User> searchCommon = userDAO.searchUsers("college.edu");
        assertEquals(2, searchCommon.size());

        // Search with non-matching query
        List<User> searchNone = userDAO.searchUsers("NonexistentXYZ");
        assertTrue(searchNone.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Update user details")
    public void testUpdateUser() throws SQLException {
        User user = new User("Original Name", "original@college.edu", "1111111111", "Mechanical");
        userDAO.addUser(user);

        // Modify details
        user.setName("Updated Name");
        user.setEmail("updated@college.edu");
        user.setPhone("9999999999");
        user.setDepartment("Robotics");

        boolean updated = userDAO.updateUser(user);
        assertTrue(updated);

        User fetched = userDAO.getUserById(user.getId());
        assertNotNull(fetched);
        assertEquals("Updated Name", fetched.getName());
        assertEquals("updated@college.edu", fetched.getEmail());
        assertEquals("9999999999", fetched.getPhone());
        assertEquals("Robotics", fetched.getDepartment());
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Delete user and verify removal")
    public void testDeleteUser() throws SQLException {
        User user = new User("Delete Me", "deleteme@college.edu", "0000000000", "Civil");
        userDAO.addUser(user);
        int id = user.getId();

        assertEquals(1, userDAO.getUserCount());

        boolean deleted = userDAO.deleteUser(id);
        assertTrue(deleted);
        assertEquals(0, userDAO.getUserCount());
        assertNull(userDAO.getUserById(id));
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Handle invalid/nonexistent ID safely")
    public void testNonexistentId() throws SQLException {
        assertNull(userDAO.getUserById(99999));
        assertFalse(userDAO.deleteUser(99999));

        User fakeUser = new User(99999, "Fake", "fake@college.edu", "123", "None");
        assertFalse(userDAO.updateUser(fakeUser));
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Email uniqueness check method (emailExists)")
    public void testEmailExists() throws SQLException {
        User user = new User("Kavin", "kavin@college.edu", "9876543210", "AI");
        userDAO.addUser(user);

        // Check for new user (excludeId = 0)
        assertTrue(userDAO.emailExists("kavin@college.edu", 0));
        assertFalse(userDAO.emailExists("newemail@college.edu", 0));

        // When updating existing user, their own email should not be considered duplicate
        assertFalse(userDAO.emailExists("kavin@college.edu", user.getId()));
    }
}
