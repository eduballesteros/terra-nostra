<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Sobre Nosotros - Terra Nostra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sobre.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrito.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
          crossorigin="anonymous">
    <link rel="icon" href="${pageContext.request.contextPath}/images/favicon.ico" type="image/x-icon">
</head>
<body>

<jsp:include page="/includes/header.jsp" />

<main class="container" style="padding-top: 3rem; padding-bottom: 3rem;">
    <section class="text">
        <h1 class="libre-baskerville-regular">Sobre Terra Nostra</h1>
    </section>

    <section class="features-container">
        <p style="font-size: 1.1rem; line-height: 1.8; color: var(--color-secondary);">
            En <strong>Terra Nostra</strong>, creemos en el poder de la naturaleza para transformar vidas. Desde nuestros inicios,
            nos hemos comprometido a ofrecer productos naturales, ecológicos y sostenibles que promuevan el bienestar integral.
            <br><br>
            Nuestro equipo está formado por personas apasionadas por la salud, la nutrición y el respeto por el planeta. Nos esforzamos
            cada día por garantizar la calidad, autenticidad y responsabilidad en cada producto que ofrecemos.
        </p>
    </section>

    <section class="features-grid">
        <div class="feature-box">
            <!-- Comprometidos con lo natural -->
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="30" stroke="#597931" stroke-width="4"/>
                <path d="M32 10C32 22 22 32 10 32C22 32 32 42 32 54C32 42 42 32 54 32C42 32 32 22 32 10Z" fill="#597931"/>
            </svg>
            <div class="feature-content">Comprometidos con lo natural</div>
        </div>

        <div class="feature-box">
            <!-- Sostenibilidad -->
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="28" stroke="#003300" stroke-width="3"/>
                <path d="M40 24C36 20 28 20 24 28C22 32 24 38 30 40C36 42 40 38 40 32V24Z" fill="#597931"/>
                <path d="M26 30C30 32 34 36 36 40" stroke="#003300" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <div class="feature-content">Sostenibilidad en cada paso</div>
        </div>

        <div class="feature-box">
            <!-- Calidad -->
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" viewBox="0 0 64 64" fill="none">
                <circle cx="32" cy="32" r="30" stroke="#597931" stroke-width="4"/>
                <path d="M20 32L28 40L44 24" stroke="#003300" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div class="feature-content">Productos certificados y de calidad</div>
        </div>

        <div class="feature-box">
            <!-- Equipo -->
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" viewBox="0 0 64 64" fill="none">
                <circle cx="20" cy="18" r="5" fill="#597931"/>
                <circle cx="44" cy="18" r="5" fill="#597931"/>
                <rect x="15" y="24" width="10" height="16" rx="2" fill="#003300"/>
                <rect x="39" y="24" width="10" height="16" rx="2" fill="#003300"/>
                <rect x="15" y="42" width="4" height="10" fill="#003300"/>
                <rect x="21" y="42" width="4" height="10" fill="#003300"/>
                <rect x="39" y="42" width="4" height="10" fill="#003300"/>
                <rect x="45" y="42" width="4" height="10" fill="#003300"/>
            </svg>
            <div class="feature-content">Un equipo comprometido</div>
        </div>
    </section>
</main>

<footer style="text-align: center; padding: 1rem 0; font-size: 0.9rem; color: #666; background-color: #f8f8f8;">
    &copy; Terra Nostra 2025
</footer>

</body>
</html>
