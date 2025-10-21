function toggleFavourite(element) {
    let productId = $(element).attr("data-id");

    $.post("/products/toggle-favourite/" + productId, function(response) {
        let isFavourite = response === true || response === "true" || response === 1; 

        $(element).attr("data-favourite", isFavourite); 
        $(element).toggleClass("bx-heart", !isFavourite);
        $(element).toggleClass("bxs-heart", isFavourite); 
    }).fail(function() {
        alert("Có lỗi xảy ra!");
    });
}

// Add to Cart handling
document.addEventListener('DOMContentLoaded', function () {
    const authEl = document.getElementById('auth');
    const isAuthenticated = authEl && authEl.getAttribute('data-authenticated') === 'true';

    // Simple message dialog helper using elements from modal_dialog.html
    function showMessage(text) {
        return new Promise((resolve) => {
            const overlay = document.getElementById('messageOverlay');
            const dlg = document.getElementById('messageDialog');
            const txt = document.getElementById('messageText');
            const ok = document.getElementById('messageOk');
            if (!overlay || !dlg || !ok) {
                alert(text);
                resolve();
                return;
            }
            if (txt) txt.textContent = text;
            overlay.style.display = 'flex';
            dlg.classList.add('show-modal');
            function finish() {
                overlay.style.display = 'none';
                dlg.classList.remove('show-modal');
                ok.removeEventListener('click', finish);
                resolve();
            }
            ok.addEventListener('click', finish);
        });
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
            // success message
            showMessage('Đã thêm vào giỏ hàng thành công.');
            // Optionally, give a tiny UI feedback
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
