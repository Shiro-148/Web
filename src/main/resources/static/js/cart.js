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
  };

  // Match template formatting: e.g., 75 -> "75.000 ₫"
  const currency = v => `${new Intl.NumberFormat('vi-VN').format(Number(v))}.000 ₫`;

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

  async function refresh() {
    try {
      const data = await getCart();
      const items = data.items || [];
      els.listTopQty && (els.listTopQty.textContent = `(${items.length} Sản phẩm)`);
      renderItems(items);
      const total = Number(data.total);
      if (els.subtotal) els.subtotal.textContent = currency(total);
      if (els.total) els.total.textContent = currency(total);
      if (data.cart) {
        if (els.note && data.cart.note != null) els.note.value = data.cart.note;
        if (els.optPlastic) els.optPlastic.checked = !!data.cart.usePlastic;
        if (els.optKetchup) els.optKetchup.checked = !!data.cart.useKetchup;
        if (els.optChilly) els.optChilly.checked = !!data.cart.useChillySauce;
      }
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
})();
