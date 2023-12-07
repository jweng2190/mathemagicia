const modalEnd = document.getElementById('modal-end');
const endClose = document.getElementById("end-close");

endClose.addEventListener("click", () => {
    let modalE = new bootstrap.Modal(modalEnd);
    modalE.hide();
});

showModal();

function showModal() {
    let modal = new bootstrap.Modal(modalEnd);
    modal.show();
}
