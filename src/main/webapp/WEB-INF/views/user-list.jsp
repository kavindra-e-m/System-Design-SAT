<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management System | College Lab Dashboard</title>
    <!-- Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Navigation Header -->
    <header class="navbar">
        <div class="nav-container">
            <div class="brand-wrapper">
                <div class="brand-icon">
                    <!-- SVG User Group Icon -->
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
                <span class="nav-status">
                    <span class="status-dot"></span>
                    <span>System Online</span>
                </span>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="main-container">

        <!-- Flash Success Notification -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success" role="alert">
                <div class="alert-content">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    <span><c:out value="${successMessage}"/></span>
                </div>
                <button type="button" class="alert-close" aria-label="Close alert">&times;</button>
            </div>
        </c:if>

        <!-- Flash Error Notification -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" role="alert">
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

        <!-- Top Statistics Cards -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                </div>
                <div class="stat-info">
                    <h3>Total Registered Users</h3>
                    <div class="stat-number"><c:out value="${totalUsers}"/></div>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon green">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                        <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path>
                        <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path>
                    </svg>
                </div>
                <div class="stat-info">
                    <h3>Database Status</h3>
                    <div class="stat-number" style="font-size: 1.25rem; font-weight: 600; color: var(--success); margin-top: 0.25rem;">
                        MySQL Connected
                    </div>
                </div>
            </div>
        </section>

        <!-- Action Toolbar: Search Bar + Add User Button -->
        <section class="toolbar-card">
            <form action="${pageContext.request.contextPath}/users" method="GET" class="search-form">
                <div class="search-input-wrapper">
                    <span class="search-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                    </span>
                    <input type="text" 
                           id="searchInput" 
                           name="search" 
                           class="search-input" 
                           placeholder="Search users by name or email..." 
                           value="<c:out value='${searchQuery}'/>" 
                           data-base-url="${pageContext.request.contextPath}/users">
                </div>
                <button type="submit" class="btn btn-outline">
                    Search
                </button>
                <c:if test="${not empty searchQuery}">
                    <a href="${pageContext.request.contextPath}/users" class="btn btn-outline" title="Clear Search Filter">
                        Clear
                    </a>
                </c:if>
            </form>

            <a href="${pageContext.request.contextPath}/users?action=new" class="btn btn-primary" id="btnAddNewUser">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                <span>Add New User</span>
            </a>
        </section>

        <!-- User Directory Table Card -->
        <section class="table-card">
            <div class="table-header">
                <h2 class="table-title">Registered Users Directory</h2>
                <span style="font-size: 0.85rem; color: var(--text-muted);">
                    Showing <c:out value="${userList.size()}"/> record(s)
                </span>
            </div>

            <c:choose>
                <c:when test="${not empty userList}">
                    <div class="table-responsive">
                        <table class="data-table" id="usersTable">
                            <thead>
                                <tr>
                                    <th style="width: 80px;">ID</th>
                                    <th>Full Name</th>
                                    <th>Email Address</th>
                                    <th>Phone</th>
                                    <th>Department</th>
                                    <th style="text-align: right; width: 170px;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="u" items="${userList}">
                                    <tr>
                                        <td>
                                            <span class="user-id-badge">#<c:out value="${u.id}"/></span>
                                        </td>
                                        <td>
                                            <span class="user-name"><c:out value="${u.name}"/></span>
                                        </td>
                                        <td>
                                            <span style="color: var(--text-muted);"><c:out value="${u.email}"/></span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty u.phone}">
                                                    <c:out value="${u.phone}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--text-light); font-style: italic;">N/A</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty u.department}">
                                                    <span class="badge-dept"><c:out value="${u.department}"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--text-light); font-style: italic;">General</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align: right;">
                                            <div class="action-buttons" style="justify-content: flex-end;">
                                                <a href="${pageContext.request.contextPath}/users?action=edit&id=${u.id}" 
                                                   class="btn btn-sm btn-edit" 
                                                   title="Edit user details"
                                                   id="btnEdit-${u.id}">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                    </svg>
                                                    Edit
                                                </a>
                                                <button type="button" 
                                                        class="btn btn-sm btn-delete btn-trigger-delete" 
                                                        data-id="${u.id}" 
                                                        data-name="<c:out value='${u.name}'/>" 
                                                        title="Delete user"
                                                        id="btnDelete-${u.id}">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                        <polyline points="3 6 5 6 21 6"></polyline>
                                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                    </svg>
                                                    Delete
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Empty State -->
                    <div class="empty-state">
                        <div class="empty-icon">
                            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="11" cy="11" r="8"></circle>
                                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                                <line x1="8" y1="11" x2="14" y2="11"></line>
                            </svg>
                        </div>
                        <c:choose>
                            <c:when test="${not empty searchQuery}">
                                <h3 class="empty-title">No Search Results Found</h3>
                                <p class="empty-text">No records match "<c:out value='${searchQuery}'/>". Try adjusting your keyword or clear the search filter.</p>
                                <a href="${pageContext.request.contextPath}/users" class="btn btn-outline">
                                    Clear Search Filter
                                </a>
                            </c:when>
                            <c:otherwise>
                                <h3 class="empty-title">No Users Registered Yet</h3>
                                <p class="empty-text">The database table is currently empty. Get started by adding the first user record.</p>
                                <a href="${pageContext.request.contextPath}/users?action=new" class="btn btn-primary">
                                    Add First User
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

    </main>

    <!-- Delete Confirmation Modal (Protects against accidental deletion & enforces POST request) -->
    <div class="modal-overlay" id="deleteModal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
        <div class="modal-box">
            <div class="modal-body">
                <div class="modal-icon">
                    <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                        <line x1="12" y1="9" x2="12" y2="13"></line>
                        <line x1="12" y1="17" x2="12.01" y2="17"></line>
                    </svg>
                </div>
                <h3 class="modal-title" id="modalTitle">Confirm Record Deletion</h3>
                <p class="modal-desc">
                    Are you sure you want to permanently delete <strong id="deleteUserName">this user</strong> from the database? This action cannot be undone.
                </p>
                <form action="${pageContext.request.contextPath}/users?action=delete" method="POST" id="deleteForm">
                    <input type="hidden" name="id" id="deleteUserId" value="">
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline" id="cancelDeleteBtn">
                            Cancel
                        </button>
                        <button type="submit" class="btn btn-primary" style="background-color: var(--danger); border-color: var(--danger);" id="confirmDeleteSubmit">
                            Yes, Delete User
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Academic Footer -->
    <footer class="site-footer">
        <p>College Lab Practical Experiment: <strong>Servlet-Based User Management with JDBC & MySQL</strong></p>
        <p style="font-size: 0.78rem; margin-top: 0.25rem;">Built with Jakarta Servlet 6.0 &bull; Tomcat 10.1 &bull; Java 17 &bull; MySQL 8</p>
    </footer>

    <!-- Client Script -->
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
