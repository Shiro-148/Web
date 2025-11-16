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

  registerModal.style.display = "none";
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

  const openRegister = document.querySelector('[data-switch-modal="register"]');
  if (openRegister) {
    openRegister.addEventListener("click", (e) => {
      e.preventDefault();
      openModal();
    });
  }

  document.querySelectorAll('[data-switch-modal="login"]').forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal();
      openLoginModal();
    });
  });

  const form = document.getElementById("registerForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const fullName = document.getElementById("fullName").value.trim();
      const sdt = document.getElementById("sdt").value.trim();
      const pw = document.getElementById("re-password").value.trim();
      const pw2 = document.getElementById("confirmPassword").value.trim();
      const pwError = document.getElementById("pwMismatch");
      const msgBox = document.querySelector(".form_msg");
      const errBox = document.querySelector(".form_error");

      if (pwError) pwError.style.display = "none";
      if (errBox) errBox.textContent = "";
      if (msgBox) msgBox.textContent = "";

      if (!fullName || !sdt || !pw || !pw2) {
        errBox.textContent = "Vui lòng nhập đầy đủ thông tin!";
        return;
      }
      if (pw !== pw2) {
        if (pwError) pwError.style.display = "block";
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
          if (msgBox)
            msgBox.textContent = "🎉 " + (text || "Đăng ký thành công!");
          form.reset();
          setTimeout(() => {
            closeModal();
            window.location.href = "/";
          }, 10);
        } else {
          if (errBox) errBox.textContent = text || "⚠️ Đăng ký thất bại!";
        }
      } catch (error) {
        console.error("Lỗi đăng ký:", error);
        if (errBox) errBox.textContent = "Không thể kết nối đến máy chủ!";
      }
    });
  }
});
