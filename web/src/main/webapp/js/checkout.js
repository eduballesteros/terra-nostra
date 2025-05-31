
document.addEventListener("DOMContentLoaded", function () {
  verificarSesionYMostrarCheckout();

  function verificarSesionYMostrarCheckout() {
    fetch("/auth/verificar-sesion")
      .then(r => r.json())
      .then(data => {
        if (!data.sesionActiva) {
          return mostrarError("Debes iniciar sesión para proceder al pago.");
        }
        if (!data.correoVerificado) {
          return mostrarError("Verifica tu correo antes de continuar.");
        }
        renderCheckout(data.usuarioId);
      })
      .catch(() => mostrarError("No se pudo verificar tu sesión."));
  }

  function renderCheckout(usuarioId) {
    const cont = document.getElementById("contenidoCheckout");
    if (!cont) return console.error("contenedor #contenidoCheckout no encontrado");

    cont.innerHTML = `
      <div class="checkout-row">
        <!-- IZQUIERDA: FORMULARIO -->
        <div class="checkout-form">
          <div class="checkout-seccion">
            <h3>Contacto</h3>
            <form id="formEnvio" novalidate>
              <input type="email" name="email" placeholder="Correo electrónico" required
                     class="form-control mb-3">
            </form>
          </div>

          <div class="checkout-seccion">
            <h3>Datos de envío</h3>
            <form id="formEnvioDetalles" novalidate>
              <div class="row g-2">
                <div class="col-md-6">
                  <input type="text" name="nombre" placeholder="Nombre" required class="form-control">
                </div>
                <div class="col-md-6">
                  <input type="text" name="apellidos" placeholder="Apellidos" required class="form-control">
                </div>
              </div>
              <input type="text" name="direccion" placeholder="Dirección" required class="form-control mt-2">
              <div class="row g-2 mt-2">
                <div class="col-md-4">
                  <input type="text" name="postal" placeholder="Código postal" required class="form-control">
                </div>
                <div class="col-md-8">
                  <input type="text" name="ciudad" placeholder="Ciudad" required class="form-control">
                </div>
              </div>
            </form>
          </div>
        </div>

        <!-- DERECHA: RESUMEN DE PEDIDO -->
        <aside class="order-summary">
          <h3>Resumen de pedido</h3>
          <div id="resumenItems"></div>
          <div class="total-line">
            <span>Total</span>
            <span id="totalCheckout">0,00 €</span>
          </div>
          <div class="checkout-accion mt-3">
            <!-- Contenedor real para PayPal SDK -->
            <div id="paypal-button-container" data-usuario-id="${usuarioId}"></div>
          </div>
        </aside>
      </div>
    `;

    cargarResumen(usuarioId);
    renderBotonPayPal(usuarioId); // ✅ solo se llama una vez aquí
  }

  function cargarResumen(usuarioId) {
    fetch(`/carrito/${usuarioId}`)
      .then(r => r.ok ? r.json() : Promise.reject())
      .then(data => {
        const cont = document.getElementById("resumenItems");
        cont.innerHTML = "";
        let total = 0;

        const agrupados = (data.items || []).reduce((m, item) => {
          const id = item.productoId ?? item.producto_id;
          if (!m[id]) {
            m[id] = {
              nombre: item.nombre,
              imagen: item.imagen,
              cantidad: item.cantidad || 0,
              precio: parseFloat(item.precioUnitario ?? item.precio_unitario) || 0
            };
          } else {
            m[id].cantidad += item.cantidad || 0;
          }
          return m;
        }, {});

        Object.values(agrupados).forEach(i => {
          total += i.precio * i.cantidad;
          cont.innerHTML += `
            <div class="item-carrito-lateral">
              <div class="img-container">
                <img src="data:image/png;base64,${i.imagen}" alt="${i.nombre}" class="img-fluid">
              </div>
              <div class="info">
                <p class="nombre mb-0">${i.nombre} × ${i.cantidad}</p>
              </div>
            </div>`;
        });

        document.getElementById("totalCheckout").textContent = total.toFixed(2) + " €";
      })
      .catch(() => mostrarError("No se pudo cargar el resumen."));
  }

  let paypalRenderizado = false;

  function renderBotonPayPal(usuarioId) {
    const observer = new MutationObserver(() => {
      const paypalContainer = document.getElementById("paypal-button-container");
      const totalEl = document.getElementById("totalCheckout");

      if (paypalContainer && totalEl && typeof paypal !== "undefined" && !paypalRenderizado) {
        paypalRenderizado = true; // ✅ protección contra render doble
        observer.disconnect();

        paypal.Buttons({
          createOrder: function (data, actions) {
            const f1 = document.getElementById("formEnvio");
            const f2 = document.getElementById("formEnvioDetalles");

            if (!f1.checkValidity() || !f2.checkValidity()) {
              f1.reportValidity();
              f2.reportValidity();
              return Promise.reject(new Error("Formulario inválido"));
            }

            const envio = {
              ...Object.fromEntries(new FormData(f1)),
              ...Object.fromEntries(new FormData(f2))
            };

            return fetch("/checkout", {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ usuarioId, envio, pago: "paypal" })
            })
              .then(r => r.ok ? r.json() : Promise.reject())
              .then(() => {
                const total = totalEl.textContent
                  .replace(/[^\d,.-]/g, '')  // elimina cualquier carácter no numérico excepto dígitos, coma, punto
                  .replace(',', '.');        // cambia coma por punto

                return actions.order.create({
                  purchase_units: [{
                    amount: {
                      value: total
                    }
                  }]
                });
              });
          },

          onApprove: function (data, actions) {
            return actions.order.capture().then(function () {
              window.location.href = '/pago/exitoso?token=' + data.orderID + '&usuarioId=' + usuarioId;
            });
          },

          onError: function (err) {
            console.error("❌ Error en el botón PayPal:", err);
            alert("Error en el proceso de pago.");
          }
        }).render('#paypal-button-container');
      }
    });

    observer.observe(document.body, { childList: true, subtree: true });
  }


  function mostrarError(msg) {
    document.getElementById("contenidoCheckout").innerHTML =
      `<div class="alert alert-danger">${msg}</div>`;
  }
});
