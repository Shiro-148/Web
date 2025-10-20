document.addEventListener('DOMContentLoaded', function () {
    const btnStep1 = document.getElementById('btn-step1');
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');

    if (btnStep1) {
        btnStep1.addEventListener('click', function (e) {
            e.preventDefault();
            // Lấy username từ step1 và gán vào input ẩn ở step2
            var username = document.getElementById('username').value;
            document.getElementById('username_hidden').value = username;
            step1.style.display = 'none';
            step2.style.display = 'flex';
        });
    }
    const btnBack = document.querySelector('#step2 .btn_back');
    if (btnBack) {
        btnBack.addEventListener('click', function (e) {
            e.preventDefault();
            step2.style.display = 'none';
            step1.style.display = 'flex';
        });
    }
});