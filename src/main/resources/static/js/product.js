function toggleFavourite(element) {
    let productId = $(element).attr("data-id");

    $.post("/products/toggle-favourite/" + productId, function(response) {
        let isFavourite = response === true || response === "true" || response === 1; // Xử lý tất cả trường hợp

        $(element).attr("data-favourite", isFavourite); // Cập nhật trạng thái mới
        $(element).toggleClass("bx-heart", !isFavourite);
        $(element).toggleClass("bxs-heart", isFavourite); // Thay đổi icon
    }).fail(function() {
        alert("Có lỗi xảy ra!");
    });
}
