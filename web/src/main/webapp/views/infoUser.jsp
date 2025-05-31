<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - Terra Nostra</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap -->
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shop.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/infoUsu.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
     <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
     <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" crossorigin="anonymous" referrerpolicy="no-referrer" />
     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">


</head>

<body data-email="${usuarioLogueado}">

<!-- Header -->

    <jsp:include page="/includes/header.jsp" />


<!-- Panel -->
<div class="container-fluid mt-4">
    <div class="row">
        <!-- Menú lateral -->
        <aside class="col-md-3 border-end">
            <h5 class="fw-bold mb-4">Tu cuenta</h5>
            <ul class="nav flex-column">
                <li class="nav-item mb-2">
                    <a id="linkPedidos" class="nav-link d-flex align-items-center gap-2" href="#">
                        <?xml version="1.0" encoding="utf-8"?><!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
                        <svg width="18px" height="18px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M15 19L17 21L21 17M13 3H8.2C7.0799 3 6.51984 3 6.09202 3.21799C5.71569 3.40973 5.40973 3.71569 5.21799 4.09202C5 4.51984 5 5.0799 5 6.2V17.8C5 18.9201 5 19.4802 5.21799 19.908C5.40973 20.2843 5.71569 20.5903 6.09202 20.782C6.51984 21 7.0799 21 8.2 21H11.5M13 3L19 9M13 3V7.4C13 7.96005 13 8.24008 13.109 8.45399C13.2049 8.64215 13.3578 8.79513 13.546 8.89101C13.7599 9 14.0399 9 14.6 9H19M19 9V13.4M9 17H11.5M9 13H15M9 9H10" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        Pedidos
                    </a>
                </li>
                <li class="nav-item mb-2">
                    <a id="linkPerfil" class="nav-link active fw-bold text-primary d-flex align-items-center gap-2" href="#">
                       <svg xmlns="http://www.w3.org/2000/svg" width="18px" height="18px" viewBox="0 0 16 16" fill="none">
                       <path d="M8 7C9.65685 7 11 5.65685 11 4C11 2.34315 9.65685 1 8 1C6.34315 1 5 2.34315 5 4C5 5.65685 6.34315 7 8 7Z" fill="#000000"/>
                       <path d="M14 12C14 10.3431 12.6569 9 11 9H5C3.34315 9 2 10.3431 2 12V15H14V12Z" fill="#000000"/>
                       </svg>
                        Datos personales
                    </a>
                </li>
            </ul>
        </aside>

        <!-- Sección de datos personales -->
        <section id="seccionPerfil" class="col-md-9">
            <h2 class="fw-bold mb-2">Datos personales</h2>
            <p class="mb-4 text-muted">Aquí puedes ver y editar tus datos personales.</p>

            <!-- Bloque datos personales -->
            <div id="infoUsuario" class="border-bottom py-3 position-relative">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <p class="mb-1 fw-semibold text-muted">Nombre</p>
                        <p id="nombreUsuario" class="mb-0"></p>
                    </div>
                    <div class="col-md-6">
                        <p class="mb-1 fw-semibold text-muted">Teléfono</p>
                        <p id="telefonoUsuario" class="mb-0"></p>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <p class="mb-1 fw-semibold text-muted">Correo electrónico</p>
                        <p id="emailUsuario" class="mb-0"></p>
                    </div>
                    <div class="col-md-6">
                        <p class="mb-1 fw-semibold text-muted">Dirección</p>
                        <p id="direccionUsuario" class="mb-0"></p>
                    </div>
                </div>
                <div class="text-end">
                    <button id="btnAbrirModalEditar" class="btn btn-outline-dark rounded-pill px-4">Editar</button>
                </div>
            </div>

            <!-- Bloque verificación de correo -->
            <div id="bloqueVerificacionCorreo" class="py-3 border-bottom d-none">
                <p class="mb-2 text-danger fw-semibold">
                    ✉ Tu correo electrónico aún no ha sido verificado.
                </p>
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted small">
                        Por favor, verifica tu cuenta para poder realizar compras y acceder a todas las funcionalidades.
                    </div>
                    <button id="btnSolicitarVerificacion" class="btn btn-warning btn-sm rounded-pill px-3">
                        Reenviar correo de verificación
                    </button>
                </div>
                <div id="mensajeVerificacion" class="alert d-none mt-3 py-2 px-3" role="alert"></div>
            </div>


            <!-- Bloque contraseña -->
            <div class="py-3 d-flex justify-content-between align-items-center">
                <div>
                    <p class="mb-1 fw-semibold text-muted">Contraseña</p>
                    <p class="mb-2">************</p>
                </div>
                <button class="btn btn-outline-dark rounded-pill px-4"
                        data-bs-toggle="modal"
                        data-bs-target="#modalCambioContrasenia">
                    Editar
                </button>
            </div>

        </section>

        <!-- Sección de pedidos -->
        <section id="seccionPedidos" class="col-md-9 d-none">
            <h2 class="fw-bold mb-2">Pedidos y devoluciones</h2>
            <div id="contenedorPedidos">
                <p class="mb-4 text-muted">Cargando pedidos...</p>
            </div>
        </section>
    </div>

    <!-- Modal de edición -->
    <div class="modal fade" id="modalEditarUsuario" tabindex="-1" aria-labelledby="modalEditarUsuarioLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="modalEditarUsuarioLabel">Editar datos personales</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
          </div>
          <div class="modal-body">
            <!-- Alerta visual -->
            <div id="alertaModal" class="alert d-none" role="alert"></div>

            <form id="formEditarUsuario">
              <div class="mb-3">
                <label for="editNombre" class="form-label">Nombre</label>
                <input type="text" class="form-control" id="editNombre" required>
              </div>
              <div class="mb-3">
                <label for="editApellido" class="form-label">Apellido</label>
                <input type="text" class="form-control" id="editApellido" required>
              </div>
              <div class="mb-3">
                <label for="editTelefono" class="form-label">Teléfono</label>
                <input type="text" class="form-control" id="editTelefono">
              </div>
              <div class="mb-3">
                <label for="editDireccion" class="form-label">Dirección</label>
                <input type="text" class="form-control" id="editDireccion">
              </div>
              <input type="hidden" id="editEmail">
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button type="submit" class="btn btn-primary" form="formEditarUsuario">Guardar cambios</button>
          </div>
        </div>
      </div>
    </div>

</div>

<!-- Modal de Cambio de Contraseña -->
<div class="modal fade" id="modalCambioContrasenia" tabindex="-1" aria-labelledby="modalCambioContraseniaLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalCambioContraseniaLabel">Cambiar contraseña</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <p>¿Quieres cambiar tu contraseña?</p>
        <p>Te enviaremos un enlace seguro a tu correo electrónico para completar el proceso.</p>
        <div id="alertaCambioContrasenia" class="alert d-none mt-3" role="alert"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn btn-primary" id="btnEnviarEnlaceCambio">Enviar enlace</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content border-success border-2">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="notificationModalLabel">✅ Factura enviada</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body" id="notificationMessage">
                Te hemos enviado un correo con el PDF adjunto. Revisa tu bandeja de entrada y la carpeta de spam.
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-success" data-bs-dismiss="modal">Aceptar</button>
            </div>
        </div>
    </div>
</div>

<footer style="text-align: center; padding: 1rem 0; font-size: 0.9rem; color: #666; background-color: #f8f8f8;">
    &copy; Terra Nostra 2025
</footer>


<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/perfilUsuario.js"></script>
<script src="${pageContext.request.contextPath}/js/carrito.js"></script>


</body>
</html>
