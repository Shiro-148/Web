document.addEventListener('DOMContentLoaded', function () {
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');
    const btnStep1 = document.getElementById('btn-step1');
    const btnBack = step2?.querySelector('.btn_back');
    const inputUsername = document.getElementById('username');
    const hiddenUsername = document.getElementById('username_hidden');
    const usernameError = document.getElementById('loginUsernameError');

    const showUsernameFormatError = (message = '') => {
        if (!usernameError) return;
        usernameError.textContent = message;
        usernameError.style.display = message ? 'block' : 'none';
    };

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
        if (!value) return false;
        // Cho phép mọi username có chứa 'admin'
        if (value.toLowerCase().includes('admin')) {
            return true;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const phoneRegex = /^[0-9]{9,11}$/;
        return emailRegex.test(value) || phoneRegex.test(value);
    }

    if (btnStep1) {
        btnStep1.addEventListener('click', function (e) {
            e.preventDefault();
            const username = inputUsername.value.trim();

            showUsernameFormatError();

            if (!username) {
                showUsernameFormatError('Vui lòng nhập số điện thoại hoặc email của bạn.');
                inputUsername.focus();
                return;
            }

            if (!isValidUsername(username)) {
                showUsernameFormatError('Số điện thoại hoặc email không hợp lệ. Vui lòng kiểm tra lại.');
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

    if (inputUsername) {
        inputUsername.addEventListener('input', () => showUsernameFormatError());

        inputUsername.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                btnStep1?.click();
            }
        });
    }

    step1.style.display = 'flex';
    step2.style.display = 'none';
});
