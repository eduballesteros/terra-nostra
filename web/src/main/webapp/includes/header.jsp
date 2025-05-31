<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Header -->
<header class="header">
    <!-- Logo de la tienda -->
    <div class="logo">
        <img src="${pageContext.request.contextPath}/images/logo.webp" alt="Logo de la tienda" />
    </div>

    <!-- Barra de navegación -->
    <nav class="navigation">
        <ul>
            <li><a href="${pageContext.request.contextPath}/" class="active">Home</a></li>
            <li><a href="${pageContext.request.contextPath}/tienda">Productos</a></li>
            <li><a href="${pageContext.request.contextPath}/sobre-nosotros">Sobre Nosotros</a></li>
        </ul>
    </nav>

    <!-- Íconos de la cabecera -->
    <div class="header-icons">
        <a class="account" aria-label="Ir a cuenta de usuario" href="#">
            <img src="${pageContext.request.contextPath}/icons/user.svg" alt="Icono de usuario" />
        </a>
        <a class="cart" aria-label="Abrir carrito" href="javascript:void(0)">
            <img src="${pageContext.request.contextPath}/icons/cart.svg" alt="Icono del carrito" />
        </a>

    </div>
</header>

<!-- Modal de Login y Registro -->
    <div id="loginModal" class="modal fade" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content login-modal-content">
            <button type="button" class="close-modal" id="closeModalButton" aria-label="Cerrar">×</button>

            <div class="modal-body">
                <div class="form-area">

                    <!-- Mi Cuenta -->
                    <div id="userManager" class="user-manager" style="display: none;">
                        <p class="modal-title">Mi Cuenta</p>
                        <div class="user-info">
                            <p class="user-welcome"><strong>Hola, <span id="userName">Usuario</span></strong></p>
                            <ul class="user-options">
                                <li><a id="profileLink" href="#"><i class="fa-solid fa-user"></i> Mi perfil</a></li>
                                <li><a href="#" id="logoutButton"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</a></li>
                            </ul>
                        </div>
                    </div>

                    <!-- Registro -->
                    <!-- Registro -->
                    <form id="registerForm">
                        <p class="modal-title" id="modalTitle">REGISTRARSE</p>

                        <div class="form_group">
                            <label class="sub_title" for="regEmail">Email</label>
                            <input type="email" id="regEmail" name="email" class="form_style" placeholder="Ingresa tu email" required>
                        </div>

                        <div class="form_group">
                            <label class="sub_title" for="nombre">Nombre</label>
                            <input type="text" id="nombre" name="nombre" class="form_style" placeholder="Ingresa tu nombre" required>
                        </div>

                        <div class="form_group">
                            <label class="sub_title" for="apellido">Apellido</label>
                            <input type="text" id="apellido" name="apellido" class="form_style" placeholder="Ingresa tu apellido" required>
                        </div>

                        <div class="form_group">
                            <label class="sub_title" for="contrasenia">Contraseña</label>
                            <input type="password" id="contrasenia" name="contrasenia" class="form_style" placeholder="Crea una contraseña" required minlength="6">
                        </div>

                        <div class="form_group">
                            <label class="sub_title" for="confirmPassword">Confirmar Contraseña</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form_style" placeholder="Repite tu contraseña" required />
                            <p class="error" id="passwordError" style="display: none;">Las contraseñas no coinciden</p>
                        </div>

                        <button class="modal-btn" type="submit">REGISTRARSE</button>

                        <button type="button" class="google-login-btn" id="googleRegisterBtn">
                            <img src="${pageContext.request.contextPath}/icons/google-icon.svg" alt="Google" />
                            Registrarse con Google
                        </button>

                        <p class="line">¿Ya tienes cuenta? <a href="javascript:void(0)" onclick="toggleForm()">Inicia Sesión</a></p>
                    </form>


                    <!-- Login -->
                    <form id="loginForm">
                        <p class="modal-title">INICIAR SESIÓN</p>

                        <div class="form_group">
                            <label class="sub_title" for="loginEmail">Email</label>
                            <input type="email" id="loginEmail" name="email" class="form_style" placeholder="Ingresa tu email" required>
                        </div>

                        <div class="form_group">
                            <label class="sub_title" for="loginPassword">Contraseña</label>
                            <input type="password" id="loginPassword" name="contrasenia" class="form_style" placeholder="Ingresa tu contraseña" required>
                        </div>

                        <button class="modal-btn" type="submit">INICIA SESIÓN</button>

                        <button type="button" class="google-login-btn" id="googleLoginBtn">
                            <img src="${pageContext.request.contextPath}/icons/google-icon.svg" alt="Google" />
                            Continuar con Google
                        </button>

                        <p class="line">¿Has olvidado tu contraseña? <a href="javascript:void(0);" onclick="abrirRecuperarContrasenia()">Recupérala aquí</a></p>
                        <p class="line">¿No tienes cuenta? <a href="javascript:void(0)" onclick="toggleForm()">Regístrate</a></p>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal de Notificación -->
<div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="notificationModalLabel">Notificación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body" id="notificationMessage"></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Aceptar</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal Recuperar Contraseña -->
<div class="modal fade" id="recuperarPasswordModal" tabindex="-1" aria-labelledby="recuperarPasswordModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content login-modal-content">
            <button type="button" class="close-modal" data-bs-dismiss="modal" aria-label="Cerrar">×</button>
            <div class="modal-body">
                <div class="form-area">
                    <form id="recuperarPasswordForm">
                        <p class="modal-title">RECUPERAR CONTRASEÑA</p>
                        <div class="form_group">
                            <label class="sub_title" for="recuperarEmail">Email</label>
                            <input type="email" id="recuperarEmail" class="form_style" placeholder="Introduce tu email" required>
                        </div>
                        <div id="recuperarAlert" class="alert d-none mt-2" role="alert" style="display:none;"></div>
                        <button id="btnEnviarRecuperacion" class="modal-btn" type="submit">Enviar enlace</button>
                        <p class="line">¿Recuerdas tu contraseña? <a href="javascript:void(0)" onclick="cerrarModalRecuperacion()">Iniciar sesión</a></p>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Google One Tap -->
<div id="g_id_onload"
     data-client_id="751231715559-3avnniuamcnc5a2dvcb88daonksn91p5.apps.googleusercontent.com"
     data-callback="onGoogleSignIn"
     data-auto_prompt="false">
</div>

<!-- Toasts -->
<!-- Toasts -->
<div class="toast-container position-fixed bottom-0 end-0 p-3" id="toastContainer"></div>

<!-- Scripts -->
<script src="${pageContext.request.contextPath}/js/authForm.js"></script>
<script src="${pageContext.request.contextPath}/js/authGoogleLogin.js"></script>
<script src="${pageContext.request.contextPath}/js/authCambiarContrasenia.js"></script>
<script src="${pageContext.request.contextPath}/js/authModal.js"></script>


<jsp:include page="/includes/carritoLateral.jsp" />


