
document.addEventListener('DOMContentLoaded', function () {
    const btn = document.getElementById('userMenuBtn');
    const dropdown = document.getElementById('userDropdown');

    // Toggle dropdown khi click vào nút
    btn.addEventListener('click', function (e) {
        e.stopPropagation(); // Ngăn sự kiện lan ra ngoài
        dropdown.style.display = (dropdown.style.display === 'block') ? 'none' : 'block';
    });

    // Ẩn dropdown khi click ra ngoài
    document.addEventListener('click', function () {
        dropdown.style.display = 'none';
    });

    // Ngăn dropdown bị tắt khi click bên trong nó
    dropdown.addEventListener('click', function (e) {
        e.stopPropagation();
    });
});

