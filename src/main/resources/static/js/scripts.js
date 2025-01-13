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
});
