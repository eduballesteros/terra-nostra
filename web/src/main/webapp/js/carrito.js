document.addEventListener("DOMContentLoaded", function () {
    verificarSesionYMostrarCarrito();

    function verificarSesionYMostrarCarrito() {
        fetch("/auth/verificar-sesion")
            .then(r => r.json())
            .then(data => {
                if (!data.sesionActiva) {
                    mostrarError("Debes iniciar sesión para ver tu carrito.");
                    return;
                }
                if (!data.correoVerificado) {
                    mostrarError("Debes verificar tu correo electrónico antes de usar el carrito.");
                    return;
                }
                const usuarioId = data.usuarioId;
                cargarCarrito(usuarioId);
                configurarVaciarCarrito(usuarioId);
                configurarAgregarProducto(usuarioId);
            })
            .catch(err => {
                console.error("❌ Error al verificar sesión:", err);
                mostrarError("Error al verificar la sesión del usuario.");
            });
    }

    function cargarCarrito(usuarioId) {
        fetch(`/carrito/${usuarioId}`)
            .then(r => {
                if (!r.ok) throw new Error("Error al obtener el carrito");
                return r.json();
            })
            .then(data => {
                const cont = document.getElementById("contenidoCarritoLateral");
                if (!cont) return;
                cont.innerHTML = "";

                if (!data.items || data.items.length === 0) {
                    cont.innerHTML = `
                      <div class="carrito-vacio">
                        <img src="/icons/cart-empty.svg" alt="Carrito vacío">
                        <p>Su carrito está vacío.</p>
                        <a href="/tienda" class="boton-empezar">EMPIEZA A COMPRAR</a>
                      </div>`;
                    return;
                }

                const agrupados = data.items.reduce((m, item) => {
                    const id = item.productoId ?? item.producto_id;
                    if (!m[id]) {
                        m[id] = {
                            productoId: id,
                            nombre: item.nombre,
                            precioUnitario: parseFloat(item.precioUnitario ?? item.precio_unitario) || 0,
                            imagen: item.imagen,
                            cantidad: item.cantidad ?? 0
                        };
                    } else {
                        m[id].cantidad += item.cantidad ?? 0;
                    }
                    return m;
                }, {});
                const items = Object.values(agrupados);

                let total = 0;
                const htmlItems = items.map(item => {
                    const subtotal = item.precioUnitario * item.cantidad;
                    total += subtotal;
                    return `
                      <div class="item-carrito-lateral">
                        <div class="img-container">
                          <img src="data:image/png;base64,${item.imagen}" alt="${item.nombre}">
                        </div>
                        <div class="info">
                          <p class="nombre">${item.nombre}</p>
                          <div class="control-cantidad">
                            <button class="btn-decrementar" data-id="${item.productoId}">−</button>
                            <span class="cantidad">${item.cantidad}</span>
                            <button class="btn-incrementar" data-id="${item.productoId}">+</button>
                          </div>
                          <p class="precio-unitario">Precio unitario: ${item.precioUnitario.toFixed(2)} €</p>
                          <p class="subtotal">Subtotal: ${subtotal.toFixed(2)} €</p>
                          <button class="btn-eliminar" data-id="${item.productoId}">Eliminar</button>
                        </div>
                      </div>`;
                }).join("");

                cont.innerHTML = `
                  <div class="lista-carrito-lateral">
                    ${htmlItems}
                    <div class="total-carrito">
                      Total: <strong>${total.toFixed(2)} €</strong>
                    </div>
                    <button class="boton-empezar" onclick="finalizarCompra()">FINALIZAR COMPRA</button>
                  </div>`;

                configurarControlesCantidad(usuarioId);
                configurarEliminarProducto(usuarioId);
            })
            .catch(err => {
                console.error("❌ Error al cargar carrito:", err);
                mostrarError("No se pudo cargar el carrito.");
            });
    }

    function configurarControlesCantidad(usuarioId) {
        document.querySelectorAll('.btn-decrementar').forEach(btn => {
            btn.onclick = () => {
                const productoId = btn.dataset.id;
                const cantElem = btn.parentElement.querySelector('.cantidad');
                const nuevaCantidad = parseInt(cantElem.textContent) - 1;
                if (nuevaCantidad < 0) return;
                actualizarCantidadCarrito(usuarioId, productoId, nuevaCantidad);
            };
        });
        document.querySelectorAll('.btn-incrementar').forEach(btn => {
            btn.onclick = () => {
                const productoId = btn.dataset.id;
                const cantElem = btn.parentElement.querySelector('.cantidad');
                const nuevaCantidad = parseInt(cantElem.textContent) + 1;
                actualizarCantidadCarrito(usuarioId, productoId, nuevaCantidad);
            };
        });
    }

    function actualizarCantidadCarrito(usuarioId, productoId, nuevaCantidad) {
        const metodo = nuevaCantidad === 0 ? 'DELETE' : 'PUT';
        const url = `/carrito/${usuarioId}/producto/${productoId}`;
        const opts = { method: metodo };
        if (metodo === 'PUT') {
            opts.headers = { 'Content-Type': 'application/json' };
            opts.body = JSON.stringify({ cantidad: nuevaCantidad });
        }

        fetch(url, opts)
            .then(r => {
                if (!r.ok) throw new Error("No se pudo actualizar la cantidad");
                return r.json();
            })
            .then(() => {
                cargarCarrito(usuarioId);
            })
            .catch(err => {
                console.error("❌ Error al actualizar cantidad:", err);
                mostrarError("Error al actualizar la cantidad.");
            });
    }

    function configurarEliminarProducto(usuarioId) {
        document.querySelectorAll('.btn-eliminar').forEach(btn => {
            btn.onclick = () => {
                const productoId = btn.dataset.id;
                if (!productoId) {
                    mostrarError("Error interno: ID de producto indefinido");
                    return;
                }
                fetch(`/carrito/${usuarioId}/producto/${productoId}`, { method: 'DELETE' })
                    .then(r => {
                        if (!r.ok) throw new Error("No se pudo eliminar el producto");
                        return r.json();
                    })
                    .then(() => {
                        cargarCarrito(usuarioId);
                    })
                    .catch(err => {
                        console.error("❌ Error al eliminar producto:", err);
                        mostrarError("No se pudo eliminar el producto.");
                    });
            };
        });
    }

    function configurarVaciarCarrito(usuarioId) {
        const btn = document.getElementById("btnVaciar");
        if (!btn) return;
        btn.onclick = () => {
            fetch(`/carrito/${usuarioId}/vaciar`, { method: "DELETE" })
                .then(r => {
                    if (!r.ok) throw new Error("No se pudo vaciar");
                    cargarCarrito(usuarioId);
                })
                .catch(err => {
                    console.error("❌ Error al vaciar carrito:", err);
                    mostrarError("Error al vaciar el carrito.");
                });
        };
    }

    function configurarAgregarProducto(usuarioId) {
        const btn = document.getElementById("btnAgregarCarrito");
        if (!btn) return;
        btn.onclick = () => {
            const productoId = btn.dataset.id;
            const precio      = btn.dataset.precio;
            const nombre      = btn.dataset.nombre || "Producto";
            const imagen      = btn.dataset.imagen || "";
            const cantidad    = parseInt(document.getElementById("cantidadProducto").value) || 1;

            const item = {
                productoId: Number(productoId),
                cantidad,
                precioUnitario: parseFloat(precio),
                nombre,
                imagen
            };

            fetch(`/carrito/agregar?usuarioId=${usuarioId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(item)
            })
            .then(r => {
                if (!r.ok) throw new Error("No se pudo añadir");
                return r.json();
            })
            .then(() => {
                mostrarAnimacionExito("✅ Producto añadido");
                cargarCarrito(usuarioId);
            })
            .catch(err => {
                console.error("❌ Error al añadir producto:", err);
                alert("No se pudo añadir el producto.");
            });
        };
    }

    function mostrarError(msg) {
        const c = document.getElementById("contenidoCarritoLateral");
        if (!c) return;
        c.innerHTML = `
          <div class="carrito-vacio">
            <img src="/icons/cart-empty.svg" alt="Carrito vacío">
            <p>${msg}</p>
            <a href="/tienda" class="boton-empezar">IR A LA TIENDA</a>
          </div>`;
    }

    function mostrarAnimacionExito(msg) {
        let aviso = document.getElementById("mensajeExitoCarrito");
        if (!aviso) {
            aviso = document.createElement("div");
            aviso.id = "mensajeExitoCarrito";
            aviso.classList.add("animacion-exito");
            Object.assign(aviso.style, {
                position: "fixed", top: "1rem", right: "1rem",
                zIndex: "9999", backgroundColor: "#28a745",
                color: "#fff", padding: "12px 20px",
                borderRadius: "8px", boxShadow: "0 0 10px rgba(0,0,0,0.1)",
                fontWeight: "bold"
            });
            document.body.appendChild(aviso);
        }
        aviso.textContent = msg;
        aviso.style.display = "block";
        aviso.classList.add("fade-in");
        setTimeout(() => {
            aviso.classList.remove("fade-in");
            aviso.style.display = "none";
        }, 2000);
    }

    // Nueva función para ir a checkout.jsp
    window.finalizarCompra = function() {
        window.location.href = "/resumenPedido  ";
    };
});
