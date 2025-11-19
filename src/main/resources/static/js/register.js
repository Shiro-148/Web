document.addEventListener("DOMContentLoaded", () => {
  const registerModal = document.getElementById("register");
  if (!registerModal) return;

  let overlay = document.getElementById("modalOverlay");
  if (!overlay) {
    overlay = document.createElement("div");
    overlay.id = "modalOverlay";
    overlay.className = "modal-overlay";
    Object.assign(overlay.style, {
      position: "fixed",
      left: "0",
      top: "0",
      width: "100%",
      height: "100%",
      display: "none",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "rgba(0, 0, 0, 0.45)",
      zIndex: "998",
    });
    document.body.appendChild(overlay);
  }

  const isStandaloneRegisterPage = !!document.querySelector(".register_page_container");
  if (isStandaloneRegisterPage) {
    registerModal.classList.add("show-modal");
    registerModal.style.display = "none";
  } else {
    registerModal.classList.remove("show-modal");
    registerModal.style.display = "none";
  }
  registerModal.style.zIndex = "999";

  function openModal() {
    overlay.style.display = "flex";
    registerModal.style.display = "block";
    registerModal.classList.add("show-modal");
    document.body.style.overflow = "hidden";
  }

  function closeModal() {
    registerModal.classList.remove("show-modal");
    registerModal.style.display = "none";
    overlay.style.display = "none";
    document.body.style.overflow = "";
  }

  function openLoginModal() {
    const loginModal = document.getElementById("login");
    if (!loginModal) return;
    loginModal.style.display = "flex";
    loginModal.classList.add("show-modal");
    overlay.style.display = "flex";
    document.body.style.overflow = "hidden";
  }

  document.querySelectorAll('[data-switch-modal="register"]').forEach((trigger) => {
    trigger.addEventListener("click", (e) => {
      e.preventDefault();
      openModal();
    });
  });

  document.querySelectorAll('[data-switch-modal="login"]').forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal();
      openLoginModal();
    });
  });

  const form = document.getElementById("registerForm");
  const registerErrorBox = document.getElementById("registerError");
  const registerMsgBox = document.getElementById("registerMsg");
  const pwError = document.getElementById("pwMismatch");
  const passwordInput = document.getElementById("re-password");
  const confirmPasswordInput = document.getElementById("confirmPassword");
  const phoneInput = document.getElementById("sdt");
  const phoneError = document.getElementById("registerPhoneError");
  const phoneRegex = /^[0-9]{9,11}$/;

  const showRegisterError = (message = "") => {
    if (!registerErrorBox) return;
    registerErrorBox.textContent = message;
    registerErrorBox.style.display = message ? "block" : "none";
  };

  const showRegisterMessage = (message = "") => {
    if (!registerMsgBox) return;
    registerMsgBox.textContent = message;
    registerMsgBox.style.display = message ? "block" : "none";
  };

  const togglePwMismatch = (show) => {
    if (!pwError) return;
    pwError.style.display = show ? "block" : "none";
  };

  const showPhoneError = (message = "") => {
    if (!phoneError) return;
    phoneError.textContent = message;
    phoneError.style.display = message ? "block" : "none";
  };

  const isValidPhone = (value) => phoneRegex.test(value);

  const passwordsMatch = () => {
    if (!passwordInput || !confirmPasswordInput) return true;
    const pw = passwordInput.value.trim();
    const pw2 = confirmPasswordInput.value.trim();
    const shouldValidate = pw.length > 0 && pw2.length > 0;
    const isMatch = pw === pw2;
    if (shouldValidate && !isMatch) {
      togglePwMismatch(true);
      return false;
    }
    if (shouldValidate && isMatch) togglePwMismatch(false);
    if (!shouldValidate) togglePwMismatch(false);
    return isMatch;
  };

  if (passwordInput && confirmPasswordInput) {
    [passwordInput, confirmPasswordInput].forEach((input) => {
      input.addEventListener("input", passwordsMatch);
      input.addEventListener("blur", passwordsMatch);
    });
  }

  if (phoneInput) {
    phoneInput.addEventListener("input", () => showPhoneError());
  }

  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const fullName = document.getElementById("fullName").value.trim();
      const sdt = document.getElementById("sdt").value.trim();
      const pw = document.getElementById("re-password").value.trim();
      const pw2 = document.getElementById("confirmPassword").value.trim();

      togglePwMismatch(false);
      showRegisterError();
      showRegisterMessage();
      showPhoneError();

      if (!fullName || !sdt || !pw || !pw2) {
        showRegisterError("Vui lòng nhập đầy đủ thông tin!");
        return;
      }
      if (!isValidPhone(sdt)) {
        showPhoneError("Số điện thoại không hợp lệ (9-11 chữ số).");
        phoneInput?.focus();
        return;
      }
      if (pw !== pw2) {
        togglePwMismatch(true);
        confirmPasswordInput?.focus();
        return;
      }

      try {
        const res = await fetch("/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ fullName, sdt, password: pw }),
        });

        const text = await res.text();
        if (res.ok) {
          showRegisterMessage("🎉 " + (text || "Đăng ký thành công!"));
          showRegisterError();
          form.reset();
          togglePwMismatch(false);
          setTimeout(() => {
            closeModal();
            window.location.href = "/";
          }, 10);
        } else {
          showRegisterError(text || "⚠️ Đăng ký thất bại!");
        }
      } catch (error) {
        console.error("Lỗi đăng ký:", error);
        showRegisterError("Không thể kết nối đến máy chủ!");
      }
    });
  }
});
