document.addEventListener('DOMContentLoaded', function () {
    var openBtn = document.getElementById('openModalBtn');
    var modal = document.getElementById('location');
    var cancelBtn = document.getElementById('cancelBtn');

    if (openBtn && modal) {
        openBtn.onclick = function () {
            modal.classList.add('show-modal');
            document.getElementById('modalOverlay').style.display = 'flex';
        };
    }
    if (cancelBtn && modal) {
        cancelBtn.onclick = function () {
            modal.classList.remove('show-modal');
            document.getElementById('modalOverlay').style.display = 'none';
        };
    }
    window.onclick = function (event) {
        if (event.target === modal) {
            modal.classList.remove('show-modal');
            document.getElementById('modalOverlay').style.display = 'none';
        }
    };
    var modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', function (event) {
            if (event.target === modalOverlay) {
                modal.classList.remove('show-modal');
                modalOverlay.style.display = 'none';
            }
        });
    }
});
document.addEventListener('DOMContentLoaded', function () {
    var openLoginBtn = document.querySelector('.button[data-bs-toggle="modal"]');
    var loginModal = document.getElementById('login');
    var closeLoginBtn = loginModal.querySelector('.btn_close');

    if (openLoginBtn && loginModal) {
        openLoginBtn.onclick = function () {
            loginModal.classList.add('show-modal');
            loginModal.style.display = 'flex';
            document.getElementById('modalOverlay').style.display = 'flex';
        };
    }
    if (closeLoginBtn && loginModal) {
        closeLoginBtn.onclick = function () {
            loginModal.classList.remove('show-modal');
            loginModal.style.display = 'none';
            document.getElementById('modalOverlay').style.display = 'none';
        };
    }
    window.onclick = function (event) {
        if (event.target === loginModal) {
            loginModal.classList.remove('show-modal');
            loginModal.style.display = 'none';
            document.getElementById('modalOverlay').style.display = 'none';
        }
    };
    var modalOverlay = document.getElementById('modalOverlay');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', function (event) {
            if (event.target === modalOverlay) {
            loginModal.classList.remove('show-modal');
            loginModal.style.display = 'none';
            document.getElementById('modalOverlay').style.display = 'none';
            }
        });
    }
});
// Note: JWT cookie is HttpOnly, client JS cannot read it. Use server-rendered flag instead.
document.addEventListener('DOMContentLoaded', function () {
    const authEl = document.getElementById('auth');
    const isAuthenticated = authEl && authEl.getAttribute('data-authenticated') === 'true';

    // Intercept clicks on any link that requires auth (class need-auth)
    const needAuthAnchors = document.querySelectorAll('a.need-auth');
    if (needAuthAnchors && needAuthAnchors.length > 0) {
        needAuthAnchors.forEach(function (anchor) {
            anchor.addEventListener('click', function (e) {
                if (!isAuthenticated) {
                    e.preventDefault();
                    const loginModal = document.getElementById('login');
                    if (loginModal) {
                        loginModal.style.display = 'flex';
                        loginModal.classList.add('show-modal');
                        const overlay = document.getElementById('modalOverlay');
                        if (overlay) overlay.style.display = 'flex';
                    }
                }
            });
        });
    }
});