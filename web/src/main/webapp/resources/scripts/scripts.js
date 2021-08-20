function addPhoneToCart(phoneId) {
    let quantity = $('#quantity-' + phoneId).val();
    let quantityInputId = 'quantity-' + phoneId;
    let addPhoneButtonId = 'btn-addPhoneToCart-' + phoneId;
    let quantityInputMessageId = 'quantityInputMessage-' + phoneId;
    $(document).ready(function () {
        $('#' + addPhoneButtonId).prop("disabled", true);
        $.post('/phoneshop-web/miniCart', {
            phoneId: phoneId,
            quantity: quantity
        }).done(function (data) {
            if (data.successful) {
                $("#" + quantityInputMessageId).text(data.message).css({'color': 'green'});
            } else {
                $("#" + quantityInputMessageId).text(data.message).css({'color': 'red'});
            }
            loadMiniCart(data.miniCart)
        }).always(function () {
            $("#" + addPhoneButtonId).prop("disabled", false);
        })
    });
}

function deletePhoneFromCart(phoneId) {
}

function getMiniCart() {
    $(document).ready(function () {
        $.get('/phoneshop-web/miniCart', function (data) {
            loadMiniCart(data)
        })
    });
}

function loadMiniCart(miniCart) {
    $("#miniCart").text("My cart: " + miniCart.totalQuantity + " items " + miniCart.totalCost + " $");
}