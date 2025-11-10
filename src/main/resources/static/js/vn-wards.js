// vn-wards.js
// Fetches provinces and wards from server endpoints and submits address via AJAX

(function () {
  const provincesApi = '/api/vn/provinces';
  const wardsApiBase = '/api/vn/provinces'; // append /{code}/wards
  const addressApiBase = '/api/address';
  const addressCreateApi = '/api/address/create';

  let provinces = [];

  const provinceSelect = document.getElementById('provinceSelect');
  const wardSelect = document.getElementById('wardSelect');

  function populateProvinces() {
    if (!provinceSelect) return;
    provinceSelect.innerHTML = '<option value="">-- Chọn tỉnh/thành --</option>';
    provinces.forEach(function (p) {
      const opt = document.createElement('option');
      opt.value = p.province_code || p.code || p.name;
      opt.textContent = p.name;
      provinceSelect.appendChild(opt);
    });
  }

  function populateWardsList(wards) {
    if (!wardSelect) return;
    wardSelect.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
    (wards || []).forEach(function (w) {
      const opt = document.createElement('option');
      opt.value = w.ward_code || w.name;
      opt.textContent = w.name;
      wardSelect.appendChild(opt);
    });
  }

  async function onProvinceChange() {
    if (!provinceSelect) return;
    const code = provinceSelect.value;
    if (!code) {
      if (wardSelect) wardSelect.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
      return;
    }
    try {
      const res = await fetch(`${wardsApiBase}/${encodeURIComponent(code)}/wards`, { cache: 'no-store' });
      if (!res.ok) throw new Error('Không tải được wards: ' + res.status);
      const wards = await res.json();
      populateWardsList(wards);
    } catch (err) {
      console.error('Lỗi load wards', err);
      if (wardSelect) wardSelect.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
    }
  }

  function init() {
    if (provinceSelect) provinceSelect.addEventListener('change', onProvinceChange);

    // fetch provinces list from API
    fetch(provincesApi, { cache: 'no-store' })
      .then(res => {
        if (!res.ok) throw new Error('Không tải được provinces API status=' + res.status);
        return res.json();
      })
      .then(json => {
        provinces = json;
        populateProvinces();
      })
      .catch(err => {
        console.error('Lỗi load provinces', err);
      });

    // Enable submit button only when required fields are present
    const submitBtn = document.getElementById('submitBtn');
  const addressInput = document.getElementById('address');
    const nameInput = document.getElementById('name_address');
  const phoneInput = document.getElementById('phone_address');
    const noteInput = document.getElementById('note');
    const defaultCheckbox = document.getElementById('check');

    function updateSubmitState() {
      if (!submitBtn) return;
      const addressOk = addressInput && addressInput.value.trim().length > 0;
      const provinceOk = provinceSelect && provinceSelect.value;
      const wardOk = wardSelect && wardSelect.value;
      submitBtn.disabled = !(addressOk && provinceOk && wardOk);
    }

    addressInput && addressInput.addEventListener('input', updateSubmitState);
    nameInput && nameInput.addEventListener('input', updateSubmitState);
    provinceSelect && provinceSelect.addEventListener('change', updateSubmitState);
    wardSelect && wardSelect.addEventListener('change', updateSubmitState);

    // Helper: open/close modal
    const modal = document.getElementById('location');
    const overlay = document.getElementById('modalOverlay');
    function openModal() {
      if (modal) modal.classList.add('show-modal');
      if (overlay) overlay.style.display = 'flex';
    }
    function closeModal() {
      if (modal) modal.classList.remove('show-modal');
      if (overlay) overlay.style.display = 'none';
    }

      // Dialog helpers: delegate to centralized dialog-noti.js if available.
      function showConfirm(text) {
        if (typeof window.showConfirm === 'function') return window.showConfirm(text);
        // fallback to native confirm
        return Promise.resolve(window.confirm(text));
      }

      function showMessage(text) {
        if (typeof window.showMessage === 'function') return window.showMessage(text);
        try { console.info(text); } catch (e) { /* ignore */ }
        return Promise.resolve();
      }

    // Edit/Delete handlers
    function attachRowButtons() {
      document.querySelectorAll('.btn_address_delete').forEach(btn => {
        btn.addEventListener('click', async function (e) {
          const id = btn.getAttribute('data-id');
          if (!id) return;
          const ok = await showConfirm('Xác nhận xoá địa chỉ này?');
          if (!ok) return;
          try {
            const res = await fetch(`${addressApiBase}/${encodeURIComponent(id)}`, { method: 'DELETE' });
            if (!res.ok) {
              const txt = await res.text();
              throw new Error(txt || ('Delete failed: ' + res.status));
            }
            reloadAddressView();
          } catch (err) {
            console.error('Delete error', err);
            await showMessage('Không thể xoá địa chỉ: ' + (err.message || '')); 
          }
        });
      });

      document.querySelectorAll('.btn_address_update').forEach(btn => {
        btn.addEventListener('click', async function (e) {
          const id = btn.getAttribute('data-id');
          if (!id) return;
          try {
            const res = await fetch(`${addressApiBase}/${encodeURIComponent(id)}`);
            if (!res.ok) throw new Error('Load address failed: ' + res.status);
            const addr = await res.json();
            // populate modal fields
            if (addressInput) addressInput.value = addr.street || '';
            if (nameInput) nameInput.value = addr.receiverName || '';
            if (phoneInput) phoneInput.value = addr.phone || '';
            // set province and ward by name (try to find option)
            if (provinceSelect) {
              // ensure provinces populated
              populateProvinces();
              for (let i = 0; i < provinceSelect.options.length; i++) {
                if (provinceSelect.options[i].textContent.trim() === (addr.city || '').trim()) {
                  provinceSelect.selectedIndex = i;
                  break;
                }
              }
            }
            if (provinceSelect && wardSelect) {
              // trigger loading wards for selected province
              await onProvinceChange();
              for (let i = 0; i < wardSelect.options.length; i++) {
                if (wardSelect.options[i].textContent.trim() === (addr.ward || '').trim()) {
                  wardSelect.selectedIndex = i;
                  break;
                }
              }
            }
            if (defaultCheckbox) defaultCheckbox.checked = !!addr.isDefault;
            // change submit action to PUT
            submitBtn.textContent = 'Cập nhật';
            submitBtn.dataset.editId = id;
            openModal();
            } catch (err) {
            console.error('Load address error', err);
            await showMessage('Không thể tải địa chỉ: ' + (err.message || ''));
          }
        });
      });

      // Support edit button on cart page to reuse the same behavior
      document.querySelectorAll('.btn_edit').forEach(btn => {
        btn.addEventListener('click', async function () {
          const id = btn.getAttribute('data-id');
          try {
            if (id) {
              // If an id is provided, behave like update: load and prefill
              const res = await fetch(`${addressApiBase}/${encodeURIComponent(id)}`);
              if (!res.ok) throw new Error('Load address failed: ' + res.status);
              const addr = await res.json();
              if (addressInput) addressInput.value = addr.street || '';
              if (nameInput) nameInput.value = addr.receiverName || '';
              if (phoneInput) phoneInput.value = addr.phone || '';
              if (provinceSelect) {
                populateProvinces();
                for (let i = 0; i < provinceSelect.options.length; i++) {
                  if (provinceSelect.options[i].textContent.trim() === (addr.city || '').trim()) {
                    provinceSelect.selectedIndex = i;
                    break;
                  }
                }
              }
              if (provinceSelect && wardSelect) {
                await onProvinceChange();
                for (let i = 0; i < wardSelect.options.length; i++) {
                  if (wardSelect.options[i].textContent.trim() === (addr.ward || '').trim()) {
                    wardSelect.selectedIndex = i;
                    break;
                  }
                }
              }
              if (defaultCheckbox) defaultCheckbox.checked = !!addr.isDefault;
              submitBtn.textContent = 'Cập nhật';
              submitBtn.dataset.editId = id;
              openModal();
            } else {
              // No id -> open modal in create mode
              if (provinces.length > 0) populateProvinces();
              if (addressInput) addressInput.value = '';
              if (nameInput) nameInput.value = '';
              if (phoneInput) phoneInput.value = '';
              if (provinceSelect) provinceSelect.selectedIndex = 0;
              if (wardSelect) {
                wardSelect.innerHTML = '<option value="">-- Chọn phường/xã --</option>';
              }
              if (defaultCheckbox) defaultCheckbox.checked = false;
              if (submitBtn) {
                submitBtn.textContent = 'Thêm mới';
                delete submitBtn.dataset.editId;
              }
              openModal();
            }
          } catch (err) {
            console.error('Open edit on cart failed', err);
            await showMessage('Không thể mở form địa chỉ: ' + (err.message || ''));
          }
        });
      });

      // default setting handler
      document.querySelectorAll('.default_setting').forEach(btn => {
        btn.addEventListener('click', async function (e) {
          const id = btn.getAttribute('data-id');
          if (!id) return;
         const ok = await showConfirm('Đặt địa chỉ này làm mặc định?');
         if (!ok) return;
          try {
            const payload = { isDefault: true };
            const res = await fetch(`${addressApiBase}/${encodeURIComponent(id)}`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(payload)
            });
              if (!res.ok) {
                const txt = await res.text();
                throw new Error(txt || ('Set default failed: ' + res.status));
              }
              reloadAddressView();
          } catch (err) {
            console.error('Set default error', err);
              await showMessage('Không thể đặt mặc định: ' + (err.message || ''));
          }
        });
      });
    }
    function reloadAddressView() {
      try { window.location.hash = 'address'; } catch (e) { /* ignore */ }
      window.location.reload();
    }
    attachRowButtons();
    const form = document.getElementById('addressForm');
    if (submitBtn) {
      submitBtn.addEventListener('click', async function (e) {
        e.preventDefault();
        submitBtn.disabled = true;
        const provOpt = provinceSelect.options[provinceSelect.selectedIndex];
        const wardOpt = wardSelect.options[wardSelect.selectedIndex];
        const provName = provOpt ? provOpt.textContent : '';
        const wardName = wardOpt ? wardOpt.textContent : '';

        const payload = {
          street: addressInput ? addressInput.value.trim() : '',
          receiverName: nameInput ? nameInput.value.trim() : '',
          phone: phoneInput ? phoneInput.value.trim() : '',
          ward: wardName,
          city: provName,
          postalCode: '',
          isDefault: defaultCheckbox ? !!defaultCheckbox.checked : false
        };
        try {
          // decide create or update
          const editId = submitBtn ? submitBtn.dataset.editId : null;
          let res;
          if (editId) {
            res = await fetch(`${addressApiBase}/${encodeURIComponent(editId)}`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(payload)
            });
          } else {
            res = await fetch(addressCreateApi, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(payload)
            });
          }
          if (!res.ok) {
            const text = await res.text();
            throw new Error('Lỗi server: ' + res.status + ' - ' + text);
          }
          const saved = await res.json();
          // success - close modal and reload the page so server-rendered list updates
          closeModal();
          reloadAddressView();
        } catch (err) {
          console.error('Lỗi khi lưu địa chỉ', err);
          await showMessage('Không thể lưu địa chỉ: ' + (err.message || ''));
        } finally {
          submitBtn.disabled = false;
        }
      });
    }

    // ensure provinces are populated when modal is opened
    document.addEventListener('click', function (e) {
      if (e.target && (e.target.id === 'openAddressDialogBtn' || (e.target.closest && e.target.closest('#openAddressDialogBtn')))) {
        if (provinces.length === 0) return; // fetch might still be in progress
        populateProvinces();
        // clear modal for create
        if (addressInput) addressInput.value = '';
        if (nameInput) nameInput.value = '';
        if (phoneInput) phoneInput.value = '';
        if (provinceSelect) provinceSelect.selectedIndex = 0;
        if (wardSelect) wardSelect.selectedIndex = 0;
        if (defaultCheckbox) defaultCheckbox.checked = false;
        if (submitBtn) {
          submitBtn.textContent = 'Thêm mới';
          delete submitBtn.dataset.editId;
        }
        openModal();
      }
    });
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
