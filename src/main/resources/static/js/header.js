document.addEventListener('DOMContentLoaded', function () {
    const userBtn = document.getElementById('menu-user');
    const dropdownMenu = userBtn.querySelector('.dropdown-menu');

    userBtn.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdownMenu.classList.toggle('show');
    });

    document.addEventListener('click', function (e) {
        if (!userBtn.contains(e.target)) {
            dropdownMenu.classList.remove('show');
        }
    });
});

