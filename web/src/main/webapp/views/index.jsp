<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    String mensaje = request.getParameter("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Terra Nostra - Productos Naturales</title>

    <!-- Estilos personalizados -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">



    <!-- Bootstrap -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
          crossorigin="anonymous">

    <!-- Icono -->
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">

    <!-- Google Login -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>
</head>
<body>

    <!-- Header -->
    <jsp:include page="/includes/header.jsp" />

    <c:choose>
        <c:when test="${param.verificacion == 'exitosa'}">
            <div id="alertaVerificacion" class="alert alert-success text-center" style="margin: 20px auto; max-width: 600px;">
                ✅ Tu correo ha sido verificado correctamente. ¡Ya puedes iniciar sesión!
            </div>
        </c:when>
        <c:when test="${param.verificacion == 'fallida'}">
            <div id="alertaVerificacion" class="alert alert-danger text-center" style="margin: 20px auto; max-width: 600px;">
                ❌ El enlace de verificación es inválido o ha expirado.
            </div>
        </c:when>
    </c:choose>


    <!-- Carrusel de imágenes -->
    <div id="carouselExampleDark" class="carousel slide custom-carousel" data-bs-ride="carousel">
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#carouselExampleDark" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
            <button type="button" data-bs-target="#carouselExampleDark" data-bs-slide-to="1" aria-label="Slide 2"></button>
            <button type="button" data-bs-target="#carouselExampleDark" data-bs-slide-to="2" aria-label="Slide 3"></button>
        </div>
        <div class="carousel-inner">
            <div class="carousel-item active" data-bs-interval="10000">
                <img src="${pageContext.request.contextPath}/images/magnesio.webp" class="d-block w-100" alt="Magnesio">
            </div>
            <div class="carousel-item" data-bs-interval="2000">
                <img src="${pageContext.request.contextPath}/images/ashwagandha.webp" class="d-block w-100" alt="Ashwagandha">
            </div>
            <div class="carousel-item">
                <img src="${pageContext.request.contextPath}/images/canela.webp" class="d-block w-100" alt="Canela">
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleDark" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleDark" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Siguiente</span>
        </button>
    </div>

    <!-- Sección de mejores productos -->
    <p class="text text-center mt-4">Nuestros mejores productos</p>

    <div class="container mt-4">
        <div class="row" id="mejoresProductos">
            <!-- Los productos destacados se insertarán aquí dinámicamente -->
        </div>
    </div>

    <!-- Sección de características -->

     <section class="container my-5">
         <div class="row g-4">
             <div class="col-12 col-md-6">
                 <div class="feature-box text-center h-100 shadow-sm">
                     <img src="https://www.biosano.es/cdnassets/redesign/icon/icon-home-1-rs.png" class="feature-icon" alt="Envío en 24/48h">
                     <div class="feature-content mt-2">Envío en 24/48h</div>
                     <a href="https://www.biosano.es/envios-y-devoluciones" class="feature-link d-inline-block mt-2">Saber más</a>
                 </div>
             </div>
             <div class="col-12 col-md-6">
                 <div class="feature-box text-center h-100 shadow-sm">
                     <img src="https://www.biosano.es/cdnassets/redesign/icon/icon-home-2-rs.png" class="feature-icon" alt="Devoluciones fáciles">
                     <div class="feature-content mt-2">Devoluciones fáciles</div>
                     <a href="https://www.biosano.es/politica-de-devoluciones" class="feature-link d-inline-block mt-2">Saber más</a>
                 </div>
             </div>
             <div class="col-12 col-md-6">
                 <div class="feature-box text-center h-100 shadow-sm">
                     <img src="https://www.biosano.es/cdnassets/redesign/icon/icon-home-3-rs.png" class="feature-icon" alt="Pago 100% seguro">
                     <div class="feature-content mt-2">Pago 100% seguro</div>
                     <a href="https://www.biosano.es/formas-de-pago" class="feature-link d-inline-block mt-2">Saber más</a>
                 </div>
             </div>
             <div class="col-12 col-md-6">
                 <div class="feature-box text-center h-100 shadow-sm">
                     <img src="https://www.biosano.es/cdnassets/redesign/icon/icon-home-4-rs.png" class="feature-icon" alt="Mejor precio garantizado">
                     <div class="feature-content mt-2">Mejor precio garantizado</div>
                     <a href="https://www.biosano.es/promociones" class="feature-link d-inline-block mt-2">Saber más</a>
                 </div>
             </div>
         </div>
     </section>


    <!-- Footer -->

    <jsp:include page="/includes/footer.jsp" />


    <div class="modal fade" id="modalPagoExitoso" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content custom-modal">
          <div class="modal-header" style="background-color: #28a745; color: white;">
            <h5 class="modal-title w-100 text-center">✅ ¡Gracias por tu compra!</h5>
          </div>
          <div class="modal-body text-center">
            <p>Tu pedido se ha procesado correctamente. Pronto lo recibirás.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Error en el pago -->
    <div class="modal fade" id="modalPagoError" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content custom-modal">
          <div class="modal-header" style="background-color: #dc3545; color: white;">
            <h5 class="modal-title w-100 text-center">❌ Error en el pago</h5>
          </div>
          <div class="modal-body text-center">
            <p>Ocurrió un problema al procesar el pago. Por favor, intenta nuevamente.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Pago cancelado -->
    <div class="modal fade" id="modalPagoCancelado" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content custom-modal">
          <div class="modal-header" style="background-color: #ffc107; color: #212529;">
            <h5 class="modal-title w-100 text-center">⚠️ Pago cancelado</h5>
          </div>
          <div class="modal-body text-center">
            <p>Has cancelado el proceso de pago. Puedes volver a intentarlo cuando quieras.</p>
          </div>
        </div>
      </div>
    </div>


    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/cargarProductos.js"></script>
    <script src="js/authForm.js"></script>
     <script src="${pageContext.request.contextPath}/js/authModal.js"></script>


</body>
</html>
