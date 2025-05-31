<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Mi Tienda – Carrito</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkout.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
</head>
<body>

  <!-- Contenedor del carrito lateral -->
  <div id="carritoLateral" class="carrito-lateral">
    <div class="carrito-header">
      <span class="titulo">CARRITO</span>
      <button class="cerrar-carrito" onclick="cerrarCarrito()">✕</button>
    </div>

    <div id="contenidoCarritoLateral" class="carrito-scroll">
      <!-- Placeholder para carrito vacío (se sustituirá por JS si hay ítems) -->
      <div class="carrito-vacio">
        <img src="${pageContext.request.contextPath}/icons/cart-empty.svg" alt="Carrito vacío">
        <p>Tu carrito está vacío.</p>
        <a href="${pageContext.request.contextPath}/tienda" class="boton-empezar">EMPIEZA A COMPRAR</a>
      </div>
    </div>
  </div>

  <!-- Resto de tu página… -->

  <!-- Script de lógica del carrito -->
  <script src="${pageContext.request.contextPath}/js/carrito.js"></script>
</body>
</html>
