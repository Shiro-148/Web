document.addEventListener('DOMContentLoaded', function () {
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');
    const btnStep1 = document.getElementById('btn-step1');
    const btnBack = step2?.querySelector('.btn_back');
    const inputUsername = document.getElementById('username');
    const hiddenUsername = document.getElementById('username_hidden');

    function switchStep(from, to) {
        from.classList.add('fade-out');
        setTimeout(() => {
            from.style.display = 'none';
            from.classList.remove('fade-out');
            to.style.display = 'flex';
            to.classList.add('fade-in');
            setTimeout(() => to.classList.remove('fade-in'), 300);
        }, 300);
    }

    function isValidUsername(value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const phoneRegex = /^[0-9]{9,11}$/;
        return emailRegex.test(value) || phoneRegex.test(value);
    }

    if (btnStep1) {
        btnStep1.addEventListener('click', function (e) {
            e.preventDefault();
            const username = inputUsername.value.trim();

            if (!username) {
                if (window.showMessage) window.showMessage('Vui lòng nhập số điện thoại hoặc email của bạn.');
                else console.info('Vui lòng nhập số điện thoại hoặc email của bạn.');
                inputUsername.focus();
                return;
            }

            if (!isValidUsername(username)) {
                if (window.showMessage) window.showMessage('Số điện thoại hoặc email không hợp lệ. Vui lòng kiểm tra lại.');
                else console.info('Số điện thoại hoặc email không hợp lệ. Vui lòng kiểm tra lại.');
                inputUsername.focus();
                return;
            }

            hiddenUsername.value = username;
            switchStep(step1, step2);
        });
    }

    if (btnBack) {
        btnBack.addEventListener('click', function (e) {
            e.preventDefault();
            switchStep(step2, step1);
        });
    }

    inputUsername.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            btnStep1.click();
        }
    });

    step1.style.display = 'flex';
    step2.style.display = 'none';
});
