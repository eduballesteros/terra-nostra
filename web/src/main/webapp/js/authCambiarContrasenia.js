document.addEventListener("DOMContentLoaded", () => {
    const formCambio = document.getElementById("formCambiarContrasenia");
    const formRecuperacion = document.getElementById("recuperarPasswordForm");
    const mensajeAlertaCambio = document.getElementById("mensajeAlerta");
    const mensajeAlertaRecuperacion = document.getElementById("recuperarAlert");

    if (formCambio) {
        const token = document.getElementById("token").value;
        const passwordInput = document.getElementById("nuevaContrasenia");
        const confirmarInput = document.getElementById("confirmarContrasenia");
        const errorCoincidencia = document.getElementById("errorCoincidencia");

        document.querySelectorAll(".toggle-password").forEach((boton) => {
            boton.addEventListener("click", () => {
                const input = document.getElementById(boton.getAttribute("data-target"));
                const icon = boton.querySelector("i");

                if (input.type === "password") {
                    input.type = "text";
                    icon.classList.remove("fa-eye");
                    icon.classList.add("fa-eye-slash");
                } else {
                    input.type = "password";
                    icon.classList.remove("fa-eye-slash");
                    icon.classList.add("fa-eye");
                }
            });
        });

        formCambio.addEventListener("submit", async (e) => {
            e.preventDefault();

            const nuevaContrasenia = passwordInput.value.trim();
            const confirmar = confirmarInput.value.trim();

            if (nuevaContrasenia !== confirmar) {
                errorCoincidencia.style.display = "block";
                confirmarInput.classList.add("is-invalid");
                return;
            } else {
                errorCoincidencia.style.display = "none";
                confirmarInput.classList.remove("is-invalid");
            }

            try {
                const response = await fetch("/cambiar-password", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: `token=${encodeURIComponent(token)}&nuevaContrasenia=${encodeURIComponent(nuevaContrasenia)}`
                });

                const data = await response.json();
                mensajeAlertaCambio.classList.remove("d-none");

                if (data.mensaje) {
                    mostrarAlertaCambio("success", data.mensaje);
                    setTimeout(() => window.location.href = "/terra-nostra", 2000);
                } else {
                    mostrarAlertaCambio("danger", data.error || "Error desconocido");
                }
            } catch (error) {
                console.error("❌ Error:", error);
                mostrarAlertaCambio("danger", "❌ Error inesperado al cambiar la contraseña.");
            }
        });
    }

    if (formRecuperacion) {
        formRecuperacion.addEventListener("submit", async (e) => {
            e.preventDefault();

            const emailInput = document.getElementById("recuperarEmail");
            const email = emailInput.value.trim();

            if (email === "") {
                mostrarAlertaRecuperacion("danger", "❌ El correo no puede estar vacío.");
                return;
            }

            try {
                const response = await fetch("/recuperacion/enviar-enlace", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: `email=${encodeURIComponent(email)}`
                });

                const data = await response.text();
                mensajeAlertaRecuperacion.classList.remove("d-none");

                if (response.ok) {
                    mostrarAlertaRecuperacion("success", "📩 Se ha enviado un enlace de recuperación a tu correo.");
                    emailInput.value = "";

                    // Cerrar modal automáticamente después de mostrar el mensaje
                    setTimeout(() => {
                        const recuperarModalElement = document.getElementById("recuperarPasswordModal");
                        const recuperarModal = bootstrap.Modal.getInstance(recuperarModalElement) || new bootstrap.Modal(recuperarModalElement);
                        recuperarModal.hide();
                    }, 2000); // Espera 2 segundos antes de cerrar
                } else {
                    mostrarAlertaRecuperacion("danger", data || "❌ Error al solicitar recuperación.");
                }
            } catch (error) {
                console.error("❌ Error:", error);
                mostrarAlertaRecuperacion("danger", "❌ Error inesperado al solicitar recuperación.");
            }
        });
    }

    function mostrarAlertaCambio(tipo, mensaje) {
        mensajeAlertaCambio.className = `alert alert-${tipo} mt-3`;
        mensajeAlertaCambio.textContent = mensaje;
        mensajeAlertaCambio.style.display = "block";
    }

    function mostrarAlertaRecuperacion(tipo, mensaje) {
        mensajeAlertaRecuperacion.className = `alert alert-${tipo} mt-2`;
        mensajeAlertaRecuperacion.textContent = mensaje;
        mensajeAlertaRecuperacion.style.display = "block";
    }
});
