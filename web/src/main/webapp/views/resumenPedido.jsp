<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Checkout - Terra Nostra</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

       <!-- Estilos -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shop.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
        <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- SDK de PayPal -->
        <script src="https://www.paypal.com/sdk/js?client-id=Ac_9zq0LnGyGCdGX67qgLQSRl-OPHh302i85WQBCrjKSP_trZOxYOpGqVwjJalmRxWcJAgRGAxgqmaic&currency=EUR"></script>
    </head>

    <body>

    <!-- Header común -->
    <jsp:include page="/includes/header.jsp" />

<!-- Contenido principal -->
<main class="container mt-5 fade-in">
    <section id="contenidoCheckout" class="checkout-wrapper"></section>
</main>


    <footer style="text-align: center; padding: 1rem 0; font-size: 0.9rem; color: #666; background-color: #f8f8f8;">
        &copy; Terra Nostra 2025
    </footer>


<!-- Scripts -->
<script src="${pageContext.request.contextPath}/js/checkout.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/carrito.js"></script>



</body>
</html>
