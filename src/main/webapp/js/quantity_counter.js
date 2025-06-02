let quantity = 1;
const quantityDisplay = document.getElementById("quantity");
const quantityInput = document.getElementById("quantityInput");

function changeQuantity(delta) {
    quantity = Math.max(1, quantity + delta);
    quantityDisplay.textContent = quantity;
    quantityInput.value = quantity;
}