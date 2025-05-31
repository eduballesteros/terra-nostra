<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Cambiar Contraseña - Terra Nostra</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Estilos -->
    <link rel="stylesheet" href="/css/cambio-contrasenia.css">
    <link rel="icon" href="images/favicon.ico" type="image/x-icon">

    <!-- Bootstrap y Font Awesome SIN integrity -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>

<div class="container-fluid bg-pattern">
    <div class="form-area shadow-lg animate__animated animate__fadeIn">

        <!-- Logo + Título + Subtítulo -->
        <div class="text-center mb-4">
            <img src="images/logo.webp" alt="Logo Terra Nostra" class="logo-form mb-3" />
            <h2 class="modal-title">Cambiar Contraseña</h2>
            <p class="form-subtitle">Establece tu nueva contraseña</p>
        </div>


        <!-- Alerta de mensajes -->
        <div id="mensajeAlerta" class="alert d-none" role="alert"></div>

        <!-- Formulario -->
        <form id="formCambiarContrasenia" autocomplete="off">
            <input type="hidden" id="token" value="${token}" />

            <!-- Nueva contraseña -->
            <div class="form-floating-group">
                <div class="position-relative">
                    <input type="password" id="nuevaContrasenia" class="form_style" required minlength="6" placeholder=" " />
                    <label for="nuevaContrasenia">Nueva Contraseña</label>
                    <button type="button" class="toggle-password" data-target="nuevaContrasenia">
                        <i class="fa fa-eye"></i>
                    </button>
                </div>
            </div>

            <!-- Confirmar contraseña -->
            <div class="form-floating-group">
                <div class="position-relative">
                    <input type="password" id="confirmarContrasenia" class="form_style" required minlength="6" placeholder=" " />
                    <label for="confirmarContrasenia">Confirmar Contraseña</label>
                    <button type="button" class="toggle-password" data-target="confirmarContrasenia">
                        <i class="fa fa-eye"></i>
                    </button>
                </div>
                <span class="error" id="errorCoincidencia" style="display: none;">Las contraseñas no coinciden.</span>
            </div>

            <!-- Botón -->
            <button type="submit" class="modal-btn mt-4">Cambiar Contraseña</button>
        </form>

    </div>
</div>

<!-- Scripts -->
<script src="/js/authCambiarContrasenia.js"></script>


</body>
</html>
