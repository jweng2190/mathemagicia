const diffSpan = document.getElementById("diff_disp");
const diffInput = document.getElementById("diff");

diffInput.addEventListener("change", change);

function change() {
    diffSpan.textContent = diffInput.value;
}