document.addEventListener("DOMContentLoaded", function () {
    const pathParts = window.location.pathname.split("/");
    const slug = pathParts[pathParts.length - 1];

    if (!slug) {
        mostrarError("Nombre de producto no especificado.");
        return;
    }

    fetch(`/productos/ver/${slug}`) // ← CORREGIDO: string entre backticks
        .then(resp => {
            if (!resp.ok) throw new Error("Producto no encontrado.");
            return resp.json();
        })
        .then(data => {
            cargarProducto(data);
            cargarResenias(data.id);
            inicializarEstrellas();
            controlarFormulario();
        })
        .catch(error => {
            console.error("Error:", error);
            mostrarError("Producto no encontrado.");
        });

    function cargarProducto(data) {
        document.getElementById("imagenProducto").src = "data:image/png;base64," + data.imagen;
        document.getElementById("nombreProducto").textContent = data.nombre;
        document.getElementById("descripcionProducto").textContent = data.descripcion;
        document.getElementById("precioProducto").textContent = `${data.precio.toFixed(2)} €`; // ← CORREGIDO
        document.getElementById("stockProducto").textContent = data.stock > 0 ? "Disponible" : "Agotado";
        document.getElementById("productoDetalle").classList.remove("d-none");
        document.getElementById("loaderProducto").style.display = "none";

        const inputProductoId = document.getElementById("productoIdInput");
        if (inputProductoId) inputProductoId.value = data.id;

        const btnAgregar = document.getElementById("btnAgregarCarrito");
        if (btnAgregar) {
            btnAgregar.dataset.id = data.id;
            btnAgregar.dataset.precio = data.precio;

            if (data.stock <= 0) {
                btnAgregar.disabled = true;
                btnAgregar.textContent = "Agotado";
                btnAgregar.classList.add("btn-disabled");
            }
        }
    }

    function cargarResenias(idProducto) {
        fetch(`/productos/producto/${idProducto}`) // ← CORREGIDO
            .then(resp => {
                if (!resp.ok) throw new Error("Error al obtener reseñas");
                return resp.json();
            })
            .then(resenias => {
                const contenedor = document.getElementById("listaResenias");
                contenedor.innerHTML = "";

                if (resenias.length === 0) {
                    contenedor.innerHTML = "<p class='text-muted'>Este producto aún no tiene reseñas.</p>";
                    return;
                }

                resenias.forEach(r => {
                    contenedor.innerHTML += `
                      <div class="resenia-card">
                        <strong>${r.nombre_usuario}</strong>
                        <div class="text-warning">${"⭐".repeat(r.valoracion)}</div>
                        <p class="mb-1 mt-2">${r.comentario}</p>
                        <small class="text-muted">${new Date(r.fecha).toLocaleDateString()}</small>
                      </div>
                    `;
                });
            })
            .catch(() => {
                document.getElementById("listaResenias").innerHTML = "<p class='text-danger'>Error al cargar reseñas.</p>";
            });
    }

    function inicializarEstrellas() {
        const estrellas = document.querySelectorAll("#estrellas i");
        const inputValoracion = document.getElementById("valoracion");

        estrellas.forEach(estrella => {
            estrella.addEventListener("click", function () {
                const valor = parseInt(this.getAttribute("data-valor"));

                estrellas.forEach(e => {
                    e.classList.remove("fa-solid", "hovered");
                    e.classList.add("fa-regular");
                });

                for (let i = 0; i < valor; i++) {
                    estrellas[i].classList.remove("fa-regular");
                    estrellas[i].classList.add("fa-solid", "hovered");
                }

                inputValoracion.value = valor;
            });
        });
    }

    function mostrarError(msg) {
        document.getElementById("loaderProducto").innerHTML = `<p class="text-danger">${msg}</p>`; // ← CORREGIDO
    }

    function mostrarMensaje(texto, tipo) {
        const header = document.getElementById("modalReseniaHeader");
        const body = document.getElementById("modalReseniaMensaje");
        const modal = new bootstrap.Modal(document.getElementById("modalResultadoResenia"));

        header.className = "modal-header";
        if (tipo === "success") {
            header.classList.add("bg-success", "text-white");
        } else if (tipo === "danger") {
            header.classList.add("bg-danger", "text-white");
        } else {
            header.classList.add("bg-warning", "text-dark");
        }

        body.textContent = texto;
        modal.show();
    }

    function controlarFormulario() {
        const productoId = parseInt(document.getElementById("productoIdInput").value);

        fetch("/auth/verificar-sesion")
            .then(resp => resp.json())
            .then(data => {
                const sesionActiva = data.sesionActiva;
                const verificado = data.correoVerificado;
                const usuarioId = data.usuarioId || null;

                const form = document.getElementById("formResenia");
                if (!form) return;

                form.addEventListener("submit", function (e) {
                    e.preventDefault();

                    if (!sesionActiva || !verificado) {
                        const modal = new bootstrap.Modal(document.getElementById("modalReseniaRestringida"));
                        modal.show();
                        return;
                    }

                    const dto = {
                        productoId: productoId,
                        valoracion: parseInt(document.getElementById("valoracion").value),
                        comentario: document.getElementById("comentario").value.trim()
                    };

                    if (usuarioId !== null) {
                        dto.usuarioId = usuarioId;
                    }

                    if (
                        isNaN(dto.valoracion) || dto.valoracion < 1 || dto.valoracion > 5 ||
                        !dto.comentario
                    ) {
                        mostrarMensaje("Completa todos los campos.", "warning");
                        return;
                    }

                    fetch("/productos/crear-resenia", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(dto)
                    })
                        .then(resp => {
                            if (!resp.ok) return resp.json().then(error => Promise.reject(error));
                            return resp.json();
                        })
                        .then(() => {
                            mostrarMensaje("✅ Reseña enviada correctamente.", "success");
                            form.reset();
                            document.getElementById("valoracion").value = "";
                            document.querySelectorAll("#estrellas i").forEach(e => {
                                e.classList.remove("fa-solid", "hovered");
                                e.classList.add("fa-regular");
                            });
                            cargarResenias(productoId);
                        })
                        .catch(err => {
                            let msg = "❌ Error al enviar la reseña.";
                            if (typeof err === "string") {
                                msg = err;
                            } else if (err?.mensaje) {
                                msg = err.mensaje;
                            } else if (err?.error) {
                                msg = err.error;
                            }

                            if (msg.toLowerCase().includes("ya existe")) {
                                msg = "Ya has enviado una reseña para este producto.";
                            }

                            mostrarMensaje(msg, "danger");
                        });
                });
            })
            .catch(err => {
                console.error("Error al verificar sesión:", err);
            });
    }
});
