document.addEventListener("DOMContentLoaded", function () {
  const overlay = document.getElementById("modalOverlay");
  const body = document.body;

  function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal || !overlay) return;

    overlay.style.display = "flex";
    modal.style.display = "flex";
    modal.classList.add("show-modal");
    body.style.overflow = "hidden";
  }

  function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    const body = document.body;

    const doClose = () => {
      // 1️⃣ Ẩn toàn bộ modal đang hiển thị
      document.querySelectorAll(".show-modal").forEach((m) => {
        m.classList.remove("show-modal");
        m.style.display = "none";
      });

      // 2️⃣ Ẩn toàn bộ lớp phủ overlay
      document.querySelectorAll(".modal-overlay").forEach((overlay) => {
        overlay.style.display = "none";
      });

      // 3️⃣ Mở lại cuộn trang
      body.style.overflow = "";
    };

    doClose();
  }

  // ====== GẮN SỰ KIỆN MỞ / ĐÓNG ======
  document.querySelectorAll("[data-open-modal]").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      const target = btn.getAttribute("data-open-modal");
      openModal(target);
    });
  });

  document.querySelectorAll("[data-close-modal]").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      const target = btn.getAttribute("data-close-modal");
      closeModal(target);
    });
  });

  // ====== ĐÓNG MODAL BẰNG OVERLAY HOẶC ESC ======
  if (overlay) {
    overlay.addEventListener("click", (e) => {
      // click ra ngoài modal
      if (e.target === overlay) {
        document.querySelectorAll(".show-modal").forEach((m) => {
          m.classList.remove("show-modal");
          m.style.display = "none";
        });
        overlay.style.display = "none";
        body.style.overflow = "";
      }
    });
  }

  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      document.querySelectorAll(".show-modal").forEach((m) => {
        m.classList.remove("show-modal");
        m.style.display = "none";
      });
      overlay.style.display = "none";
      body.style.overflow = "";
    }
  });

  // ====== CHUYỂN GIỮA LOGIN ↔ REGISTER ======
  document.querySelectorAll("[data-switch-modal]").forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();

      const targetId = link.getAttribute("data-switch-modal");
      const currentModal = link.closest(".login, .register");
      const targetModal = document.getElementById(targetId);

      if (!targetModal) return;

      // Ẩn modal hiện tại
      if (currentModal) {
        currentModal.classList.remove("show-modal");
        currentModal.style.display = "none";
      }

      // Hiện modal mục tiêu
      targetModal.style.display = "flex";
      targetModal.classList.add("show-modal");
      overlay.style.display = "flex";
      body.style.overflow = "hidden";
    });
  });

  // ====== NGĂN CHUYỂN TRANG KHI CHƯA ĐĂNG NHẬP ======
  const authEl = document.getElementById("auth");
  const isAuthenticated =
    authEl && authEl.getAttribute("data-authenticated") === "true";

  document.querySelectorAll("a.need-auth").forEach((anchor) => {
    anchor.addEventListener("click", (e) => {
      if (!isAuthenticated) {
        e.preventDefault();
        openModal("login");
      }
    });
  });

  // ====== (TÙY CHỌN) TỰ ĐỘNG MỞ MODAL LOGIN KHI CÓ QUERY login=true ======
  const params = new URLSearchParams(window.location.search);
  if (params.get("login") === "true") {
    openModal("login");
  }
});
