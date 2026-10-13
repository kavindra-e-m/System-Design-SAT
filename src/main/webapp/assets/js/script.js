/**
 * User Management System - Client Side Interactions
 * Features:
 *  - Delete Confirmation Modal
 *  - Client-side Form Validation
 *  - Alert Dismissal
 *  - Search input helpers
 */

document.addEventListener('DOMContentLoaded', () => {

    // --- 1. Delete Confirmation Modal Handling ---
    const deleteModal = document.getElementById('deleteModal');
    const deleteForm = document.getElementById('deleteForm');
    const deleteUserIdInput = document.getElementById('deleteUserId');
    const deleteUserNameSpan = document.getElementById('deleteUserName');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');

    // Attach event listeners to all delete buttons
    document.querySelectorAll('.btn-trigger-delete').forEach(button => {
        button.addEventListener('click', (e) => {
            const userId = button.getAttribute('data-id');
            const userName = button.getAttribute('data-name');

            if (deleteUserIdInput && deleteUserNameSpan && deleteModal) {
                deleteUserIdInput.value = userId;
                deleteUserNameSpan.textContent = userName ? `"${userName}" (ID #${userId})` : `User #${userId}`;
                openModal(deleteModal);
            }
        });
    });

    if (cancelDeleteBtn && deleteModal) {
        cancelDeleteBtn.addEventListener('click', () => {
            closeModal(deleteModal);
        });
    }

    // Close modal when clicking outside box
    if (deleteModal) {
        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                closeModal(deleteModal);
            }
        });
    }

    // Keyboard ESC to close modal
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && deleteModal && deleteModal.classList.contains('active')) {
            closeModal(deleteModal);
        }
    });

    function openModal(modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeModal(modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }

    // --- 2. Alert Dismissal Handling ---
    document.querySelectorAll('.alert-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const alert = btn.closest('.alert');
            if (alert) {
                alert.style.transition = 'opacity 0.2s ease, transform 0.2s ease';
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-6px)';
                setTimeout(() => alert.remove(), 200);
            }
        });
    });

    // Auto dismiss success alert after 6 seconds
    const successAlert = document.querySelector('.alert-success');
    if (successAlert) {
        setTimeout(() => {
            if (document.body.contains(successAlert)) {
                successAlert.style.transition = 'opacity 0.3s ease';
                successAlert.style.opacity = '0';
                setTimeout(() => successAlert.remove(), 300);
            }
        }, 6000);
    }

    // --- 3. Client-Side Form Validation (User Form) ---
    const userForm = document.getElementById('userForm');
    if (userForm) {
        const nameInput = document.getElementById('name');
        const emailInput = document.getElementById('email');
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        userForm.addEventListener('submit', (e) => {
            let isValid = true;

            // Validate Name
            if (nameInput) {
                const nameVal = nameInput.value.trim();
                const nameErr = document.getElementById('nameError');
                if (!nameVal) {
                    showError(nameInput, nameErr, 'Full Name is required.');
                    isValid = false;
                } else if (nameVal.length > 100) {
                    showError(nameInput, nameErr, 'Full Name cannot exceed 100 characters.');
                    isValid = false;
                } else {
                    clearError(nameInput, nameErr);
                }
            }

            // Validate Email
            if (emailInput) {
                const emailVal = emailInput.value.trim();
                const emailErr = document.getElementById('emailError');
                if (!emailVal) {
                    showError(emailInput, emailErr, 'Email Address is required.');
                    isValid = false;
                } else if (!emailRegex.test(emailVal)) {
                    showError(emailInput, emailErr, 'Please enter a valid email (e.g. name@college.edu).');
                    isValid = false;
                } else if (emailVal.length > 150) {
                    showError(emailInput, emailErr, 'Email cannot exceed 150 characters.');
                    isValid = false;
                } else {
                    clearError(emailInput, emailErr);
                }
            }

            if (!isValid) {
                e.preventDefault();
            }
        });

        // Instant validation on input blur
        if (nameInput) {
            nameInput.addEventListener('blur', () => {
                const nameErr = document.getElementById('nameError');
                if (!nameInput.value.trim()) {
                    showError(nameInput, nameErr, 'Full Name is required.');
                } else {
                    clearError(nameInput, nameErr);
                }
            });
        }

        if (emailInput) {
            emailInput.addEventListener('blur', () => {
                const emailErr = document.getElementById('emailError');
                const val = emailInput.value.trim();
                if (!val) {
                    showError(emailInput, emailErr, 'Email Address is required.');
                } else if (!emailRegex.test(val)) {
                    showError(emailInput, emailErr, 'Please enter a valid email address.');
                } else {
                    clearError(emailInput, emailErr);
                }
            });
        }
    }

    function showError(input, errorElement, message) {
        input.classList.add('is-invalid');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    function clearError(input, errorElement) {
        input.classList.remove('is-invalid');
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
    }

    // --- 4. Search Form Clear helper ---
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                if (searchInput.value.trim() !== '') {
                    searchInput.value = '';
                } else {
                    window.location.href = searchInput.getAttribute('data-base-url') || 'users';
                }
            }
        });
    }

});
