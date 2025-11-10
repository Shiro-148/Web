// dialog-noti.js
// Centralized dialog utilities for the whole site.
// Exposes:
// - window.showMessage(text, [onOk]) -> returns Promise resolved when OK clicked. If onOk provided it's called once.
// - window.showConfirm(text, [onOk]) -> returns Promise<boolean> resolved true if OK clicked, false otherwise.

(function () {
  // Provide immediate globals that will work even if DOMContentLoaded hasn't fired.
  // Internally they will defer to implementations installed when ready.

  // placeholder flags/impls
  window._dialogNotiReady = false;

  window.showMessage = function (text, onOk) {
    // If implementation ready, delegate
    if (window._dialogNotiReady && typeof window._dialogShowMessage === 'function') {
      return window._dialogShowMessage(text, onOk);
    }
    // otherwise wait until ready
    return new Promise(function (resolve) {
      const once = function () {
        try {
          if (typeof window._dialogShowMessage === 'function') {
            window._dialogShowMessage(text, onOk).then(resolve, resolve);
          } else {
            // fallback: do not use alert() — log and call onOk immediately
            try { console.info(text); } catch (e) { /* ignore */ }
            if (typeof onOk === 'function') onOk();
            resolve();
          }
        } finally {
          document.removeEventListener('dialog-noti-ready', once);
        }
      };
      document.addEventListener('dialog-noti-ready', once);
    });
  };

  window.showConfirm = function (text, onOk) {
    if (window._dialogNotiReady && typeof window._dialogShowConfirm === 'function') {
      return window._dialogShowConfirm(text, onOk);
    }
    return new Promise(function (resolve) {
      const once = function () {
        try {
          if (typeof window._dialogShowConfirm === 'function') {
            window._dialogShowConfirm(text, onOk).then(resolve, resolve);
          } else {
            const r = window.confirm(text);
            if (r && typeof onOk === 'function') onOk();
            resolve(!!r);
          }
        } finally {
          document.removeEventListener('dialog-noti-ready', once);
        }
      };
      document.addEventListener('dialog-noti-ready', once);
    });
  };

  function onReady(fn) {
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', fn);
    else fn();
  }

  onReady(function () {
    const messageOverlay = document.getElementById('messageOverlay');
    const messageDialog = document.getElementById('messageDialog');
    const messageText = document.getElementById('messageText');
    const messageOk = document.getElementById('messageOk');
    const messageClose = document.getElementById('messageClose');

    const confirmOverlay = document.getElementById('confirmOverlay');
    const confirmDialog = document.getElementById('confirmDialog');
    const confirmText = document.getElementById('confirmText');
    const confirmOk = document.getElementById('confirmOk');
    const confirmCancel = document.getElementById('confirmCancel');

    function showElement(el) {
      if (!el) return;
      el.style.display = 'flex';
      const inner = el.querySelector('.modal, .modal-confirm, .message-dialog');
      if (inner) { inner.style.display = 'flex'; inner.classList.add('show-modal'); }
      document.body.style.overflow = 'hidden';
    }

    function hideElement(el) {
      if (!el) return;
      el.style.display = 'none';
      el.querySelectorAll('.show-modal').forEach(m => { m.classList.remove('show-modal'); m.style.display = 'none'; });
      document.body.style.overflow = '';
    }

    // message handlers
    if (messageOk) messageOk.addEventListener('click', function () {
      document.dispatchEvent(new CustomEvent('message:ok'));
      hideElement(messageOverlay);
    });
    if (messageClose) messageClose.addEventListener('click', function () { hideElement(messageOverlay); });
    if (messageOverlay) messageOverlay.addEventListener('click', function (e) { if (e.target === messageOverlay) hideElement(messageOverlay); });

    // confirm handlers
    if (confirmCancel) confirmCancel.addEventListener('click', function () { document.dispatchEvent(new CustomEvent('confirm:cancel')); hideElement(confirmOverlay); });
    if (confirmOk) confirmOk.addEventListener('click', function () { document.dispatchEvent(new CustomEvent('confirm:ok')); hideElement(confirmOverlay); });
    if (confirmOverlay) confirmOverlay.addEventListener('click', function (e) { if (e.target === confirmOverlay) hideElement(confirmOverlay); });

    // internal implementations used by the immediate globals
    window._dialogShowMessage = function (textOrObj, onOk) {
      return new Promise(function (resolve) {
        // allow operation when overlay + text exist even if OK button was removed
        if (!messageOverlay || !messageText) {
          // fallback: do not use alert(); just log
          try { console.info(typeof textOrObj === 'string' ? textOrObj : (textOrObj && textOrObj.message) || ''); } catch (e) { /* ignore */ }
          if (typeof onOk === 'function') onOk();
          return resolve();
        }

        // Support object param: { message, thumb, autoClose }
        var message = '';
        var thumb = null;
        var autoCloseMs = null;
        if (typeof textOrObj === 'object' && textOrObj !== null) {
          message = textOrObj.message || '';
          thumb = textOrObj.thumb || null;
          autoCloseMs = textOrObj.autoClose != null ? Number(textOrObj.autoClose) : null;
        } else {
          message = String(textOrObj || '');
        }

        // set text (use innerHTML if provided as safe string)
        try { messageText.innerHTML = message; } catch (e) { messageText.textContent = message; }

        // set thumbnail if available
        var thumbEl = document.getElementById('messageThumb');
        if (thumbEl) {
          if (thumb) { thumbEl.src = thumb; thumbEl.style.display = ''; }
          else { thumbEl.src = ''; thumbEl.style.display = 'none'; }
        }

        // Determine message type (success, error, etc.) and set icon + background
        var type = null;
        if (typeof textOrObj === 'object' && textOrObj !== null) {
          type = textOrObj.type || null;
        }
        var iconHtml = '✓';
        var iconBg = '#21c997';
        if (type === 'error' || type === 'fail' || type === 'danger') {
          iconHtml = '✕';
          iconBg = '#ff5b6a';
        }
        // set the icon element (replace inner content of messageIcon)
        var iconContainer = document.getElementById('messageIcon');
        if (iconContainer) {
          try {
            iconContainer.innerHTML = '<div style="width:64px;height:64px;border-radius:50%;background:' + iconBg + ';display:flex;align-items:center;justify-content:center;color:#fff;font-size:32px;">' + iconHtml + '</div>';
          } catch (e) { /* ignore */ }
        }

        // Show or hide actions area depending on autoClose presence and whether OK exists.
        var actionsEl = document.querySelector('.message-actions');
        if (actionsEl) {
          // if no OK button present, always hide actions area
          if (!document.getElementById('messageOk')) {
            actionsEl.style.display = 'none';
          } else {
            if (autoCloseMs && autoCloseMs > 0) actionsEl.style.display = 'none';
            else actionsEl.style.display = '';
          }
        }

        // Handler cleanup helpers
        let timeoutId = null;
        const cleanup = function () {
          if (timeoutId) { clearTimeout(timeoutId); timeoutId = null; }
          document.removeEventListener('message:ok', okHandler);
        };

        const okHandler = function () { try { if (typeof onOk === 'function') onOk(); } finally { cleanup(); resolve(); } };

        // Wire ok handler only if OK button exists and will dispatch message:ok
        if (document.getElementById('messageOk')) {
          document.addEventListener('message:ok', okHandler);
        }

        // If there's no OK button, ensure we auto-close — default to 2000ms when not specified
        if ((!document.getElementById('messageOk')) && (!autoCloseMs)) {
          autoCloseMs = 2000;
        }

        // Auto-close behavior
        if (autoCloseMs && autoCloseMs > 0) {
          timeoutId = setTimeout(function () {
            try { if (typeof onOk === 'function') onOk(); } catch (e) { /* ignore */ }
            try { hideElement(messageOverlay); } catch (e) { /* ignore */ }
            cleanup();
            resolve();
          }, autoCloseMs);
        }

        showElement(messageOverlay);
      });
    };

    window._dialogShowConfirm = function (text, onOk) {
      return new Promise(function (resolve) {
        if (!confirmOverlay || !confirmOk || !confirmCancel || !confirmText) {
          const r = window.confirm(text);
          if (r && typeof onOk === 'function') onOk();
          return resolve(!!r);
        }
        confirmText.textContent = text || '';
        if (typeof onOk === 'function') {
          const once = function () { try { onOk(); } finally { document.removeEventListener('confirm:ok', once); } };
          document.addEventListener('confirm:ok', once);
        }
        const okHandler = function () { cleanup(); resolve(true); };
        const cancelHandler = function () { cleanup(); resolve(false); };
        function cleanup() {
          document.removeEventListener('confirm:ok', okHandler);
          document.removeEventListener('confirm:cancel', cancelHandler);
        }
        document.addEventListener('confirm:ok', okHandler);
        document.addEventListener('confirm:cancel', cancelHandler);
        showElement(confirmOverlay);
      });
    };

    window._dialogNotiReady = true;
    document.dispatchEvent(new CustomEvent('dialog-noti-ready'));
  });
})();
document.addEventListener("DOMContentLoaded", function () {
  const overlay = document.getElementById("messageOverlay");
  const dlg = document.getElementById("messageDialog");
  const ok = document.getElementById("messageOk");
  const close = document.getElementById("messageClose");
  function hide() {
    if (overlay) overlay.style.display = "none";
    if (dlg) {
      dlg.classList.remove("show-modal");
      dlg.style.display = "none";
    }
    document.body.style.overflow = "";
  }
  if (ok) ok.addEventListener("click", hide);
  if (close) close.addEventListener("click", hide);
  if (overlay)
    overlay.addEventListener("click", function (e) {
      if (e.target === overlay) hide();
    });
});
// Helper to show message using existing message overlay/dialog (from location or modal_dialog fragments)
function showMessage(message, onOk) {
  const overlay =
    document.getElementById("messageOverlay") ||
    document.getElementById("modalOverlay");
  const dialog = document.getElementById("messageDialog");
  const textEl = document.getElementById("messageText");
  const okBtn =
    document.getElementById("messageOk") ||
    document.getElementById("messageOk");

  if (textEl) textEl.textContent = message;

  // Show overlay and dialog
  if (overlay) overlay.style.display = "flex";
  if (dialog) {
    dialog.style.display = "flex";
    dialog.classList.add("show-modal");
  }
  document.body.style.overflow = "hidden";

  if (onOk && okBtn) {
    // attach one-time listener that executes onOk then lets existing handler hide the dialog
    const handler = function (e) {
      try {
        onOk();
      } finally {
        okBtn.removeEventListener("click", handler);
      }
    };
    okBtn.addEventListener("click", handler);
  }
}

// AJAX handler to save account profile
document
  .getElementById("saveProfileBtn")
  ?.addEventListener("click", function () {
    const fullName = document.getElementById("full_name")?.value || null;
    const birthDate = document.getElementById("birthday")?.value || null;
    const email = document.getElementById("email")?.value || null;

    fetch("/account/update", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        fullName: fullName,
        birthDate: birthDate,
        email: email,
      }),
    })
      .then(async (res) => {
        const text = await res.text();
        if (res.ok) {
          // show success message and reload when user confirms
          showMessage("Cập nhật thành công", function () {
            window.location.reload();
          });
        } else {
          showMessage("Lỗi: " + text);
        }
      })
      .catch((err) => {
        console.error(err);
        showMessage("Lỗi kết nối");
      });
  });
