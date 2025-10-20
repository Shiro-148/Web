// vn-wards.js
// Fetches provinces and wards from server endpoints and submits address via AJAX

(function () {
  const provincesApi = '/api/vn/provinces';
  const wardsApiBase = '/api/vn/provinces'; // append /{code}/wards
  const addressApi = '/api/address/create';

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
    provinceSelect && provinceSelect.addEventListener('change', updateSubmitState);
    wardSelect && wardSelect.addEventListener('change', updateSubmitState);

    // AJAX submit
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
          phone: '',
          ward: wardName,
          city: provName,
          postalCode: '',
          isDefault: defaultCheckbox ? !!defaultCheckbox.checked : false
        };

        try {
          const res = await fetch(addressApi, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
          });
          if (!res.ok) {
            const text = await res.text();
            throw new Error('Lỗi server: ' + res.status + ' - ' + text);
          }
          const saved = await res.json();
          // success - close modal and optionally refresh addresses
          const overlay = document.getElementById('modalOverlay');
          if (overlay) overlay.style.display = 'none';
          // trigger a custom event so page can refresh address list if needed
          document.dispatchEvent(new CustomEvent('address:created', { detail: saved }));
        } catch (err) {
          console.error('Lỗi khi lưu địa chỉ', err);
          alert('Không thể lưu địa chỉ: ' + err.message);
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
      }
    });
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
