function abrirCarrito() {
    document.getElementById("carritoLateral")?.classList.add("abierto");
}
function cerrarCarrito() {
    document.getElementById("carritoLateral")?.classList.remove("abierto");
}
document.addEventListener("DOMContentLoaded", () => {
    document.querySelector(".cart")?.addEventListener("click", (e) => {
        e.preventDefault();
        abrirCarrito();
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const loginModalElement = document.getElementById("loginModal");
    const recuperarPasswordModalElement = document.getElementById("recuperarPasswordModal");

    const accountIcon = document.querySelector(".account");
    const closeModalButton = document.getElementById("closeModalButton");

    const modalTitle = document.getElementById("modalTitle");
    const registerForm = document.getElementById("registerForm");
    const loginForm = document.getElementById("loginForm");
    const userManager = document.getElementById("userManager");
    const userName = document.getElementById("userName");
    const profileLink = document.getElementById("profileLink");

    let loginModal = null;
    let recuperarPasswordModal = null;
    let isRegister = false;

    if (loginModalElement) loginModal = new bootstrap.Modal(loginModalElement);
    if (recuperarPasswordModalElement) recuperarPasswordModal = new bootstrap.Modal(recuperarPasswordModalElement);

    if (accountIcon) {
        accountIcon.addEventListener("click", function (e) {
            e.preventDefault();
            if (loginModal) loginModal.show();
            verificarSesion();
        });
    }

    if (closeModalButton) {
        closeModalButton.addEventListener("click", function () {
            if (loginModal) loginModal.hide();
        });
    }

    window.toggleForm = function () {
        isRegister = !isRegister;
        modalTitle.textContent = isRegister ? "REGISTRARSE" : "INICIAR SESIÓN";
        registerForm.style.display = isRegister ? "block" : "none";
        loginForm.style.display = isRegister ? "none" : "block";
        (isRegister ? registerForm : loginForm).reset();
    };

    window.abrirRecuperarContrasenia = function () {
        bootstrap.Modal.getOrCreateInstance(loginModalElement).hide();
        bootstrap.Modal.getOrCreateInstance(recuperarPasswordModalElement).show();
    };

    window.cerrarModalRecuperacion = function () {
        bootstrap.Modal.getOrCreateInstance(recuperarPasswordModalElement).hide();
        bootstrap.Modal.getOrCreateInstance(loginModalElement).show();
    };

    function verificarSesion() {
        fetch("/auth/verificar-sesion", {
            method: "GET",
            credentials: "include"
        })
        .then(res => res.json())
        .then(data => {
            if (data.sesionActiva) {
                userManager && (userManager.style.display = "block");
                loginForm && (loginForm.style.display = "none");
                registerForm && (registerForm.style.display = "none");
                userName && (userName.textContent = data.nombreUsuario || "Usuario");
                if (profileLink) {
                    profileLink.href = data.rol === "ROLE_ADMIN" ? "/admin" : "/infoUser";
                }
                const saludo = document.getElementById("saludoHeader");
                if (saludo) saludo.textContent = `Hola, ${data.nombreUsuario}`;
            } else {
                userManager && (userManager.style.display = "none");
                loginForm && (loginForm.style.display = "block");
                registerForm && (registerForm.style.display = "none");
                const saludo = document.getElementById("saludoHeader");
                if (saludo) saludo.textContent = "";
            }
        })
        .catch(error => console.error("❌ Error verificando sesión:", error));
    }

    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            fetch("/auth/logout", {
                method: "POST",
                credentials: "include"
            })
            .then(res => res.json())
            .then(data => {
                console.log("🔴 Sesión cerrada:", data.mensaje);
                location.reload();
            })
            .catch(error => console.error("❌ Error cerrando sesión:", error));
        });
    }

    verificarSesion();

    // Mostrar modal de pago si corresponde
    const urlParams = new URLSearchParams(window.location.search);
    const pagoParam = urlParams.get("pago");
    let modal = null;

    if (pagoParam === "exito") {
        modal = new bootstrap.Modal(document.getElementById("modalPagoExitoso"));
    } else if (pagoParam === "error") {
        modal = new bootstrap.Modal(document.getElementById("modalPagoError"));
    }

    if (modal) {
        modal.show();
        setTimeout(() => {
            modal.hide();

            // Limpiar parámetro de la URL
            const nuevaUrl = window.location.origin + window.location.pathname;
            window.history.replaceState({}, document.title, nuevaUrl);
        }, 3000);
    }
});
