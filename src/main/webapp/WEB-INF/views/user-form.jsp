<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}"/> | User Management System</title>
    <!-- Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Navigation Header -->
    <header class="navbar">
        <div class="nav-container">
            <div class="brand-wrapper">
                <div class="brand-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                </div>
                <div>
                    <h1 class="brand-title">
                        User Management System
                        <span class="brand-badge">Lab Exp: Servlets & JDBC</span>
                    </h1>
                </div>
            </div>
            <div class="nav-meta">
                <a href="${pageContext.request.contextPath}/users" class="btn btn-sm btn-outline">
                    &larr; Back to Dashboard
                </a>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="main-container">

        <!-- Error Notification Banner -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert" style="max-width: 680px; margin: 0 auto 1.5rem auto;">
                <div class="alert-content">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    <span><c:out value="${errorMessage}"/></span>
                </div>
                <button type="button" class="alert-close" aria-label="Close alert">&times;</button>
            </div>
        </c:if>

        <!-- Form Card -->
        <div class="form-card">
            <div class="form-header">
                <h2><c:out value="${pageTitle}"/></h2>
                <p>
                    <c:choose>
                        <c:when test="${formAction eq 'update'}">
                            Modify the details below and click Update User to save changes.
                        </c:when>
                        <c:otherwise>
                            Fill in the user information below to create a new database record.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>

            <form action="${pageContext.request.contextPath}/users?action=${formAction}" method="POST" id="userForm" class="form-body" novalidate>
                
                <!-- Hidden ID Field for Update Action -->
                <c:if test="${formAction eq 'update'}">
                    <input type="hidden" name="id" value="<c:out value='${user.id}'/>">
                </c:if>

                <!-- Full Name -->
                <div class="form-group">
                    <label for="name" class="form-label">
                        Full Name <span class="req-star">*</span>
                    </label>
                    <input type="text" 
                           id="name" 
                           name="name" 
                           class="form-control" 
                           placeholder="e.g., Aarav Sharma" 
                           value="<c:out value='${user.name}'/>" 
                           maxlength="100" 
                           required>
                    <div class="form-error-feedback" id="nameError"></div>
                    <div class="form-hint">Enter the complete name of the user (maximum 100 characters).</div>
                </div>

                <!-- Email Address -->
                <div class="form-group">
                    <label for="email" class="form-label">
                        Email Address <span class="req-star">*</span>
                    </label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           class="form-control" 
                           placeholder="e.g., student@college.edu" 
                           value="<c:out value='${user.email}'/>" 
                           maxlength="150" 
                           required>
                    <div class="form-error-feedback" id="emailError"></div>
                    <div class="form-hint">Must be unique across all users. Used for communications and login identification.</div>
                </div>

                <!-- Phone Number -->
                <div class="form-group">
                    <label for="phone" class="form-label">
                        Phone Number <span style="font-size: 0.8rem; color: var(--text-muted); font-weight: normal;">(Optional)</span>
                    </label>
                    <input type="tel" 
                           id="phone" 
                           name="phone" 
                           class="form-control" 
                           placeholder="e.g., +91 9876543210" 
                           value="<c:out value='${user.phone}'/>" 
                           maxlength="15">
                    <div class="form-hint">Standard mobile or telephone format (up to 15 digits).</div>
                </div>

                <!-- Department -->
                <div class="form-group">
                    <label for="department" class="form-label">
                        Department <span style="font-size: 0.8rem; color: var(--text-muted); font-weight: normal;">(Optional)</span>
                    </label>
                    <input list="departmentsList" 
                           id="department" 
                           name="department" 
                           class="form-control" 
                           placeholder="Select or type department..." 
                           value="<c:out value='${user.department}'/>" 
                           maxlength="100">
                    <datalist id="departmentsList">
                        <option value="Computer Science and Engineering">
                        <option value="Information Technology">
                        <option value="Electronics and Communication">
                        <option value="Electrical and Electronics">
                        <option value="Mechanical Engineering">
                        <option value="Data Science & AI">
                        <option value="Civil Engineering">
                        <option value="Biotechnology">
                        <option value="Administration & Staff">
                    </datalist>
                    <div class="form-hint">Choose from college departments or enter a custom department.</div>
                </div>

                <!-- Form Action Buttons -->
                <div class="form-footer">
                    <a href="${pageContext.request.contextPath}/users" class="btn btn-outline" id="btnCancel">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary" id="btnSubmitUser">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                        <span>
                            <c:choose>
                                <c:when test="${formAction eq 'update'}">Update User</c:when>
                                <c:otherwise>Save User</c:otherwise>
                            </c:choose>
                        </span>
                    </button>
                </div>

            </form>
        </div>

    </main>

    <!-- Academic Footer -->
    <footer class="site-footer">
        <p>College Lab Practical Experiment: <strong>Servlet-Based User Management with JDBC & MySQL</strong></p>
    </footer>

    <!-- Client Script -->
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
