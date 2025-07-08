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
