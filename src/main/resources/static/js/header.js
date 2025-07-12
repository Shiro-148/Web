
document.addEventListener('DOMContentLoaded', function () {
    const btn = document.getElementById('userMenuBtn');
    const dropdown = document.getElementById('userDropdown');

    // Toggle dropdown khi click vào nút
    btn.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdown.style.display = (dropdown.style.display === 'block' || dropdown.style.display === '') ? 'none' : 'block';
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

