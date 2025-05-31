let registroEnCurso = false; // 🔒 Previene doble clic rápido

// Validación de contraseña
function validarRegistro() {
    const pass = document.getElementById("contrasenia").value;
    const confirm = document.getElementById("confirmPassword").value;
    const error = document.getElementById("passwordError");

    const coincide = pass === confirm;
    error.style.display = coincide ? "none" : "block";
    return coincide;
}

// Mostrar notificación modal
function mostrarNotificacion(titulo, mensaje, tipo) {
    const modal = new bootstrap.Modal(document.getElementById("notificationModal"));
    const tituloElem = document.getElementById("notificationModalLabel");
    const mensajeElem = document.getElementById("notificationMessage");

    tituloElem.textContent = titulo;
    mensajeElem.textContent = mensaje;

    tituloElem.classList.toggle("text-success", tipo === "success");
    tituloElem.classList.toggle("text-danger", tipo === "error");

    modal.show();
    setTimeout(() => modal.hide(), 2000);
}

// Envío de formulario de registro
document.getElementById("registerForm")?.addEventListener("submit", async function (e) {
    e.preventDefault();
    if (registroEnCurso) return; // ⛔ Bloquea si ya hay uno en proceso
    if (!validarRegistro()) return;

    registroEnCurso = true; // 🚫 Marca que ya se está enviando

    const formData = new FormData(this);

    try {
        const response = await fetch("/usuario/registrar", {
            method: "POST",
            body: new URLSearchParams(formData)
        });

        const data = await response.json();
        if (response.ok) {
            mostrarNotificacion("Éxito", data.mensaje || "✅ Registro exitoso! Revisa el correo para la verificación de la cuenta.", "success");
            setTimeout(() => location.reload(), 1500);
        } else {
            mostrarNotificacion("Error", data.mensaje || "❌ Error en el registro.", "error");
        }
    } catch (err) {
        console.error("❌ Error en la solicitud de registro:", err);
        mostrarNotificacion("Error", "❌ Error inesperado al registrar.", "error");
    } finally {
        registroEnCurso = false; // 🔁 Libera para permitir otro intento
    }
});

// Envío de formulario de login
document.getElementById("loginForm")?.addEventListener("submit", async function (e) {
    e.preventDefault();
    const formData = new FormData(this);

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            body: new URLSearchParams(formData)
        });

        const data = await response.json();
        if (response.ok) {
            mostrarNotificacion("Éxito", data.mensaje || "✅ Inicio de sesión exitoso!", "success");
            setTimeout(() => location.reload(), 1500);
        } else {
            mostrarNotificacion("Error", data.mensaje || "❌ Credenciales incorrectas.", "error");
        }
    } catch (err) {
        console.error("❌ Error en la solicitud de login:", err);
        mostrarNotificacion("Error", "❌ Error inesperado al iniciar sesión.", "error");
    }
});

document.addEventListener("DOMContentLoaded", function () {
        const alerta = document.getElementById("alertaVerificacion");
        if (alerta) {
            setTimeout(() => {
                alerta.style.display = "none";
            }, 5000); // Oculta después de 5 segundos
        }
    });

// Verificación desde URL (?verificacion=exitosa|fallida)
window.addEventListener("DOMContentLoaded", () => {
    const estado = new URLSearchParams(window.location.search).get("verificacion");
    if (estado === "exitosa") {
        mostrarNotificacion("Verificación", "✅ Tu correo ha sido verificado correctamente.", "success");
    } else if (estado === "fallida") {
        mostrarNotificacion("Verificación", "❌ El enlace ha expirado o es inválido.", "error");
    }
    window.history.replaceState({}, document.title, window.location.pathname);
});
