function toggleFavourite(element) {
    let productId = $(element).attr("data-id");

    $.post("/products/toggle-favourite/" + productId, function(response) {
        let isFavourite = response === true || response === "true" || response === 1; 

        $(element).attr("data-favourite", isFavourite); 
        $(element).toggleClass("bx-heart", !isFavourite);
        $(element).toggleClass("bxs-heart", isFavourite); 
    }).fail(function() {
        if (window.showMessage) window.showMessage('Có lỗi xảy ra!'); else console.error('Có lỗi xảy ra!');
    });
}

// Add to Cart handling
document.addEventListener('DOMContentLoaded', function () {
    const authEl = document.getElementById('auth');
    const isAuthenticated = authEl && authEl.getAttribute('data-authenticated') === 'true';

    // Use centralized dialog utilities when available
    function showMessage(text) {
        if (typeof window.showMessage === 'function') return window.showMessage(text);
        // fallback: use console to avoid blocking native alert
        try { console.info(text); } catch (e) { /* ignore */ }
        return Promise.resolve();
    }

    function showLoginModalIfNeeded() {
        if (!isAuthenticated) {
            const loginModal = document.getElementById('login');
            if (loginModal) {
                loginModal.classList.add('show-modal');
                loginModal.style.display = 'flex';
                const overlay = document.getElementById('modalOverlay');
                if (overlay) overlay.style.display = 'flex';
            }
            return true;
        }
        return false;
    }

    async function addToCart(productId, qty = 1) {
        try {
            const res = await fetch('/api/cart/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ productId: Number(productId), quantity: Number(qty) || 1 })
            });
            if (!res.ok) {
                const msg = await res.text();
                console.error('Add to cart failed:', msg);
                return;
            }
            // Try to parse response JSON to extract new cart count
            let resJson = null;
            try {
                // some backends return an empty body; guard against parse errors
                resJson = await res.json();
            } catch (e) {
                resJson = null;
            }

            (function updateCartCount(data) {
                try {
                    const el = document.getElementById('cartCount');
                    if (!el) return;
                    let qty = null;
                    if (data) {
                        if (data.totalQuantity != null) qty = Number(data.totalQuantity);
                        else if (data.totalItems != null && data.totalQuantity == null) {
                            qty = Number(data.totalItems);
                        } else if (Array.isArray(data.items)) {
                            qty = data.items.reduce((s, it) => s + (Number(it.quantity) || 0), 0);
                        } else if (data.cart && Array.isArray(data.cart.items)) {
                            qty = data.cart.items.reduce((s, it) => s + (Number(it.quantity) || 0), 0);
                        } else if (data.cart && data.cart.totalQuantity != null) {
                            qty = Number(data.cart.totalQuantity);
                        }
                    }
                    if (qty == null) {
                        fetch('/api/cart', { credentials: 'same-origin' }).then(r => r.ok ? r.json() : null).then(json => {
                            if (!json) return;
                            const items = json.items || (json.cart && json.cart.items) || [];
                            let c = 0;
                            if (Array.isArray(items)) c = items.reduce((s, it) => s + (Number(it.quantity) || 0), 0);
                            else c = Number(json.totalQuantity || json.totalItems || 0);
                                el.textContent = c;
                                el.style.display = (c ? '' : 'none');
                        }).catch(() => {});
                        return;
                    }
                    el.textContent = qty;
                        el.style.display = (qty ? '' : 'none');
                } catch (e) {
                }
            })(resJson);

            // success notification (rich dialog with thumbnail if available)
            try {
                let imgSrc = null;
                const cardBtn = document.querySelector(`.button_card_add[data-id="${productId}"]`);
                if (cardBtn) {
                    const card = cardBtn.closest('.product-card') || cardBtn.closest('.product-item') || cardBtn.parentElement;
                    const img = card ? card.querySelector('img') : null;
                    if (img && img.src) imgSrc = img.src;
                }
                if (!imgSrc) {
                    const detailImg = document.querySelector('.product-image img') || document.querySelector('.product-detail img') || document.querySelector('.product_img img');
                    if (detailImg && detailImg.src) imgSrc = detailImg.src;
                }
                showMessage({ message: 'Sản phẩm đã được thêm vào Giỏ hàng', thumb: imgSrc, autoClose: 2000 });
            } catch (e) {
                showMessage('Sản phẩm đã được thêm vào Giỏ hàng');
            }
            // Optionally, give a tiny UI feedback on the source button
            const btn = document.querySelector(`.button_card_add[data-id="${productId}"]`);
            if (btn) {
                btn.classList.add('added');
                setTimeout(() => btn.classList.remove('added'), 600);
            }
        } catch (e) {
            console.error(e);
        }
    }

    document.querySelectorAll('.button_card_add').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            if (showLoginModalIfNeeded()) return;
            const id = btn.getAttribute('data-id');
            if (!id) return;
            addToCart(id);
        });
    });

    // Detail page handler
    const detail = document.querySelector('.add_to_cart');
    if (detail) {
        const pid = detail.getAttribute('data-id');
        const qtyInput = document.querySelector('.add_to_cart .txt_count');
        const minus = document.querySelector('.add_to_cart .btn_minus');
        const plus = document.querySelector('.add_to_cart .btn_plus');
        const btn = document.querySelector('.add_to_cart .btn_add_to_cart');
        const clamp = (n) => Math.max(1, Math.min(99, n));
        if (minus && qtyInput) minus.addEventListener('click', () => { qtyInput.value = clamp((parseInt(qtyInput.value||'1',10)-1)); });
        if (plus && qtyInput) plus.addEventListener('click', () => { qtyInput.value = clamp((parseInt(qtyInput.value||'1',10)+1)); });
        if (btn) btn.addEventListener('click', function () {
            if (showLoginModalIfNeeded()) return;
            const q = clamp(parseInt(qtyInput && qtyInput.value ? qtyInput.value : '1', 10));
            addToCart(pid, q);
        });
    }
});
