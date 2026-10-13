package com.lab.usermanagement.servlet;

import com.lab.usermanagement.dao.UserDAO;
import com.lab.usermanagement.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Controller Servlet managing web requests for User operations.
 * Implements MVC pattern routing, server-side validation, and Post/Redirect/Get (PRG).
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/users", "/user", ""})
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
        LOGGER.info("UserServlet initialized successfully");
    }

    // Constructor for testing / dependency injection
    public UserServlet(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserServlet() {
        // default constructor used by servlet container
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action.toLowerCase()) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "list":
                default:
                    listUsers(request, response);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error handling GET action: " + action, e);
            request.setAttribute("errorMessage", "Database connection or query error occurred. Please verify MySQL status.");
            try {
                request.setAttribute("userList", List.of());
                request.setAttribute("totalUsers", 0);
            } catch (Exception ignored) {}
            request.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in GET request", e);
            request.setAttribute("errorMessage", "An unexpected error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action.toLowerCase()) {
                case "create":
                    createUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/users");
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error handling POST action: " + action, e);
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Database error occurred: " + sanitizeDbError(e), StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error handling POST action: " + action, e);
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("An error occurred while processing the request.", StandardCharsets.UTF_8));
        }
    }

    /**
     * Lists all users or displays filtered search results.
     */
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchQuery = request.getParameter("search");
        List<User> userList;
        int totalUsers = 0;

        try {
            totalUsers = userDAO.getUserCount();
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                userList = userDAO.searchUsers(searchQuery.trim());
                request.setAttribute("searchQuery", searchQuery.trim());
            } else {
                userList = userDAO.getAllUsers();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not fetch users or count from database", e);
            throw e;
        }

        request.setAttribute("userList", userList);
        request.setAttribute("totalUsers", totalUsers);

        // Flash message handling from URL query params
        String msg = request.getParameter("message");
        if ("created".equalsIgnoreCase(msg)) {
            request.setAttribute("successMessage", "User record has been successfully created!");
        } else if ("updated".equalsIgnoreCase(msg)) {
            request.setAttribute("successMessage", "User details have been successfully updated!");
        } else if ("deleted".equalsIgnoreCase(msg)) {
            request.setAttribute("successMessage", "User record has been successfully deleted!");
        }

        String err = request.getParameter("error");
        if (err != null && !err.isBlank()) {
            request.setAttribute("errorMessage", err);
        }

        request.getRequestDispatcher("/WEB-INF/views/user-list.jsp").forward(request, response);
    }

    /**
     * Shows the form to add a new user.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("formAction", "create");
        request.setAttribute("pageTitle", "Add New User");
        request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
    }

    /**
     * Shows the form to edit an existing user.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Missing user ID for edit operation.", StandardCharsets.UTF_8));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Invalid user ID provided.", StandardCharsets.UTF_8));
            return;
        }

        User existingUser = userDAO.getUserById(id);
        if (existingUser == null) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("User with ID #" + id + " was not found.", StandardCharsets.UTF_8));
            return;
        }

        request.setAttribute("user", existingUser);
        request.setAttribute("formAction", "update");
        request.setAttribute("pageTitle", "Edit User (ID: #" + existingUser.getId() + ")");
        request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
    }

    /**
     * Creates a new user record after server-side validation.
     */
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String name = trimOrNull(request.getParameter("name"));
        String email = trimOrNull(request.getParameter("email"));
        String phone = trimOrNull(request.getParameter("phone"));
        String department = trimOrNull(request.getParameter("department"));

        User candidate = new User(name, email, phone, department);

        // Server-Side Validation
        String validationError = validateUser(candidate, 0);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("user", candidate);
            request.setAttribute("formAction", "create");
            request.setAttribute("pageTitle", "Add New User");
            request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = userDAO.addUser(candidate);
            if (success) {
                // Post/Redirect/Get pattern
                response.sendRedirect(request.getContextPath() + "/users?message=created");
            } else {
                request.setAttribute("errorMessage", "Failed to save user record. Please try again.");
                request.setAttribute("user", candidate);
                request.setAttribute("formAction", "create");
                request.setAttribute("pageTitle", "Add New User");
                request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.log(Level.WARNING, "Duplicate email attempt: " + email, e);
            request.setAttribute("errorMessage", "The email address '" + email + "' is already registered. Please use another email.");
            request.setAttribute("user", candidate);
            request.setAttribute("formAction", "create");
            request.setAttribute("pageTitle", "Add New User");
            request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
        }
    }

    /**
     * Updates an existing user record after server-side validation.
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Missing user ID for update.", StandardCharsets.UTF_8));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Invalid user ID format.", StandardCharsets.UTF_8));
            return;
        }

        String name = trimOrNull(request.getParameter("name"));
        String email = trimOrNull(request.getParameter("email"));
        String phone = trimOrNull(request.getParameter("phone"));
        String department = trimOrNull(request.getParameter("department"));

        User candidate = new User(id, name, email, phone, department);

        // Server-Side Validation
        String validationError = validateUser(candidate, id);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("user", candidate);
            request.setAttribute("formAction", "update");
            request.setAttribute("pageTitle", "Edit User (ID: #" + id + ")");
            request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
            return;
        }

        try {
            boolean success = userDAO.updateUser(candidate);
            if (success) {
                // Post/Redirect/Get pattern
                response.sendRedirect(request.getContextPath() + "/users?message=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/users?error=" +
                        URLEncoder.encode("User with ID #" + id + " was not found or could not be updated.", StandardCharsets.UTF_8));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            LOGGER.log(Level.WARNING, "Duplicate email violation during update: " + email, e);
            request.setAttribute("errorMessage", "The email address '" + email + "' is already in use by another user.");
            request.setAttribute("user", candidate);
            request.setAttribute("formAction", "update");
            request.setAttribute("pageTitle", "Edit User (ID: #" + id + ")");
            request.getRequestDispatcher("/WEB-INF/views/user-form.jsp").forward(request, response);
        }
    }

    /**
     * Deletes a user record by ID (protected POST request).
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Missing user ID for delete operation.", StandardCharsets.UTF_8));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Invalid user ID format for deletion.", StandardCharsets.UTF_8));
            return;
        }

        boolean success = userDAO.deleteUser(id);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/users?message=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/users?error=" +
                    URLEncoder.encode("Unable to delete: User ID #" + id + " does not exist.", StandardCharsets.UTF_8));
        }
    }

    /**
     * Server-side user validator enforcing required fields, length limits, email format, and uniqueness.
     */
    private String validateUser(User user, int excludeId) throws SQLException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return "Full Name is required and cannot be blank.";
        }
        if (user.getName().length() > 100) {
            return "Full Name cannot exceed 100 characters.";
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email Address is required and cannot be blank.";
        }
        if (user.getEmail().length() > 150) {
            return "Email Address cannot exceed 150 characters.";
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return "Please provide a valid email address (e.g., student@college.edu).";
        }
        if (user.getPhone() != null && user.getPhone().length() > 15) {
            return "Phone number cannot exceed 15 characters.";
        }
        if (user.getDepartment() != null && user.getDepartment().length() > 100) {
            return "Department name cannot exceed 100 characters.";
        }
        // Unique email check
        if (userDAO.emailExists(user.getEmail(), excludeId)) {
            return "The email address '" + user.getEmail() + "' is already registered.";
        }
        return null;
    }

    private String trimOrNull(String val) {
        if (val == null) return null;
        String t = val.trim();
        return t.isEmpty() ? null : t;
    }

    private String sanitizeDbError(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return "Duplicate record constraint violated.";
        }
        return "Database operation could not be completed.";
    }
}
