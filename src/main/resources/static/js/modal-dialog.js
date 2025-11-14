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
      document.querySelectorAll(".show-modal").forEach((m) => {
        m.classList.remove("show-modal");
        m.style.display = "none";
      });

      document.querySelectorAll(".modal-overlay").forEach((overlay) => {
        overlay.style.display = "none";
      });

      body.style.overflow = "";
    };

    doClose();
  }

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

  if (overlay) {
    overlay.addEventListener("click", (e) => {
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

  document.querySelectorAll("[data-switch-modal]").forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();

      const targetId = link.getAttribute("data-switch-modal");
      const currentModal = link.closest(".login, .register");
      const targetModal = document.getElementById(targetId);

      if (!targetModal) return;

      if (currentModal) {
        currentModal.classList.remove("show-modal");
        currentModal.style.display = "none";
      }

      targetModal.style.display = "flex";
      targetModal.classList.add("show-modal");
      overlay.style.display = "flex";
      body.style.overflow = "hidden";
    });
  });

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

  const params = new URLSearchParams(window.location.search);
  if (params.get("login") === "true") {
    openModal("login");
  }
});
