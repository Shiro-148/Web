const swiper_products = new Swiper(".swiper_products", {
  effect: "slider",
  slidesPerView: 3,
  spaceBetween: 20,
  grabCursor: false,
  pagination: {
    el: ".swiper-pagination",
    clickable: true,
  },
});
const swiper_news = new Swiper(".swiper_news", {
  effect: "slider",
  slidesPerView: 4,
  spaceBetween: 20,
  grabCursor: false,
  pagination: {
    el: ".swiper-pagination",
    clickable: true,
  },
});
const swiper_main_slide = new Swiper(".swiper_main_slide", {
  effect: "slider",
  slidesPerView: 1,
  grabCursor: false,
  autoplay: {
    delay: 4000,
    disableOnInteraction: false,
  },
  effect: "fade",
  fadeEffect: {
    crossFade: true,
  },
});
const swiper_cate_menu_list = new Swiper(".swiper_cate_menu_list", {
  effect: "slider",
  slidesPerView: 8,
  spaceBetween: 20,
  grabCursor: false,
  pagination: {
    el: ".swiper-pagination",
    clickable: true,
  },
});
$(document).ready(function () {
  $(".account_menu a").click(function () {
    var target = $(this).data("target");
    $(".account_menu li").removeClass("active");

    $(this).addClass("active");

    $(".my_account_right > div").hide();

    $("." + target).show();
  });

  (function initCartBadge() {
    function setBadge(count) {
      try {
        const el = document.getElementById("cartCount");
        if (!el) return;
        const n = Number(count) || 0;
        el.textContent = n;
        el.style.display = n ? "" : "none";
      } catch (e) {
        /* ignore */
      }
    }

    fetch("/api/cart", { credentials: "same-origin", cache: "no-store" })
      .then((r) => (r.ok ? r.json() : null))
      .then((json) => {
        if (!json) return;
        if (json.totalQuantity != null)
          return setBadge(Number(json.totalQuantity));
        if (json.totalItems != null && json.items == null)
          return setBadge(Number(json.totalItems));
        const items = json.items || (json.cart && json.cart.items) || [];
        if (Array.isArray(items)) {
          const total = items.reduce(
            (s, it) => s + (Number(it.quantity) || 0),
            0
          );
          setBadge(total);
        }
      })
      .catch(() => {});
  })();

  const initialHash = window.location.hash;
  if (initialHash && initialHash.length > 1) {
    const target = initialHash.substring(1); // remove '#'
    $(".account_menu li").removeClass("active");
    const anchor = $('.account_menu a[data-target="' + target + '"]');
    if (anchor && anchor.length > 0) {
      anchor.addClass("active");
    }
    $(".my_account_right > div").hide();
    $("." + target).show();
  }
});
