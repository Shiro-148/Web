const swiper_products = new Swiper('.swiper_products', {
    effect: 'slider',
    slidesPerView: 3,
    spaceBetween: 20,
    grabCursor: false,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },  
  });
  const swiper_news = new Swiper('.swiper_news', {
    effect: 'slider',
    slidesPerView: 4,
    spaceBetween: 20,
    grabCursor: false,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },  
  });
  const swiper_main_slide = new Swiper('.swiper_main_slide', {
    effect: 'slider',
    slidesPerView: 1,
    grabCursor: false,
    autoplay: {
      delay: 4000,
      disableOnInteraction: false,
    },
    effect: 'fade',
    fadeEffect: {
      crossFade: true
    }, 
  });
  const swiper_cate_menu_list = new Swiper('.swiper_cate_menu_list', {
    effect: 'slider',
    slidesPerView: 8,
    spaceBetween: 20,
    grabCursor: false,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },  
  });
  $(document).ready(function() {
    $('.account_menu a').click(function() {
        // Lấy giá trị data-target của mục được nhấp
        var target = $(this).data('target');
         // Xóa lớp 'active' khỏi tất cả các mục
         $('.account_menu li').removeClass('active');
        
         // Thêm lớp 'active' cho mục được nhấp
         $(this).addClass('active');

        // Ẩn tất cả các div con bên trong .my_account_right
        $('.my_account_right > div').hide();

        // Hiển thị div tương ứng với data-target
        $('.' + target).show();
    });
  
  // Ensure header cart badge shows total quantity on page load (sum of item.quantity)
  (function initCartBadge() {
    function setBadge(count) {
      try {
        const el = document.getElementById('cartCount');
        if (!el) return;
        const n = Number(count) || 0;
        el.textContent = n;
        el.style.display = (n ? '' : 'none');
      } catch (e) { /* ignore */ }
    }

    // fetch cart summary and compute total quantity
    fetch('/api/cart', { credentials: 'same-origin', cache: 'no-store' })
      .then(r => r.ok ? r.json() : null)
      .then(json => {
        if (!json) return;
        if (json.totalQuantity != null) return setBadge(Number(json.totalQuantity));
        if (json.totalItems != null && json.items == null) return setBadge(Number(json.totalItems));
        const items = json.items || (json.cart && json.cart.items) || [];
        if (Array.isArray(items)) {
          const total = items.reduce((s, it) => s + (Number(it.quantity) || 0), 0);
          setBadge(total);
        }
      }).catch(() => {});
  })();

  // On load, if a hash is present (e.g. #address), show that tab
  const initialHash = window.location.hash;
  if (initialHash && initialHash.length > 1) {
    const target = initialHash.substring(1); // remove '#'
    // hide all and show the target
    $('.account_menu li').removeClass('active');
    // find the matching anchor with data-target
    const anchor = $('.account_menu a[data-target="' + target + '"]');
    if (anchor && anchor.length > 0) {
      anchor.addClass('active');
    }
    $('.my_account_right > div').hide();
    $('.' + target).show();
  }
});

