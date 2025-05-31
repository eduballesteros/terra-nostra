<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tienda - Terra Nostra</title>

    <!-- Estilos -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shop.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">


</head>
<body>

    <!-- Header común -->
    <jsp:include page="/includes/header.jsp" />

    <main class="container-fluid mt-5 px-4">
        <!-- Buscador -->
        <div class="row mb-4">
            <div class="col-12 d-flex justify-content-center">
                <input type="text" id="buscadorProductos" class="form-control shadow-sm" placeholder="Buscar productos...">
            </div>
        </div>

        <div class="row">
            <!-- Panel lateral -->
            <aside class="col-md-3 mb-4">
                <div class="p-3 bg-white rounded-4 shadow-sm">
                    <h5 class="fw-bold mb-3">Categorías</h5>
                    <ul class="list-unstyled">
                        <li><a href="#" class="categoria-link active d-block" data-categoria="">Todas las categorías</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Suplementos Naturales">Suplementos Naturales</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Tés e Infusiones Naturales">Tés e Infusiones Naturales</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Superalimentos">Superalimentos</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Alimentación Saludable">Alimentación Saludable</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Cosmética Natural">Cosmética Natural</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Bienestar y Relax">Bienestar y Relax</a></li>
                        <li><a href="#" class="categoria-link d-block" data-categoria="Packs y Regalos Naturales">Packs y Regalos Naturales</a></li>
                    </ul>


                    <hr class="my-4">

                    <h5 class="fw-bold mb-2">Filtros</h5>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="filtroStock">
                        <label class="form-check-label" for="filtroStock">Solo disponibles</label>
                    </div>
                </div>
            </aside>

            <!-- Contenido productos -->
            <section class="col-md-9">
                <h3 class="mb-4 text-success fw-semibold">Nuestros Productos</h3>

                <div id="loaderProductos" class="text-center my-5">
                    <div class="spinner-border text-success" role="status" style="width: 3rem; height: 3rem;">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                    <p class="mt-3">Estamos cargando los mejores productos para ti...</p>
                </div>

                <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4 d-none" id="contenedorProductos">
                    <!-- Productos dinámicos -->
                </div>
            </section>
        </div>
    </main>

    <!-- Scripts -->
    <script src="js/tienda.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="${pageContext.request.contextPath}/js/carrito.js"></script>

</body>
</html>
