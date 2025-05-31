// adminPanel.js
document.addEventListener("DOMContentLoaded", () => {
    const linkUsuarios = document.getElementById("linkUsuarios");
    const linkProductos = document.getElementById("linkProductos");

    const seccionUsuarios = document.getElementById("seccionUsuarios");
    const seccionProductos = document.getElementById("seccionProductos");

    // Agregar eventos a los enlaces del menú
    linkUsuarios.addEventListener("click", () => {
        seccionUsuarios.classList.remove("d-none");
        seccionProductos.classList.add("d-none");

        linkUsuarios.classList.add("active", "fw-bold", "text-primary");
        linkProductos.classList.remove("active", "fw-bold", "text-primary");
    });

    linkProductos.addEventListener("click", () => {
        seccionProductos.classList.remove("d-none");
        seccionUsuarios.classList.add("d-none");

        linkProductos.classList.add("active", "fw-bold", "text-primary");
        linkUsuarios.classList.remove("active", "fw-bold", "text-primary");
    });
});
function mostrarAlerta(idElemento, mensaje, tipo = "success") {
    const alerta = document.getElementById(idElemento);
    if (!alerta) return;

    alerta.classList.remove("d-none", "alert-success", "alert-danger");
    alerta.classList.add(tipo === "success" ? "alert-success" : "alert-danger");
    alerta.innerText = mensaje;

    setTimeout(() => {
        alerta.classList.add("d-none");
    }, 4000); // Ocultar automáticamente en 4 segundos
}
