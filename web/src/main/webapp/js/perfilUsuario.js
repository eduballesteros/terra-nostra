document.addEventListener("DOMContentLoaded", function () {
    let nombreActual = "";
    let apellidoActual = "";
    let telefonoActual = "";
    let direccionActual = "";

    fetch("/auth/verificar-sesion", {
        method: "GET",
        credentials: "include"
    })
    .then(res => res.json())
    .then(sessionData => {
        if (!sessionData.sesionActiva) return;

        const emailUsuario = sessionData.emailUsuario;
        document.body.dataset.email = emailUsuario;

        fetch(`/usuario/detalle?email=${encodeURIComponent(emailUsuario)}`)
            .then(response => {
                if (!response.ok) throw new Error("No se pudo cargar la información del usuario.");
                return response.json();
            })
            .then(data => {
                document.body.dataset.id = data.id;

                nombreActual = data.nombre || "";
                apellidoActual = data.apellido || "";
                telefonoActual = data.telefono || "";
                direccionActual = data.direccion || "";

                const nombreCompleto = `${nombreActual} ${apellidoActual}`.trim();
                document.getElementById("nombreUsuario").textContent = nombreCompleto;
                document.getElementById("emailUsuario").textContent = data.email || "";
                document.getElementById("telefonoUsuario").textContent = telefonoActual || "No especificado";
                document.getElementById("direccionUsuario").textContent = direccionActual || "No especificada";

                if (data.correoVerificado === false) {
                    const bloqueVerificacion = document.getElementById("bloqueVerificacionCorreo");
                    if (bloqueVerificacion) bloqueVerificacion.classList.remove("d-none");

                    const btnSolicitar = document.getElementById("btnSolicitarVerificacion");
                    const mensaje = document.getElementById("mensajeVerificacion");

                    if (btnSolicitar && mensaje) {
                        btnSolicitar.addEventListener("click", () => {
                            fetch("/usuario/reenviar-verificacion", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/x-www-form-urlencoded"
                                },
                                body: `email=${encodeURIComponent(data.email)}`
                            })
                            .then(res => {
                                if (!res.ok) throw new Error();
                                return res.text();
                            })
                            .then(mensajeRespuesta => {
                                mensaje.textContent = mensajeRespuesta;
                                mensaje.className = "alert alert-success d-block";
                            })
                            .catch(() => {
                                mensaje.textContent = "❌ Error al reenviar el correo de verificación.";
                                mensaje.className = "alert alert-danger d-block";
                            });
                        });
                    }
                }
            })
            .catch(() => {
                document.getElementById("infoUsuario").innerHTML = `
                    <div class="alert alert-danger mt-3">
                        ⚠️ Error al cargar tus datos. Intenta nuevamente.
                    </div>`;
            });
    });

    function cargarPedidos() {
        const usuarioId = document.body.dataset.id;
        if (!usuarioId) return;

        fetch(`/usuario/pedidos?usuarioId=${usuarioId}`)
            .then(res => res.json())
            .then(pedidos => {
                const contenedor = document.getElementById("contenedorPedidos");
                if (!contenedor) return;

                if (!pedidos.length) {
                    contenedor.innerHTML = `<div class="alert alert-info">No tienes pedidos registrados aún.</div>`;
                    return;
                }

                let html = "";

                pedidos.forEach(pedido => {
                    const fecha = new Date(pedido.fecha).toLocaleDateString('es-ES');

                    html += `
                        <div class="border rounded-4 p-4 mb-4 shadow-sm bg-light-subtle">
                            <div class="d-flex justify-content-between align-items-center flex-wrap">
                                <div>
                                    <h5 class="fw-semibold mb-1 text-primary">Pedido realizado el ${fecha}</h5>
                                    <div class="text-muted small">Estado: <strong>${pedido.estado}</strong></div>
                                    <div class="text-muted small">Pago: <strong>${pedido.metodoPago || 'Paypal'}</strong></div>
                                </div>
                                <div class="text-end mt-3 mt-md-0">
                                    <span class="fw-bold text-dark">Total: ${pedido.total.toFixed(2)} €</span>
                                </div>
                            </div>

                            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3 mt-3">
                    `;

                    pedido.productos.forEach(prod => {
                        html += `
                            <div class="col">
                                <div class="card h-100 border-0 shadow-sm">
                                    <div class="card-body d-flex flex-column">
                                        <h6 class="card-title fw-semibold">${prod.nombre || 'Producto'}</h6>
                                        <p class="mb-1 small">Cantidad: <strong>${prod.cantidad}</strong></p>
                                    </div>
                                </div>
                            </div>
                        `;
                    });

                    html += `
                            </div>
                            <div class="text-end mt-4">
                                <button class="btn btn-outline-success rounded-pill px-4" onclick="solicitarFactura(${pedido.id})">
                                    Solicitar factura
                                </button>
                            </div>
                        </div>
                    `;
                });

                contenedor.innerHTML = html;
            })
            .catch(error => {
                console.error("❌ Error al cargar pedidos:", error);
                const contenedor = document.getElementById("contenedorPedidos");
                if (contenedor) {
                    contenedor.innerHTML = `<div class="alert alert-danger">❌ Error al cargar los pedidos.</div>`;
                }
            });
    }


    window.solicitarFactura = function (pedidoId) {
        const email = document.body.dataset.email;
        if (!email) {
            mostrarModal("❌ Error", "No se pudo recuperar el email del usuario.", "danger");
            return;
        }

        const boton = event.target;
        boton.disabled = true;
        boton.innerHTML = 'Enviando...';

        fetch(`/factura/enviar?pedidoId=${pedidoId}&email=${encodeURIComponent(email)}`, {
            method: "POST"
        })
        .then(response => response.text())
        .then(mensaje => {
            mostrarModal("✅ Factura enviada", "Tu factura se ha enviado correctamente al correo proporcionado. Revisa tu bandeja de entrada o spam.", "success");
            boton.innerHTML = 'Solicitar factura';
            boton.disabled = false;
        })
        .catch(error => {
            console.error("❌ Error al enviar la factura:", error);
            mostrarModal("❌ Error", "Hubo un error al enviar la factura. Inténtalo más tarde.", "danger");
            boton.innerHTML = 'Solicitar factura';
            boton.disabled = false;
        });
    };

    function mostrarModal(titulo, mensaje, tipo = "primary") {
        const modalTitulo = document.getElementById("notificationModalLabel");
        const modalMensaje = document.getElementById("notificationMessage");
        const modal = new bootstrap.Modal(document.getElementById("notificationModal"));

        modalTitulo.textContent = titulo;
        modalMensaje.textContent = mensaje;

        // Cambia el color del botón en función del tipo
        const footerBtn = document.querySelector("#notificationModal .modal-footer .btn");
        footerBtn.className = "btn btn-" + tipo;

        modal.show();
    }


    const linkPedidos = document.getElementById("linkPedidos");
    const linkPerfil = document.getElementById("linkPerfil");
    const seccionPedidos = document.getElementById("seccionPedidos");
    const seccionPerfil = document.getElementById("seccionPerfil");

    if (linkPedidos && linkPerfil && seccionPedidos && seccionPerfil) {
        linkPedidos.addEventListener("click", function (e) {
            e.preventDefault();
            seccionPedidos.classList.remove("d-none");
            seccionPerfil.classList.add("d-none");
            linkPedidos.classList.add("active", "fw-bold", "text-primary");
            linkPerfil.classList.remove("active", "fw-bold", "text-primary");
            cargarPedidos();
        });

        linkPerfil.addEventListener("click", function (e) {
            e.preventDefault();
            seccionPerfil.classList.remove("d-none");
            seccionPedidos.classList.add("d-none");
            linkPerfil.classList.add("active", "fw-bold", "text-primary");
            linkPedidos.classList.remove("active", "fw-bold", "text-primary");
        });
    }

    const btnEnviarEnlace = document.getElementById("btnEnviarEnlaceCambio");
    const alertaCambio = document.getElementById("alertaCambioContrasenia");

    if (btnEnviarEnlace && alertaCambio) {
        btnEnviarEnlace.addEventListener("click", () => {
            const email = document.body.dataset.email;
            if (!email) {
                alertaCambio.classList.remove("d-none");
                alertaCambio.className = "alert alert-danger";
                alertaCambio.textContent = "❌ No se pudo recuperar el email del usuario.";
                return;
            }

            fetch("/recuperacion/enviar-enlace", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `email=${encodeURIComponent(email)}`
            })
            .then(res => {
                if (!res.ok) throw new Error();
                return res.text();
            })
            .then(data => {
                alertaCambio.classList.remove("d-none");
                alertaCambio.className = "alert alert-success";
                alertaCambio.textContent = data;
            })
            .catch(() => {
                alertaCambio.classList.remove("d-none");
                alertaCambio.className = "alert alert-danger";
                alertaCambio.textContent = "❌ Error al enviar el correo. Intenta más tarde.";
            });
        });
    }
});
