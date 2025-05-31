<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del Producto - Terra Nostra</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Estilos -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shop.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productos.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>

<!-- Header común -->
<jsp:include page="/includes/header.jsp" />

<!-- Contenido principal -->
<main class="container my-5">

    <!-- Loader -->
    <div id="loaderProducto" class="loader-terra">
        <div class="spinner"></div>
        <p>Cargando producto...</p>
    </div>

    <!-- Detalle del producto -->
    <div id="productoDetalle" class="producto-detalle d-none row gx-5 px-4 mx-auto align-items-start py-4" style="border-radius: 20px; background: linear-gradient(135deg, #f5f5f5, #ffffff); box-shadow: 0 10px 25px rgba(0,0,0,0.05); max-width: 1100px;">
        <div class="col-lg-6 mb-4 text-center px-4">
            <div class="p-3 rounded-4 bg-white shadow-sm" style="border: 1px solid #ddd;">
                <img id="imagenProducto" src="" alt="Imagen del producto" class="img-fluid rounded-4" style="max-height: 420px; object-fit: contain;">
            </div>
        </div>
        <div class="col-lg-6 px-4">
            <h1 id="nombreProducto" class="fw-bold fs-3 mb-3 text-dark"></h1>
            <p id="descripcionProducto" class="text-muted mb-4" style="font-size: 0.95rem;"></p>

            <div class="producto-precio-wrapper mb-3 d-flex align-items-center gap-2">
                <span id="precioProducto" class="fs-4 text-success fw-semibold"></span>
                <span class="badge bg-light text-dark border" style="font-size: 0.85rem;">IVA incluido</span>
            </div>

            <p class="producto-stock text-dark fw-medium mb-3">Disponibilidad: <span id="stockProducto" class="fw-semibold text-success"></span></p>

            <div class="d-flex align-items-center gap-3 mb-3">
                <label for="cantidadProducto" class="form-label fw-semibold mb-0">Cantidad:</label>
                <input type="number" id="cantidadProducto" name="cantidad" class="form-control" value="1" min="1" style="width: 80px;">
            </div>

            <button id="btnAgregarCarrito"
                    class="btn btn-dark px-4 py-2 fw-semibold w-100"
                    style="border-radius: 12px;"
                    data-id="${producto.id}"
                    data-precio="${producto.precio}"
                    data-cantidad="1">
                🛒 Añadir al carrito
            </button>

            <div class="mt-4 px-3 py-2 bg-white border rounded-4 shadow-sm">
                <p class="mb-1 text-muted small">🚚 Entrega en 3-5 días laborables</p>
                <p class="text-success small fw-semibold">📦 Envío gratuito en pedidos superiores a 50 €</p>
            </div>
        </div>
    </div>

    <!-- Sección de reseñas -->
    <section id="resenias" class="my-5">
        <div class="container">
            <div class="resenias-wrapper mx-auto text-start p-4 bg-white shadow-sm rounded-3" style="max-width: 960px;">
                <h2 class="mb-4 text-dark fw-semibold border-bottom pb-2">Reseñas del producto</h2>
                <div id="listaResenias" class="d-flex flex-column gap-4">
                    <p class="text-muted">Cargando reseñas...</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Formulario de nueva reseña -->
    <section class="my-5">
        <div class="container">
            <div class="formulario-resenia mx-auto text-start bg-white shadow-sm rounded-3 p-4" style="max-width: 800px;">
                <h3 class="mb-4 text-dark fw-semibold border-bottom pb-2">Deja tu reseña</h3>
                <form id="formResenia">
                    <input type="hidden" name="productoId" id="productoIdInput" />
                    <div class="mb-3">
                        <label for="comentario" class="form-label fw-semibold text-dark">Comentario</label>
                        <textarea class="form-control rounded-3" name="comentario" id="comentario" rows="4" required placeholder="¿Qué te ha parecido este producto?"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold text-dark">Valoración</label>
                        <div id="estrellas" class="text-warning fs-3 d-flex gap-2">
                            <i class="fa-regular fa-star star-hover" data-valor="1"></i>
                            <i class="fa-regular fa-star star-hover" data-valor="2"></i>
                            <i class="fa-regular fa-star star-hover" data-valor="3"></i>
                            <i class="fa-regular fa-star star-hover" data-valor="4"></i>
                            <i class="fa-regular fa-star star-hover" data-valor="5"></i>
                        </div>
                        <input type="hidden" name="valoracion" id="valoracion" required>
                    </div>
                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-success px-4">Enviar reseña</button>
                    </div>
                </form>
            </div>
        </div>
    </section>

    <!-- Modal de acceso restringido -->
    <div class="modal fade" id="modalReseniaRestringida" tabindex="-1" aria-labelledby="modalReseniaLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-warning" id="modalReseniaHeader">
                    <h5 class="modal-title" id="modalReseniaLabel">Inicia sesión para opinar</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    Debes iniciar sesión y verificar tu cuenta para poder dejar una reseña.
                </div>
                <div class="modal-footer">
                    <a href="/login" class="btn btn-primary">Iniciar sesión</a>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de confirmación -->
    <div class="modal fade" id="modalResultadoResenia" tabindex="-1" aria-labelledby="modalResultadoReseniaLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title" id="modalResultadoReseniaLabel">Reseña enviada</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body" id="modalReseniaMensaje">
                    <!-- Mensaje dinámico -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

</main>



<footer style="text-align: center; padding: 1rem 0; font-size: 0.9rem; color: #666; background-color: #f8f8f8;">
    &copy; Terra Nostra 2025
</footer>


<!-- Scripts -->
<script src="${pageContext.request.contextPath}/js/productos.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/carrito.js"></script>

</body>
</html>
