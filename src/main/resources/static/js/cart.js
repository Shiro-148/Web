(() => {
  const els = {
    list: document.querySelector('.cart_list'),
    listTopQty: document.querySelector('.cart_list_quantity'),
    subtotal: document.querySelector('.order .sub_total .price'),
    total: document.querySelector('.order .total .price'),
    note: document.querySelector('.cart_list_note .note_cart'),
    optPlastic: document.querySelector('#use_plastic'),
    optKetchup: document.querySelector('#use_ketchup'),
    optChilly: document.querySelector('#use_chilly_sauce'),
    addressText: document.querySelector('.cart_detail .address .address_content .address_inf'),
    addressEditBtn: document.querySelector('.cart_detail .address .address_content .btn_edit'),
  };

  // Match template formatting: e.g., 75 -> "75.000 ₫"
  const currency = v => `${new Intl.NumberFormat('vi-VN').format(Number(v))}.000 ₫`;
  // Current items cache (used to prevent checkout when empty)
  let currentCartItems = [];
  const STEP_TRANS_MS = 300;

  async function getCart() {
    const res = await fetch('/api/cart', { credentials: 'same-origin' });
    if (!res.ok) throw new Error('Failed to load cart');
    return res.json();
  }

  async function addItem(productId, qty=1) {
    const res = await fetch('/api/cart/add', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin', body: JSON.stringify({ productId, quantity: qty })
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  }

  async function updateItem(itemId, qty) {
    const res = await fetch(`/api/cart/item/${itemId}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin', body: JSON.stringify({ quantity: qty })
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  }

  async function deleteItem(itemId) {
    const res = await fetch(`/api/cart/item/${itemId}`, { method: 'DELETE', credentials: 'same-origin' });
    if (!res.ok) throw new Error(await res.text());
  }

  async function updateOptions(payload) {
    const res = await fetch('/api/cart/options', {
      method: 'PUT', headers: { 'Content-Type': 'application/json' },
      credentials: 'same-origin', body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  }

  async function loadDefaultAddress() {
    if (!els.addressText) return;
    try {
      const res = await fetch('/api/address/default', { credentials: 'same-origin', cache: 'no-store' });
      if (res.status === 404) {
        els.addressText.textContent = 'Chưa có địa chỉ mặc định. Vui lòng thêm địa chỉ giao hàng.';
        if (els.addressEditBtn) {
          els.addressEditBtn.removeAttribute('data-id');
        }
        return;
      }
      if (!res.ok) throw new Error('Load default address failed');
      const addr = await res.json();
      const parts = [];
      if (addr.street) parts.push(addr.street);
      if (addr.ward) parts.push(addr.ward);
      if (addr.city) parts.push(addr.city);
      els.addressText.textContent = parts.join(', ');
      if (els.addressEditBtn && addr.id != null) {
        els.addressEditBtn.setAttribute('data-id', addr.id);
      }
    } catch (e) {
      console.error('Could not load default address', e);
    }
  }

  function renderItems(items) {
    const container = els.list;
    const noteBlock = container.querySelector('.cart_list_note');
    const header = container.querySelector('.cart_list_top');
    container.querySelectorAll('.cart_item').forEach(n => n.remove());

    items.forEach(item => {
      const div = document.createElement('div');
      div.className = 'cart_item';
      div.innerHTML = `
        <div class="cart_img">
          <a href="/product_detail/${item.product.idProduct}">
            <img src="${item.product.imgPathProduct}" alt="${item.product.nameProduct}">
          </a>
        </div>
        <div class="cart_inf">
          <a class="name_cart_item" href="/product_detail/${item.product.idProduct}">
            ${item.product.nameProduct}
          </a>
          <span class="price_cart_item">${currency(item.unitPrice)}</span>
        </div>
        <div class="btn_cart_item">
          <div class="btn_quantity">
            <button class="button btn_minus"><i class='bx bx-minus'></i></button>
            <span class="quantity">${item.quantity}</span>
            <button class="button btn_plus"><i class='bx bx-plus'></i></button>
          </div>
          <div class="btn_delete"><i class='bx bx-trash-alt'></i></div>
        </div>`;

      div.querySelector('.btn_minus').addEventListener('click', async () => {
        const q = Math.max(1, item.quantity - 1);
        await updateItem(item.id, q); await refresh();
      });
      div.querySelector('.btn_plus').addEventListener('click', async () => {
        await updateItem(item.id, item.quantity + 1); await refresh();
      });
      div.querySelector('.btn_delete').addEventListener('click', async () => {
        await deleteItem(item.id); await refresh();
      });

      noteBlock.before(div);
    });
  }

  function renderPaymentList(items) {
    try {
      const container = document.querySelector('.payment_list_product');
      if (!container) return;
      const itemsWrap = container.querySelector('#paymentItemsContainer') || container.querySelector('.payment_items');
      const tpl = document.getElementById('tplPaymentItem');
      if (!itemsWrap) return;
      // Clear existing items (preserve template element)
      // Remove any existing .payment_item nodes (but keep the <template> and payment_empty placeholder)
      Array.from(itemsWrap.querySelectorAll('.payment_item')).forEach(n => n.remove());
      const emptyEl = itemsWrap.querySelector('.payment_empty');
      if (!items || items.length === 0) {
        if (emptyEl) emptyEl.style.display = '';
        return;
      }
      if (emptyEl) emptyEl.style.display = 'none';
      items.forEach(it => {
        let node = null;
        if (tpl && tpl.content) {
          node = tpl.content.firstElementChild.cloneNode(true);
        } else {
          // fallback: build minimal node
          node = document.createElement('div');
          node.className = 'payment_item';
          node.innerHTML = `\n            <div class="payment_item_thumb"><img src="" alt=""/></div>\n            <div class="payment_item_info">\n              <div class="payment_item_name"></div>\n              <div class="payment_item_qty"></div>\n            </div>\n            <div class="payment_item_price"></div>`;
        }
        const imgEl = node.querySelector('.payment_item_thumb img');
        const nameEl = node.querySelector('.payment_item_name');
        const qtyEl = node.querySelector('.payment_item_qty');
        const priceEl = node.querySelector('.payment_item_price');
        if (imgEl) imgEl.src = (it.product && it.product.imgPathProduct) ? it.product.imgPathProduct : '';
        if (nameEl) nameEl.textContent = (it.product && it.product.nameProduct) ? it.product.nameProduct : '';
        if (qtyEl) qtyEl.textContent = `x${it.quantity || 1}`;
        if (priceEl) priceEl.textContent = it.unitPrice != null ? currency(it.unitPrice) : '0 ₫';
        itemsWrap.appendChild(node);
      });
    } catch (e) {
      console.error('renderPaymentList error', e);
    }
  }

  async function refresh() {
    try {
      const data = await getCart();
      const items = data.items || [];
      // cache items for other handlers
      currentCartItems = items;
      els.listTopQty && (els.listTopQty.textContent = `(${items.length} Sản phẩm)`);
      renderItems(items);
      renderPaymentList(items);
      const total = Number(data.total);
      if (els.subtotal) els.subtotal.textContent = currency(total);
      if (els.total) els.total.textContent = currency(total);
      if (data.cart) {
        if (els.note && data.cart.note != null) els.note.value = data.cart.note;
        if (els.optPlastic) els.optPlastic.checked = !!data.cart.usePlastic;
        if (els.optKetchup) els.optKetchup.checked = !!data.cart.useKetchup;
        if (els.optChilly) els.optChilly.checked = !!data.cart.useChillySauce;
      }
      // Update global header cart badge to show total quantity (sum of item.quantity)
      try {
        const headerBadge = document.getElementById('cartCount');
        if (headerBadge) {
          let totalQty = 0;
          if (Array.isArray(items)) totalQty = items.reduce((s, it) => s + (Number(it.quantity) || 0), 0);
          else totalQty = Number(data.totalQuantity || 0);
          headerBadge.textContent = totalQty;
          headerBadge.style.display = (totalQty ? '' : 'none');
        }
      } catch (e) { /* ignore */ }
    } catch (e) {
      console.error(e);
    }
  }

  // Wire options
  if (els.note) els.note.addEventListener('change', () => updateOptions({ note: els.note.value }));
  if (els.optPlastic) els.optPlastic.addEventListener('change', () => updateOptions({ usePlastic: els.optPlastic.checked }));
  if (els.optKetchup) els.optKetchup.addEventListener('change', () => updateOptions({ useKetchup: els.optKetchup.checked }));
  if (els.optChilly) els.optChilly.addEventListener('change', () => updateOptions({ useChillySauce: els.optChilly.checked }));

  refresh();
  loadDefaultAddress();
  // Show step1 by default and hide step2 when entering cart
  function showStep(stepNumber) {
    try {
      const shouldShow1 = stepNumber === 1;
      const s1 = Array.from(document.querySelectorAll('.step1'));
      const s2 = Array.from(document.querySelectorAll('.step2'));
      const toShow = shouldShow1 ? s1 : s2;
      const toHide = shouldShow1 ? s2 : s1;

      // Show targets: set display then add visible class for animation
      toShow.forEach(el => {
        el.style.display = '';
        // ensure a reflow before adding class
        requestAnimationFrame(() => el.classList.add('is-visible'));
      });

      // Hide other targets: remove visible class then set display:none after transition
      toHide.forEach(el => {
        el.classList.remove('is-visible');
        const onEnd = (ev) => {
          if (ev && ev.target !== el) return;
          el.style.display = 'none';
          el.removeEventListener('transitionend', onEnd);
        };
        el.addEventListener('transitionend', onEnd);
        setTimeout(() => onEnd(), STEP_TRANS_MS + 50);
      });

      // update stepper active class if present
      const stepperSteps = document.querySelectorAll('.stepper .step');
      if (stepperSteps && stepperSteps.length) {
        stepperSteps.forEach((el, idx) => {
          if (stepNumber === idx + 1) el.classList.add('active'); else el.classList.remove('active');
        });
      }
    } catch (e) { console.error('showStep error', e); }
  }

  // Initialize to step 1 on page load
  showStep(1);

  // Wire payment button to switch to step 2
  try {
    const payLink = document.querySelector('.btn_payment .btn_payment_link') || document.querySelector('.btn_payment');
    if (payLink) {
      payLink.addEventListener('click', (ev) => {
        ev && ev.preventDefault && ev.preventDefault();
        // Prevent moving to payment if cart empty
        try {
          if (!currentCartItems || currentCartItems.length === 0) {
            if (window.showMessage) window.showMessage({ message: 'Giỏ hàng đang rỗng. Vui lòng thêm sản phẩm trước khi thanh toán.', type: 'error', autoClose: 3000 });
            else console.info('Giỏ hàng đang rỗng. Vui lòng thêm sản phẩm trước khi thanh toán.');
            return;
          }
        } catch (e) { /* ignore */ }
        showStep(2);
  // no automatic scrolling (user requested no auto-scroll)
      });
    }
  } catch (e) { /* ignore */ }

  // Wire Back button in payment area
  try {
    const backBtn = document.querySelector('.cart_payment .btn_back');
    if (backBtn) {
      backBtn.addEventListener('click', (ev) => {
        ev && ev.preventDefault && ev.preventDefault();
  showStep(1);
  // do not auto-scroll when returning to step1 (user requested)
      });
    }
  } catch (e) { /* ignore */ }

  // Wire confirmation button to create order
  try {
    const confirmBtn = document.querySelector('.confirmation_payment .btn_payment_link');
    if (confirmBtn) {
      confirmBtn.addEventListener('click', async (ev) => {
        ev && ev.preventDefault && ev.preventDefault();
        try {
          const res = await fetch('/api/cart/confirm', { method: 'POST', credentials: 'same-origin' });
          if (!res.ok) {
            const text = await res.text();
            // Use dialog for errors as well (avoid alert for success; errors use dialog too)
            if (window.showMessage) await window.showMessage({ message: text || 'Đặt hàng thất bại', autoClose: 2500 });
            else console.error(text || 'Đặt hàng thất bại');
            return;
          }
          const data = await res.json();
          // show success using dialog-noti; do NOT use alert
          const orderId = data && data.orderId ? data.orderId : null;
          const msgHtml = orderId ? `Đặt hàng thành công\nMã đơn: #${orderId}` : 'Đặt hàng thành công';
          if (window.showMessage) {
            await window.showMessage({ message: msgHtml, autoClose: 1800 });
          } else {
            // fallback: log only (explicitly avoid alert)
            console.log('Đặt hàng thành công', data);
          }
          // Redirect to account order history after dialog closed
          window.location.href = '/account#order_history';
        } catch (e) {
          console.error(e);
          if (window.showMessage) await window.showMessage({ message: 'Lỗi hệ thống', autoClose: 2500 }); else console.error('Lỗi hệ thống', e);
        }
      });
    }
  } catch (e) { /* ignore */ }
})();
