package com.lab.usermanagement.dao;

import com.lab.usermanagement.model.User;
import com.lab.usermanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) for managing User persistence operations.
 * Implements CRUD operations, count, search, and validation using JDBC PreparedStatements.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // SQL Statements
    private static final String SELECT_ALL_USERS = 
            "SELECT id, name, email, phone, department, created_at FROM users ORDER BY id DESC";

    private static final String SELECT_USER_BY_ID = 
            "SELECT id, name, email, phone, department, created_at FROM users WHERE id = ?";

    private static final String INSERT_USER = 
            "INSERT INTO users (name, email, phone, department) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_USER = 
            "UPDATE users SET name = ?, email = ?, phone = ?, department = ? WHERE id = ?";

    private static final String DELETE_USER = 
            "DELETE FROM users WHERE id = ?";

    private static final String SEARCH_USERS = 
            "SELECT id, name, email, phone, department, created_at FROM users " +
            "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) ORDER BY id DESC";

    private static final String COUNT_USERS = 
            "SELECT COUNT(*) FROM users";

    private static final String CHECK_EMAIL_EXISTS = 
            "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND id != ?";

    /**
     * Retrieves all users from the database.
     *
     * @return List of User objects
     * @throws SQLException if a database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all users", e);
            throw e;
        }
        return users;
    }

    /**
     * Retrieves a user by their unique primary key ID.
     *
     * @param id User ID
     * @return User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public User getUserById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user with ID: " + id, e);
            throw e;
        }
        return null;
    }

    /**
     * Inserts a new user record into the database.
     *
     * @param user User object containing name, email, phone, department
     * @return true if insertion succeeded, false otherwise
     * @throws SQLException if database or integrity error occurs
     */
    public boolean addUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getDepartment());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + user.getEmail(), e);
            throw e;
        }
    }

    /**
     * Updates an existing user's record in the database.
     *
     * @param user User object with updated details and matching ID
     * @return true if record was updated, false if user did not exist
     * @throws SQLException if database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getDepartment());
            ps.setInt(5, user.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user with ID: " + user.getId(), e);
            throw e;
        }
    }

    /**
     * Deletes a user by their unique primary key ID.
     *
     * @param id User ID to delete
     * @return true if user was deleted, false if user did not exist
     * @throws SQLException if database error occurs
     */
    public boolean deleteUser(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw e;
        }
    }

    /**
     * Searches users matching the given keyword in either name or email.
     *
     * @param keyword search keyword (case-insensitive)
     * @return List of matching User records
     * @throws SQLException if database error occurs
     */
    public List<User> searchUsers(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }

        String searchPattern = "%" + keyword.trim() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_USERS)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users with keyword: " + keyword, e);
            throw e;
        }
        return users;
    }

    /**
     * Retrieves the total count of registered users in the database.
     *
     * @return total count of user records
     * @throws SQLException if database error occurs
     */
    public int getUserCount() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_USERS);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user count", e);
            throw e;
        }
        return 0;
    }

    /**
     * Checks if an email address is already registered to another user.
     *
     * @param email email address to check
     * @param excludeId current user ID to exclude (e.g. 0 for new user, or current user ID on edit)
     * @return true if email is already taken, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean emailExists(String email, int excludeId) throws SQLException {
        if (email == null) return false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL_EXISTS)) {

            ps.setString(1, email.trim());
            ps.setInt(2, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence: " + email, e);
            throw e;
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet row to a User model object.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setDepartment(rs.getString("department"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
